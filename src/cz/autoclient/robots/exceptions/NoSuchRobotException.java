/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.exceptions;

/**
 * Thrown when reflection or other indirect constructor calling method fails to create robot.
 * @author Jakub
 */
public class NoSuchRobotException extends Exception {

  /**
   * Creates a new instance of <code>NoSuchRobotException</code> without detail
   * message.
   */
  public NoSuchRobotException() {
  }

  /**
   * Constructs an instance of <code>NoSuchRobotException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public NoSuchRobotException(String msg) {
    super(msg);
  }
}
