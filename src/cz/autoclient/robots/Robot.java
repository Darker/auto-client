/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.autoclick.MSWindow;
import cz.autoclient.autoclick.Window;



/**
 *
 * @author Jakub
 */
public abstract class Robot implements Runnable {
  protected Window window = null;
  
  protected Thread t;
  
  protected long lastRan = -1;
  protected long lastExit = -1;
  

  public void start() {
    if(isAlive())
      return;
    getWindow();
    if(!canRun()) {
      throw new IllegalStateException("Can't run! Call canRun() before start().");
    }
    lastRan = System.currentTimeMillis();
    init();
    t = new Thread(this);
    t.start();
  }
  
  @Override
  public final void run() {
    System.out.println("Robot thread "+t.getName()+" started.");
    try {
      go();
    }
    catch(Exception e) {
      System.out.println(e);
    }
    System.out.println("Robot thread "+t.getName()+" terminated.");
    lastExit = System.currentTimeMillis();
  }
  
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = MSWindow.windowFromName(getWindowName(), true);
    }
    return window;
  }
  
  public boolean isAlive() {
    return t!=null && t.isAlive(); 
    
  }
  
  public boolean canRun() {
    return getWindow()!=null; 
  }
  
  public int fromLastRun() {
    if(lastRan<0)
      return Integer.MAX_VALUE;
    return (int) (System.currentTimeMillis()-lastRan);
  }
  public int fromLastExit() {
    if(lastExit<0)
      return Integer.MAX_VALUE;
    return (int) (System.currentTimeMillis()-lastExit);
  }
  /**
   * Inner initialisation method. Can be overriden, but dummy by default.
   */
  protected void init() {}
  
  public abstract String getWindowName();
  
  protected abstract void go() throws InterruptedException;
}
