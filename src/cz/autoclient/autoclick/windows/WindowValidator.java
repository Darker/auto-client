/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows;

import cz.autoclient.autoclick.windows.Window;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public interface WindowValidator {
  public boolean isValid(Window w);
  
  public static class TitlePatternWindowValidator implements WindowValidator {
    private final Pattern pattern;
    public TitlePatternWindowValidator(Pattern pattern) {
      this.pattern = pattern;
    }
    @Override
    public boolean isValid(Window w) {
      return pattern.matcher(w.getTitle()).matches();
    }
  }
}
