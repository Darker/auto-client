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
public class NegativeGroup implements PixelGroup {
  protected final PixelGroup group;

  public NegativeGroup(PixelGroup group) {
    this.group = group;
  }
  @Override
  public boolean test(BufferedImage i) {
    return !group.test(i);
  }
}
