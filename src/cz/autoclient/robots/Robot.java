/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.autoclick.ms_windows.MSWindow;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;



/**
 *
 * @author Jakub
 */
public abstract class Robot implements Runnable {
  protected Window window = null;
  
  protected Thread t;

  protected BotActionListener listener = null;
  public BotActionListener getListener() {
    return listener;
  }
  public void setListener(BotActionListener listener) {
    this.listener = listener;
  }
  
  protected long lastRan = -1;
  protected long lastExit = -1;
  
  protected boolean lastCanRun = false;

  public void start() {
    if(isRunning()) {
      //System.out.println("Robot can't start, already running!");
      return;
    }
    
    getWindow();
    if(!canRun()) {
      throw new IllegalStateException("Can't run! Call canRun() before start().");
    }
    lastRan = System.currentTimeMillis();
    init();
    t = new Thread(this);
    t.start();
  }
  
  public void stop() {
    if(t.isAlive()) {
      t.interrupt(); 
    }
  }
  
  @Override
  public final void run() {
    System.out.println("Robot thread "+t.getName()+" started.");
    if(listener!=null) 
      listener.started();
    try {
      go();
      if(listener!=null) 
        listener.terminated();
      System.out.println("Robot thread "+t.getName()+" terminated.");
    }
    catch(Exception e) {
      //System.out.println(e);
      if(listener!=null) 
        listener.terminated(e);
      System.out.println("Robot thread "+t.getName()+" terminated with error:\n     "+e);
    }
    
    lastExit = System.currentTimeMillis();
  }
  
  public Window getWindow() {
    if(window==null || !window.isValid()) {
      window = CacheByTitle.initalInst.getWindow(getWindowName());//MSWindow.windowFromName(getWindowName(), true);
    }
    return window;
  }
  
  public boolean isRunning() {
    return t!=null && t.isAlive(); 
  }
  
  /**
   * Indicate whether this thread can run. Override canRunEx instead of this method.
   * @return
   */
  public final boolean canRun() {
    if(listener==null)
      return lastCanRun = canRunEx();
    else {
      boolean can = canRunEx();
      if(can!=lastCanRun)
        listener.enabledStateChanged(can);
      return lastCanRun = can;
    }
  }
  protected boolean canRunEx() {
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
