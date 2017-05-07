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
    Accept_ProgressBar(0.58633890923329D, 0.7516655808867138D, new Color(71, 252, 246, 1), 20),
    
    Lobby_Chat(0.141431781011012D, 0.9498319613023019D, new Color(1, 10, 19, 1), 3),
    Lobby_Search(0.6516816408080522D, 0.09908319020779409D, new Color(2, 5, 12, 1), 3),
    Lobby_ClientChatButton(0.9678251509271224D, 0.9481236304366503D, new Color(205, 190, 145, 1), 1),
    Lobby_ClientChatButtonOutside(0.9582159256955398D, 0.963498608227515D, new Color(30, 35, 40, 1), 1),
    Lobby_ChanpionSlot1(0.29229661714686006D, 0.19474971868428495D, new Color(36, 135, 246, 1), 1),
    
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
