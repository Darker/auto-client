/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging;


import java.util.logging.Logger;
import java.util.HashMap;
/**
 *
 * @author Jakub
 */
public interface Logging {
  static HashMap loggers = new HashMap(4,1); 
  default void Log(Level lvl, String msg) {
    Logger log = Logger.getLogger(this.getClass().getName());
  }
  public Level getLogLevel();
}
