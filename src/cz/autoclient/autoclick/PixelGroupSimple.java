/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import cz.autoclient.main_automation.WindowTools;
import java.awt.image.BufferedImage;

/**
 * This groups' test() returns true when all members are valid.
 * @author Jakub
 */
public class PixelGroupSimple implements PixelGroup {
  protected final ComparablePixel[] pixels;

  public PixelGroupSimple(ComparablePixel... pixels) {
    this.pixels = pixels;
  }

  @Override
  public boolean test(BufferedImage i) {
    for(ComparablePixel p:pixels) {
      if(!WindowTools.checkPoint(i, p))
        return false;
    }
    return true;
  }
}
