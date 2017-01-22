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
    StoreButton (0.6709000912712176D, 0.059961561224977186D, new Color(170, 80, 0, 1), 10),
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
    LobbyLockGray (0.6737828588406924D, 0.6532991545853033D, new Color(124, 124, 124, 1), 1),

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
    Blind_Runes_Dropdown_First (0.25385971622052933D, 0.6565022216170578D),
    Blind_Runes_Dropdown_Spaces (0D, 0.03D),
    Blind_SearchChampion(0.779484336388102D, 0.16596235113330773D, new Color(255, 255, 255, 1)),
    /** ARAM **/
    ARAM_REROLL_BLUE (0.6689782462249011D, 0.6440742990122299D, new Color(37, 81, 134, 1), 10),
    ARAM_REROLL_WHITE (0.690118541734383D, 0.6655989620160678D, new Color(255, 255, 255, 1), 10),
    ARAM_REROLL_GRAY (0.665134556132268D, 0.647149250869921D, new Color(127, 127, 127, 1), 10),
    /** Bans in normal lobby **/
    BAN_BANNING (0.42778669291217586D, 0.047661753794212634D, new Color(21, 62, 123, 1), 5),
    BAN_BANNING_ACTIVE (0.14815823867311986D, 0.13991030952494676D, new Color(203, 101, 25, 1), 5),
    /** DRAFT **/
    Draft_Accept_1 (0.4076073199258522D, 0.5581037621709415D, new Color(226, 112, 0, 1), 10),
    Draft_Accept_2 (0.4969731145795711D, 0.5564381710680324D, new Color(228, 114, 0, 1), 10),
    // This point is not recommended for matching
    Draft_Accept_Mid (0.4556534460837656D, 0.5748878822141793D, new Color(252, 255, 254, 1), 1),
    Draft_Lobby_Chat (0.1356662458720624D, 0.9209480813784958D, new Color(14, 31, 41, 1), 5),
    Draft_Lobby_MainBar (0.6958840768733325D, 0.9023702550582854D, new Color(6, 21, 26, 1), 5),
    Draft_Lobby_TopBar (0.41241193254164354D, 0.05842408529613162D, new Color(9, 33, 37, 1), 6),
    // Player list in norma lobby
    Lobby_Player1 (0.015550930477278908D, 0.0845611760865062D),
    Lobby_Player2 (0.015550930477278908D, 0.2105060870777794D),
    Lobby_Player3 (0.015550930477278908D, 0.3350416373142705D),
    Lobby_Player4 (0.015550930477278908D, 0.4611146634796071D),
    Lobby_Player5 (0.015550930477278908D, 0.5842408529613162D),
    Lobby_Player_Box_Size (0.1623959064137473D, 0.10762331501918981D),
    
    MatchFound(0.495117D, 0.515625D, new Color(255, 255, 255),1),
    MatchFound_Black (0.5027386497185208D, 0.5026265135584376D, new Color(0, 0, 0, 1), 1),
    
    AcceptButton(0.39004512649241546D, 0.5599898226129755D, new Color(25, 60, 101, 1)),
    DeclineButton (0.5824952191406569D, 0.5625880747834147D, new Color(17, 49, 89, 1), 1),
    //Chat betweeen the players invited in a game
    InviteChat (0.4950512695332547D, 0.9208058039244397D, new Color(255, 255, 255, 1), 1),
    //Start button for a game with invited players
    InviteStart (0.7237508300449225D, 0.9146547965815433D, new Color(204, 110, 22, 1),8),
    //Patcher's orange launch button
    Patcher_Launch (0.49697311457957116D, 0.06457398901151389D,new Color(138, 41, 8, 1)),
    Patcher_SetServer (0.9572550031723815D, 0.08763612794419742D),
    Patcher_Eula_Button (0.3643658063837302D, 0.8333119534342984D, new Color(21, 21, 21, 1)),
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
  
  @Override
  public String getName() {
    return name(); 
  }
}
