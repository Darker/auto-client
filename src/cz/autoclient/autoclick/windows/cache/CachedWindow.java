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
 * @param <T> Type by which this window is searched and cached.
 */
public interface CachedWindow<T> {
  /** Find or retrieve window from cache.
   * @return window or null if nothing can be found.
   */
  public Window getWindow();
  /** Return currently cached value regardless of validity (can return null).
   * @return window or null
   */
  public Window getCache();
  /** Delete any cached values.
   */
  public void deleteCache();
  /** Check if given value would be match to find window represented by this class.
   * @param valueToCompare find the window using this value
   * @param strict if the comparison is to be strict, what this means is up to the implementation.
   * @return 
   */
  public boolean isMatch(T valueToCompare, boolean strict);
  
  public default boolean hasValidWindow() {
    Window w = getCache();
    return w!=null && w.isValid();
  }
  
  public static final CachedWindow INVALID_CACHED_WINDOW = new CachedWindow() {
    @Override
    public Window getWindow() {return null;}
    @Override
    public Window getCache() {return null;}

    @Override
    public void deleteCache() {}
    @Override
    public boolean isMatch(Object valueToCompare, boolean strict) {return false;}
    
  };
}
