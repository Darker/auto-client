/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.cache;

import cz.autoclient.autoclick.windows.Window;

/**
 *
 * @author Jakub
 * @param <T> The type by which these windows will be cached
 */
public interface WindowCache<T> {
  public Window getWindow(T searchCriteria);
  public Window getWindowNocache(T searchCriteria);
  /** Retrieve the cache entry without looking for window. Returns null if no cache entry exists.
   * 
   * @param searchCriteria search key as allways
   * @return the cache node or CachedWindow.INVALID_CACHED_WINDOW if nothing is cached. MUST NOT RETURN NULL!
   */
  public CachedWindow getCache(T searchCriteria);
}
