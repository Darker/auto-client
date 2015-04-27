/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting.matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public class MatchableRegex implements Matchable {
  private Pattern value;
  private String uncompiled_regex = null;
  public MatchableRegex(String regex) {
    if(regex==null)
      throw new IllegalArgumentException("Matchable value must not be null!");
    this.uncompiled_regex = regex;
  }
  public MatchableRegex(Pattern regex) {
    if(regex==null)
      throw new IllegalArgumentException("Matchable value must not be null!");
    this.value = regex;
  }
  private void compile() {
    this.value = Pattern.compile(uncompiled_regex);
  }
  @Override
  public boolean matches(String input) {
    if(value==null) {
      compile();
    }
    Matcher m = value.matcher(input);
    return m.matches();
  }
}
