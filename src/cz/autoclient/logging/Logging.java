/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.logging;


import java.util.logging.Logger;
/**
 *
 * @author Jakub
 */
public interface Logging {
  default void Log(Level lvl, String msg) {
    Logger log = Logger.getLogger(this.getClass().getName());
  }
  public Level getLogLevel();
}
