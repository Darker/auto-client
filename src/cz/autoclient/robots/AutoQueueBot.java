/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.WindowTools;
import cz.autoclient.robots.helpers.ValueChangeToWatcher;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.exceptions.APIError;
import cz.autoclient.robots.helpers.IterationLimiter;
import cz.autoclient.settings.Settings;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class AutoQueueBot extends Robot {
  private final Settings settings;
  //public 
  public ActionListener requeued = null;
  
  public AutoQueueBot(Settings s) {
    settings = s;
  }
  @Override
  public String getWindowName() {
    return ConstData.window_title_part;
  }
  /**
   * Remembers whether the last go() went with errors or not
   */
  private boolean lastError = false;
  
  private ValueChangeToWatcher<Boolean> watchLoLExit = new ValueChangeToWatcher<>(false, false);

  /**
   *
   * @throws InterruptedException when the htread is iterrupted externally
   */
  @Override
  protected void go() throws InterruptedException {
    IterationLimiter limit = new IterationLimiter();
    watchLoLExit.resetChanged();
    //System.out.println("Start waiting for launch button.");
    try {
      while(true) {
        //System.out.println("  - entered the loop");
        if(t.interrupted())
          throw new InterruptedException("Interrupted during main while(true).");
        if(limit.limitIteration(10)) {
          throw new InterruptedException("Interrupting self because iteration limit was reached!");
        }
        //window.restoreNoActivate();
        //window.repaint();
        BufferedImage img = window.screenshot();
        
        if(WindowTools.checkPoint(img, PixelOffset.AM_CHAT_FIELD) && 
           WindowTools.checkPoint(img, PixelOffset.AM_MINION_ICON) && 
           WindowTools.checkPoint(img, PixelOffset.AM_SUMMONER_SPELL_COLUMN)) {
          Rect size = window.getRect();
          
          WindowTools.say(window, settings.getString(Setnames.AM_SAY.name), PixelOffset.AM_CHAT_FIELD.toRect(size));
          //Play again!
          if(WindowTools.checkPoint(img, PixelOffset.AM_PLAY_AGAIN)) {
            window.click(PixelOffset.AM_PLAY_AGAIN.toRect(size));
          }
          else {
            window.click(PixelOffset.AM_HOME.toRect(size));
            Thread.sleep(800);
            window.click(PixelOffset.PlayButton_red.toRect(size));
            Thread.sleep(800);
            window.click(PixelOffset.Play_Solo.toRect(size));
          }
          //window.click(PixelOffset.AM_PLAY_AGAIN.toRect(size));
          /*Thread.sleep(800);
          window.click(PixelOffset.PlayButton_red.toRect(size));
          Thread.sleep(800);
          window.click(PixelOffset.Play_Solo.toRect(size));*/
          
          if(requeued!=null)
            requeued.actionPerformed(null);
          //Exit the bot now
          break;
        }
        /*WindowTools.drawCheckPoint(img, PixelOffset.AM_CHAT_FIELD);
        WindowTools.drawCheckPoint(img, PixelOffset.AM_MINION_ICON);
        WindowTools.drawCheckPoint(img, PixelOffset.AM_PLAY_AGAIN); 
        WindowTools.drawCheckPoint(img, PixelOffset.AM_SUMMONER_SPELL_COLUMN);
        DebugDrawing.displayImage(img);*/
        //System.out.println("  - Going to sleep.");
        Thread.sleep(3000);
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

  @Override
  public boolean canRunEx() {
    //Check if League of legends is running
    if(watchLoLExit.checkValueChangedDebug(MSWindow.windowFromName(ConstData.game_window_title, false)!=null)) {
      System.out.println("AUTOQUEUE: Game just was closed!"); 
    }
    return watchLoLExit.hasChanged() && super.canRunEx() && window.isVisible();
  }
  
  /*@Override
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = MSWindow.windowFromName(getWindowName(), false);
    }
    return window;
  }*/
  
}
