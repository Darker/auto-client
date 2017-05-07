/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.main_automation.WindowTools;
import cz.autoclient.GUI.Gui;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.WindowValidator;
import cz.autoclient.robots.exceptions.RobotNotConfiguredException;
import cz.autoclient.robots.helpers.ValueChangeToWatcher;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.EncryptedSetting;
import cz.autoclient.settings.secure.InvalidPasswordException;
import cz.autoclient.settings.secure.PasswordFailedException;
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
      try {
         settings.getEncryptor().init();
      }
      catch(PasswordFailedException e) {
        Gui.inst.dialogErrorAsync("Cannot generate key to decrypt your saved password: "+e.getMessage());
        disableDueToException(e);
        return;
      }
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
    //System.out.println("Start waiting for login screen at "+window.getTitle());
    PVPAppeared.resetChanged();
    try {
      //Increment as the bot is running, terminate bot at certain value
      int duration = 0;
      while(!Thread.interrupted() && duration++<20) {
        BufferedImage img = window.screenshot();
        PixelOffset[] points = new PixelOffset[] {
          PixelOffset.Login_ButtonDisabled,
          PixelOffset.Login_PasswordField,
          PixelOffset.Login_UsernameField
        };
        //BufferedImage debugClone = DebugDrawing.cloneImage(img);
        //WindowTools.drawCheckPoint(debugClone, points);
        //DebugDrawing.displayImage(debugClone, "Hello", true);
        
        if(WindowTools.checkPoint(img, points)>=3) {
          Rect size = window.getRect();
          //System.out.println("Starting debug loop."+size);
//          java.awt.Robot robot = null;
//          try {
//            robot = new java.awt.Robot();
//          } catch (AWTException ex) {
//            System.out.println("Cannot create robot!");
//            throw new IllegalStateException("Robot cannot be initialized.");
//          }
//
//          while(true) {
//              /*window.keyDown(0x45);
//              Thread.sleep(200);
//              window.keyUp(0x45);
//              Thread.sleep(200);
//              window.slowClick(PixelOffset.Login_UsernameField.toRect(size), 30);*/
//              //robot.keyPress(KeyEvent.VK_F);
//              System.out.println("Window is "+(window.isForeground()?"focused":"not focused")+".");
//              //robot.mouseMove(size.left()+PixelOffset.Login_UsernameField.toRect(size).left,
//              //    size.top()+PixelOffset.Login_UsernameField.toRect(size).top);
//              Thread.sleep(800);
//              if(false)
//                  break;
//          }
//          Thread.sleep(4000);
//          window.mouseOver(0,0);
//          for(double i=0.0; i<1; i+=0.01) {
//            window.mouseOver((int)(i*size.width),(int)(i*size.height));
//            Thread.sleep(40);
//          }
//          window.mouseOver(size.width,size.height);
//          Thread.sleep(4000);
          //window.mouseOver(0,0);
          WindowTools.say(window,
                         (String)settings.getEncrypted(Setnames.REMEMBER_PASSWORD.name),
                         PixelOffset.Login_PasswordField.toRect(size));
          //Click ok
          Thread.sleep(400);
          window.click(PixelOffset.Login_ButtonDisabled.toRect(size));
          break;
        }
        //System.out.println("  - Going to sleep.");
        Thread.sleep(2000);
      }
    }
    catch(InvalidPasswordException e) {
      brokenPassword(e);
      return;      
    }
    catch(InterruptedException e) {
      System.out.println("[AUTO-LOGIN] Killed by interrupt.");
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
  protected void init() {}
  /**
   * The bot will also claim it can run if the encryption framework needs to be initialized.
   * @return true if encryption initialization is needed or window is available and was not available before
   */
  @Override
  public boolean canRunEx() {
    if(!initialized) {
      initializing = true;
      return true;
    }

    return PVPAppeared.checkValueChangedSometime(super.canRunEx());
  }

  @Override
  public WindowValidator getWindowValidator() {
    return new WindowValidator.ProcessNameValidator(ConstData.process_name);
  }
}
