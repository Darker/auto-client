/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import cz.autoclient.scripting.exception.IllegalCmdArgumentException;
import cz.autoclient.scripting.exception.UnknownCommandException;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jakub
 */
public abstract class ScriptCommand {
  protected ScriptEnvironment environment;
  
  public ScriptCommand() {}
  public ScriptCommand(ScriptEnvironment e) {
    this.environment = e;
  }
  /**
   * Converts string argument array to object argument array.
   * @param args
   * @throws cz.autoclient.scripting.exception.IllegalCmdArgumentException if the string array is not valid
   */
  public abstract void parseArguments(ArrayList<String> args) throws IllegalCmdArgumentException;
  
  public abstract boolean execute() throws Exception;

  public void setEnvironment(ScriptEnvironment environment) {
    this.environment = environment;
  }
  
  

  /**
   * Array of registered commands.
   */
  private static final HashMap<String, Class<? extends ScriptCommand>> commands = new HashMap();
  public static ScriptCommand getCommand(String commandName, ScriptEnvironment e) throws UnknownCommandException {
    Class<? extends ScriptCommand> c = commands.get(commandName);
    
    if(c==null) {
      throw new UnknownCommandException("Unknown command: "+commandName);
    }
    else {
      Object[] args = e!=null?new Object[]{e}:new Object[]{};
      try {
        System.out.println("Generating command '"+commandName+"'.");
        return (ScriptCommand)new Expression(c, "new", args).getValue(); 
      }
      catch(Exception ex) {
        throw new UnknownCommandException("Invalid command '"+commandName+"' caused exception "+ex);
      }
    }
  }
  public static ScriptCommand getCommand(String commandName) throws UnknownCommandException {
    return getCommand(commandName, null);
  }
  
  public static void setCommand(String name, Class<? extends ScriptCommand> implementation) {
    synchronized(commands) {
      commands.put(name, implementation);
    }
  }
  
  static {
    commands.put("echo", CommandEcho.class);
  }
  
  public static class CommandEcho extends ScriptCommand {
    private final List<String> strings = new ArrayList();
    @Override
    public void parseArguments(ArrayList<String> args) throws IllegalCmdArgumentException {
      for(String s:args) {
        strings.add(s);
      }
    }

    @Override
    public boolean execute() {
      for(String str:strings) {
        System.out.println(str);
      }
      return true;
    }
  }
  
}
