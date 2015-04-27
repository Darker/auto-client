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
public class CommandException extends Exception {

  /**
   * Creates a new instance of <code>CommandException</code> without detail
   * message.
   */
  public CommandException() {
  }

  /**
   * Constructs an instance of <code>CommandException</code> with the specified
   * detail message.
   *
   * @param msg the detail message.
   */
  public CommandException(String msg) {
    super(msg);
  }
}
