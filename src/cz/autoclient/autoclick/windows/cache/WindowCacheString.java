/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.cache;

import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowValidator;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public interface WindowCacheString extends WindowCache<String> {
  /**
   * Fetch window that matches given regular expression. If no such window
   * is cached, system will be scanned for existing windows.
   * @param regex
   * @return 
   */
  Window getWindow(Pattern regex);
  /**
   * Fetch window that matches given regular expression without reading cache.
   * @param regex
   * @return 
   */
  default Window getWindowNocache(Pattern regex) {
    return null;
  }
  

}
