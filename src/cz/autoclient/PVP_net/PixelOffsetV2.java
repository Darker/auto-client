/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.ColorPixel;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import java.awt.Color;

/**
 *
 * @author Jakub
 */
public enum PixelOffsetV2 implements ComparablePixel {
    Login_UsernameField (0.9092088770144681D, 0.2664996150416531D, new Color(12, 20, 28, 1), 30),
    Login_PasswordField (0.9092088770144681D, 0.34508283486162766D, new Color(0, 3, 6, 1), 30),
    Login_ButtonDisabled (0.8736547436576121D, 0.7379989339615008D, new Color(30, 35, 40, 1), 5),
    
    Accept_AcceptButton (0.5008168046722041D, 0.7977905142593076D, new Color(30, 37, 42, 1), 5),
    Accept_DeclineButton (0.4594971361763987D, 0.8558737636914627D, new Color(30, 35, 40, 1), 5),
    Accept_ProgressBar(0.58633890923329D, 0.7516655808867138D, new Color(71, 252, 246, 1), 100),
    
    Lobby_Chat(0.09657947941589755D, 0.9455205811138014D, new Color(1, 10, 19, 1), 3),
    Lobby_Search(0.6122156608511027D, 0.14285714285714285D, new Color(2, 5, 12, 1), 15),
    Lobby_ClientChatButton(0.9678251509271224D, 0.9481236304366503D, new Color(205, 190, 145, 1), 3),
    Lobby_ClientChatButtonOutside(0.9582159256955398D, 0.963498608227515D, new Color(30, 35, 40, 1), 3),
    Lobby_ChanpionSlot1(0.29229661714686006D, 0.19474971868428495D, new Color(36, 135, 246, 1), 1),
    Lobby_EditRunesLight(0.32208521536476636D, 0.9515402921679534D, new Color(205, 190, 145, 1), 15),
    Lobby_EditRunesDark(0.3192024477952916D, 0.9447069687053471D, new Color(30, 35, 40, 1), 10),
    Lobby_Locked_GoldFrame(0.25578156126684587D, 0.41683273121899583D, new Color(200, 167, 95, 1), 10),
    Lobby_Locked_GoldFrame2(0.7429692805080876D, 0.40487441515943445D, new Color(200, 168, 101, 1), 10),
    Lobby_NotLocked_GoldFrame(0.7919763291891594D, 0.39974942256247964D, new Color(200, 169, 105, 1), 10),
    Lobby_SumSpellButton1(0.5267617127974774D, 0.931040321780134D, new Color(122, 27, 136, 1), 1),
    Lobby_SumSpellButton2(0.5700032263395995D, 0.9327486526457858D, new Color(28, 18, 10, 1), 1),
    Lobby_SumSpellDialog_Bg1(0.46237990374587346D, 0.5347075609489578D, new Color(1, 10, 19, 1), 1),
    Lobby_SumSpellDialog_Bg2(0.640150570530153D, 0.5347075609489578D, new Color(1, 10, 19, 1), 1),
    Lobby_SumSpellDialog_Bg3(0.45373160103744914D, 0.43562437074116367D, new Color(1, 10, 19, 1), 1),
    
    Main_PlayButton(0.12413517559416318D, 0.052958256835200286D, new Color(30, 35, 40, 1), 2),
    Main_HomeButton(0.21446189277104036D, 0.051249925969548665D, new Color(4, 4, 6, 1), 1),
    
    Play_CancelTop(0.34899104601319786D, 0.9327486526457858D, new Color(200, 170, 108, 1), 5),
    Play_Cancel(0.3470692009668813D, 0.9515402921679534D, new Color(43, 47, 47, 1), 1),
    Play_ConfirmTop(0.4787155866395641D, 0.9327486526457858D, new Color(6, 167, 175, 1), 5),
    Play_Confirm(0.4787155866395641D, 0.9498319613023019D, new Color(163, 199, 199, 1), 1),
    Play_CreateCustom(0.3076713775173924D, 0.1315414766551749D, new Color(10, 45, 65, 1), 1),
    Play_Custom_GameName(0.14047085848785373D, 0.6252490968284937D, new Color(4, 13, 16, 1), 15),
    Play_Custom_Lobby_Chat(0.14527547110364508D, 0.9498319613023019D, new Color(0, 9, 18, 1), 19),
    Play_Custom_Lobby_Invite(0.6920403867806996D, 0.15887477050560087D, new Color(30, 35, 40, 1), 3),
    Play_Custom_Lobby_Start_Top(0.4767937415932475D, 0.9327486526457858D, new Color(5, 166, 175, 1), 5),
    Play_Custom_Lobby_Start(0.47775466411640577D, 0.9515402921679534D, new Color(18, 21, 23, 1), 1),
    Play_Custom_Lobby_NoXPDialog_Yes(0.48736388934798847D, 0.5962074721124162D, new Color(30, 35, 40, 1), 4),
    Play_Custom_Lobby_NoXPDialog_Background(0.6122838173585633D, 0.5603325239337321D, new Color(1, 10, 19, 1), 3),
    
    NOOP(0.9582159256955398D, 0.963498608227515D)
    ;
  
    public final double x;
    public final double y;
    public final Color color;
    public final int tolerance;
    PixelOffsetV2(double x, double y) {
      this(x,y,null);
    }
    PixelOffsetV2(double x, double y, Color color) {
      this(x,y,color, 1);
    }
    PixelOffsetV2(double x, double y, Color color, int tolerance) {
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
