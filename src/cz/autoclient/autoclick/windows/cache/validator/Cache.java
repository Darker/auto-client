/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.cache.validator;

import com.sun.jna.platform.win32.WinDef;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.CachedWindow;
import cz.autoclient.autoclick.windows.cache.WindowCache;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class Cache implements WindowCache {
  protected ArrayList<WinDef.HWND> cachedWindows;
  @Override
  public Window getWindow(Object searchCriteria) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Window getWindowNocache(Object searchCriteria) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public CachedWindow getCache(Object searchCriteria) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
