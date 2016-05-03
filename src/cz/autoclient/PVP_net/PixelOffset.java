package cz.autoclient.PVP_net;


import cz.autoclient.autoclick.ColorPixel;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub
 */
public enum PixelOffset implements ComparablePixel {
    /** AFTERMATCH **/
    //Spell icon
    AM_SUMMONER_SPELL_COLUMN (0.35283473610583094D, 0.4120435489306124D, new Color(254, 244, 51, 1), 80),
    AM_MINION_ICON (0.6334241128680451D, 0.41037795782770337D, new Color(178, 57, 210, 1), 30),
    AM_PLAY_AGAIN (0.8717328986112957D, 0.9039077309871311D, new Color(45, 101, 170, 1), 25),
    AM_CHAT_FIELD (0.7429692805080877D, 0.8178090789717791D, new Color(255, 255, 255, 1), 3),
    AM_SEND_CHAT (0.9687860734502807D, 0.814734127114088D, new Color(35, 74, 122, 1)),
    AM_HOME (0.7151025273364979D, 0.8746956883390652D, new Color(35, 78, 128, 1)),
    
    /** MAIN SCREEN **/
    //PlayButton_red_old(0.5282632272641906D, 0.05335365853658536D, new Color(216, 38, 20, 1)),
    PlayButton_red (0.5277226353206357D, 0.053811657509594914D, new Color(213, 36, 20, 1), 15),
    //The small cross button to stop searching for game
    PlayButton_cancel(0.5603940011080168D, 0.06611146494035947D, new Color(202, 160, 98, 1), 24),
    /**
     * @deprecated No longer used because the text is now aligned in the middle so it moves
     */
    @Deprecated
    PlayButton_SearchingForGame_Approx (0.42531457289624774D, 0.06710726787193565D, new Color(255, 255, 255, 1), 10),
    // Top right corner of the box with searching game indicator
    PlayButton_SearchingCorner (0.5700032263395995D, 0.009224855573073414D, new Color(0, 64, 77, 1), 5),
    StoreButton (0.7179852949059728D, 0.05839416058394161D, new Color(189, 97, 0, 1), 10),
    /** GAME SELECTION **/
    //The button you can press after game mode has been selected
    Play_Solo(0.5306054028901105D, 0.8869954957698297D, new Color(200, 81, 0, 1)),
    
    /** BLIND PICK LOBBY**/
    LobbyChat(0.495117D, 0.91875D, new Color(255, 255, 255, 1), 1),
    LobbyChat2(0.5387487383572218D, 0.9184036396562681D, new Color(255, 255, 255, 1)),
    LobbyChatBlueTopFrame(0.5190743326122113D, 0.7101857639525893D,new Color(28, 51, 81, 1),5),
    //Quit button for lobby (not allways available)s
    LobbyQuit(0.11646860979241919D, 0.9207317073170731D),
    LobbyChampionSlot1(0.25182702572064036D, 0.26219512195121947D),
    
    LobbyTopBar (0.70837606967439D, 0.08148622422881514D, new Color(33, 33, 33, 1), 3),
    //White separator line in the lobby champion skill details
    LobbyHoverchampSeparator (0.49889495962588765D, 0.8362587901179259D, new Color(204, 204, 204, 1)),
    LobbyHoverchampTop (0.4844811217785136D, 0.739397806600655D,new Color(49, 49, 49, 1)),
    //Orange text "Spells"
    //LobbySummonerSpellsHeader(0.4441223758058664D, 0.590262641502635D, new Color(251, 150, 1, 1),50),
    //Green checkmark seen above the rune selectbox
    LobbyRunesCheckmark (0.3441864333974065D, 0.6302370156526198D,new Color(0, 135, 55, 1),10),

    //Button for editing masteries in lobby
    Masteries_Edit(0.38550610189321205D, 0.6609865342295311D, new Color(34, 75, 125, 1),10),
    //The first mastery tab offset
    Masteries_Big_First (0.29229661714686006D, 0.22546405904893319D),
    //Spaces between mastery tabs
    Masteries_Big_Spaces(0.0258D, 0D),
    //Close button for the mastery editor
    Masteries_Big_Close (0.9115D, 0.219D, new Color(225, 203, 131, 1), 46),
    //Summoner spell icons
    Blind_SumSpell1 (0.46045805869955697D, 0.6502242027276122D),
    
    Blind_SumSpell2 (0.5046604947648372D, 0.6532991545853033D),
    //Fortunatelly, this one works for both spell 1 and spell 2 dialogs
    Blind_SumSpell_CloseDialog (0.6228539651133043D, 0.2520179371566098D),
    //Open dropdown menu with runes
    Blind_Runes_Dropdown (0.35860027124478056D, 0.6240871119372374D),
    //First item in the rune dropdown menu
    Blind_Runes_Dropdown_First (0.25385971622052933D, 0.6502242027276122D),
    Blind_Runes_Dropdown_Spaces (0D, 0.0275D),
    Blind_SearchChampion(0.779484336388102D, 0.16596235113330773D, new Color(255, 255, 255, 1)),
    /** ARAM **/
    ARAM_REROLL_BLUE (0.6689782462249011D, 0.6440742990122299D, new Color(37, 81, 134, 1), 10),
    ARAM_REROLL_WHITE (0.690118541734383D, 0.6655989620160678D, new Color(255, 255, 255, 1), 10),
    ARAM_REROLL_GRAY (0.665134556132268D, 0.647149250869921D, new Color(127, 127, 127, 1), 10),
    // Player list in norma lobby
    Lobby_Player1 (0.015550930477278908D, 0.0845611760865062D),
    Lobby_Player2 (0.015550930477278908D, 0.2105060870777794D),
    Lobby_Player3 (0.015550930477278908D, 0.3350416373142705D),
    Lobby_Player4 (0.015550930477278908D, 0.4611146634796071D),
    Lobby_Player5 (0.015550930477278908D, 0.5842408529613162D),
    Lobby_Player_Box_Size (0.1623959064137473D, 0.10762331501918981D),
    
    MatchFound(0.495117D, 0.515625D, new Color(255, 255, 255),1),
    AcceptButton(0.39004512649241546D, 0.5599898226129755D, new Color(25, 60, 101, 1)),
   
    
    TeamBuilder_AcceptGroup  (0.36240150633806045D, 0.5802060676136939D, new Color(43, 85, 137, 1),25),
    TeamBuilder_CaptainIcon (0.5120583464840514D, 0.17717336270716075D, new Color(56, 97, 99, 1), 11),
    //The green arrow after the Invited heading on the right
    TeamBuilder_CaptainLobby_Invited (0.9524498123913625D, 0.23029994201505188D, new Color(74, 221, 26, 1),23),
    //Vertical distance between slots
    TeamBuilder_CaptainLobby_slot_dist(-1D, 0.11591255359697972D),
    //Top left corner of the first team-builder player slot
    TeamBuilder_CaptainLobby_slot (0.026865151361061504D, 0.4135739654378432D),
    //The first summoner spell location. If this matches, there is NO SUMMONER SPELL => no player
    TeamBuilder_CaptainLobby_slot_summonerSpell (0.09359113104398743D, 0.47763090295196353D, new Color(3, 20, 24, 1), 12),
    //The green button for accepting player
    TeamBuilder_CaptainLobby_slot_acceptPlayer (0.48441472632969634D, 0.4472D, new Color(29, 164, 53, 1), 60),
    //Right blue shining border - when player is missing or non-ready
    TeamBuilder_CaptainLobby_slot_blueBorder (0.5469410857838011D, 0.46111466347960717D, new Color(16, 33, 68, 1), 7),
    //Right blue shining border - when player is waiting for accept, joining or ready - tolerance 30
    TeamBuilder_CaptainLobby_slot_greenBorder(0.5479020083069593D, 0.46124277865367064D, new Color(88, 177, 167, 1), 33),
    //Marks that the player is ready to play
    TeamBuilder_CaptainLobby_slot_playerReady (0.3489910460131979D, 0.46730888061534553D, new Color(5, 80, 68, 1)),
    //This matches both Kick and no-accept buttons with >10 tolerationnew 
    TeamBuilder_CaptainLobby_slot_kickPlayer (0.5023969319271333D, 0.4607308854697224D, new Color(121, 16, 16, 1), 20),
    //XXXX (0.09359113104398743D, 0.47763090295196353D+0.5294865190348229D-0.4135739654378432D, new Color(3, 23, 27, 1)),
    TeamBuilder_CaptainReady (0.34619662555792124D, 0.17844432593219253D, new Color(191, 255, 248, 1), 5),
    TeamBuilder_Chat (0.7713364389662778D, 0.8840874231308464D),
    TeamBuilder_FindAnotherGroup (0.12409443604189642D, 0.9008642400988305D, new Color(45, 88, 142, 1)),
    TeamBuilder_Ready (0.40529677899136995D, 0.8833248405413927D),
    TeamBuilder_Ready_Enabled (0.5196841727335286D, 0.874173849467947D, new Color(42, 97, 100, 1),5),
    TeamBuilder_Ready_Pressed (0.5196841727335286D, 0.874173849467947D, new Color(14, 42, 43, 1)),
    TeamBuilder_MatchFound(0.4996663788286509D, 0.49961873194195505D, new Color(255, 255, 255, 1), 2),
    TeamBuilder_MatchFound2(0.5072922050781281D, 0.5353329777965776D, new Color(255, 255, 255, 1), 2),
    //Chat betweeen the players invited in a game
    InviteChat (0.4950512695332547D, 0.9208058039244397D, new Color(255, 255, 255, 1), 1),
    //Start button for a game with invited players
    InviteStart (0.7237508300449225D, 0.9146547965815433D, new Color(204, 110, 22, 1),8),
    //Patcher's orange launch button
    Patcher_Launch (0.49697311457957116D, 0.06457398901151389D,new Color(138, 41, 8, 1)),
    Patcher_SetServer (0.9572550031723815D, 0.08763612794419742D),
    Patcher_Eula_Button (0.36244396133741363D, 0.8454836456909994D),
    Patcher_Eula_Heading (0.5046604947648373D, 0.08148622422881516D, new Color(24, 117, 143, 1),5),
    Patcher_Eula_BlackBottom (0.4993459613385627D, 0.8505740227715557D, new Color(0, 0, 0, 1), 2),
    
    Login_UsernameField (0.30478860994791757D, 0.3982062655710023D, new Color(242, 243, 243, 1), 4),
    Login_PasswordField (0.10107303503836476D, 0.47648942276806283D, new Color(242, 243, 243, 1), 4),
    Login_ButtonDisabled (0.28557015948475223D, 0.5254193957017744D, new Color(140, 140, 140, 1), 5)
    ;
    
    
    
    public final double x;
    public final double y;
    public final Color color;
    public final int tolerance;
    PixelOffset(double x, double y) {
      this(x,y,null);
    }
    PixelOffset(double x, double y, Color color) {
      this(x,y,color, 1);
    }
    PixelOffset(double x, double y, Color color, int tolerance) {
      this.x = x;
      this.y = y;
      this.color = color;
      this.tolerance = tolerance;
    }
    @Override
    public ColorPixel offset(double ox, double oy) {
      return new ColorPixel(x+ox, y+oy, color, tolerance);
    }
    
    public double realX(double width) {
        return width*x;   
    }
    public double realY(double height) {
        return height*y;   
    }
    @Override
    public String toSource() {
      return this.name()+"("
                         +x+"D, "
                         +y+"D"
                         +(color!=null?", new Color("
                           +color.getRed()+", "
                           +color.getGreen()+", "
                           +color.getBlue()+", "
                           +color.getAlpha()
                         +")":"")+")";
    }
  @Override
  public Rect toRect(Rect win_dimensions) {
    return new Rect((int)(x*(double)win_dimensions.width), (int)(y*(double)win_dimensions.height));
  }
  @Override
  public Rect toRect(int width, int height) {
    return new Rect((int)(x*(double)width), (int)(y*(double)height));
  }
  

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public int getTolerance() {
    return tolerance;
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public double getY() {
    return y;
  }

  @Override
  public double distanceSq(Rect r) {
    return (r.left-this.x)*(r.left-this.x)+(r.top-this.y)*(r.top-this.y);
  }
  
  @Override
  public double distance(Rect r) {
    return Math.sqrt(distanceSq(r));
  }
}
