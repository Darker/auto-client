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
public class APIError extends RuntimeException {

  /**
   * Creates a new instance of <code>APIError</code> without detail message.
   */
  public APIError() {
  }

  /**
   * Constructs an instance of <code>APIError</code> with the specified detail
   * message.
   *
   * @param msg the detail message.
   */
  public APIError(String msg) {
    super(msg);
  }
}
