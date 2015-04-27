/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting.matching;

/**
 *
 * @author Jakub
 */
public class MatchableString implements Matchable {
  public final String value;
  public MatchableString(String value) {
    if(value==null)
      throw new IllegalArgumentException("Matchable value must not be null!");
    this.value = value;    
  }

  @Override
  public boolean matches(String input) {
    if(input==null)
      return false;
    return input.startsWith(value);
  }
}
