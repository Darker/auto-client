/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.WindowTools;
import cz.autoclient.GUI.Gui;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.robots.exceptions.RobotNotConfiguredException;
import cz.autoclient.robots.helpers.ValueChangeToWatcher;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.EncryptedSetting;
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
    return ConstData.window_title_part;
  }
  private boolean initializing = false;
  
  private boolean initialized = false;
  /** 
   * This bot only starts when PVP.net window suddenly appears. As such, it remembers last state of the window.
   */
  private ValueChangeToWatcher<Boolean> PVPAppeared = new ValueChangeToWatcher<>(false, true);
  /**
   *
   * @throws InterruptedException when the htread is iterrupted externally
   */
  @Override
  protected void go() throws InterruptedException, APIException {
    System.out.println("Auto login thread started.");
    if(initializing) {
      System.out.println("Initializing phase of auto login.");
      initializing = false;
      settings.getEncryptor().init();
      if(!settings.exists(Setnames.REMEMBER_PASSWORD.name, EncryptedSetting.class)) {
        Gui.inst.dialogErrorAsync("Setup a password before enabling this function.");
        disableDueToException(new RobotNotConfiguredException("Login password is not set."));
        return;
      }
      try {
        settings.getEncrypted(Setnames.REMEMBER_PASSWORD.name);
        initialized = true;
      }
      catch(InvalidPasswordException e) {
        brokenPassword(e);
      }
      if(!canRun()) {
        System.out.println("Initializing done. Nothing more to do, so terminating.");
        return;
      }
    }
    System.out.println("Start waiting for login screen at "+window.getTitle());
    PVPAppeared.resetChanged();
    try {
      //Increment as the bot is running, terminate bot at certain value
      int duration = 0;
      while(!Thread.interrupted() && duration++<15) {
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
      brokenPassword(e);
      return;      
    }
  }
  /**
   * Called internally when password cannot be decrypted.
   */
  private void brokenPassword(InvalidPasswordException e) throws InvalidPasswordException {
    settings.setSetting(Setnames.REMEMBER_PASSWORD.name, "");
    //This will disable the bot
    disableDueToException(e);
    //This will appear assynchronously and wait for the user to close it
    System.out.println("Your saved password could not be decrypted. It will be deleted now.");
    new Exception().printStackTrace();
    Gui.inst.dialogErrorAsync("Your saved password could not be decrypted. It will be deleted now.");
    //The execution stops here
    throw e;
  }
  
  @Override
  public void reset() {
    super.reset();
    initialized = initializing = false;
  }
  
  @Override
  protected void init() {
   
  }
  /**
   * The bot will also claim it can run if the encryption framework needs to be initialized.
   * @return true if encryption initialization is needed or window is available and was not available before
   */
  @Override
  public boolean canRunEx() {
    //If the bot is initializing it cannot do anything else
    //if(initializing)
    //  return false;
    
    if(!initialized) {
      //System.out.println("Initializing - run once.");
      initializing = true;
      return true;
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
