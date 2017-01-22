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
public class InvalidWindow extends Exception {

  /**
   * Creates a new instance of <code>InvalidWindow</code> without detail
   * message.
   */
  public InvalidWindow() {
  }

  /**
   * Constructs an instance of <code>InvalidWindow</code> with the specified
   * detail message.
   *
   * @param msg the detail message.
   */
  public InvalidWindow(String msg) {
    super(msg);
  }
}
