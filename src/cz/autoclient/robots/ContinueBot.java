/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowCallback;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.exceptions.APIException;

/**
 *
 * @author Jakub
 */
public class ContinueBot extends Robot {

  @Override
  public String getWindowName() {
    return ConstData.game_window_title;
  }
  /**
   * Remembers whether the last go() went with errors or not
   */
  private boolean lastError = false;
  private boolean overSuccesful = false;

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
        DebugDrawing.displayImage(window.screenshot());
        //System.out.println("  - Going to sleep.");
        Thread.sleep(2000);
      }
    } catch(APIException e) {
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
    lastError = overSuccesful = false;
  }
  /**
   * Additionally to valid window, LaunchBot will require certain delay between
   * executions if last execution was successful.
   * @return true if there's a patcher window that ought to be clicked
   */
  /*@Override
  public boolean canRun() {
    return super.canRun();
  }*/
  
  /*@Override
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = MSWindow.windowFromName(getWindowName(), false);
    }
    return window;
  }*/
  
}
