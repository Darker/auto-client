/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


 
package cz.autoclient.autoclick;

import cz.autoclient.autoclick.exceptions.APIError;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public interface Window {
  /** MOUSE EVENTS **/
  public void mouseDown(int x, int y);
  public void mouseDown(int x, int y, MouseButton button);
  public void mouseDown(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift);
  
  public void mouseUp(int x, int y);
  public void mouseUp(int x, int y, MouseButton button);
  public void mouseUp(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift);
  
  public void click(int x, int y);
  public void click(int x, int y, MouseButton button);
  public void click(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift);

  public void doubleclick(int x, int y);
  public void doubleclick(int x, int y, MouseButton button);
  public void doubleclick(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift);
  
  
  public void mouseOver(int x, int y);
  /** UNIVERSAL EVENT SENDING FUNC **/
  //Not used now. It would encourage to make non-cross platform applications
  
  /** KEYBOARD EVENTS **/
  
  public void keyDown(int key);
  public void keyPress(int key);
  public void keyUp(int key);
  
  public void typeString(String text);
  
  /** WINDOW Manipulation **/
  public void maximize();
  public void minimize();
  public void minimize(boolean force);

  /** Puts the window in focus and gives it default dimensions **/
  public void restore();
  /** Restore the window (to make it have valid client rect) but do not put it in focus **/
  public void restoreNoActivate();
  /** Hide window completely **/
  public void hide();
  public void focus();
  public void close();
  /** Disable window interaction**/
  public void disable();
  /** Enable window interaction**/
  public void enable();
  /**Is enabled or disabled
   * @return true if the input is not blocked**/
  public boolean isEnabled();
  
  public boolean isMinimized();
  /** If true, the window is visible and rendered.
   * The user may interact with dfferent window however.
   * @return true if the window can be seen on screen**/
  public boolean isVisible();
  
  /** Check whether this Window object still links to some real valid window
   * @return true if window related to this object still exists in the system
   */
  public boolean isValid();
  
  public void move(int x, int y) throws APIError;
  public void resize(int w, int h) throws APIError;
  /** Force window to repaint **/
  public void repaint();

  /**
   *
   * @param x top position
   * @param y left position
   * @param w new width
   * @param h new height
   * @throws APIError when the API fails, whis is system specific. Not every system may provide feedback
   */
  public void moveresize(int x, int y, int w, int h) throws APIError;
  
  /** GETTING VISUAL FEEDBACK **/
  public Color getColor(int x, int y);
  public Color getAvgColor(int x, int y, int w, int h);
  public BufferedImage screenshot() throws APIError;
  public BufferedImage screenshotCrop(int x, int y, int w, int h) throws APIError;
  public Rect getRect() throws APIError;
  
  /** STATIC METHODS FOR GETTING WINDOWS **/
  //Will not declare abstract static methods now for backwards compatibility
  //Once it is a standard, this will be redesigned
  //public static long FindWindowByName(String name);
}
