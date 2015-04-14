/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.cache.title;

import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.CachedWindow;

/**
 *
 * @author Jakub
 */
public class CachedWindowByTitle implements CachedWindow<String> {
  public final String cachedTitle;
  public final String cachedTitleLowercase;
  protected Window cachedWindow = null;
  public CachedWindowByTitle(Window cachedWindow) {
    //Invalid window is to be set to null
    if(cachedWindow!=null && !cachedWindow.isValid())
      cachedWindow=null;
    if(cachedWindow==null) {
      throw new IllegalArgumentException("Cannot cache window of null instance or invalid window. Such cache would be void.");
    }
    //Get title of non-null window
    cachedTitle = cachedWindow.getTitle();
    cachedTitleLowercase = cachedTitle.toLowerCase();
    this.cachedWindow = cachedWindow;
  }
  
  @Override
  public Window getWindow() {
    if(cachedWindow==null || !cachedWindow.isValid()) {
      windowGetterFullTitle();
    }
    return cachedWindow;
  }
  
  private void windowGetterFullTitle() {
    //System.out.println("Seeking window by exact title \""+cachedTitle+"\".");
    cachedWindow = Window.FindWindowByName(cachedTitle, true);
    //Invalid window is to be set to null
    if(cachedWindow!=null && !cachedWindow.isValid())
      cachedWindow=null;
  }

  @Override
  public void deleteCache() {
    cachedWindow = null;
  }

  @Override
  public Window getCache() {
    return cachedWindow;
  }

  @Override
  public boolean isMatch(String valueToCompare, boolean strict) {
    return strict?cachedTitle.equals(valueToCompare):cachedTitleLowercase.contains(valueToCompare.toLowerCase());
  }
  
}
