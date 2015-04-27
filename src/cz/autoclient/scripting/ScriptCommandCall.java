/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

/**
 *
 * @author Jakub
 */
public class ScriptCommandCall {
  public final Object[] arguments;
  public final ScriptCommand command;
  ScriptCommandCall(ScriptCommand com, Object... args) {
    arguments = args;
    command = com;
  }
  public boolean execute() {
    return command.execute(arguments); 
  }
}
