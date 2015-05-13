/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.cache.title;

import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.CachedWindow;
import cz.autoclient.autoclick.windows.cache.WindowCacheString;
import static cz.autoclient.autoclick.windows.WindowValidator.TitlePatternWindowValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public class CacheByTitle implements WindowCacheString {
  public static final CacheByTitle initalInst = new CacheByTitle();
  
  
  private final ArrayList<CachedWindowByTitle> cache = new ArrayList<>();
  @Override
  public Window getWindow(String searchCriteria) {
    //Find cache entry
    for(CachedWindowByTitle w:cache) {
      if(w.isMatch(searchCriteria, false)) {
        return w.getWindow();
      }
    }
    //Find new window
    Window w = getWindowNocache(searchCriteria);
    try {
      cache.add(new CachedWindowByTitle(w));
    }
    catch(IllegalArgumentException e) {
      return null; 
    }
    return w;
  }
  
  @Override
  public Window getWindow(Pattern regex) {
    //Find cache entry
    for(CachedWindowByTitle w:cache) {
      if(w.isMatch(regex)) {
        return w.getWindow();
      }
    }
    //Find new window
    Window w = getWindowNocache(regex);
    try {
      cache.add(new CachedWindowByTitle(w));
    }
    catch(IllegalArgumentException e) {
      return null; 
    }
    return w;
  }
 

  @Override
  public Window getWindowNocache(String searchCriteria) {
    //System.out.println("Seeking window by non strict title \""+searchCriteria+"\".");
    return Window.FindWindowByName(searchCriteria, false);
  }
  @Override
  public Window getWindowNocache(Pattern regex) {
    List<Window> all = Window.FindWindows(new TitlePatternWindowValidator(regex));
    return !all.isEmpty()?all.get(0):null;
  }
  
  @Override
  public CachedWindow getCache(String searchCriteria) {
    //Find cache entry
    for(CachedWindowByTitle w:cache) {
      if(w.isMatch(searchCriteria, false)) {
        return w;
      }
    }
    return CachedWindow.INVALID_CACHED_WINDOW;
  }


  
}
