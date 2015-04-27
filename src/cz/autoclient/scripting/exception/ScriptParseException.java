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
public class ScriptParseException extends Exception {
  /**
   * Creates a new instance of <code>ScriptParseException</code> without detail
   * message.
   */
  public ScriptParseException() {
  }

  /**
   * Constructs an instance of <code>ScriptParseException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public ScriptParseException(String msg) {
    super(msg);
  }
}
