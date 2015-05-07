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
public class WindowAccessDeniedException extends APIException {

  /**
   * Creates a new instance of <code>WindowAccessDeniedException</code> without
   * detail message.
   */
  public WindowAccessDeniedException() {
  }

  /**
   * Constructs an instance of <code>WindowAccessDeniedException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public WindowAccessDeniedException(String msg) {
    super(msg);
  }
}
