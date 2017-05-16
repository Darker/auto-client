/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.scripting.SleepAction;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public abstract class AutomatInterface extends Thread {

  public AutomatInterface(String name) {
    super(name);
  }
  protected final Object macroQueueWaitMutex = new Object();
  protected final ArrayList<AutomatCallable> macros = new ArrayList<>();
  abstract void callText(final String message) throws InterruptedException;

  /**
   * Call text to chat in standard lobby. Supports scripts.
   *
   * @param message text to call or script to process
   * @param actions actions to perform when sleeping if message is script
   * @throws InterruptedException
   */
  public abstract void callText(final String message, SleepAction[] actions) throws InterruptedException;

  public abstract void simulateAccepted();
  
  public abstract Window getWindow();
  
  protected void dbgmsg(final String data) {
    System.out.println("[MAIN BOT] " + data);
  }

  protected void errmsg(final String data) {
    System.err.println("[MAIN BOT] Error: " + data);
  }
  // runs passed macro as soon as possible
  // this macro can do anything
  // but should work with the automat api
  // this method is thread safe and returns immediatelly
  // the macro will be executed when the thread has time for it
  public void runMacro(AutomatCallable macro) {
    synchronized(macros) {
      macros.add(macro);
    }
    synchronized(macroQueueWaitMutex) {
      macroQueueWaitMutex.notifyAll(); 
    }
  }
  // handles all macros and returns
  // will wait for macros for up to @timeout seconds
  protected void handleMacros(int timeout) throws InterruptedException {
    synchronized(macros) {
      if(macros.size()>0) {
        handleMacrosNow();
        return;
      }
    }
    synchronized(macroQueueWaitMutex) {
      macroQueueWaitMutex.wait(timeout);
    }
    synchronized(macros) {
      if(macros.size()>0) {
        handleMacrosNow();
        return;
      }
    }
  }
  // assumes you already locked macros with a mutex!
  protected void handleMacrosNow() {
    for(AutomatCallable macro: macros) {
      try {
        macro.setMyAutomat(this);
        macro.call();
      } catch (Exception ex) {
        errmsg("Macro failed: "+ex.getMessage());
        ex.printStackTrace();
      }
    }
    macros.clear();
  }
  /**
   * HELPERS
   */
  public void Enter() {
    this.getWindow().keyDown(KeyEvent.VK_ENTER);
    this.getWindow().keyUp(KeyEvent.VK_ENTER);
  }
  public void click(PixelOffset pos) {
    try {
      Rect rect = getWindow().getRect();
      getWindow().click((int) (rect.width * pos.x), (int) (rect.height * pos.y));
    } catch (APIException e) {
      errmsg("Can't click because no window is available for clicking :(");
    }
  }

  public void click(ComparablePixel pos) {
    try {
      Rect rect = getWindow().getRect();
      getWindow().click((int) (rect.width * pos.getX()), (int) (rect.height * pos.getY()));
    } catch (APIException e) {
      errmsg("Can't click because no window is available for clicking :(");
    }
  }

  /**
   * Clicks at the top left corner of the rectangle. Use Rect.middle() to click
   * in the middle.
   *
   * @param pos rectangle to click on.
   */
  public void click(Rect pos) {
    getWindow().click((int) (pos.left), (int) (pos.top));
  }
  
  public void slowClick(ComparablePixel pos, int delay) throws InterruptedException {
    try {
      Rect rect = getWindow().getRect();
      getWindow().slowClick((int) (rect.width * pos.getX()), (int) (rect.height * pos.getY()), delay);
    } catch (APIException e) {
      errmsg("Can't click because no window is available for clicking :(");
    }
  }
}
