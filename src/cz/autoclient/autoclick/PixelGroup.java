/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;


import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public interface PixelGroup extends GraphicPredicate {  
  public static class AlwaysTrue implements PixelGroup {
    @Override
    public boolean test(BufferedImage i) {
      return true;
    }
  }
  public static class AlwaysFalse implements PixelGroup {
    @Override
    public boolean test(BufferedImage i) {
      return false;
    }
  }
}
