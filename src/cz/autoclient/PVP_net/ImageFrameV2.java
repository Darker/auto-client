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
public enum ImageFrameV2 implements RelativeRectangle {
  Lobby_Spell1Button(0.506D, 0.89D, 0.5498238533532759D, 0.9686236008244697D),
  Lobby_Spell2Button(0.5507847758764342D, 0.8883320501388435D, 0.5949872119417144D, 0.9686236008244697D),
  Lobby_SpellDialog(0.4585362136532404D, 0.5808324943215516D, 0.6555253309006853D, 0.8575820945571143D),
  NOOP(0,0,0,0);
  
  public final Rectangle2D.Double rectangle;
  ImageFrameV2(double x1, double y1, double x2, double y2) {
    rectangle = new Rectangle2D.Double(x1, y1, Math.abs(x2-x1), Math.abs(y2-y1));
  }
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
