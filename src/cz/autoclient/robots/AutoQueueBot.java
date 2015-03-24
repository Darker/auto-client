/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.Constants;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.MSWindow;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.Window;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.exceptions.APIError;
import cz.autoclient.settings.Settings;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class AutoQueueBot extends Robot {
  private final Settings settings;
  public AutoQueueBot(Settings s) {
    settings = s;
  }
  @Override
  public String getWindowName() {
    return Constants.window_title_part;
  }
  /**
   * Remembers whether the last go() went with errors or not
   */
  private boolean lastError = false;

  /**
   *
   * @throws InterruptedException when the htread is iterrupted externally
   */
  @Override
  protected void go() throws InterruptedException {
    //System.out.println("Start waiting for launch button.");
    try {
      while(true) {
        //System.out.println("  - entered the loop");
        if(t.interrupted())
          throw new InterruptedException("Interrupted during main while(true).");
        window.restoreNoActivate();
        window.repaint();
        BufferedImage img = window.screenshot();
        if(WindowTools.checkPoint(img, PixelOffset.AM_CHAT_FIELD, 3) && 
           WindowTools.checkPoint(img, PixelOffset.AM_MINION_ICON, 30) && 
           WindowTools.checkPoint(img, PixelOffset.AM_PLAY_AGAIN, 20) && 
           WindowTools.checkPoint(img, PixelOffset.AM_SUMMONER_SPELL_COLUMN, 50)) {
          Rect size = window.getRect();
          
          WindowTools.say(window, settings.getString(Setnames.AM_SAY.name), PixelOffset.AM_CHAT_FIELD.toRect(size));
          //Play again!
          window.click(PixelOffset.AM_PLAY_AGAIN.toRect(size));
          Thread.sleep(800);
          window.click(PixelOffset.PlayButton_red.toRect(size));
          Thread.sleep(800);
          window.click(PixelOffset.Play_Solo.toRect(size));
        }
        DebugDrawing.displayImage(window.screenshot());
        //System.out.println("  - Going to sleep.");
        Thread.sleep(3000);
        Thread.yield();
      }
    } catch(APIError e) {
      lastError = true;
      //Recover if the window is still valid
      if(window.isValid() && !lastError) {
        go();
      }
    }
    //System.out.println("WUT!");
  }
  
  @Override
  protected void init() {
    lastError = false;
  }

  /*@Override
  public boolean canRun() {
    return 
      *  super.canRun();
  }*/
  
  @Override
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = MSWindow.windowFromName(getWindowName(), false);
    }
    return window;
  }
  
}
