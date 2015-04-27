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
public class MatchableChar implements Matchable {
  public final char value;
  public MatchableChar(char value) {
    this.value = value;    
  }

  @Override
  public boolean matches(String input) {
    return input.charAt(0)==value;
  }
}
