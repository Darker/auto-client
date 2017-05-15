/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.LazyLoadedImage;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrameV2;
import cz.autoclient.PVP_net.PixelOffsetV2;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.PixelGroupSimple;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.RecursiveGroupOR;
import cz.autoclient.autoclick.RelativeRectangle;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.comvis.RectMatch;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowRobot;
import cz.autoclient.autoclick.windows.WindowValidator;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.league_of_legends.SummonerSpell;
import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.SleepAction;
import cz.autoclient.scripting.SleepActionLambda;
import cz.autoclient.scripting.exception.CommandException;
import cz.autoclient.scripting.exception.ScriptParseException;
import cz.autoclient.settings.Settings;
import java.awt.Color;
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
  public Window getWindow() {
    if(window == null) {
      window = ConstData.getClientWindow();
      if(window != null) 
        window = new WindowRobot(window);
    }
    return window;
  }
  @Override
  public void run() {
    dbgmsg("Automation started!");
    accepted = false;
    window = null;
    window = getWindow();
    //window = MSWindow.
    if (window == null) {
      errmsg("No PVP.net window found!");
      end();
      return;
    }
    dbgmsg("PVP.net window available.");
    try {
      while(true) {
        waitForGame();
        if(handleStandardLobby())
          break;
        handleMacros(1000);
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
        break;
      }
      if(pretendAccepted) {
        pretendAccepted = false;
        return;
      }
      //WindowTools.drawCheckPoint(img, pixels);
      //DebugDrawing.displayImage(img);
      handleMacros(400);
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
      final ComparablePixel[] lobbyPixels = new ComparablePixel[] {
          PixelOffsetV2.Lobby_Chat,
          PixelOffsetV2.Lobby_ClientChatButton,
          PixelOffsetV2.Lobby_ClientChatButtonOutside,
          PixelOffsetV2.Lobby_Search,
          PixelOffsetV2.Lobby_NotLocked_GoldFrame,
          PixelOffsetV2.Lobby_EditRunesLight,
          PixelOffsetV2.Lobby_EditRunesDark,
      };
      boolean inLobby = false;
      while(System.currentTimeMillis()- waitStart < 13000) {
          if(WindowTools.checkPoint(window, lobbyPixels)>=4) {
              inLobby = true;
              break;
          }
          sleep(30);
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
    // Select summoner spells
    selectSummonerSpells();
    
    final ComparablePixel[] lockedPixels = new ComparablePixel[] {
      PixelOffsetV2.Lobby_Chat,
      PixelOffsetV2.Lobby_Locked_GoldFrame,
      PixelOffsetV2.Lobby_Locked_GoldFrame,
      PixelOffsetV2.Lobby_ClientChatButton,
      PixelOffsetV2.Lobby_EditRunesLight,
      PixelOffsetV2.Lobby_EditRunesDark,
    };
    RecursiveGroupOR requiredPixels = new RecursiveGroupOR(
        new PixelGroupSimple(lobbyPixels),
        new PixelGroupSimple(lockedPixels)
    );
    // Wait for lobby to dissapear
    while(true) {
      while(requiredPixels.test(window)) {
        sleep(500L);
      }
      //DebugDrawing.displayImage(DebugDrawing.lastDebugImage);
      dbgmsg("Lobby gone, has the game started?");
      long startTime = System.currentTimeMillis();
      while(!requiredPixels.test(window)) {
        Window gameWindow = MSWindow.findWindow(new WindowValidator.CompositeValidatorAND(new WindowValidator[] {
          new WindowValidator.ProcessNameValidator(ConstData.game_process_name)
        }));
        if(gameWindow != null) {
          dbgmsg("Game started, ending.");
          return true;
        }
        if(System.currentTimeMillis()-startTime>8000) {
          dbgmsg("Lobby still gone, assuming that someone dodged the game.");
          return false;
        }
        sleep(300);
      }
      dbgmsg("Lobby reappeared, waiting for game to start.");
    }
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
    sleep(600L);
    //click(PixelOffsetV2.Lobby_ChanpionSlot1);
    //sleep(30L);
    Rect wrect = window.getRect();
    Rect coords = PixelOffsetV2.Lobby_ChanpionSlot1.toRect(wrect);
    //window.click(coords.left, coords.top, MouseButton.Right);
    //sleep(32L);
    for(int y=0; y<4; ++y)
      for(int x=0; x<4; ++x) {
        //window.mouseOver(coords.left+x-5, coords.top+y-5);
        window.click(coords.left+x-5, coords.top+y-5);
        sleep(6L);
      }
    
    slowClick(PixelOffsetV2.Lobby_ChanpionSlot1, 40);
  }
  public void selectSummonerSpells() throws InterruptedException {
    final Rect winRect = window.getRect();
    double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
    BufferedImage spellDialog = null;
    
    
    final RelativeRectangle[] spellButtonRectangles = {
      ImageFrameV2.Lobby_Spell1Button,
      ImageFrameV2.Lobby_Spell2Button
    };
    final String[] spells = {
      (String) settings.getSetting(Setnames.BLIND_SUMMONER1.name),
      (String) settings.getSetting(Setnames.BLIND_SUMMONER2.name)
    };
    for (int i = 0; i < 2; i++) {
      final SummonerSpell s = ConstData.lolData.getSummonerSpells().get(spells[i]);
      if (s == null) {
        dbgmsg("  Spell #" + (i + 1) + " is null.");
        continue;
      }
      /** 
       * Phase 1: check if the spell is selected already.
       * First we get cropped screenshot of spell button, then
       * we compare it with the summoner spell image.
       */
      Rect buttonCropRect = spellButtonRectangles[i].rect(winRect);
      BufferedImage selected_spell = ScreenWatcher.resampleImage(
          window.screenshotCrop(buttonCropRect),
          winSizeCoef, winSizeCoef
      );
      // Get spell and
      // ... resample it to button size
      // ... crop it's border, borders are often distorted in gui
      // ... darken it, icons in Lol client are darkened
      BufferedImage small_icon_no_crop = s.img.getScaledDiscardOriginal(ConstData.summonerSpellButtonSize, ConstData.summonerSpellButtonSize);
      BufferedImage small_icon = LazyLoadedImage.crop(small_icon_no_crop, 1);
      ScreenWatcher.changeHSB(small_icon, -0.0F, -0.114F);

      //DebugDrawing.displayImage(selected_spell, "Spell button #"+(i+1));
      //DebugDrawing.displayImage(small_icon_no_crop, "Required spell #"+(i+1));
      //DebugDrawing.displayImage(small_icon, "Required spell #"+(i+1));  

      RectMatch selected_spell_rect = ScreenWatcher.findByAvgColor(small_icon, selected_spell, 0.003f, false, null);
      // Spell found selected
      if (selected_spell_rect != null) {
        dbgmsg("  Spell #" + (i + 1) + " already selected, difference: "+selected_spell_rect.diff);
        continue;
      }
      //Crop the icon - the GUI disorts the icon borders so I ignore them
      BufferedImage icon = s.img.getScaledDiscardOriginal(ConstData.summonerSpellChoseButtonSize, ConstData.summonerSpellChoseButtonSize);
      ScreenWatcher.changeHSB(icon, -0.01F, -0.25F);
      if (icon != null) {

        click(i == 0 ? PixelOffsetV2.Lobby_SumSpellButton1:PixelOffsetV2.Lobby_SumSpellButton2);
        //Wait till the launcher screen redraws
        if(!WindowTools.waitForPredicate(window,
            new PixelGroupSimple(
                PixelOffsetV2.Lobby_SumSpellDialog_Bg1,
                PixelOffsetV2.Lobby_SumSpellDialog_Bg2
            ),
            1000)) {
          errmsg("Cannot open dialog for summoner spells!");
          return;
        }
        sleep(300L);
        
        //Calculate crop rectangle 
        if(spellDialog == null) {
          BufferedImage screenshot = window.screenshot();
          spellDialog = cropToSummonerSpellDialog(screenshot, winRect);
        }

        Rect pos = findSummonerSpell(s, spellDialog, 3);

        if (pos != null) {
           dbgmsg("Original result: "+pos);
           BufferedImage clone = DebugDrawing.cloneImage(spellDialog);
           DebugDrawing.drawResult(clone, pos, Color.RED);
           DebugDrawing.displayImage(clone, "Non-normalized");
           // recalculate the relative position to the screen position
           Rect realPosition = normalizeRect(pos,  ImageFrameV2.Lobby_SpellDialog.rect(winRect), winSizeCoef);
           click(realPosition.middle());
//           clone = DebugDrawing.cloneImage(testScreen);
//           DebugDrawing.drawResult(clone, realPosition, Color.RED);
//           DebugDrawing.displayImage(clone, "Non-normalized");
        } else {
          dbgmsg("  Spell #" + (i + 1) + " image corrupted.");
          BufferedImage clone = DebugDrawing.cloneImage(spellDialog);
          //DebugDrawing.drawResult(clone, pos, Color.RED);
          DebugDrawing.displayImage(clone, "Non-normalized");
        }
      }
    }
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
  public static BufferedImage cropToSummonerSpellDialog(BufferedImage windowScreenshot, Rect winRect) {
    double resampleCoefficient = ConstData.sizeCoeficientInverted(winRect);
    BufferedImage screenshot = ScreenWatcher.resampleImage(
        LazyLoadedImage.crop(windowScreenshot, ImageFrameV2.Lobby_SpellDialog.rect(winRect)),//window.screenshotCrop(cropRect),
        resampleCoefficient, resampleCoefficient);
    System.out.println("Resampled: "+resampleCoefficient);
    return screenshot;
  }
  public static Rect findSummonerSpell(SummonerSpell s, BufferedImage area, int crop) {
    BufferedImage icon = s.img.getScaledDiscardOriginal(ConstData.summonerSpellChoseButtonSize, ConstData.summonerSpellChoseButtonSize);
    icon = LazyLoadedImage.crop(icon, 3);
    ScreenWatcher.changeHSB(icon, -0.01F, -0.25F);
    return ScreenWatcher.findByAvgColor(icon, area, 0.001f, true, null);
  }
  
  /**
   * If you have rectangle in cropped and resized area, this method converts it to 
   * rectangle for the original screen. It assumes that you forst croped and then resampled
   * the original image and therefore applies operations in reverse order.
   * @param relativeRect rect that has 0,0 relative to some cropped area
   * @param cropRect the position of cropped area relative to the whole image
   * @param resampleRatio ratio of resampling (0.5 means the original image is 2 times bigger)
   * @return 
   */
  public static Rect normalizeRect(Rect relativeRect, Rect cropRect, double resampleRatio) {
    return relativeRect.multiply(1.0/resampleRatio).move(cropRect);
  }
}
