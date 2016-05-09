/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.RelativeRectangle;
import cz.autoclient.autoclick.windows.Window;
import java.awt.geom.Rectangle2D;


/**
 *
 * @author Jakub
 */
public enum ImageFrame implements RelativeRectangle {
  ble(0.43066946048165067D, 0.01229980743076455D,0.5584721560617002D, 0.08609865201535186D),
  
  NormalLobby_SummonerSpellPopup(0.3249679829342412D, 0.25048046122776424D, 0.6516816408080524D, 0.5841127377872527D),
  // The player boxes on the left
  NormalLobby_PlayerList_Left(0.012668162907804105D, 0.07841127237112401D, 0.17890775941418446D, 0.6994234324506703D),
  // Dimensions of the first player champion avatar
  NormalLobby_PlayerList_Champion1(0.063D, 0.0938D, 0.10779949270047263D, 0.168D),
  NormalLobby_PlayerList_Box1(0.015550930477278908D, 0.0845611760865062D, 0.17890775941418446D, 0.19372196703454167D),
 
  NormalLobby_SummonerSpell_1(0.44220053075954985D, 0.6211402752536098D, 0.4806374316858806D, 0.6855861490910602D),
  NormalLobby_SummonerSpell_2(0.48544204430167187D, 0.6210121600795463D, 0.5248398677511609D, 0.6855861490910602D),
  Invite_InvitedPlayerList(0.5882607542796067D, 0.4767030690744722D, 0.8179212373144327D, 0.8900507672099576D),
  
  ;
  
  
  public final Rectangle2D.Double rectangle;
  ImageFrame(double x1, double y1, double x2, double y2) {
    rectangle = new Rectangle2D.Double(x1, y1, Math.abs(x2-x1), Math.abs(y2-y1));
  }
  /*public Rect multiplyRound(double coefficient) {
    return Rect.byWidthHeight((int)(Math.round(rectangle.x*coefficient)),
                    (int)(Math.round(rectangle.y*coefficient)),
                    (int)(Math.round(rectangle.width*coefficient)),
                    (int)(Math.round(rectangle.height*coefficient)));              
  }*/
  @Override
  public Rect rect(Window win) {
    return multiplyBySize(win.getRect());
  }
  
  @Override
  public Rect multiplyBySize(Rect windowSize) {
    return Rect.byWidthHeight((int)(Math.round(rectangle.y*windowSize.height)),
                    (int)(Math.round(rectangle.x*windowSize.width)),
                    (int)(Math.round(rectangle.width*windowSize.width)),
                    (int)(Math.round(rectangle.height*windowSize.height)));              
  }
  @Override
  public double getTop() {
    return rectangle.y;
  }

  @Override
  public double getLeft() {
    return rectangle.x;
  }

  @Override
  public double getBottom() {
    return rectangle.y+rectangle.height;
  }

  @Override
  public double getRight() {
    return rectangle.x+rectangle.width;
  }
}
