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
  
 /**
  * Crop the current rectangle. Cropping rectangle by 1 pixel means top and left
  * will increase by one while bottom and right will decrease by one.
  * @param howMuch
  * @return 
  */
  public RectInterface crop(int howMuch);
}
