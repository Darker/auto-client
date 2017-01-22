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
  public InvalidPasswordException() {
  }
  public InvalidPasswordException(String msg) {
    super(msg);
  }
  public InvalidPasswordException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
