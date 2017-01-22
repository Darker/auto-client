/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

/**
 *
 * @author Jakub
 */
public class CantCreateNotification extends IllegalArgumentException {

  /**
   * Creates a new instance of <code>CantCreateException</code> without detail
   * message.
   */
  public CantCreateNotification() {
  }

  /**
   * Constructs an instance of <code>CantCreateException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public CantCreateNotification(String msg) {
    super(msg);
  }
}
