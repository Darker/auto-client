/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.Constants;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.MSWindow;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.Window;
import cz.autoclient.autoclick.WindowCallback;
import cz.autoclient.autoclick.exceptions.APIError;

/**
 *
 * @author Jakub
 */
public class LaunchBot extends Robot {

  @Override
  public String getWindowName() {
    return Constants.patcher_window_title;
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
        
        //System.out.println("  - looking for the pixel");
        if(WindowTools.checkPoint(window, PixelOffset.Patcher_Launch, 20)) {
          Rect win_rect = window.getRect();
          Rect pos = PixelOffset.Patcher_Launch.toRect(win_rect);
          
          /*BufferedImage im = window.screenshot();
          DebugDrawing.drawPointOrRect(im, pos, Color.RED);
          DebugDrawing.drawPointOrRect(im, PixelOffset.Patcher_SetServer.toRect(win_rect), Color.YELLOW);
          DebugDrawing.displayImage(im);*/
          
          
          window.everyChild(new WindowCallback() {
            @Override
            public void run(Window w) {
              w.slowClick(pos.left, pos.top, 80);
            }
          });//slowClick(pos.left, pos.top, 80);
          
          window.click(PixelOffset.Patcher_SetServer.toRect(win_rect));
          
          lastError = false;
          overSuccesful = true;
          //System.out.println("  - Clicked");
          
          break;
        }
        else {
          //int[] diff = WindowTools.diffPoint(window, PixelOffset.Patcher_Launch.offset(0,0));
          /*BufferedImage im = window.screenshot();
          DebugDrawing.displayImage(im);*/
          /*System.out.println("   Too much diff: ["+diff[0]+", "+diff[1]+", "+diff[2]+"]");
          System.out.println("   Window size: "+window.getRect());
          System.out.println("   Window title: "+window.getTitle());
          System.out.println("   Window class name: "+MSWindow.getWindowClass(((MSWindow)window).hwnd));
          System.out.println("   Window HWND : "+((MSWindow)window).hwnd);*/
          //Get the window again
          if(window.getRect().width<200) {
            window = MSWindow.windowFromName(Constants.patcher_window_title, false);
          }
        }
        //System.out.println("  - Going to sleep.");
        Thread.sleep(1000);
      }
    } catch(APIError e) {
      lastError = true;
      //Recover if the window is still valid
      if(window.isValid() && !lastError) {
        //System.out.println("WINDOW ERROR!");
        go();
      }
      else {
        //System.out.println("WINDOW ERROR! GAME OVAR!");
        return;
      }
      //else
        //return;
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
  @Override
  public boolean canRun() {
    return super.canRun() && (!overSuccesful || fromLastExit()>8000);
  }
  
}
