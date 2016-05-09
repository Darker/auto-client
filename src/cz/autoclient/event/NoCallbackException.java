/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

/**
 * Exception thrown when invalid arguments are passed to callback. That does not necesarily
 * mean something's wrong, but it's good to know why wasn't callback called.
 * @author Jakub
 */
public class NoCallbackException extends CallbackException {

  /**
   * Creates a new instance of <code>NoCallbackException</code> without detail
   * message.
   */
  public NoCallbackException() {
  }
  public NoCallbackException(Object[] arguments) {
    this("Invalid arguments: "+EventEmitter.classesToStrings(EventEmitter.objectsToClasses(arguments)));
  }
  /**
   * Constructs an instance of <code>NoCallbackException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public NoCallbackException(String msg) {
    super(msg);
  }

  public NoCallbackException(String msg, Exception cause) {
    super(msg, cause);
  }
}
