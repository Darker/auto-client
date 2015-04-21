/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.exceptions;

/**
 * This exception is thrown when robot is unable to run due to it's errorneous behaviour observed
 * before. This state can be removed by calling {@link cz.autoclient.robots.Robot#forgetErrors}.
 * @author Jakub
 */
public class RobotDisabledException extends IllegalStateException {
  public final Throwable lastError;
  /**
   * Creates a new instance of <code>RobotDisabledException</code> without
   * detail message.
   */
  public RobotDisabledException() {
    this(null, null);
  }

  /**
   * Constructs an instance of <code>RobotDisabledException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public RobotDisabledException(String msg) {
    this(msg, null);
  }
  /**
   * Constructs an instance of <code>RobotDisabledException</code> with the
   * specified detail message and defined last error
   *
   * @param msg the detail message.
   * @param lastError the error that disabled the bot
   */
  public RobotDisabledException(String msg, Throwable lastError) {
    super(msg);
    this.lastError = lastError;
  }
  
}
