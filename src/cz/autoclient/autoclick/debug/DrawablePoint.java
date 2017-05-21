/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.debug;

import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * This class represents match point that either failed or succeeded to match some color.
 * 
 * @author Jakub
 */
public class DrawablePoint implements DrawableResult {
  public final boolean state;
  public final String name;
  public final int x;
  public final int y;

  public DrawablePoint(int x, int y, String name, boolean state) {
    this.state = state;
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public DrawablePoint(int x, int y, String name) {
    this(x, y, name, true);
  }
  public DrawablePoint(int x, int y) {
    this(x, y, "", true);
  }
  public DrawablePoint(ComparablePixel p, int width, int height) {
    this((int)(p.getX()*width), (int)(p.getY()*height), "", true);
  }
  
  @Override
  public Rect getDrawRect(List<Rect> occupiedFields) {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void draw(BufferedImage target, List<Rect> occupiedFields) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
 
}
