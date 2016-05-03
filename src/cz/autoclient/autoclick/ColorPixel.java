/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import java.awt.Color;

/**
 *
 * @author Jakub
 */
public class ColorPixel implements ComparablePixel {
    public final double x;   // in kilograms
    public final double y; // in meters
    public final Color color;
    public final int tolerance;
    public ColorPixel(double x, double y) {
      this(x,y,null,1);
    }
    public ColorPixel(double x, double y, Color color) {
      this(x,y,color,1);
    }
    public ColorPixel(double x, double y, Color color, int tolerance) {
      this.x = x;
      this.y = y;
      this.color = color;
      this.tolerance = tolerance;
    }
    /*public double[] offset(double ox, double oy) {
      return new double[] {x+ox, y+oy};
    }*/
    
    public String toSource() {
      return "new ColorPixel("
                         +x+"D, "
                         +y+"D"
                         +(color!=null?", "+ColorToSource(color):"")
                         +")";
    }
    @Override
    public String toString() {
      return "ColorPixel("
                         +x+"D, "
                         +y+"D"
                         +(color!=null?", "+ColorToSource(color):"")
                         +")";
    }
    public String toString(String name) {
      return name+" ("
                         +x+"D, "
                         +y+"D"
                         +(color!=null?", "+ColorToSource(color):"")
                         +")";
    }
    
    public static String ColorToSource(Color color) {
      return  "new Color("
                           +color.getRed()+", "
                           +color.getGreen()+", "
                           +color.getBlue()+", "
                           +color.getAlpha()
                         +")";
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
  public ComparablePixel offset(double x, double y) {
    return new ColorPixel(this.x+x, this.y+y, color, tolerance);
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
