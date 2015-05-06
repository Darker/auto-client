/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

/**
 *
 * @author Jakub
 */
public interface RectInterface {
  public int top();
  public int left();
  public int width();
  public int height();

  /**
   *
   * @return Rectangle that represents the center point of this rectangle. Such rectangle has 0 size.
   */
  public RectInterface middle();
}
