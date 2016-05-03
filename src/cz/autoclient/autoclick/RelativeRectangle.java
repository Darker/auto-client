/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import cz.autoclient.autoclick.windows.Window;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Jakub
 */
public interface RelativeRectangle {
  /**
   * Set the coordinates as if the given rectangle's @r top left corner was [0,0].
   * @param r
   * @return 
   */
  public default RelativeRectangle relativeTo(RelativeRectangle r) {
    return new Basic(getLeft()-r.getLeft(), getTop()-r.getTop(), 
                    getRight()-r.getLeft(), getBottom()-r.getTop()); 
  }
  public double getTop();
  public double getLeft();
  public double getBottom();
  public double getRight();
  
  public Rect rect(Window win);
  public Rect multiplyBySize(Rect windowSize);
  
  public static class Basic implements RelativeRectangle {
    public final Rectangle2D.Double rectangle;
    Basic(double x1, double y1, double x2, double y2) {
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
}
