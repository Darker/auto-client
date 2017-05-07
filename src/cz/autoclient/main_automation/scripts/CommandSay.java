/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation.scripts;

import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.PixelOffsetV2;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.scripting.ScriptCommand;
import cz.autoclient.scripting.exception.IllegalCmdArgumentException;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class CommandSay extends ScriptCommand {
  String sentence;
  int repeat = 1;
  int timeout = 200;
  @Override
  public void parseArguments(ArrayList<String> args) throws IllegalCmdArgumentException {
    if(args.size()<1)
      throw new IllegalCmdArgumentException("Too few arguments!");
    sentence = args.get(0);
    if(args.size()>1) {
      try {
        repeat = Integer.parseInt(args.get(1));
        if(args.size()>2)
          timeout = Integer.parseInt(args.get(2));
      } catch (NumberFormatException e) {
        throw new IllegalCmdArgumentException("Argument must be a number!");
      }
    }
  }
  @Override
  public boolean execute() throws InterruptedException {
    Object window = environment.get("window", Window.class);
    if(window!=null && window instanceof Window) {
      Window w = (Window)window;
      w.click(PixelOffsetV2.Lobby_Chat.toRect(w.getRect()));
      for(int i=0; i<repeat; i++) {
        if(i!=0 && timeout>0)
          Thread.sleep(timeout);

        w.typeString(sentence);
        w.keyPress(KeyEvent.VK_ENTER);
      }
      return true;
    }
    return false;
  }
}
