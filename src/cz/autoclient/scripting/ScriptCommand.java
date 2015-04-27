/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import cz.autoclient.scripting.exception.IllegalCmdArgumentException;
import cz.autoclient.scripting.exception.UnknownCommandException;
import java.util.HashMap;

/**
 *
 * @author Jakub
 */
public abstract class ScriptCommand {

  /**
   * Converts string argument array to object argument array. Output array
   * can be passed to execute() without errors.
   * @param args
   * @return 
   * @throws cz.autoclient.scripting.exception.IllegalCmdArgumentException if the string array is not valid
   */
  public abstract Object[] parseArguments(Iterable<String> args) throws IllegalCmdArgumentException;
  
  public abstract boolean execute(Object... args);
  
  

  /*ScriptCommand() {
  }*/
  private static final HashMap<String, ScriptCommand> commands = new HashMap();
  public static ScriptCommand getCommand(String commandName) throws UnknownCommandException {
    ScriptCommand c = commands.get(commandName);
    
    if(c==null) {
      throw new UnknownCommandException("Unknown command: "+commandName);
    }
    else
      return c;
  }
  
  static {
    commands.put("echo", new ScriptCommand() {

      @Override
      public Object[] parseArguments(Iterable<String> args) throws IllegalCmdArgumentException {
        StringBuilder b = new StringBuilder();
        for(String s:args) {
          b.append(s); 
        }
        return new Object[] {b.toString()};
      }

      @Override
      public boolean execute(Object... args) {
        System.out.println(args[0]);
        return true;
      }


    });
  }
  
}
