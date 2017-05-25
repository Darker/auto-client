/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation.scripts;

import cz.autoclient.scripting.ScriptCommand;
import cz.autoclient.scripting.exception.IllegalCmdArgumentException;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class CommandDelay extends ScriptCommand {
  int delay = 500;
  @Override
  public void parseArguments(ArrayList<String> args) throws IllegalCmdArgumentException {
    if(args.size()!=1)
      throw new IllegalCmdArgumentException("Delay expects exactly 1 argument - duration in milliseconds.");
    try {
      delay = Integer.parseInt(args.get(0));
    } catch (NumberFormatException e) {
      throw new IllegalCmdArgumentException("Delay argument must be a number!");
    }
  }
  @Override
  public boolean execute() throws InterruptedException {
    //Thread.sleep(delay);
    environment.sleep(delay);
    return true;
  }
}