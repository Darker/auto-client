/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import java.awt.geom.Rectangle2D;


/**
 *
 * @author Jakub
 */
public enum ImageFrame {
  ble(0.43066946048165067D, 0.01229980743076455D,0.5584721560617002D, 0.08609865201535186D),
  
  NormalLobby_SummonerSpellPopup(0.3249679829342412D, 0.25048046122776424D, 0.6516816408080524D, 0.5841127377872527D),
  NormalLobby_SummonerSpell_1(0.43547407309744196D, 0.6102498285776273D, 0.48352019925535544D, 0.6871236250199058D),
  NormalLobby_SummonerSpell_2(0.47679374159324756D, 0.6163997322930096D, 0.528683557843794D, 0.6917360528064425D),
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
  public Rect rect(Window win) {
    return multiplyBySize(win.getRect());
  }
  
  public Rect multiplyBySize(Rect windowSize) {
    return Rect.byWidthHeight((int)(Math.round(rectangle.y*windowSize.height)),
                    (int)(Math.round(rectangle.x*windowSize.width)),
                    (int)(Math.round(rectangle.width*windowSize.width)),
                    (int)(Math.round(rectangle.height*windowSize.height)));              
  }
}
