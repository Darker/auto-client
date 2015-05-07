/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.exceptions;

/**
 *
 * @author Jakub
 */
public class APIException extends RuntimeException {

  /**
   * Creates a new instance of <code>APIError</code> without detail message.
   */
  public APIException() {
  }

  /**
   * Constructs an instance of <code>APIError</code> with the specified detail
   * message.
   *
   * @param msg the detail message.
   */
  public APIException(String msg) {
    super(msg);
  }
}
