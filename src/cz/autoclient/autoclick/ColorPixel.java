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
public class ColorPixel {
    public final double x;   // in kilograms
    public final double y; // in meters
    public final Color color;
    public ColorPixel(double x, double y) {
        this.x = x;
        this.y = y;
        color = null;
    }
    public ColorPixel(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public double[] offset(double ox, double oy) {
      return new double[] {x+ox, y+oy};
    }
    
    
    public double realX(double width) {
        return width*x;   
    }
    public double realY(double height) {
        return height*y;   
    }
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
}
