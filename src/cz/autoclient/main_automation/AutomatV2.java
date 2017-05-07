/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.Gui;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffsetV2;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.MouseButton;
import cz.autoclient.autoclick.windows.WindowRobot;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import static cz.autoclient.main_automation.Automat.dbgmsg;
import static cz.autoclient.main_automation.Automat.errmsg;
import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.SleepAction;
import cz.autoclient.scripting.SleepActionLambda;
import cz.autoclient.scripting.exception.CommandException;
import cz.autoclient.scripting.exception.ScriptParseException;
import cz.autoclient.settings.Settings;
import java.awt.image.BufferedImage;
import static java.lang.Thread.sleep;

/**
 *
 * @author Jakub
 */
public class AutomatV2 extends Automat {

  public AutomatV2(Gui acgui, Settings settings) {
    super(acgui, settings);
  }
  protected boolean accepted = false;
  @Override
  public void run() {
    dbgmsg("Automation started!");
    accepted = false;
    window = MSWindow.windowFromName(ConstData.window_title_part, false);
    //window = MSWindow.
    if (window == null) {
      errmsg("No PVP.net window found!");
      end();
      return;
    }
    window = new WindowRobot(window);
    dbgmsg("PVP.net window available.");
    try {
      while(true) {
        waitForGame();
        if(handleStandardLobby())
          break;
      }
    } catch (InterruptedException e) {
      dbgmsg(e.getMessage());
      end();
    } catch (APIException e) {
      dbgmsg("The Window API has failed:" + e);
      end();
    }
    dbgmsg("Ended gracefully.");
    end();
  }
  
  public void waitForGame() throws InterruptedException {
    accepted = false;
    ComparablePixel[] pixels = new ComparablePixel[] {
      PixelOffsetV2.Accept_AcceptButton,
      PixelOffsetV2.Accept_DeclineButton,
      PixelOffsetV2.Accept_ProgressBar
    };
    int timeAccepted = 0;
    while(!accepted && !isInterrupted()) {
      BufferedImage img = window.screenshot();
      if(WindowTools.checkPoint(img, pixels)>=3) {
        click(PixelOffsetV2.Accept_AcceptButton);
        sleep(10L);
        break;
      }
      if(pretendAccepted) {
        pretendAccepted = false;
        return;
      }
      //WindowTools.drawCheckPoint(img, pixels);
      //DebugDrawing.displayImage(img);
      sleep(400L);
    }
    if(isInterrupted())
      throw new InterruptedException("Interrupted during accept phase.");
  }
  /**
   * 
   * @return True uf lobby was handled and game begun
   * @throws InterruptedException 
   */
  public boolean handleStandardLobby() throws InterruptedException {
      long waitStart = System.currentTimeMillis();
      ComparablePixel[] lobbyPixels = new ComparablePixel[] {
          PixelOffsetV2.Lobby_Chat,
          PixelOffsetV2.Lobby_ClientChatButton,
          PixelOffsetV2.Lobby_ClientChatButtonOutside,
          PixelOffsetV2.Lobby_Search,
      };
      boolean inLobby = false;
      while(System.currentTimeMillis()- waitStart < 12000) {
          if(WindowTools.checkPoint(window, lobbyPixels)>=4) {
              inLobby = true;
              break;
          }
          sleep(40);
      }
      if(!inLobby) {
          dbgmsg("Lobby didn't open, waiting for another game.");
          return false; 
      }
      // Do lobby stuff here
    SleepAction selectChampion = new SleepAction.NoAction();
    String chname;
    if ((chname=settings.getStringEquivalent(Setnames.BLIND_CHAMP_NAME.name)).length() > 1) {
      selectChampion = new SleepActionLambda(()->{
        dbgmsg("  Setting champion name in between scripts.");
        selectChampion(chname);
        return true;
      }, 750);
    }
    this.callText(settings.getStringEquivalent(Setnames.BLIND_CALL_TEXT.name), new SleepAction[] {
      selectChampion
    });
    // If champion wasn't selected while calling text, now it's the time
    if(!selectChampion.done() || (selectChampion instanceof SleepAction.NoAction)) {
      selectChampion(chname);
    }
    // Wait for game to start
    
    
    return true;
  }
  @Override
  public void selectChampion(final String name) throws InterruptedException {
    if(name == null || name.length()==0) {
      dbgmsg("Error: empty champion name given.");
      return;
    }
    dbgmsg("Selecting champion "+name);
    slowClick(PixelOffsetV2.Lobby_Search, 30);
    sleep(80L);

    window.typeString(name);
    sleep(300L);
    //click(PixelOffsetV2.Lobby_ChanpionSlot1);
    //sleep(30L);
    Rect wrect = window.getRect();
    Rect coords = PixelOffsetV2.Lobby_ChanpionSlot1.toRect(wrect);
    //window.click(coords.left, coords.top, MouseButton.Right);
    //sleep(32L);
    for(int y=0; y<8; ++y)
      for(int x=0; x<8; ++x) {
        window.mouseOver(coords.left+x, coords.top+y);
        sleep(10L);
      }
    
    slowClick(PixelOffsetV2.Lobby_ChanpionSlot1, 40);
    sleep(50L);
  }
  
  
  /**
   * Call text to chat in standard lobby. Supports scripts.
   *
   * @param message text to call or script to process
   * @param actions actions to perform when sleeping if message is script
   * @throws InterruptedException
   */
  @Override
  public void callText(final String message, SleepAction[] actions) throws InterruptedException {
    if (message == null || message.length() == 0) {
      return;
    }
    click(PixelOffsetV2.Lobby_Chat);
    // Test for scripts
    if (message.startsWith("S>")) {
      dbgmsg("Compiling " + message + " for chat messaging.");
      try {
        OneLineScript say = OneLineScript.parse(message);
        if(actions!=null && actions.length>0)
          say.getEnvironment().addSleepActions(actions);
        say.compile();
        say.setenv("window", window);
        slowClick(PixelOffsetV2.Lobby_Chat, 50);
        say.run();
      } catch (ScriptParseException e) {
        Dialogs.dialogErrorAsync("Your script couldn't be parsed, se error below:<br /><pre>" + e.getMessage() + "</pre>", "Syntax error");
      } catch (CommandException e) {
        Dialogs.dialogErrorAsync("One of commands used in the script reported an error:<br /><pre>" + e.getMessage() + "</pre>", "Invalid command");
      }
    } else {
      slowClick(PixelOffsetV2.Lobby_Chat, 50);
      dbgmsg("Typping '" + settings.getString(Setnames.BLIND_CALL_TEXT.name) + "' in chat window.");
      window.typeString(settings.getString(Setnames.BLIND_CALL_TEXT.name));
      Enter();
    }
    //if(true){ return; }
    //dbgmsg(this.gui.chatTextField().getText());
    sleep(200L);
  }
}
