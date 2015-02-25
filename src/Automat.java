import PVP_net.Images;
import PVP_net.TeamBuilderPlayerSlot;
import autoclick.ColorPixel;
 import java.awt.Color;
 import autoclick.Window;
 import autoclick.MSWindow;
 import autoclick.Rect;
import autoclick.comvis.ScreenWatcher;
import autoclick.computervision.RectMatch;
 import autoclick.exceptions.APIError;
import java.io.IOException;
import java.util.ArrayList;
 
 
 public class Automat
   extends Thread
 {
   Window window;
   //User32 user32 = this.window.getUser32();
   //Kernel32 kernel = this.window.getKernel32();
   Gui gui;
   
   public Automat(Gui acgui)
   {
     this.gui = acgui;
   }
   
   @Override
   public void run()
   {
     System.out.println("Auto call thread started!");
     
     window = MSWindow.windowFromName("PVP.net", false);
     if(window==null) {
       System.err.println("No PVP.net window found!");
       end();
       return;
     }
     System.out.println("PVP.net window available.");
     //long cID = this.window.FindWindow("PVP");
     this.gui.getProgressBar1().setValue(0);
     
     try
     {
       if (this.gui.getSelectedMode().length > 1) {
           StartMode(this.gui.getSelectedMode());
       }
       else {
           StartMode(null);
       }
     }
     catch (InterruptedException e)
     {
       System.out.println(e);
       end();
     }
     catch (APIError e) {
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
       gui.setToggleButton1State(false);
       gui.getProgressBar1().setValue(0);
       gui.setTitle("Idle...");
   }
   
   private void StartMode(String[] mode)
     throws InterruptedException, APIError
   {
     //This IF triggered even what the game wasn't running
     // well, it was running but just as a process on background...
     /*if (this.window.isRunning("League of Legends.exe")) {
       System.out.println("The game is running, the thread must stop now!");
       interrupt();
       return;
     }*/
     this.gui.getProgressBar1().setValue(5);
     
     
     
     if (mode!=null && ((mode[(mode.length - 1)].equals("Beginner")) || (mode[(mode.length - 1)].equals("Intermediate")) || (mode[(mode.length - 1)].equals("ARAM")) || (mode[(mode.length - 1)].equals("Draft")) || (mode[(mode.length - 1)].equals("Blind"))) && (!isInterrupted()))
     {
       Rect cRec = window.getRect();
       int height = cRec.bottom - cRec.top;
       int width = cRec.right - cRec.left;
       switch (mode[(mode.length - 1)])
       {
       case "ARAM": 
         SelectItem("home");
         SelectItem("play");
         SelectItem("pvp");
         SelectItem("aram");
         SelectItem("5v5");
         SelectItem("blind");
         SelectItem("match");
         break;
       case "Beginner": 
         switch (mode[(mode.length - 2)])
         {
         case "5v5": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("classic");
           SelectItem("5v5");
           SelectItem("blind");
           SelectItem("match");
           break;
         case "3v3": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("classic");
           SelectItem("3v3");
           SelectItem("blind");
           SelectItem("match");
           break;
         case "Dominion": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("dominion");
           SelectItem("5v5");
           SelectItem("blind");
           SelectItem("match");
         }
         break;
       case "Intermediate": 
         switch (mode[(mode.length - 2)])
         {
         case "5v5": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("classic");
           SelectItem("5v5");
           SelectItem("intermediate");
           SelectItem("match");
           break;
         case "3v3": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("classic");
           SelectItem("3v3");
           SelectItem("intermediate");
           SelectItem("match");
           break;
         case "Dominion": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("ai");
           SelectItem("dominion");
           SelectItem("5v5");
           SelectItem("intermediate");
           SelectItem("match");
         }
         break;
       case "Draft": 
         switch (mode[(mode.length - 2)])
         {
         case "5v5": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("pvp");
           SelectItem("classic");
           SelectItem("5v5");
           SelectItem("draft");
           SelectItem("match");
           break;
         case "Dominion": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("pvp");
           SelectItem("dominion");
           SelectItem("5v5");
           SelectItem("draft");
           SelectItem("match");
         }
         break;
       case "Blind": 
         switch (mode[(mode.length - 2)])
         {
         case "5v5": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("pvp");
           SelectItem("classic");
           SelectItem("5v5");
           SelectItem("blind");
           SelectItem("match");
           break;
         case "3v3": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("pvp");
           SelectItem("classic");
           SelectItem("3v3");
           SelectItem("blind");
           SelectItem("match");
           break;
         case "Dominion": 
           SelectItem("home");
           SelectItem("play");
           SelectItem("pvp");
           SelectItem("dominion");
           SelectItem("5v5");
           SelectItem("blind");
           SelectItem("match");
         }
         break;
       }
     }
     else if(mode==null) {
       System.out.println("Mode was null. I assume user has started game manually.");   
     }
     this.gui.getProgressBar1().setValue(30);
     if (interrupted()) {
       System.out.println("Interupted after picking mode!");
     }
     else {
       handleMatch();
     }
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
     throws InterruptedException, APIError
   {
     Rect cRec = window.getRect();
     int height = cRec.height;
     int width = cRec.width;
     long accepted = -1;
     //If the accepted mode is team builder
     boolean tb = false;
     long time = 0;
     
     //If the play button is there, do not do anything
     boolean play_button = true;
     
     gui.setTitle("Waiting for match.");
     
     for (;;)
     {
       time = System.currentTimeMillis()/1000L;
       if (!isInterrupted())
       {
         sleep(accepted>0 ? 100L : 600L);
         try
         {
           if (accepted>0)
           {   
             boolean lobby = false;
             if(checkPoint(PixelOffset.LobbyChat, 1)
                && checkPoint(PixelOffset.LobbyChat2, 1)
                && checkPoint(PixelOffset.Blind_SearchChampion, 1)
             )
             {
               System.out.println("Lobby detected. Picking champion and lane.");
               normal_lobby();
               break;
             }
             //Here detect teambuilder lobby
             else if(checkPoint(PixelOffset.TeamBuilder_CaptainIcon, 5)) {
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
                 
                 checkPoint(PixelOffset.MatchFound, 1)
                )
             {
               //SelectItem("accept");

               click(PixelOffset.AcceptButton);
               this.gui.getProgressBar1().setValue(60);
               accepted = time;
               gui.setTitle("Match accepted, waiting for lobby.");
               tb = false;
               
             }
             else if(checkPoint(PixelOffset.TeamBuilder_AcceptGroup,25)) {
               click(PixelOffset.TeamBuilder_AcceptGroup);
               this.gui.getProgressBar1().setValue(60);
               gui.setTitle("Group accepted, waiting for lobby.");
               System.out.println("Group accepted, waiting for lobby.");
               accepted = time;
               tb = true;
             }
             /*else if (pixelCheckS(new Color(255, 255, 255), width * 0.7361D, height * 0.91875D, 1))
             {
               accepted = -1;
               this.gui.getProgressBar1().setValue(40);
             }*/
             else if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_Invited, 23)) {
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
             else if(checkPoint(PixelOffset.InviteChat, 1) && checkPoint(PixelOffset.InviteStart, 8)) {
               invite_lobby();
               
             }
             //If play button wasn't there and sudenly appeared, the program shall quit
             else if(checkPoint(PixelOffset.PlayButton_red,15) && !play_button) {
               System.out.println("The play button is red. Something must've gone wrong. Aborting.");
               end();
               break;
             }
             else {
               play_button = false;
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
   public void normal_lobby() throws InterruptedException, APIError {
     this.gui.getProgressBar1().setValue(70);
     if(this.gui.chatTextField().getText().length()>0) {
       sleep(this.gui.getDelay());
       click(PixelOffset.LobbyChat);
       click(PixelOffset.LobbyChat);
       sleep(10L);
       click(PixelOffset.LobbyChat);
       System.out.println("Typping '"+gui.chatTextField().getText()+"' in chat window.");
       window.typeString(this.gui.chatTextField().getText());
       Enter();
       //if(true){ return; }
       //System.out.println(this.gui.chatTextField().getText());
       sleep(200L);
     }
     else
       System.out.println("No chat message to type, skipping this step.");
     this.gui.getProgressBar1().setValue(85);

     if (this.gui.getChampField().getText().length() > 1)
     {
       click(PixelOffset.Blind_SearchChampion);
       sleep(20L);
       window.typeString(this.gui.getChampField().getText());
       sleep(200L);
       click(PixelOffset.LobbyChampionSlot1);

     }
   }
   
   public boolean teamBuilder_lobby() throws InterruptedException {
     System.out.println("In team builder lobby now.");
     gui.setTitle("Waiting for ready button. (Team builder)");
     
     click(PixelOffset.TeamBuilder_Chat);
     if(this.gui.chatTextField().getText().length()>0) {
       sleep(50L);
       window.typeString(this.gui.chatTextField().getText());
       Enter();
     }
     sleep(50L);
     //Wait for ready button
     System.out.println("Waiting for ready button.");
     while(true) {
       if(!checkPoint(PixelOffset.TeamBuilder_CaptainIcon, 11)) {
         System.out.println("The group was disbanded.");
         return false;
       }
       sleep(700L);
       //If ready button is available
       if(checkPoint(PixelOffset.TeamBuilder_Ready_Enabled, 5)) {
         System.out.println("Clicking ready button!");
         click(PixelOffset.TeamBuilder_Ready);
       }
       //If ready button is selected
       else if(checkPoint(PixelOffset.TeamBuilder_CaptainReady, 5) && checkPoint(PixelOffset.PlayButton_SearchingForGame_Approx, 10)) {
         System.out.println("Searching for game!");
         gui.setTitle("Waiting for game. (Team builder)");
         //TODO: add a while that waits for game to make really sre a game will be joined
         while(checkPoint(PixelOffset.PlayButton_SearchingForGame_Approx, 8)) {
           sleep(500L);
           /*click(PixelOffset.PlayButton_cancel);
           sleep(800L);
           return false*/;
           
           if(checkPoint(PixelOffset.TeamBuilder_MatchFound, 2) && checkPoint(PixelOffset.TeamBuilder_MatchFound2, 2)) {
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
     //Wait for the slots to be filled
     while(true) {
       if(!checkPoint(PixelOffset.TeamBuilder_CaptainLobby_Invited, 23)) {
         System.out.println("Lobby has been canceled.");
         return false;
       }
       sleep(1500L);

       //Check slot statuses
       for(int i=0; i<4; i++) {
         slots[i] = TeamBuilderPlayerSlot.Error;
         //Summoner spell - player is in - may need to be accepted
         if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_kickPlayer.offset(0, i*offset), 20)) {           
           if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset), 60)) {
             click(PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset));
             slots[i] = TeamBuilderPlayerSlot.Accepted;
             //Time penalty for clicking
             sleep(80L);
           }
           //WARNING - this match can be errorneous if previous match fails to match properly
           else if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_greenBorder.offset(0, i*offset), 25)) {
             slots[i] = TeamBuilderPlayerSlot.Ready;
           }
           else if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_blueBorder.offset(0, i*offset), 7)) {
             slots[i] = TeamBuilderPlayerSlot.Occupied; 
           }
           else {
             slots[i] = TeamBuilderPlayerSlot.ErrorPlayer; 
             System.out.println("Matching problems. Slot #"+(i+1));
             ColorPixel point = PixelOffset.TeamBuilder_CaptainLobby_slot_acceptPlayer.offset(0, i*offset);
             System.out.println("    "+point.toString("TeamBuilder_CaptainLobby_slot_acceptPlayer"));
             try {
               Rect rect = window.getRect();
               Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
               System.out.println("    Expected: "+ColorPixel.ColorToSource(a));
             }
             catch(APIError e) {
               
             }
             
           }
         }
         //No summoner spell = no player in lobby at this slot
         else if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_summonerSpell.offset(0, i*offset), 12)) {
           slots[i] = TeamBuilderPlayerSlot.Empty;
         }
         //Green means the player is now joining
         else if(checkPoint(PixelOffset.TeamBuilder_CaptainLobby_slot_greenBorder.offset(0, i*offset), 30)){
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
           if(this.gui.chatTextField().getText().length()>0)
             teamBuilder_say(this.gui.chatTextField().getText());
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
       //If all have joined and are ready, start the game
       if(ready==4) {
         System.out.println("Clicking play button!");
         click(PixelOffset.TeamBuilder_Ready);
         //Wait for screen update
         sleep(500L);
       }
       else if(joined==4) {
         if(this.gui.getChampField().getText().length() > 0) {
           teamBuilder_say(this.gui.getChampField().getText());
         }
         allReadyCalled = true;
       }
       //Reset all ready message until somebody joins again
       else {
         allReadyCalled = false;
       }
       
       //Test if game is being searched, in which case break this loop
       while(checkPoint(PixelOffset.PlayButton_SearchingForGame_Approx, 8)) {
         sleep(500L);
         if(checkPoint(PixelOffset.TeamBuilder_MatchFound, 2) && checkPoint(PixelOffset.TeamBuilder_MatchFound2, 2)) {
           System.out.println("Match found!");
           return true;
         }
       }
     }
     
     /*if(true)
       return true;
     else
       return false;*/
   }
   public void invite_lobby() throws APIError, InterruptedException {
     System.out.println("Inviting players now. ");
     gui.setTitle("Waiting for players. (Invite)");
     double[][][] integral_image = null;
     double[] accepted, pending;
     try {
       accepted = Images.INVITE_ACCEPTED.getColorSum();
       pending = Images.INVITE_PENDING.getColorSum();
     }
     catch(IOException e) {
       System.err.println("Can't find required image! Invite lobby can't be automated!"); 
       return;
     }
     
     while(checkPoint(PixelOffset.InviteChat, 1) && checkPoint(PixelOffset.InviteStart, 8)) {
       integral_image = ScreenWatcher.integralImage(window.screenshot());
       ArrayList<RectMatch> accepted_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    accepted,
                    integral_image,
                    Images.INVITE_ACCEPTED.getWidth(),
                    Images.INVITE_ACCEPTED.getHeight(),
                    0.00009f);
       ArrayList<RectMatch> pending_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    pending,
                    integral_image,
                    Images.INVITE_PENDING.getWidth(),
                    Images.INVITE_PENDING.getHeight(),
                    0.00009f);
       //Only start if all players accepted or declined and at least one accepted
       if(accepted_all.size()>0 && pending_all.isEmpty()) {
         System.out.println("All players have been invited and are in lobby. Time to start!");
         gui.setTitle("Game started!");
         click(PixelOffset.InviteStart);
         break;
       }
       sleep(1200L);
     }
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
   
   private void SelectItem(String item)
     throws InterruptedException,APIError
   {
     Rect cRec = window.getRect();
     double height = cRec.width;
     double width = cRec.height;
     this.gui.getProgressBar1().setValue(this.gui.getProgressBar1().getValue() + 3);
     switch (item)
     {
     case "chat": 
       click(PixelOffset.LobbyChat);
       break;
     case "search": 
       this.click(PixelOffset.Blind_SearchChampion);
       break;
     case "slot1": 
       window.click((int)(width * 0.253125D), (int)(height * 0.2575D));
       break;
     case "play": 
       this.window.click((int)(width * 0.5D), (int)(height * 0.051562D));
       break;
     case "match": 
       this.window.click((int)(width * 0.592773D), (int)(height * 0.8875D));
       break;
     case "pvp": 
       this.window.click((int)(width * 0.2568D), (int)(height * 0.15D));
       break;
     case "ai": 
       this.window.click((int)(width * 0.2568D), (int)(height * 0.2175D));
       break;
     case "classic": 
       this.window.click((int)(width * 0.373047D), (int)(height * 0.18125D));
       break;
     case "home": 
       this.window.click((int)(width * 0.067383D), (int)(height * 0.051562D));
       break;
     case "aram": 
       this.window.click((int)(width * 0.373047D), (int)(height * 0.26875D));
       break;
     case "dominion": 
       this.window.click((int)(width * 0.373047D), (int)(height * 0.225D));
       break;
     case "accept": 
       this.click(PixelOffset.AcceptButton);
       break;
     case "again": 
       this.window.click((int)(width * 0.875D), (int)(height * 0.92D));
       break;
     case "3v3": 
       this.window.click((int)(width * 0.539062D), (int)(height * 0.25125D));
       break;
     case "5v5": 
       this.window.click((int)(width * 0.5625D), (int)(height * 0.195312D));
       break;
     case "blind": 
       this.window.click((int)(width * 0.725586D), (int)(height * 0.2525D));
       break;
     case "intermediate": 
       this.window.click((int)(width * 0.725586D), (int)(height * 0.22375D));
       break;
     case "draft": 
       this.window.click((int)(width * 0.725586D), (int)(height * 0.3025D));
       break;
     }
     sleep(75L);
   }
   private void click(PixelOffset pos) {
     try {
       Rect rect = window.getRect();
       window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
     }
     catch(APIError e) {
       System.err.println("Can't click because no window is available for clicking :("); 
     }
   }
   private void click(ColorPixel pos) {
     try {
       Rect rect = window.getRect();
       window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
     }
     catch(APIError e) {
       System.err.println("Can't click because no window is available for clicking :("); 
     }
   }
   private boolean checkPoint(PixelOffset point) {
     if(point.color==null)
       return false;
     try {
       Rect rect = window.getRect();
       return point.color.equals(window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y)));
     }
     catch(APIError e) {
       System.err.println("Can't click because no window is available for clicking :("); 
       return false;
     }
   }
   private boolean checkPoint(PixelOffset point, int tolerance) {
     return checkPoint(point, tolerance, null);
   }
   private boolean checkPoint(PixelOffset point, int tolerance, String debug) {
     if(point.color==null)
       return false;
     try {
       Rect rect = window.getRect();
       Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
       Color b = point.color;
       if(debug!=null) {
         System.out.println("DEBUG#"+debug+" checkPoint("+point.toSource()+"), "+tolerance+")");
         System.out.println("   Comparing to: "+a);
         System.out.println("    R: "+Math.abs(a.getRed() - b.getRed())+" => "+(Math.abs(a.getRed() - b.getRed()) < tolerance));
         System.out.println("    G: "+Math.abs(a.getGreen() - b.getGreen())+" => "+(Math.abs(a.getGreen() - b.getGreen()) < tolerance));
         System.out.println("    B: "+Math.abs(a.getBlue() - b.getBlue())+" => "+(Math.abs(a.getBlue() - b.getBlue()) < tolerance));
       }
       return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
              (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
              (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
     }
     catch(APIError e) {
       System.err.println("Can't click because no window is available for clicking :("); 
       return false;
     }
   }
   private boolean checkPoint(ColorPixel point, int tolerance) {
     if(point.color==null)
       return false;
     try {
       Rect rect = window.getRect();
       Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
       Color b = point.color;

       return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
              (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
              (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
     }
     catch(APIError e) {
       System.err.println("Can't click because no window is available for clicking :("); 
       return false;
     }
   }
 }