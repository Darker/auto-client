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
public class RecursiveGroupAND extends RecursiveGroup {
  public RecursiveGroupAND(PixelGroup... groups) {
    super(groups);
  }
  @Override
  public boolean test(BufferedImage i) {
    for(PixelGroup g:groups) {
      if(!g.test(i))
        return false;
    }
    return true;
  }
}
