/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.autoclient.autoclick;

import cz.autoclient.main_automation.WindowTools;
import java.awt.image.BufferedImage;

/**
 * This group is valid if at least `minimumMatches` of points are valid.
 * @author Jakub
 */
public class PixelGroupThreshold extends PixelGroupSimple {
  private final int maxFailures;
  
  public PixelGroupThreshold(int minimumMatches, ComparablePixel... pixels) {
    super(pixels);
    this.maxFailures = pixels.length-minimumMatches;
  }

  @Override
  public boolean test(BufferedImage i) {
    int failures = 0;
    for(ComparablePixel p:pixels) {
      if(!WindowTools.checkPoint(i, p)) {
        ++failures;
        if(failures>maxFailures)
          return false;
      }
    }
    return true;
  }
}
