/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.main_automation.WindowTools;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public interface ComparablePixel extends GraphicPredicate {

  public default int realX(int width) {
    return (int)Math.round(getX()*width);
  }
  public default int realY(int height) {
    return (int)Math.round(getY()*height);
  }
  public double getX();
  public double getY();
  
  public default String getName() {
    return "";
  }
  public default boolean test(BufferedImage i) {
    return WindowTools.checkPoint(i, this);
  }
  
  public ComparablePixel offset(double x, double y);
  
  public default Rect toRect(Rect win_dimensions) {
    return new Rect((int)(getX()*(double)win_dimensions.width), (int)(getY()*(double)win_dimensions.height));
  }
  public default Rect toRect(int width, int height) {
    return new Rect((int)(getX()*(double)width), (int)(getY()*(double)height));
  }
  
  
  public default String toSource() {
     return "ComparablePoint("+getX()+", "+getY()+", "+getColor()+", "+getTolerance()+")"; 
  }
  
  public default boolean equals(ComparablePixel px) {
    return px.getX()==getX() && px.getY()==getY() && px.getColor().equals(getColor());
  }
  
  public default Rect toRect(Rect win_dimensions, Rect offsets, Rect size) {
    return Rect.byWidthHeight(
      (int)Math.round(getY()*ConstData.smallestSize.height)-offsets.top,
      (int)Math.round(getX()*ConstData.smallestSize.width)-offsets.left,
      size.left,
      size.top
    );
  }
  /**
   * Returns distance to rectangle's top left corner
   * @param r
   * @return 
   */
  public double distance(Rect r);
  /**
   * Returns squared distance to rectangle's top left corner
   * @param r
   * @return 
   */
  public double distanceSq(Rect r);
  
  /** Getter for the point color, if any.
   * 
   * @return Color or null
   */
  public Color getColor();
  /** Return for recognition tolerance of the pixel.
   * 
   * @return 0 means no tolerance
   */
  public int getTolerance();
  /* Recalculate to real pixel x coordinate.
  
  */
}
