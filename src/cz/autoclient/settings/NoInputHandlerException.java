/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;

import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class NoInputHandlerException extends RuntimeException {
  public final JComponent input;
  /**
   * Creates a new instance of <code>NoInputHandlerException</code> 
   * only specifiyng which component has no handler.
   * @param problematicComponent
   */
  public NoInputHandlerException(JComponent problematicComponent) {
    this(null, problematicComponent);
  }

  /**
   * Constructs an instance of <code>NoInputHandlerException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   * @param problematicComponent input that has no handler
   */
  public NoInputHandlerException(String msg, JComponent problematicComponent) {
    super(msg);
    input = problematicComponent;
  }
}
