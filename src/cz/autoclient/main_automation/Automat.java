package cz.autoclient.main_automation;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.LazyLoadedImage;
import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.PVP_net.AcceptedGameType;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrame;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.settings.Settings;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Images;
import cz.autoclient.PVP_net.PxGroup;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.exceptions.APIException;
import java.awt.Color;

import cz.autoclient.autoclick.comvis.RectMatch;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;
import cz.autoclient.autoclick.exceptions.WindowAccessDeniedException;
import cz.autoclient.league_of_legends.SummonerSpell;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.ArrayList;
import cz.autoclient.autoclick.ComparablePixel;
import static cz.autoclient.main_automation.WindowTools.*;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.main_automation.SituationDetector.LobbyType;
import cz.autoclient.main_automation.scripts.CommandDelay;
import cz.autoclient.main_automation.scripts.CommandSay;
import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.ScriptCommand;
import cz.autoclient.scripting.SleepAction;
import cz.autoclient.scripting.SleepActionLambda;
import cz.autoclient.scripting.exception.CommandException;
import cz.autoclient.scripting.exception.ScriptParseException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Automat
    extends AutomatInterface {

  Window window;
   //User32 user32 = this.window.getUser32();
  //Kernel32 kernel = this.window.getKernel32();
  Gui gui;
  protected Settings settings;
  protected ChampionImages championColors = null;

  protected boolean pretendAccepted = false;

  //Logger for thread
  {
    ScriptCommand.setCommand("s", CommandSay.class);
    ScriptCommand.setCommand("d", CommandDelay.class);
  }

  public Automat(Gui acgui, Settings settings) {
    super("LoLClientAutomation");
    this.settings = settings;
    this.gui = acgui;
  }
  @Override
  public Window getWindow() {
    if(window == null) {
      window = MSWindow.windowFromName(ConstData.window_title_part, false); 
    }
    return window;
  }
  @Override
  public void run() {
    dbgmsg("Automation started!");
    
    window = null;
    //Get PVP.net window
    window = getWindow();
    if (window == null) {
      errmsg("No PVP.net window found!");
      end();
      return;
    }
    dbgmsg("PVP.net window available.");
     //long cID = this.window.FindWindow("PVP");
    //this.gui.getProgressBar1().setValue(0);
    //First check if we have access to the window
    try {
      window.mouseOver(0, 0);
    } catch (WindowAccessDeniedException e) {
      gui.dialogElevateAsync();
      end();
      return;
    }

    try {
      handleMatch();
    } catch (InterruptedException e) {
      dbgmsg(e.getMessage());
      end();
    } catch (APIException e) {
      dbgmsg("The Window API has failed:" + e);
      end();
    }
    end();
  }

  protected void end() {
    gui.displayToolAction(false);
  }

  public boolean pixelCheckS(Color rgb, double x, double y, int tolerance) {
    Color c = window.getColor((int) x, (int) y);
    return ((Math.abs(c.getRed() - rgb.getRed()) < tolerance)
        && (Math.abs(c.getGreen() - rgb.getGreen()) < tolerance)
        && (Math.abs(c.getBlue() - rgb.getBlue()) < tolerance));
  }

  private void handleMatch()
      throws InterruptedException, APIException {
    /*Rect cRec = window.getRect();
     int height = cRec.height;
     int width = cRec.width;*/
    long accepted = -1;

    long time;
    
    AcceptedGameType type = AcceptedGameType.UNKNOWN;
    //If the play button is there, do not do anything
    boolean play_button = true;

    gui.setTitle("Waiting for match.");
    while (!isInterrupted()) {
      time = System.currentTimeMillis() / 1000L;
      if (pretendAccepted == true) {
        pretendAccepted = false;
        accepted = time;
        type = AcceptedGameType.NORMAL;
        gui.setTitle("Match accepted, waiting for lobby.");
      }

      sleep(accepted > 0 ? 10L : 600L);
      try {
        if (accepted > 0) {
          if(type == AcceptedGameType.NORMAL) {
            if (PxGroup.NORMAL_LOBBY.test(window)) {
              dbgmsg("Lobby detected. Picking champion and lane.");
              if (normal_lobby()) {
                break;
              } else {
                continue;
              }
            }
          }
          else if(type == AcceptedGameType.DRAFT) {
            if(PxGroup.DRAFT_LOBBY.test(window)) {
              dbgmsg("Draft lobby handled, that's all I can do right now.");
              accepted = -1;
              gui.notification(Notification.Def.DRAFT_TEAM_JOINED);
            }
            else {
              dbgmsg("Draft lobby match failed!"); 
            }
          }
          if (time - accepted > type.timeout) {
            dbgmsg("Match was declined.");
            gui.setTitle("Waiting for match.");
            accepted = -1;
            type = AcceptedGameType.UNKNOWN;
          }
        } else {
          if (PxGroup.BLIND_MATCH_FOUND.test(window)) {
            click(PixelOffset.AcceptButton);
            accepted = time;
            type = AcceptedGameType.NORMAL;
            gui.setTitle("Match accepted, waiting for lobby.");
            play_button = false;
          }
          /** DRAFT LOBBY ACCEPT **/
          else if (PxGroup.DRAFT_ACCEPT.test(window)) {
            click(PixelOffset.Draft_Accept_Mid);
            accepted = time;
            type = AcceptedGameType.DRAFT;
            gui.setTitle("Draft match accepted, waiting for lobby.");
          } 
          else if (checkPoint(window, PixelOffset.InviteChat) && checkPoint(window, PixelOffset.InviteStart)) {
            invite_lobby();
            gui.setTitle("Waiting for match.");
            play_button = false;
          } 
          else if (checkPoint(window, PixelOffset.PlayButton_red) && !play_button) {
            dbgmsg("The play button is red. Something must've gone wrong.");
            play_button = true;
            gui.setTitle("Waiting for match.");
          }
        }
      } catch (IllegalArgumentException fe) {
        fe.printStackTrace();
        //Run standard end actions
        end();
        interrupt();
      }
    }

    if (!isInterrupted()) {
      dbgmsg("All done :)");

      interrupt();
    } else {
      dbgmsg("Match handling interrupted.");
    }
  }

  @Override
  public synchronized void simulateAccepted() {
    pretendAccepted = true;
  }

  public boolean normal_lobby() throws InterruptedException, APIException {
    {
      gui.notification(Notification.Def.BLIND_TEAM_JOINED);
      dbgmsg("Triggered notification for normal lobby.");
    }
    sleep(100L);
    dbgmsg("In normal lobby.");
    //boolean ARAM = false;
    LobbyType current_lobby = LobbyType.NORMAL_BLIND;
    if(Setnames.DEBUG_PRETEND_ARAM.getBoolean(settings))
      current_lobby = LobbyType.ARAM;
    else
      current_lobby = SituationDetector.loggyType(window);
    
    if (current_lobby==LobbyType.ARAM) {
      dbgmsg("Probably ARAM!");
      if(Setnames.ARAM_ENABLED.getBoolean(settings)) {
        aram_lobby();
      }
    }
    else if(current_lobby==LobbyType.NORMAL_BAN) {
      dbgmsg("Probably BAN LOBBY");
      ban_lobby();
    }
    else {
      blind_lobby(); 
    }
    
    dbgmsg("NORMAL LOBBY: Waiting for a game to start.");
    gui.setTitle("In normal lobby, waiting for a game to start.");
    //Wait and return false if lobby ends unexpectedly
    PixelOffset[] points = new PixelOffset[] {
      PixelOffset.LobbyChat,
      PixelOffset.LobbyChat2,
      //PixelOffset.Blind_SearchChampion,
      PixelOffset.LobbyTopBar,
      PixelOffset.LobbyHoverchampTop,
      //PixelOffset.LobbySummonerSpellsHeader,
      PixelOffset.LobbyRunesCheckmark,
      PixelOffset.Masteries_Edit
    };
    PixelOffset[] failPoints = new PixelOffset[] {
      PixelOffset.StoreButton,
      PixelOffset.PlayButton_red,
      PixelOffset.PlayButton_SearchingCorner,
      PixelOffset.PlayButton_cancel
    };
    while (true) {
      if (WindowTools.checkPoint(window, points) < 3) {
        dbgmsg("NORMAL LOBBY: lobby gone, waiting for the game.");
         //Internal loop will wait until lobby reappears or game starts
        //or main screen reappears
        do {
          //Wait 2 seconds, then check if back in main screen
          sleep(2000);

          BufferedImage img = window.screenshot();
          if ((WindowTools.checkPoint(img, failPoints)) > 1) {
            dbgmsg("NORMAL LOBBY: Game did not start, waiting for another game.");
            return false;
          } //Check if the game is running, return true if it does
          else if (CacheByTitle.initalInst.getWindow(ConstData.game_window_title) != null) {
            dbgmsg("NORMAL LOBBY: Game started.");
            return true;
          } else {
            dbgmsg("NORMAL LOBBY: Waiting...");
          }

           //WindowTools.drawCheckPoint(img, points);
          //WindowTools.drawCheckPoint(img, failPoints);
          //WindowTools.drawCheckPoint(screenshot, failPoints);
          //DebugDrawing.displayImage(img);
          //else if(WindowTools.checkPoint(window, points)>=4) {
          //  dbgmsg("NORMAL LOBBY: lobby back here, waiting for game again."); 
          //dbgmsg("NORMAL LOBBY: Game did not start, waiting for another game."); 
          //return false;
          //}
        } while (WindowTools.checkPoint(window, points) < 4);
        dbgmsg("NORMAL LOBBY: lobby back here, looping again.");
      }
      sleep(800);
    }
  }
  public void blind_lobby() throws InterruptedException {
    // Create parallel action for selecting champion
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
    if(!selectChampion.done()) {
      selectChampion(chname);
    }
    //Set summoner spells
    String[] spells = {
      (String) settings.getSetting(Setnames.BLIND_SUMMONER1.name),
      (String) settings.getSetting(Setnames.BLIND_SUMMONER2.name)
    };
    dbgmsg("Setting summoner spells.");
    //Loop that just does the same thing for both spells
    Rect winRect = window.getRect();
    double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
    Rect cropRect = null;
    // The rectangles of the spell buttons
    ImageFrame[] spellButtonRectangles = {
      ImageFrame.NormalLobby_SummonerSpell_1,
      ImageFrame.NormalLobby_SummonerSpell_2
    };

    for (int i = 0; i < 2; i++) {
      SummonerSpell s = ConstData.lolData.getSummonerSpells().get(spells[i]);
      if (s == null) {
        dbgmsg("  Spell #" + (i + 1) + " is null.");
        continue;
      }
       // First check if the same spell is already selected
      // Get the screenshot of selected spells first
      BufferedImage selected_spell = ScreenWatcher.resampleImage(
          window.screenshotCrop(spellButtonRectangles[i].rect(window)),
          winSizeCoef, winSizeCoef);

      BufferedImage small_icon_no_crop = s.img.getScaledDiscardOriginal(35, 35);
      BufferedImage small_icon = LazyLoadedImage.crop(small_icon_no_crop, 1);
      //DebugDrawing.displayImage(selected_spell, "Spell #"+(i+1));
      //DebugDrawing.displayImage(small_icon_no_crop, "Spell #"+(i+1));
      //DebugDrawing.displayImage(small_icon, "Spell #"+(i+1));  

      RectMatch selected_spell_rect = ScreenWatcher.findByAvgColor(small_icon, selected_spell, 0.001f, false, null);
      // Spell found selected
      if (selected_spell_rect != null) {
        //BufferedImage test = DebugDrawing.cloneImage(selected_spell);
        //DebugDrawing.drawResult(test, selected_spell_rect, Color.GREEN);
        //DebugDrawing.displayImage(test, "Spell #"+(i+1)+" icon "+small_icon.getWidth()+"x"+small_icon.getHeight());

        dbgmsg("  Spell #" + (i + 1) + " already selected, difference: "+selected_spell_rect.diff);
        continue;
      }

      //Crop the icon - the GUI disorts the icon borders so I ignore them
      BufferedImage icon = LazyLoadedImage.crop(s.img.getScaledDiscardOriginal(48, 48), 5);
      if (icon != null) {
        click(i == 0 ? PixelOffset.Blind_SumSpell1 : PixelOffset.Blind_SumSpell2);
        //Wait till the launcher screen redraws
        sleep(500L);

        //Calculate crop rectangle 
        if (cropRect == null) {
          cropRect = ImageFrame.NormalLobby_SummonerSpellPopup.rect(window);
        }
          //Use base resolution window - the icons are saved in base resolution too
          /*BufferedImage screenshot = ScreenWatcher.resampleImageTo(
         window.screenshot(),
         ConstData.smallestSize.width, ConstData.smallestSize.height);*/

        BufferedImage screenshot = ScreenWatcher.resampleImage(
            window.screenshotCrop(cropRect),
            winSizeCoef, winSizeCoef);
          //double[][][] integral_image = ScreenWatcher.integralImage(screenshot);
        //Some CV
        Rect pos = ScreenWatcher.findByAvgColor(icon, screenshot, 0.001f, true, null);

        if (pos != null) {
//           dbgmsg("Original result: "+pos);
//           screenshot = window.screenshot();
//           DebugDrawing.drawResult(screenshot, cropRect, Color.RED);
//           DebugDrawing.displayImage(screenshot, "Non-normalized");
//
//           screenshot = ScreenWatcher.resampleImageTo(
//           window.screenshot(),
//           ConstData.smallestSize.width, ConstData.smallestSize.height);
          //Add the normalized top/left coordinates of the search rectangle we used
          Rect cropNormalized = ConstData.normalize(cropRect, winRect);
          pos = pos.move(cropNormalized.left, cropNormalized.top);
//           dbgmsg("Search region: "+cropNormalized);
//           dbgmsg("Moved result: "+pos);
//           DebugDrawing.drawResult(screenshot, cropNormalized, Color.RED);
//           DebugDrawing.drawResult(screenshot, pos, Color.GREEN);
//           DebugDrawing.displayImage(screenshot, "Normalized");

  //             dbgmsg("Crop rect: "+cropRect+" and normalized: "+cropNormalized);
          //             screenshot = window.screenshot();
          //             DebugDrawing.drawResult(screenshot, pos, Color.RED, Color.YELLOW);
          //             dbgmsg("Moved result: "+pos);
          //             DebugDrawing.displayImage(screenshot, "Moved result");
            //De normalize the rectangle (don't forget we rescaled the screenshot prior to 
          // searching the summoner spell)
          pos = ConstData.deNormalize(pos, winRect);

//          screenshot = window.screenshot();
//           DebugDrawing.drawResult(screenshot, pos, Color.RED, Color.YELLOW);
//           dbgmsg("Rescaled result: "+pos);
//           DebugDrawing.displayImage(screenshot, "Rescaled result");
//
//           //Show some debug
//           screenshot = window.screenshot();
//           DebugDrawing.drawResult(screenshot, pos, Color.RED);
          // Click in middle of button rather than the corner
          pos = pos.middle();

//          DebugDrawing.drawPoint(screenshot, pos.left, pos.top, 5, Color.YELLOW);
//           DebugDrawing.displayImage(screenshot);
          //Click in the middle of the found rectangle
          dbgmsg("  Spell #" + (i + 1) + " CLICKING: " + pos);
          window.mouseDown(pos.left, pos.top);
          sleep(30L);
          window.mouseUp(pos.left, pos.top);
          sleep(400L);
        } else {
          dbgmsg("  Spell #" + (i + 1) + " not seen on screen.");
          //DebugDrawing.displayImage(screenshot);
          click(PixelOffset.Blind_SumSpell_CloseDialog);
          sleep(80L);
        }
      } else {
        dbgmsg("  Spell #" + (i + 1) + " image corrupted.");
      }
    }
    //Set masteries:
    int mastery = settings.getInt(Setnames.BLIND_MASTERY.name, 0);
    setMastery(mastery);
    //Set runes:
    int rune = settings.getInt(Setnames.BLIND_RUNE.name, 0);
    setRunes(rune);
  }
  private void ban_lobby() throws InterruptedException {
    BufferedImage screenshot = window.screenshot();
    this.callText(settings.getStringEquivalent(Setnames.BLIND_CALL_TEXT.name));
    while(LobbyType.NORMAL_BAN.test(screenshot)) {
      if(WindowTools.checkPoint(screenshot, PixelOffset.BAN_BANNING_ACTIVE)) {
        dbgmsg("You are banning!"); 
      }
      sleep(1000);
      screenshot = window.screenshot();
    }
    dbgmsg("Banning over, time to pick champion!");
    selectChampion(Setnames.BLIND_CHAMP_NAME.getString(settings));
  }
  public void selectChampion(final String name) throws InterruptedException {
    if(name == null || name.length()==0) {
      dbgmsg("Error: empty champion anme given.");
      return;
    }
    dbgmsg("Selecting champion "+name);
    slowClick(PixelOffset.Blind_SearchChampion, 30);
    sleep(80L);

    window.typeString(name);
    sleep(200L);
    click(PixelOffset.LobbyChampionSlot1);
    sleep(10L);
    click(PixelOffset.LobbyChampionSlot1);
    sleep(100L);
    click(PixelOffset.LobbyChampionSlot1);
    sleep(50L);
    click(PixelOffset.LobbyTopBar);
  }
  
  @Override
  public void callText(final String message) throws InterruptedException {
    callText(message, null);
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
    click(PixelOffset.LobbyChat);
    click(PixelOffset.LobbyChat);
    // Test for scripts
    if (message.startsWith("S>")) {
      dbgmsg("Compiling " + message + " for chat messaging.");
      try {
        OneLineScript say = OneLineScript.parse(message);
        if(actions!=null && actions.length>0)
          say.getEnvironment().addSleepActions(actions);
        say.compile();
        say.setenv("window", window);
        click(PixelOffset.LobbyChat);
        say.run();
      } catch (ScriptParseException e) {
        Dialogs.dialogErrorAsync("Your script couldn't be parsed, se error below:<br /><pre>" + e.getMessage() + "</pre>", "Syntax error");
      } catch (CommandException e) {
        Dialogs.dialogErrorAsync("One of commands used in the script reported an error:<br /><pre>" + e.getMessage() + "</pre>", "Invalid command");
      }
    } else {
      click(PixelOffset.LobbyChat);
      dbgmsg("Typping '" + settings.getString(Setnames.BLIND_CALL_TEXT.name) + "' in chat window.");
      window.typeString(settings.getString(Setnames.BLIND_CALL_TEXT.name));
      Enter();
    }
    //if(true){ return; }
    //dbgmsg(this.gui.chatTextField().getText());
    sleep(200L);
  }

  public void aram_lobby() throws InterruptedException {
    // Constant sleep to ensure the ARAM champion is already displayed
    sleep(1500);
    // Get screenshot of the player box area to the left
    BufferedImage player_list = WindowTools.getNormalizedScreenshot(
        window,
        ImageFrame.NormalLobby_PlayerList_Left
    );

    Rect player_pos;
    PixelOffset[] playerOffsets = {
      PixelOffset.Lobby_Player1,
      PixelOffset.Lobby_Player2,
      PixelOffset.Lobby_Player3,
      PixelOffset.Lobby_Player4,
      PixelOffset.Lobby_Player5,
    };

    try {
      player_pos = ScreenWatcher.findByAvgColor(
          Images.LOBBY_BOX_LOCKED.getImg(),
          player_list,
          0.001f,
          false,
          null
      );
      if (player_pos != null) {
        if(championColors==null)
          championColors = new ChampionImages(new File("LOLResources"));
        championColors.getColorsAsync();
        //DebugDrawing.drawResult(player_list, player_pos, Color.RED);
        // The offset by which the current area is cropped
        Rect moveOffset = ImageFrame.NormalLobby_PlayerList_Left.multiplyBySize(ConstData.smallestSize);
        // Dimensions of the full window (I'm usning the const minimal dimensions)
        // applied to dimension of player box
        Rect size = PixelOffset.Lobby_Player_Box_Size.toRect(ConstData.smallestSize);

        Rect fullRect = null;
        double bestDist = Double.MAX_VALUE;
        for(PixelOffset o: playerOffsets) {
          // Convert relative point offset to offset in our cropped wiew
          Rect fullRectTmp = o.toRect(ConstData.smallestSize, moveOffset, size);

          double dist = fullRectTmp.distanceSq(player_pos);
          if(dist<bestDist) {
            fullRect = fullRectTmp;
            bestDist = dist;
          }
          // Draw the pixel
          DebugDrawing.drawPoint(player_list,
              fullRectTmp.left,
              fullRectTmp.top,
              6,
              Color.WHITE,
              fullRectTmp.distanceSq(player_pos)+""
          );
        }

        //Champion face relative to (any) champion box
        // This is because 1st box is subtracted from 1st avatar pos
        // so only relative difference remains
        Rect champion = (Rect)ImageFrame.NormalLobby_PlayerList_Champion1
                  .relativeTo(ImageFrame.NormalLobby_PlayerList_Box1)
                  .multiplyBySize(ConstData.smallestSize)
                  .move(fullRect.left, fullRect.top)
                  .crop(1);
        BufferedImage avatar = player_list.getSubimage(
            champion.left,
            champion.top,
            champion.width+1,
            champion.height+1
        );
        // move the rectangle towards the champion box that was found
        //DebugDrawing.drawResult(player_list, champion, Color.magenta);

        //moveOffset = moveOffset.move(-moveOffset.left, -moveOffset.top);
        //dbgmsg("The list frame: "+moveOffset);
        //DebugDrawing.drawResult(player_list, moveOffset, Color.YELLOW);

        //DebugDrawing.displayImage(player_list, "Player position.");

        //DebugDrawing.displayImage(avatar, "Player position.");
        //Color avg = ScreenWatcher.averageColor(avatar);

        ChampionImages.Match best = championColors.find(avatar, 5000);
        dbgmsg("Detected champion: "+best+" with diff "+best.difference);
        //Detected champion: Elise with diff 5240
        
        if(best.valid()) {
          String name = best.name;
          if(settings.exists("ConfigurationManager", HashMap.class)) {
            HashMap cust_settings = (HashMap)settings.getSetting("ConfigurationManager_0"); 
            if(cust_settings.containsKey(name)) {
              Settings custom = (Settings)cust_settings.get(name);
              setMastery(custom.getInt(Setnames.BLIND_MASTERY.name, 0));
              setRunes(custom.getInt(Setnames.BLIND_RUNE.name, 0));
            }
            else {
              dbgmsg("Error: no settings for champion "+name+"!");
            }
          }
          else {
            dbgmsg("Error: no settings manager available for custom settings.");
          }
        }
        else {
          dbgmsg("Error: invalid champion match."); 
        }
      }
      else {
        dbgmsg("Error: player lobby bar was not found.");
      }
    } catch (IOException ex) {
      dbgmsg("Missing image for aram lobby.");
    }
  }
  public void invite_lobby() throws APIException, InterruptedException {
    //Handle disabled invite lobby
    if (!settings.getBoolean(Setnames.INVITE_ENABLED.name, (boolean) Setnames.INVITE_ENABLED.default_val)) {
      dbgmsg("Invite lobby automation disabled, waiting.");
      gui.setTitle("Automation disabled (Invite)");
      while (checkPoint(window, PixelOffset.InviteChat, PixelOffset.InviteStart) == 2) {
        sleep(1000L);
      }
      return;
    }

    dbgmsg("Inviting players now. ");
    gui.setTitle("Waiting for players. (Invite)");
    double[][][] integral_image;
    double[] accepted, pending;
    try {
      accepted = Images.INVITE_ACCEPTED_SMALL.getColorSum();
      pending = Images.INVITE_PENDING.getColorSum();
    } catch (IOException e) {
      errmsg("Can't find required image! Invite lobby can't be automated!");
      settings.setSetting(Setnames.INVITE_ENABLED.name, false);
      return;
    }
    //Declare the two arrays of matches
    ArrayList<RectMatch> accepted_all, pending_all;

    //Calculate the region where to search the player list
    Rect player_list = ImageFrame.Invite_InvitedPlayerList.rect(window);

    while (checkPoint(window, PixelOffset.InviteChat, PixelOffset.InviteStart) == 2) {

      //dbgmsg("Taking screenshot from window.");
      BufferedImage screenshot = window.screenshotCrop(player_list);
      integral_image = ScreenWatcher.integralImage(screenshot);
      //dbgmsg("Analysing the screenshot.");
      dbgmsg("  Invited players: ");

      pending_all = ScreenWatcher.findByAvgColor_isolated_matches(
          pending.clone(),
          integral_image,
          Images.INVITE_PENDING.getWidth(),
          Images.INVITE_PENDING.getHeight(),
          0.003f);
      dbgmsg("    Pending: " + pending_all.size());

      accepted_all = ScreenWatcher.findByAvgColor_isolated_matches(
          accepted.clone(),
          integral_image,
          Images.INVITE_ACCEPTED_SMALL.getWidth(),
          Images.INVITE_ACCEPTED_SMALL.getHeight(),
          //This is maximum safe tolerance before "Owner" gets matched too
          0.0012f);
      dbgmsg("    Accepted: " + accepted_all.size());

      /*DebugDrawing.drawPointOrRect(screenshot, Color.yellow, pending_all);
       DebugDrawing.drawPointOrRect(screenshot, Color.green, accepted_all);
       DebugDrawing.displayImage(screenshot);*/
      //Only start if all players accepted or declined and at least one accepted
      if (accepted_all.size() > 0 && pending_all.isEmpty()) {
        dbgmsg("All players have been invited and are in lobby. Time to start!");
        gui.setTitle("Waiting for match.");
        click(PixelOffset.InviteStart);
        return;
      }
      //dbgmsg("Next test in 2 seconds.");
      sleep(2000L);
      //dbgmsg("Timeout over, next test?");
    }
    dbgmsg("Lobby has exit spontaneously.");
  }
  private boolean setMastery(int mastery) throws InterruptedException {
    //int mastery = settings.getInt(Setnames.BLIND_MASTERY.name, 0);
    if (mastery > 0) {
      dbgmsg("  Setting mastery to mastery #" + mastery);
      click(PixelOffset.Masteries_Edit);
      dbgmsg("  Waiting for mastery dialog.");
      if(!WindowTools.waitForPixel(window, PixelOffset.Masteries_Big_Close, 5000)) {
        dbgmsg("Error: Mastery wait timeout.");
        return false;
      }
      click(PixelOffset.Masteries_Big_First.offset(PixelOffset.Masteries_Big_Spaces.x * (mastery - 1), 0));
      sleep(50);
      click(PixelOffset.Masteries_Big_Close);
      return true;
    }
    return false;
  }
  private boolean setRunes(int rune) throws InterruptedException {
    //int rune = settings.getInt(Setnames.BLIND_RUNE.name, 0);
    if (rune > 0) {
      dbgmsg("Set RUNES to #"+rune);
      click(PixelOffset.Blind_Runes_Dropdown);
      sleep(700);
      click(PixelOffset.Blind_Runes_Dropdown_First.offset(0, PixelOffset.Blind_Runes_Dropdown_Spaces.y * (rune - 1)));
      return true;
    }
    return false;
  }



  

}
// [^/ ][^/ ]\s*DebugDrawing.displayImage
