/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.windows.Window;
import java.awt.image.BufferedImage;

/**
 * Anything that can be validated over an image.
 * @author Jakub
 */
public interface GraphicPredicate {
  
  public default boolean test(Window window) {
    // debug only
    //@TODO: remove when not debugging
    BufferedImage img = window.screenshot();
    DebugDrawing.lastDebugImage = DebugDrawing.cloneImage(img);
    return test(img);
  }
  public boolean test(BufferedImage i);
}
