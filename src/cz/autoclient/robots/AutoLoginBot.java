/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.Constants;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.robots.helpers.ValueChangeToWatcher;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.InvalidPasswordException;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class AutoLoginBot extends Robot {
  private final Settings settings;
 public AutoLoginBot(Settings s) {
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
  private boolean overSuccesful = false;
  
  private boolean initializing = false;
  
  private boolean invalidPassword = false;
  /** This bot only starts when PVP.net window suddenly appears. As such, it remembers last state of the window.
   * 
   */
  private ValueChangeToWatcher<Boolean> PVPAppeared = new ValueChangeToWatcher<>(false, true);
  /**
   *
   * @throws InterruptedException when the htread is iterrupted externally
   */
  @Override
  protected void go() throws InterruptedException {
    //System.out.println("Start waiting for login screen.");
    if(initializing) {
      //System.out.println("Initializing phase of auto login.");
      initializing = false;
      settings.getEncryptor().init();
      return;
    }
    PVPAppeared.resetChanged();
    try {
      //Increment as the bot is running, terminate bot at certain value
      int duration = 0;
      while(!Thread.interrupted() && duration++<8) {
        BufferedImage img = window.screenshot();
        
        if(WindowTools.checkPoint(img, PixelOffset.Login_ButtonDisabled, 5) && 
           WindowTools.checkPoint(img, PixelOffset.Login_PasswordField, 2) && 
           WindowTools.checkPoint(img, PixelOffset.Login_UsernameField, 2)) {
          Rect size = window.getRect();
          
          WindowTools.say(window,
                         (String)settings.getEncrypted(Setnames.REMEMBER_PASSWORD.name),
                         PixelOffset.Login_PasswordField.toRect(size));
          //Click ok
          Thread.sleep(400);
          window.click(PixelOffset.Login_ButtonDisabled.toRect(size));
          break;
        }

        /*WindowTools.drawCheckPoint(img, PixelOffset.Login_ButtonDisabled, 5);
        WindowTools.drawCheckPoint(img, PixelOffset.Login_PasswordField, 2);
        WindowTools.drawCheckPoint(img, PixelOffset.Login_UsernameField, 2); 
        DebugDrawing.displayImage(img);*/
        //System.out.println("  - Going to sleep.");
        Thread.sleep(1000);
      }
    }
    catch(InvalidPasswordException e) {
      invalidPassword = true;
      return;      
    }
    catch(Throwable e) {
      lastError = true;
      //Recover if the window is still valid
      if(canRun()) {
        go();
      }
    }
  }
  
  @Override
  protected void init() {
    lastError = overSuccesful = false;
  }
  /**
   * The bot will also claim it can run if the encryption framework needs to be initialized.
   * @return true if encryption initialization is needed or window is available and was not available before
   */
  @Override
  public boolean canRunEx() {
    if(!settings.getEncryptor().isInitialized() && settings.getEncryptor().doesUse_password()) {
      //System.out.println("Initializing - run once.");
      initializing = true;
      return true;
    }
    else {
      
    }
    if(invalidPassword) {
      //System.out.println("Cannot run - no valid password.");
      return false;
    }

    
    //boolean windowState = super.canRunEx();
    return PVPAppeared.checkValueChangedSometime(super.canRunEx());
    /*
    //If true was detected last time
    if(windowStateChangedToTrue&&windowState) {
      return true; 
    }
    //If the state is same all the time
    else if(windowState == lastWindowState) {
      //No longer try to run if the window dissapeared
      windowStateChangedToTrue = false;
      //System.out.println("Window is "+(windowState?"available":"unavailable")+" just as before. Do not run.");
      return false;
    }
    else {
      //System.out.print("Window state changed to "+(windowState?"available":"unavailable")+".");
      //System.out.println(((lastWindowState = windowState)?" Run.":" Do not run."));
      return windowStateChangedToTrue = lastWindowState = windowState; 
    }*/
  }
  
  /*@Override
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = MSWindow.windowFromName(getWindowName(), false);
    }
    return window;
  }*/
  
}
