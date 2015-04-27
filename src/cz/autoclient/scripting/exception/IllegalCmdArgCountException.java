/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting.exception;

/**
 *
 * @author Jakub
 */
public class IllegalCmdArgCountException extends CommandException {
  final int expectedCount;
  /**
   * Creates a new instance of <code>IllegalCmdArgCountException</code> without
   * detail message.
   * @param expectedCount expected count of arguments
   */
  public IllegalCmdArgCountException(int expectedCount) {
    this.expectedCount = expectedCount;
  }

  /**
   * Constructs an instance of <code>IllegalCmdArgCountException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   * @param expectedCount expected count of arguments
   */
  public IllegalCmdArgCountException(String msg, int expectedCount) {
    super(msg);
    this.expectedCount = expectedCount;
  }
}
