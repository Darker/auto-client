/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import cz.autoclient.scripting.matching.Matchable;
import cz.autoclient.scripting.matching.MatchableChar;
import cz.autoclient.scripting.matching.MatchableRegex;
import cz.autoclient.scripting.matching.MatchableString;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public enum ScriptSymbol implements Matchable {
  SCRIPT_START("S>"),
  COMMAND_SEPARATOR(';'),
  ESCAPE('\\'),
  LETTER("[a-zA-Z]", (Pattern)null),
  PARAMETER_SEPARATOR(','),
  NUMBER("[0-9]", (Pattern)null),
  SPACE(' ', false),
  END('\0', false),
  UNKNOWN_SYMBOL("[^;\\\\a-zA-Z0-9 ,]", (Pattern)null)
  ;
  public final Matchable comparator;
  public final boolean escapable;
  public final int length;
  
  ScriptSymbol(char match, boolean escapable) {
    comparator = new MatchableChar(match);
    this.escapable = escapable;
    length = 1;
  }
  ScriptSymbol(char match) {
    this(match, true);
  }
  ScriptSymbol(String match, boolean escapable) {
    comparator = new MatchableString(match);
    this.escapable = escapable;
    length = match.length();
  }
  ScriptSymbol(String match) {
    this(match, true);
  }
  ScriptSymbol(String match, Pattern regex) {
    length = 1;
    comparator = new MatchableRegex("^"+match+".*");
    escapable = false;
  }
  ScriptSymbol(Pattern regmatch) {
    comparator = new MatchableRegex(regmatch);
    escapable = false;
    length = 1;
  }
  
  public String prettyPrint(String data, int offset) {
    return prettyPrint(data.substring(offset, offset+length));
  }
  public String prettyPrint(String data) {
    return name()+" ('"+data+"')";
  }
  
  public boolean is(String data, int offset) {
    return matches(data.substring(offset));
  }
  public boolean is(String data) {
    return matches(data);
  }
  public boolean is(char ch) {
    return matches(ch+"");
  }

  @Override
  public boolean matches(String input) {
    return comparator.matches(input);
  }
  public static ScriptSymbol identify(String data, int offset) {
    for(ScriptSymbol s:values()) {
      if(s.is(data, offset))
        return s;
    }
    return ScriptSymbol.UNKNOWN_SYMBOL;
  }
  public static ScriptSymbol identify(String data) {
    for(ScriptSymbol s:values()) {
      if(s.is(data))
        return s;
    }
    return ScriptSymbol.UNKNOWN_SYMBOL;
  }
}
