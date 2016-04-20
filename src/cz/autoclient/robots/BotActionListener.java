/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

/**
 *
 * @author Jakub
 */
public interface BotActionListener {
  /** Is called when the thread is started. If the thread is started while
   * already running, this method is not called.
   */
  public default void started() {};
  /** Called whenever the thread ends. 
   * @param e Exception that stopped the thread. Null if the thread stopped without error.
   */
  public default void terminated(Throwable e) {};
  /**
   * Called whenever the robot detects that it's canRun state changed. This 
   * means whenever canRun method is called and gave different result than last time,
   * not whenever the state actually changes.
   * @param state new state - true if the bot can run now
   */
  public default void enabledStateChanged(boolean state) {};
  /** Called whenever the thread ends normally.
   */
  public default void terminated() {terminated(null);};
  /** Called when the bot is automatically disabled due to it being errorneous.
   * @param error error that disabled to bot
   */
  public default void disabledByError(Throwable error) {};
  
}
