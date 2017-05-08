/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.main_automation.WindowTools;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This groups' test() returns true when all members are valid.
 * @author Jakub
 */
public class PixelGroupSimple implements PixelGroupWithPixels {
  protected final ComparablePixel[] pixels;

  public PixelGroupSimple(ComparablePixel... pixels) {
    this.pixels = pixels;
  }
  
  @Override
  public boolean test(BufferedImage i) {
    boolean result = true;
    for(ComparablePixel p:pixels) {
      if(!WindowTools.checkPoint(i, p)) {
        result = false;
        DebugDrawing.drawPoint(DebugDrawing.lastDebugImage, p, Color.red);
      }
      else {
        DebugDrawing.drawPoint(DebugDrawing.lastDebugImage, p, Color.green);
      }      
    }
    return result;
  }

  @Override
  public ComparablePixel[] getPixels() {
    return pixels;
  }
  
}
