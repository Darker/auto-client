/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.debug;

import cz.autoclient.autoclick.Rect;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Jakub
 */
public interface DrawableResult {
  Rect getDrawRect(List<Rect> occupiedFields);
  String getName();
  void draw(BufferedImage target, List<Rect> occupiedFields);
}
