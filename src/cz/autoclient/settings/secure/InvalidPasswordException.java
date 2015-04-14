/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;

/**
 *
 * @author Jakub
 */
public class InvalidPasswordException extends IllegalStateException {

  /**
   * Creates a new instance of <code>InvalidPasswordException</code> without
   * detail message.
   */
  public InvalidPasswordException() {
  }

  /**
   * Constructs an instance of <code>InvalidPasswordException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public InvalidPasswordException(String msg) {
    super(msg);
  }
}
