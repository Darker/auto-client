/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;


import cz.autoclient.autoclick.windows.Window;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public interface PixelGroup {
  public default boolean test(Window window) {
    return test(window.screenshot());
  }
  public boolean test(BufferedImage i);
  
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
