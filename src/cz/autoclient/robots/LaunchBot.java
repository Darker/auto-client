/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowCallback;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.main_automation.WindowTools;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class LaunchBot extends Robot {
  /**
   * Remembers whether the last go() went with errors or not
   */
  private boolean overSuccesful = false;

  /**
   *
   * @throws InterruptedException when the htread is iterrupted externally
   */
  @Override
  protected void go() throws InterruptedException {

    while(true) {
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "  - entered the loop");
      if(Thread.interrupted())
        throw new InterruptedException("Interrupted during main while(true).");

      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "  - looking for the pixel");
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
            try {
              w.slowClick(pos.left, pos.top, 80);
              Thread.sleep(100);
            }
            catch(InterruptedException e) {
              t.interrupt();
            }
          }
        });
        while(!t.isInterrupted()) {
          Thread.sleep(1200);
          try {
            win_rect = window.getRect();
            while(WindowTools.checkPoint(window, PixelOffset.Patcher_Eula_Heading)) {
              Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Accepting eula.");
              final Rect r = win_rect;
              window.everyChild((final Window w)->{
                try {w.slowClick(PixelOffset.Patcher_Eula_Button.toRect(r), 80);}
                catch(InterruptedException e) {t.interrupt();}
              });
              Thread.sleep(300);
            }
          }
          catch(APIException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[AUTO-LAUNCH] Window is gone.");
            break; 
          }
        }
//slowClick(pos.left, pos.top, 80);

        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "  - Clicked");

        break;
      }
      else {
        //int[] diff = WindowTools.diffPoint(window, PixelOffset.Patcher_Launch.offset(0,0));
        /*BufferedImage im = window.screenshot();
        DebugDrawing.displayImage(im);*/
        /*Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   Too much diff: ["+diff[0]+", "+diff[1]+", "+diff[2]+"]");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   Window size: "+window.getRect());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   Window title: "+window.getTitle());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   Window class name: "+MSWindow.getWindowClass(((MSWindow)window).hwnd));
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   Window HWND : "+((MSWindow)window).hwnd);*/
        //Get the window again
        if(window.getRect().width<200) {
          window = MSWindow.windowFromName(ConstData.patcher_window_title, false);
        }
      }
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "  - Going to sleep.");
      Thread.sleep(1000);
    }
    overSuccesful = true;

    //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "WUT!");
  }
  
  @Override
  protected void init() {
    overSuccesful = false;
  }
  /**
   * Additionally to valid window, LaunchBot will require certain delay between
   * executions if last execution was successful.
   * @return true if there's a patcher window that ought to be clicked
   */
  @Override
  public boolean canRunEx() {
    //if(System.currentTimeMillis()%5==0)
    //  throw new Error("TEST ERROR");
    //If the PVP.net client is running, the patcher cannot be running so this can be skipped
    if(CacheByTitle.initalInst.getCache(ConstData.window_title_part).hasValidWindow())
      return false;
    return super.canRunEx() && (!overSuccesful || fromLastExit()>8000);
  }
  
}
