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
public class IllegalCmdArgumentException extends CommandException {

  /**
   * Creates a new instance of <code>IllegalCommandArgumentException</code>
   * without detail message.
   */
  public IllegalCmdArgumentException() {
  }

  /**
   * Constructs an instance of <code>IllegalCommandArgumentException</code> with
   * the specified detail message.
   *
   * @param msg the detail message.
   */
  public IllegalCmdArgumentException(String msg) {
    super(msg);
  }
}
