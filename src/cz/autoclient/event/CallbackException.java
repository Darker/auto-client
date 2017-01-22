/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

/**
 *
 * @author Jakub
 */
public class CallbackException extends Exception {

  /**
   * Creates a new instance of <code>CallbackException</code> without detail
   * message.
   */
  public CallbackException() {
  }

  /**
   * Constructs an instance of <code>CallbackException</code> with the specified
   * detail message.
   *
   * @param msg the detail message.
   */
  public CallbackException(String msg) {
    super(msg);
  }
  public CallbackException(String msg, Exception cause) {
    super(msg, cause);
  }
}
