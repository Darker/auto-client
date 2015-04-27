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
public class UnknownCommandException extends CommandException {
  
  /**
   * Creates a new instance of <code>UnknownCommandException</code> without
   * detail message.
   */
  public UnknownCommandException() {
  }

  /**
   * Constructs an instance of <code>UnknownCommandException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public UnknownCommandException(String msg) {
    super(msg);
  }
}
