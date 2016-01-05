package cz.autoclient.main_automation;

import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.LazyLoadedImage;
import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrame;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.settings.Settings;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.PVP_net.Images;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.PVP_net.TeamBuilderPlayerSlot;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.ColorPixel;
import java.awt.Color;

import cz.autoclient.autoclick.comvis.RectMatch;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;
import cz.autoclient.PVP_net.WindowTools;
import cz.autoclient.autoclick.exceptions.WindowAccessDeniedException;
import cz.autoclient.league_of_legends.SummonerSpell;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.ArrayList;
import cz.autoclient.autoclick.ComparablePixel;
import static cz.autoclient.PVP_net.WindowTools.*;
 
 
 public class Automat
   extends Thread
 {
   Window window;
   //User32 user32 = this.window.getUser32();
   //Kernel32 kernel = this.window.getKernel32();
   Gui gui;
   private Settings settings;
   
   private boolean pretendAccepted = false;
   //Logger for thread
   
   
   public Automat(Gui acgui, Settings settings)
   {
     super("LoLClientAutomation");
     this.settings = settings;
     this.gui = acgui;
   }
   
   @Override
   public void run()
   {
     System.out.println("Automation started!");
     //Get PVP.net window
     window = MSWindow.windowFromName(ConstData.window_title_part, false);
     if(window==null) {
       System.err.println("No PVP.net window found!");
       end();
       return;
     }
     System.out.println("PVP.net window available.");
     //long cID = this.window.FindWindow("PVP");
     //this.gui.getProgressBar1().setValue(0);
     //First check if we have access to the window
     try {
       window.mouseOver(0, 0);
     }
     catch(WindowAccessDeniedException e) {
       gui.dialogElevateAsync();
       end();
       return;
     }
     
     
     try
     {
       handleMatch();
     }
     catch (InterruptedException e)
     {
       System.out.println(e);
       end();
     }
     catch (APIException e) {
       System.out.println("The Window API has failed:" +e);
       end();
     }
     end();
     /*try
     {
       for (;;)
       {
         sleep(1000L);
         if (this.gui.getSelectedMode().length > 1) {
           StartMode(cID, this.gui.getSelectedMode());
         }
       }
     }
     catch (InterruptedException e)
     {
       if (this.gui.getToggleButton1().isSelected()) {
         this.gui.getToggleButton1().doClick();
       }
     }*/
   }
   private void end() {
       gui.displayToolAction(false);
   }

   
   public boolean pixelCheckS(Color rgb, double x, double y, int tolerance)
   {
     Color c = window.getColor((int)x, (int)y);
     return (
         (Math.abs(c.getRed() - rgb.getRed()) < tolerance) &&
         (Math.abs(c.getGreen() - rgb.getGreen()) < tolerance) &&
         (Math.abs(c.getBlue() - rgb.getBlue()) < tolerance));
   }
   
   private void handleMatch()
     throws InterruptedException, APIException
   {
     /*Rect cRec = window.getRect();
     int height = cRec.height;
     int width = cRec.width;*/
     long accepted = -1;
     //If the accepted mode is team builder
     boolean tb = false;
     long time;
     
     //If the play button is there, do not do anything
     boolean play_button = true;
     
     gui.setTitle("Waiting for match.");
     
     for (;;)
     {
       time = System.currentTimeMillis()/1000L;
       if(pretendAccepted == true) {
         pretendAccepted = false;
         accepted = time;        
       }
       if (!isInterrupted())
       {
         sleep(accepted>0 ? 20L : 600L);
         try
         {
           if (accepted>0)
           {   
             boolean lobby = false;
             if(checkPoint(window, PixelOffset.LobbyChat)
                && checkPoint(window, PixelOffset.LobbyChat2)
                //&& checkPoint(PixelOffset.Blind_SearchChampion, 1)
             )
             {
               System.out.println("Lobby detected. Picking champion and lane.");
               if(normal_lobby())
                 break;
             }
             //Here detect teambuilder lobby
             else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainIcon)) {
               System.out.println("Team builder lobby detected.");
               
               if(!isInterrupted())
                 //Function returns true if it sucessfully matched you into game
                 if(teamBuilder_lobby()) {
                   end();
                   break;
                 }
                 //Function returns false when you are kicked from the group
                 else {
                   accepted = -1;
                   gui.setTitle("Waiting for group.");
                 }
             }
             /*else {
               Rect rect = window.getRect();
               PixelOffset point = PixelOffset.TeamBuilder_CaptainIcon;
               Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
             
               System.out.println("new Color("+a.getRed()+", "+a.getGreen()+", "+a.getBlue()+", 1)");
               sleep(600L);
             }*/
             
             if(time-accepted>12) {
               System.out.println("Match was declined.");
               gui.setTitle("Waiting for match.");
               accepted = -1;
               tb = false;
             }
           }
           else
           {
             if (/*pixelCheckS(new Color(255, 255, 255), width * PixelOffset.MatchFound.x, height * PixelOffset.MatchFound.y, 1)*/
                 
                 checkPoint(window, PixelOffset.MatchFound)
                )
             {
               //SelectItem("accept");

               click(PixelOffset.AcceptButton);
               //this.gui.getProgressBar1().setValue(60);
               accepted = time;
               gui.setTitle("Match accepted, waiting for lobby.");
               tb = false;
               play_button = false;
             }
             else if(checkPoint(window, PixelOffset.TeamBuilder_AcceptGroup)) {
               click(PixelOffset.TeamBuilder_AcceptGroup);
               //this.gui.getProgressBar1().setValue(60);
               gui.setTitle("Group accepted, waiting for lobby.");
               System.out.println("Group accepted, waiting for lobby.");
               accepted = time;
               tb = true;
               play_button = false;
             }
             /*else if (pixelCheckS(new Color(255, 255, 255), width * 0.7361D, height * 0.91875D, 1))
             {
               accepted = -1;
               this.gui.getProgressBar1().setValue(40);
             }*/
             else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_Invited)) {
               
               if( teamBuilder_captain_lobby()) {
                 System.out.println("Game started as captain, the job is over.");
                 end();
                 break;
               }
               else {
                 System.out.println("Lobby failed, waiting for another game.");
               }
               
             }
             //If this is a lobby with invited players
             else if(checkPoint(window, PixelOffset.InviteChat) && checkPoint(window, PixelOffset.InviteStart)) {
               invite_lobby();
               gui.setTitle("Waiting for match.");
               play_button = false;
               
             }
             //If play button wasn't there and sudenly appeared, the program shall quit
             else if(checkPoint(window, PixelOffset.PlayButton_red) && !play_button) {
               System.out.println("The play button is red. Something must've gone wrong.");
               play_button = true;
               tb = false;
               gui.setTitle("Waiting for match.");
             }

             //Please kick me, I need to test something :)
             /*Color cCol = window.getColor((int)(width * PixelOffset.PlayButton.x), (int)(height * PixelOffset.PlayButton.y));
             if ((cCol.getRed() > 70) && (cCol.getGreen() < 10) && (cCol.getBlue() < 5) && (!isInterrupted()))
             {
               sleep(700L);
               
               cCol = window.getColor((int)(width * PixelOffset.PlayButton.x), (int)(height * PixelOffset.PlayButton.y));
               if ((cCol.getRed() > 70) && (cCol.getGreen() < 10) && (cCol.getBlue() < 5) && (!isInterrupted()))
               {
                 System.out.println("The play button is red. Something must've gone wrong. Aborting.");
                 interrupt();
                 break;
               }
             }*/
           }
         }
         catch (IllegalArgumentException fe)
         {
           fe.printStackTrace();
           //Run standard end actions
           end();
           interrupt();
         }
       }
     }

     if (!isInterrupted())
     {
       System.out.println("All done :)");
       
       interrupt();
     }
     else
     {
       System.out.println("Match handling interrupted.");
     }
   }
   public synchronized void simulateAccepted() {
     pretendAccepted = true;     
   }
   public boolean normal_lobby() throws InterruptedException, APIException {
     if(settings.getBoolean(Setnames.NOTIF_MENU_BLIND_IN_LOBBY.name, false))
       gui.notification(Notification.Def.BLIND_TEAM_JOINED);
     sleep(200L);
     System.out.println("In normal lobby.");
     //boolean ARAM = false;
     //this.gui.getProgressBar1().setValue(70);
     if(settings.getStringEquivalent(Setnames.BLIND_CALL_TEXT.name).length()>0) {
       //sleep(this.gui.getDelay());
       click(PixelOffset.LobbyChat);
       click(PixelOffset.LobbyChat);
       sleep(10L);
       click(PixelOffset.LobbyChat);
       System.out.println("Typping '"+settings.getString(Setnames.BLIND_CALL_TEXT.name)+"' in chat window.");
       window.typeString(settings.getString(Setnames.BLIND_CALL_TEXT.name));
       Enter();
       //if(true){ return; }
       //System.out.println(this.gui.chatTextField().getText());
       sleep(200L);
     }
     else
       System.out.println("No chat message to type, skipping this step.");
     //this.gui.getProgressBar1().setValue(85);

     if (settings.getStringEquivalent(Setnames.BLIND_CHAMP_NAME.name).length() > 1)
     {
       click(PixelOffset.Blind_SearchChampion);
       sleep(20L);
       window.typeString(settings.getStringEquivalent(Setnames.BLIND_CHAMP_NAME.name));
       sleep(200L);
       click(PixelOffset.LobbyChampionSlot1);
       sleep(10L);
       click(PixelOffset.LobbyChampionSlot1);
       sleep(100L);
       click(PixelOffset.LobbyChampionSlot1);
     }
     
     System.out.println("Setting summoner spells.");
     
     
     //Set summoner spells
     String[] spells = {
       (String)settings.getSetting(Setnames.BLIND_SUMMONER1.name),
       (String)settings.getSetting(Setnames.BLIND_SUMMONER2.name)
     };

     

     //Loop that just does the same thing for both spells
     Rect winRect = window.getRect();
     double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
     Rect cropRect = null;
     for(int i=0; i<2; i++) {
       SummonerSpell s = ConstData.lolData.getSummonerSpells().get(spells[i]);
       if(s!=null) {
         //Crop the icon - the GUI disorts the icon borders so I ignore them
         BufferedImage icon = LazyLoadedImage.crop(s.img.getScaledDiscardOriginal(48, 48), 5);
         if(icon!=null) {
           click(i==0?PixelOffset.Blind_SumSpell1:PixelOffset.Blind_SumSpell2);
           //Wait till the launcher screen redraws
           sleep(500L);
           
           //Calculate crop rectangle 
           if(cropRect==null)
             cropRect = ImageFrame.NormalLobby_SummonerSpellPopup.rect(window);
           //Use base resolution window - the icons are saved in base resolution too
           /*BufferedImage screenshot = ScreenWatcher.resampleImageTo(
                  window.screenshot(),
                  ConstData.smallestSize.width, ConstData.smallestSize.height);*/
           
           BufferedImage screenshot = ScreenWatcher.resampleImage(
                  window.screenshotCrop(cropRect),
                  winSizeCoef,winSizeCoef);
           //double[][][] integral_image = ScreenWatcher.integralImage(screenshot);
           //Some CV
           Rect pos = ScreenWatcher.findByAvgColor(icon, screenshot, 0.001f, true, null);
        
           if(pos!=null) {
             /*System.out.println("Original result: "+pos);
             screenshot = window.screenshot();
             DebugDrawing.drawResult(screenshot, cropRect, Color.RED);
             DebugDrawing.displayImage(screenshot, "Non-normalized");
             
             screenshot = ScreenWatcher.resampleImageTo(
                  window.screenshot(),
                  ConstData.smallestSize.width, ConstData.smallestSize.height);*/
             //Add the normalized top/left coordinates of the search rectangle we used
             Rect cropNormalized = ConstData.normalize(cropRect, winRect);
             pos = pos.move(cropNormalized.left, cropNormalized.top);
             /*System.out.println("Search region: "+cropNormalized);
             System.out.println("Moved result: "+pos);
             DebugDrawing.drawResult(screenshot, cropNormalized, Color.RED);
             DebugDrawing.drawResult(screenshot, pos, Color.GREEN);
             DebugDrawing.displayImage(screenshot, "Normalized");*/
             
             

             
             
//             System.out.println("Crop rect: "+cropRect+" and normalized: "+cropNormalized);
//             screenshot = window.screenshot();
//             DebugDrawing.drawResult(screenshot, pos, Color.RED, Color.YELLOW);
//             System.out.println("Moved result: "+pos);
//             DebugDrawing.displayImage(screenshot, "Moved result");
             
             //De normalize the rectangle (don't forget we rescaled the screenshot prior to 
             // searching the summoner spell)
             
             pos = ConstData.deNormalize(pos, winRect);
             
             /*screenshot = window.screenshot();
             DebugDrawing.drawResult(screenshot, pos, Color.RED, Color.YELLOW);
             System.out.println("Rescaled result: "+pos);
             DebugDrawing.displayImage(screenshot, "Rescaled result");

             //Show some debug
             screenshot = window.screenshot();
             DebugDrawing.drawResult(screenshot, pos, Color.RED);*/
             // Click in middle of button rather than the corner
             pos = pos.middle();
             
             /*DebugDrawing.drawPoint(screenshot, pos.left, pos.top, 5, Color.YELLOW);
             DebugDrawing.displayImage(screenshot);*/
             //Click in the middle of the found rectangle
             System.out.println("  Spell #"+(i+1)+" CLICKING: "+pos);
             window.mouseDown(pos.left, pos.top);
             sleep(30L);
             window.mouseUp(pos.left, pos.top);
             sleep(400L);
           }
           else {
             System.out.println("  Spell #"+(i+1)+" not seen on screen.");
             //DebugDrawing.displayImage(screenshot);
             click(PixelOffset.Blind_SumSpell_CloseDialog);
             sleep(80L);
           }
         }
         else {
           System.out.println("  Spell #"+(i+1)+" image corrupted.");
         }
       }
       else {
         System.out.println("  Spell #"+(i+1)+" is null."); 
       }
     }
     //Set masteries:
     int mastery = settings.getInt(Setnames.BLIND_MASTERY.name, 0);
     if(mastery>0) {
       System.out.println("  Setting mastery to mastery #"+mastery); 
       click(PixelOffset.Masteries_Edit);
       sleep(100);
       click(PixelOffset.Masteries_Big_First.offset(PixelOffset.Masteries_Big_Spaces.x*(mastery-1), 0));
       sleep(50);
       click(PixelOffset.Masteries_Big_Close);
     }
     //Set runes:
     int rune = settings.getInt(Setnames.BLIND_RUNE.name, 0);
     if(rune>0) {
       click(PixelOffset.Blind_Runes_Dropdown);
       sleep(700);
       click(PixelOffset.Blind_Runes_Dropdown_First.offset(0, PixelOffset.Blind_Runes_Dropdown_Spaces.y*(rune-1)));
     }
     System.out.println("NORMAL LOBBY: Waiting for a game to start.");
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
       PixelOffset.PlayButton_SearchingForGame_Approx,
       PixelOffset.PlayButton_cancel
     };
     while(true) {
       if(WindowTools.checkPoint(window, points)<3) {
         System.out.println("NORMAL LOBBY: lobby gone, waiting for the game.");
         //Internal loop will wait until lobby reappears or game starts
         //or main screen reappears
         do {
           //Wait 2 seconds, then check if back in main screen
           sleep(2000);
           if(WindowTools.checkPoint(window, failPoints)>1) {
             System.out.println("NORMAL LOBBY: Game did not start, waiting for another game."); 
             return false;
           }
           //Check if the game is running, return true if it does
           else if(CacheByTitle.initalInst.getWindow(ConstData.game_window_title)!=null) {
             System.out.println("NORMAL LOBBY: Game started.");
             return true;
           }
           /*BufferedImage img = window.screenshot();
           WindowTools.drawCheckPoint(img, points);
           WindowTools.drawCheckPoint(img, failPoints);
           DebugDrawing.displayImage(img);*/
           //else if(WindowTools.checkPoint(window, points)>=4) {
           //  System.out.println("NORMAL LOBBY: lobby back here, waiting for game again."); 
             //System.out.println("NORMAL LOBBY: Game did not start, waiting for another game."); 
             //return false;
           //}
         } while(WindowTools.checkPoint(window, points)<4);
         System.out.println("NORMAL LOBBY: lobby back here, waiting for game again."); 
       }
       sleep(800);
     }

   }
   
   public boolean teamBuilder_lobby() throws InterruptedException {
     gui.notification(Notification.Def.TB_GROUP_JOINED);
     System.out.println("In team builder lobby now.");
     //Check if the teambuilder is enabled
     if(!settings.getBoolean(Setnames.TEAMBUILDER_ENABLED.name, (boolean)Setnames.TEAMBUILDER_ENABLED.default_val)) {
       gui.setTitle("Team builder - actions are disabled");
       while(true) {
         if(!checkPoint(window, PixelOffset.TeamBuilder_CaptainIcon)) {
           System.out.println("The group was disbanded.");
           return false;
         }
         sleep(1000L);
       }
     }
     gui.setTitle("Waiting for ready button. (Team builder)");
     /*click(PixelOffset.TeamBuilder_Chat);
     if(settings.getStringEquivalent(Setnames.BLIND_CALL_TEXT.name).length()>0) {
       sleep(50L);
       window.typeString(settings.getStringEquivalent(Setnames.BLIND_CALL_TEXT.name));
       Enter();
     }
     sleep(50L);*/
     //Wait for ready button
     System.out.println("Waiting for ready button.");
     while(true) {
       if(!checkPoint(window, PixelOffset.TeamBuilder_CaptainIcon)) {
         System.out.println("The group was disbanded.");
         return false;
       }
       sleep(700L);
       //If ready button is available
       if(checkPoint(window, PixelOffset.TeamBuilder_Ready_Enabled)) {
         System.out.println("Clicking ready button!");
         WindowTools.click(window, PixelOffset.TeamBuilder_Ready);
       }
       //If ready button is selected
       else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainReady, PixelOffset.PlayButton_SearchingForGame_Approx)==2) {
         System.out.println("Searching for game!");
         gui.setTitle("Waiting for game. (Team builder)");
         //TODO: add a while that waits for game to make really sre a game will be joined
         while(checkPoint(window, PixelOffset.PlayButton_SearchingForGame_Approx)) {
           sleep(500L);
           /*click(PixelOffset.PlayButton_cancel);
           sleep(800L);
           return false;*/
           
           if(checkPoint(window, PixelOffset.TeamBuilder_MatchFound, PixelOffset.TeamBuilder_MatchFound2) == 2) {
             System.out.println("Match found!");
             return true;
           }
         }
         System.out.println("Game cancelled!");
       }
       else {
         //System.out.println("   ... still waiting.");
       }
     }
     /*if(true)
     return false;*/
     //click(PixelOffset.TeamBuilder_FindAnotherGroup);
   }
   public boolean teamBuilder_captain_lobby() throws InterruptedException {
     System.out.println("In team builder lobby as captain now.");
     gui.setTitle("Waiting for players. (Team builder)");
     //Player slots - initally 4 empty ones
     TeamBuilderPlayerSlot slots[] = {TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty};
     //Another array required for comparison and change detection
     TeamBuilderPlayerSlot oldslots[] = {TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty, TeamBuilderPlayerSlot.Empty};
     //Distance between player slots, vertically
     double offset = PixelOffset.TeamBuilder_CaptainLobby_slot_dist.y;
     
     //If all ready message was called, do not call it again (would be a lot of spam)
     boolean allReadyCalled = false;
     //If all ready notification has been issued
     boolean gameReadyNotified = false;
     
     byte old_ready = 0;
     byte old_joined = 0;
     //Wait for the slots to be filled
     while(true) {
       if(!checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_Invited)) {
         System.out.println("Lobby has been canceled.");
         return false;
       }
       sleep(1500L);

       //Check slot statuses
       for(int i=0; i<4; i++) {
         slots[i] = TeamBuilderPlayerSlot.Error;
         //Summoner spell - player is in - may need to be accepted
         if(checkPoint(window, (ComparablePixel)PixelOffset.TeamBuilder_CaptainLobby_slot_kickPlayer.offset(0, i*offset))) {           
           if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset))) {
             click(PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset));
             slots[i] = TeamBuilderPlayerSlot.Accepted;
             //Time penalty for clicking
             sleep(80L);
           }
           //WARNING - this match can be errorneous if previous match fails to match properly
           else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_slot_greenBorder.offset(0, i*offset))) {
             slots[i] = TeamBuilderPlayerSlot.Ready;
           }
           else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_slot_blueBorder.offset(0, i*offset))) {
             slots[i] = TeamBuilderPlayerSlot.Occupied; 
           }
           else {
             slots[i] = TeamBuilderPlayerSlot.ErrorPlayer; 
             System.out.println("Matching problems. Slot #"+(i+1));
             
             ColorPixel[] points = {
               PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset),
               PixelOffset.TeamBuilder_CaptainLobby_slot_greenBorder.offset(0, i*offset),
               PixelOffset.TeamBuilder_CaptainLobby_slot_blueBorder.offset(0, i*offset)
             };
             String[] names = {
               "TeamBuilder_CaptainLobby_slot_acceptPlayer",
               "TeamBuilder_CaptainLobby_slot_greenBorder",
               "TeamBuilder_CaptainLobby_slot_blueBorder"
             };
             for(byte ii=0; ii<points.length; ii++) {
               ColorPixel point = points[ii];
               System.out.println("    "+point.toString(names[ii]));
               try {
                 Rect rect = window.getRect();
                 Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
                 System.out.println("     - Real color: "+ColorPixel.ColorToSource(a));
               }
               catch(APIException e) {

               }
             }
           }
         }
         //No summoner spell = no player in lobby at this slot
         else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_slot_summonerSpell.offset(0, i*offset))) {
           slots[i] = TeamBuilderPlayerSlot.Empty;
         }
         //Green means the player is now joining
         else if(checkPoint(window, PixelOffset.TeamBuilder_CaptainLobby_slot_greenBorder.offset(0, i*offset))){
           slots[i] = TeamBuilderPlayerSlot.Accepted; 
         }

         
         /*if(slots[i] == TeamBuilderPlayerSlot.Error) {
           System.out.println("Matching problems. Slot #"+(i+1));
           System.out.println("    "+PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset).toString("TeamBuilder_CaptainLobby_slot_acceptPlayer"));

         }*/
       }
       //Now check player status and react to it
       byte ready = 0;
       byte joined = 0;
       System.out.println("Current slot status:");
       for(int i=0; i<4; i++) {
         if(slots[i].isJoined) {
           joined++; 
         }
         if(slots[i]==TeamBuilderPlayerSlot.Ready) {
           ready++; 
         }
         System.out.println("  "+(i+1)+" - "+slots[i]);
         //React to individual changes
         //Greet new players here
         if(!oldslots[i].isJoined && slots[i].isJoined) {
           if(!settings.getStringEquivalent("tb_cap_greet").isEmpty())
             teamBuilder_say(settings.getStringEquivalent("tb_cap_greet"));
           System.out.println("    A new player appeared in slot #"+(i+1));
           //System.out.println("Matchpoint: "+PixelOffset.TeamBuilder_CaptainLobby_slot_kickPlayer.offset(0, i*offset).toSource());
         }
         if(oldslots[i]!=TeamBuilderPlayerSlot.Accepted && slots[i]==TeamBuilderPlayerSlot.Accepted) {
           System.out.println("    A new player accepted #"+(i+1));
           //System.out.println("Matchpoint: "+PixelOffset.TeamBuilder_CaptainLobby_slot_kickPlayer.offset(0, i*offset).toSource());
         }

         //Update old slots to new slots here (it's probably shitty to update it before reading is finished)
         oldslots[i] = slots[i];
       }
       if(old_joined<joined) {
         gui.notification(Notification.Def.TB_PLAYER_JOINED);
       }
       //If all have joined and are ready, start the game
       if(ready==4) {
         if(!gameReadyNotified)
           gui.notification(Notification.Def.TB_GAME_CAN_START);
         if(settings.getBoolean(Setnames.TEAMBUILDER_AUTOSTART_ENABLED.name, false)) {
           System.out.println("Clicking play button!");
           click(PixelOffset.TeamBuilder_Ready);
         }
         //Wait for screen update
         sleep(500L);
         gameReadyNotified = true;
       }
       else if(joined==4) {
         if(!allReadyCalled && settings.getStringEquivalent("tb_cap_lock").length() > 0) {
           teamBuilder_say(settings.getStringEquivalent("tb_cap_lock"));
         }
         allReadyCalled = true;
         gameReadyNotified = false;
       }
       //Reset all ready message until somebody joins again
       else {
         allReadyCalled = false;
         gameReadyNotified = false;
       }
       
       //Test if game is being searched, in which case break this loop
       while(checkPoint(window, PixelOffset.PlayButton_SearchingForGame_Approx)) {
         sleep(500L);
         if(checkPoint(window, PixelOffset.TeamBuilder_MatchFound, PixelOffset.TeamBuilder_MatchFound2) == 2) {
           System.out.println("Match found!");
           return true;
         }
       }
       old_ready = ready;
       old_joined = joined;
     }
     
     /*if(true)
       return true;
     else
       return false;*/
   }
   public void invite_lobby() throws APIException, InterruptedException {
     //Handle disabled invite lobby
     if(!settings.getBoolean(Setnames.INVITE_ENABLED.name, (boolean)Setnames.INVITE_ENABLED.default_val)) {
       System.out.println("Invite lobby automation disabled, waiting.");
       gui.setTitle("Automation disabled (Invite)");
       while(checkPoint(window, PixelOffset.InviteChat, PixelOffset.InviteStart) == 2) {
         sleep(1000L);
       }
       return;
     }
     
     
     
     System.out.println("Inviting players now. ");
     gui.setTitle("Waiting for players. (Invite)");
     double[][][] integral_image;
     double[] accepted, pending;
     try {
       accepted = Images.INVITE_ACCEPTED_SMALL.getColorSum();
       pending = Images.INVITE_PENDING.getColorSum();
     }
     catch(IOException e) {
       System.err.println("Can't find required image! Invite lobby can't be automated!"); 
       settings.setSetting(Setnames.INVITE_ENABLED.name, false);
       return;
     }
     //Declare the two arrays of matches
     ArrayList<RectMatch> accepted_all, pending_all;
     
     //Calculate the region where to search the player list
     Rect player_list = ImageFrame.Invite_InvitedPlayerList.rect(window);
     
     while(checkPoint(window, PixelOffset.InviteChat, PixelOffset.InviteStart)==2) {

       
       //System.out.println("Taking screenshot from window.");
       BufferedImage screenshot = window.screenshotCrop(player_list);
       integral_image = ScreenWatcher.integralImage(screenshot);
       //System.out.println("Analysing the screenshot.");
       System.out.println("  Invited players: ");
       

       pending_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    pending.clone(),
                    integral_image,
                    Images.INVITE_PENDING.getWidth(),
                    Images.INVITE_PENDING.getHeight(),
                    0.003f);
       System.out.println("    Pending: "+pending_all.size());
       
       accepted_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    accepted.clone(),
                    integral_image,
                    Images.INVITE_ACCEPTED_SMALL.getWidth(),
                    Images.INVITE_ACCEPTED_SMALL.getHeight(),
                    //This is maximum safe tolerance before "Owner" gets matched too
                    0.0012f);
       System.out.println("    Accepted: "+accepted_all.size());
       
       
       /*DebugDrawing.drawPointOrRect(screenshot, Color.yellow, pending_all);
       DebugDrawing.drawPointOrRect(screenshot, Color.green, accepted_all);
       DebugDrawing.displayImage(screenshot);*/
       //Only start if all players accepted or declined and at least one accepted
       if(accepted_all.size()>0 && pending_all.isEmpty()) {
         System.out.println("All players have been invited and are in lobby. Time to start!");
         gui.setTitle("Waiting for match.");
         click(PixelOffset.InviteStart);
         return;
       }
       //System.out.println("Next test in 2 seconds.");
       sleep(2000L);
       //System.out.println("Timeout over, next test?");
     }
     System.out.println("Lobby has exit spontaneously.");
   }
   private void teamBuilder_say(String message) throws InterruptedException {
     click(PixelOffset.TeamBuilder_Chat);
     sleep(50L);
     window.typeString(message);
     Enter();
   }
   
   private void Enter()
   {
     this.window.keyDown(13);
     this.window.keyUp(13);
   }
   private void click(PixelOffset pos) {
     try {
       Rect rect = window.getRect();
       window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
     }
     catch(APIException e) {
       System.err.println("Can't click because no window is available for clicking :("); 
     }
   }
   private void click(ColorPixel pos) {
     try {
       Rect rect = window.getRect();
       window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
     }
     catch(APIException e) {
       System.err.println("Can't click because no window is available for clicking :("); 
     }
   }
   /**
    * Clicks at the top left corner of the rectangle. Use Rect.middle() to click in the middle.
    * @param pos rectangle to click on.
    */
   private void click(Rect pos) {
     try {
       Rect rect = window.getRect();
       window.click((int)(rect.width * pos.left), (int)(rect.height * pos.top));
     }
     catch(APIException e) {
       System.err.println("Can't click because no window is available for clicking :("); 
     }
   }
 }