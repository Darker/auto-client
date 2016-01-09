/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;
import cz.autoclient.robots.exceptions.RobotDisabledException;
import cz.autoclient.robots.helpers.DummyLogger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;




/**
 * Abstract robot class. This class excepts that you're about to create a robot
 * that automates a window, however this is not necessary.
 * @author Jakub
 */
public abstract class Robot implements Runnable {
  
  private Logger logger = null;
  
  /**
   * Retrieves logger  for this class.
   * @return current logger. Creates new logger if no logger is available.
   */
  public Logger getLogger() {
    return (logger!=null ?
              logger
               :
              (DummyLogger.inst)
           );
  }
  public void setLogger(Logger log) {
    logger = log; 
  }
  /**
   * Shortcut to {@link Robot#getLogger()}.
   * @return see {@link Robot#getLogger()}.
   */
  public Logger glg() {
    return getLogger(); 
  }
  
  
  protected Window window = null;
  
  protected Thread t;
  
  /** Enum for recognition of bot execution phases when identifying error location **/
  public enum ExecutionPhase {CAN_RUN, RUN, GET_WINDOW}
  
  
  /**
   *  Phase where the last error occured.
   */
  private ExecutionPhase errorPhase;
  /**
   * The last error that occured.
   */
  private Throwable lastError;
  /**
   * Determines whether this bot should be disabled due to errors.
   */
  protected boolean errorDisabled = false;
  /**
   * Sets whether the bot is disabled due to error or not. If error has occured during 
   * getWindow, the bot will be disabled immediatelly. If the error occured during canRun() or run()
   * the bot will only be disabled if the error is repetitive.
   * @param error error that occured
   * @param phase phase of execution in which the error occured
   * @return true if this error is not significant enough to cancel this bot
   */
  protected boolean continueOnError(Throwable error, ExecutionPhase phase) {
    glg().log(Level.ERROR, error.toString());
    //System.err.println("Error "+error+" caught in robot "+this.getClass().getName());
    //System.err.println("Last: "+lastError+((/*error.equals(lastError)*/errorsSame(error, lastError)?" which is the same as last":" which is defferent than last")));
    if(phase == ExecutionPhase.GET_WINDOW || (errorsSame(error, lastError) && phase==errorPhase)) {
      //System.err.println("Disabling robot "+this.getClass().getName()+" too many errors.");
      //Remember last
      setLastError(error, phase);
      //Set errorDisabled before calling the callback, so that calls to isErrorDisabled will return true already
      errorDisabled = true;
      //If robot state listener is listening, inform it about this event
      if(listener!=null)
        listener.disabledByError(error);
      //Disable the robot - on attempt to run, it will throw RobotDisabledException
      return !(errorDisabled);
    }
    //Remmeber last
    setLastError(error, phase);
    //The robot can remain running, but next same error will turn it down
    return true;
  }
  protected void disableDueToException(Exception error) {
    setLastError(error, ExecutionPhase.RUN);
    //Set errorDisabled before calling the callback, so that calls to isErrorDisabled will return true already
    errorDisabled = true;
    //If robot state listener is listening, inform it about this event
    if(listener!=null)
      listener.disabledByError(error);
  }
  
  public static boolean errorsSame(Throwable A, Throwable B) {
    //Same instance (or two nulls) is always equal
    if(A==B)
      return true;
    //if one of them is null and other is not, they are not equal
    if(B!=A && (A==null || B==null))
      return false;
    //Check messages
    if(!A.getMessage().equals(B.getMessage())) 
      return false;
    
    if(!A.getClass().equals(B.getClass()))
      return false;
    if(!A.getStackTrace()[0].equals(B.getStackTrace()[0]))
      return false;
    return true;
  }
  
  protected void setLastError(Throwable error, ExecutionPhase phase) {
    lastError = error;
    errorPhase = phase;
  }

  public ExecutionPhase getErrorPhase() {
    synchronized (errorPhase) {
      return errorPhase;
    }
  }

  public Throwable getLastError() {
    synchronized (lastError) {
      return lastError;
    }
  }
  
  
  public void forgetErrors() {
    lastError = null;
    errorPhase = null;
    errorDisabled = false;
    //System.out.println("forgetErrors called!");
    //new Exception().printStackTrace();
  }
  
  public boolean isErrorDisabled() {
    return errorDisabled; 
  }
  
  protected BotActionListener listener = null;
  public BotActionListener getListener() {
    return listener;
  }
  public void setListener(BotActionListener listener) {
    this.listener = listener;
  }
  /**
   * Resets internal data of the robot, forgeting
   * any statistics or configuration. Good when it's supposed to start anew.
   */
  public void reset() {
    forgetErrors();
    window = null;
  }
  
  protected long lastRan = -1;
  protected long lastExit = -1;
  
  protected boolean lastCanRun = false;

  public final void start() {
    if(isRunning()) {
      throw new IllegalStateException("Robot can't start, already running!");
    }
    try {
      getWindow();
    }
    catch(Throwable e) {
      if(!continueOnError(e, ExecutionPhase.GET_WINDOW))
        throw new RobotDisabledException("Robot disabled by error in getWindow.", e);
    }
    if(!canRun()) {
      throw new IllegalStateException("Can't run! Call canRun() before start().");
    }
    lastRan = System.currentTimeMillis();
    
    init();
    t = new Thread(this, "Robot "+this.getClass().getName());
    t.setDaemon(true);
    t.start();
  }
  
  public void stop() {
    if(t!=null && t.isAlive()) {
      t.interrupt(); 
      glg().debug("Robot stopped forcefully.");
    }
  }
  
  @Override
  public final void run() {
    glg().debug("Robot thread {0} started.", t.getName());//System.out.println("Robot thread "+t.getName()+" started.");
    if(listener!=null)
      listener.started();
    try {
      go();
      if(listener!=null) 
        listener.terminated();
      glg().debug("Robot thread {0} terminated.", t.getName());
      //System.out.println("Robot thread "+t.getName()+" terminated.");
    }
    catch(Throwable e) {
      //System.out.println(e);
      if(listener!=null)
        listener.terminated(e);
      glg().debug("Robot thread {0} terminated with error: "+e.getMessage(), t.getName());
      continueOnError(e, ExecutionPhase.RUN);
      //System.out.println("Robot thread "+t.getName()+" terminated with error:\n     "+e);
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
   * Indicate whether this thread can run. Override {@link #canRunEx()} instead of this method. This method catches any errors
   * that occur within canRunEx and also - 1 year later I don't really know how to finish this sentence
   * @return
   */
  public final boolean canRun() {
    try {
      if(errorDisabled)
        return false;
      if(listener==null)
        return lastCanRun = canRunEx();
      else {
        boolean can = canRunEx();
        if(can!=lastCanRun) {
          glg().debug(can?"Robot can run now.":"Robot can't run anymore.");
          listener.enabledStateChanged(can);
        }
        return lastCanRun = can;
      }
    }
    catch(Throwable t) {
      //Return value is not used here. Prompting for canRun doesn't account for attempt to run bot
      //so no exception is thrown down the line
      continueOnError(t, ExecutionPhase.CAN_RUN);
      return false;
    }
  }
  /** Overridable detection whether the robot can run or not.
   * 
   * @return true if the robot shold be launched
   */
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
   * Called each time before robot run() thread is started.
   * Inner initialisation method. Can be overriden, but dummy by default. 
   * 
   */
  protected void init() {}
  
  public abstract String getWindowName();
  
  protected abstract void go() throws InterruptedException;
}
