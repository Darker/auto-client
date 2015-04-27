/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class ScriptObject {
  public enum objs {
    START(
          ScriptSymbol.SCRIPT_START,
          (ScriptSymbol)null,
          ScriptSymbol.LETTER
    ),
    COMMAND(
          new ScriptSymbol[] {ScriptSymbol.COMMAND_SEPARATOR, ScriptSymbol.SCRIPT_START},
          ScriptSymbol.LETTER,
          new ScriptSymbol[] {ScriptSymbol.COMMAND_SEPARATOR, ScriptSymbol.PARAMETER_SEPARATOR}
    ),
    COMMAND_PARAMETER(
          new ScriptSymbol[] {ScriptSymbol.LETTER, ScriptSymbol.ESCAPE, ScriptSymbol.NUMBER, ScriptSymbol.SPACE},
          new ScriptSymbol[] {ScriptSymbol.COMMAND_SEPARATOR, ScriptSymbol.PARAMETER_SEPARATOR}
    );
    public final ScriptSymbol[] starts;
    public final ScriptSymbol[] contains;
    public final ScriptSymbol[] ends;
    objs(ScriptSymbol starts, ScriptSymbol contains, ScriptSymbol ends) {
      this(new ScriptSymbol[] {starts}, new ScriptSymbol[] {contains}, new ScriptSymbol[] {ends});
    }
    objs(ScriptSymbol starts[], ScriptSymbol contains, ScriptSymbol ends[]) {
      this(starts, new ScriptSymbol[] {contains}, ends);
    }
    objs(ScriptSymbol starts, ScriptSymbol contains, ScriptSymbol ends[]) {
      this(new ScriptSymbol[] {starts}, new ScriptSymbol[] {contains}, ends);
    }
    objs(ScriptSymbol starts[], ScriptSymbol contains, ScriptSymbol ends) {
      this(starts, new ScriptSymbol[] {contains}, new ScriptSymbol[] {ends});
    }
    objs(ScriptSymbol starts[], ScriptSymbol contains[], ScriptSymbol ends) {
      this(starts, contains, new ScriptSymbol[] {ends});
    }
    objs(ScriptSymbol starts, ScriptSymbol contains[], ScriptSymbol ends) {
      this(new ScriptSymbol[] {starts}, contains, new ScriptSymbol[] {ends});
    }
    objs(ScriptSymbol[] starts, ScriptSymbol[] contains, ScriptSymbol[] ends) {
      this.starts = starts;
      this.contains = contains;
      this.ends = ends;
    }
    objs(ScriptSymbol[] starts, ScriptSymbol[] ends) {
      this.starts = starts;
      this.contains = starts;
      this.ends = ends;
    }
    public ScriptObject get() {
      return new ScriptObject(starts, contains, ends); 
    }
    
    public static objs find(ScriptSymbol symbol) {
      for(objs ob:values()) {
        if(inArray(ob.starts, symbol)) {
          return ob;
        }
      }
      return null;
    }
    public static boolean inArray(Object[] ar, Object val) {
      for(Object v:ar) {
        if(v==val || v!=null && v.equals(val))
          return true;
      }
      return false;
    }
  };
  
  
  public final ScriptSymbol[] starts;
  public final ScriptSymbol[] contains;
  public final ScriptSymbol[] ends;
  ScriptObject(ScriptSymbol[] starts, ScriptSymbol[] contains, ScriptSymbol[] ends) {
    this.starts = starts;
    this.contains = contains;
    this.ends = ends;
  }
}
