/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autoclick.computervision;

import autoclick.comvis.ScreenWatcher;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class IntegralImage {
  public final double[][][] data;
  public final int width;
  public final int height;
  public IntegralImage(BufferedImage image) {
    width = image.getWidth();
    height = image.getHeight();
    data = ScreenWatcher.integralImage(image);
  }
  public IntegralImage(double[][][] d, int w, int h) {
    data = d;
    width = w;
    height = h;    
  }
}
