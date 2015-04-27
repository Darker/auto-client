/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import cz.autoclient.scripting.exception.UnknownCommandException;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class CommandBuilder {
  public ArrayList<String> args = new ArrayList();
  public String name;
  public ScriptCommand createCommand() throws UnknownCommandException {
    ScriptCommand c = ScriptCommand.getCommand(name);
    //String arg[] = args.toArray(new String[args.size()]);
    //c.parseArguments(arg);
    return c;
  }
}
