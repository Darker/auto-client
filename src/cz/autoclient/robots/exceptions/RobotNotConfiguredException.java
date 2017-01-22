/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.exceptions;

/**
 *
 * @author Jakub
 */
public class RobotNotConfiguredException extends IllegalStateException {

  /**
   * Creates a new instance of <code>RobotNotConfigured</code> without detail
   * message.
   */
  public RobotNotConfiguredException() {
    
  }

  /**
   * Constructs an instance of <code>RobotNotConfigured</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public RobotNotConfiguredException(String msg) {
    super(msg);
  }
}
