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
public class TooManyArgsException extends IllegalCmdArgCountException {
  /**
   * Creates a new instance of <code>IllegalCmdArgCountException</code> without
   * detail message.
   * @param expectedCount expected count of arguments
   */
  public TooManyArgsException(int expectedCount) {
    super(expectedCount);
  }

  /**
   * Constructs an instance of <code>IllegalCmdArgCountException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   * @param expectedCount expected count of arguments
   */
  public TooManyArgsException(String msg, int expectedCount) {
    super(msg, expectedCount);
  }
}
