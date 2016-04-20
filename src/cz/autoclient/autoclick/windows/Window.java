/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


 
package cz.autoclient.autoclick.windows;

import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.RectInterface;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.exceptions.WindowAccessDeniedException;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Jakub
 */

public interface Window {
  /** MOUSE EVENTS **/
  /**
   * Simulates {@link MouseButton.Left} mouse bottun down event in the target window.
   * @param x x coordinate form the top-left corner of the window client area
   * @param y y coordinate form the top-left corner of the window client area
   */
  public void mouseDown(int x, int y);
  /**
   * Simulates mouse botton down event in the target window.
   * @param x x coordinate form the top-left corner of the window client area
   * @param y y coordinate form the top-left corner of the window client area
   * @param button button picked from supplied enum {@link MouseButton}
   */
  public void mouseDown(int x, int y, MouseButton button) throws WindowAccessDeniedException;
  public void mouseDown(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException;
  
  public void mouseUp(int x, int y) throws WindowAccessDeniedException;
  public void mouseUp(int x, int y, MouseButton button) throws WindowAccessDeniedException;
  public void mouseUp(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException;
  
  public void click(int x, int y) throws WindowAccessDeniedException;
  public void click(int x, int y, MouseButton button) throws WindowAccessDeniedException;
  public void click(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException;

  public void doubleclick(int x, int y) throws WindowAccessDeniedException;
  public void doubleclick(int x, int y, MouseButton button) throws WindowAccessDeniedException;
  public void doubleclick(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException;
  
  /**
   * 
   * @param x
   * @param y
   * @throws WindowAccessDeniedException If the control of the window was denied
   */
  public void mouseOver(int x, int y) throws WindowAccessDeniedException;
  
  /**Default methods **/
  /**
   * Simulates slow mouse click using mouseUp and mouseDown methods. If interrupted, throws the exception.
   * @param x
   * @param y
   * @param delay
   * @throws InterruptedException If the owner thread was interrupted.
   * @throws WindowAccessDeniedException If the control of the window was denied
   */
  public default void slowClick(int x, int y, int delay) throws InterruptedException, WindowAccessDeniedException {
    mouseDown(x,y);
  
    Thread.sleep(delay);

    mouseUp(x,y);
  }
  /**
   * Clicks at the top left corner of the rectangle. Use Rect.middle() to click in the middle.
   * @param delay
   * @param pos rectangle to click on.
   * @throws java.lang.InterruptedException
   */
  public default void slowClick(Rect pos, int delay) throws InterruptedException {
    slowClick(pos.left, pos.top, delay);
  }
  
  public default void click(Rect pos) throws WindowAccessDeniedException {
    //Rect rect = getRect();
    click(pos.left, pos.top);
  }
  
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
  
  public void move(int x, int y) throws APIException;
  public void resize(int w, int h) throws APIException;
  /** Force window to repaint **/
  public void repaint();

  /**
   *
   * @param x top position
   * @param y left position
   * @param w new width
   * @param h new height
   * @throws APIException when the API fails, whis is system specific. Not every system may provide feedback
   */
  public void moveresize(int x, int y, int w, int h) throws APIException;
  
  /** GETTING VISUAL FEEDBACK **/
  public Color getColor(int x, int y);
  public Color getAvgColor(int x, int y, int w, int h);
  public BufferedImage screenshot() throws APIException;
  public BufferedImage screenshotCrop(int x, int y, int w, int h) throws APIException;
  
  public default BufferedImage screenshotCrop(RectInterface rect) {
    return screenshotCrop(rect.left(), rect.top(), rect.width(), rect.height());
  }
  public Rect getRect() throws APIException;
  
  /** WINDOW PROPERTIES **/
  
  /**
   * Returns window's displayed title.
   * @return Empty string if no title, never null
   */
  public String getTitle();
  /**
   * Determine whether this window is running under administrator account.
   * @return 
   */
  public boolean isAdmin();
  
  public Process getProcess();
  /** OTHER WINDOWS **/
  
  /**
   * Generates list of child windows of this window. Child windows
   * are for example dialogs.
   * @return List of Window objects.
   */
  public List<Window> getChildWindows();
  
  public default void everyChild(WindowCallback c, boolean recursive) {
    List<Window> children = getChildWindows();
    for(Window child:children) {
      c.run(child);
      if(recursive)
        child.everyChild(c, true);
    }
  }
  
  public default void everyChild(WindowCallback c) {
    everyChild(c, true);
  }
  /** STATIC METHODS FOR GETTING WINDOWS **/
  //Will not declare abstract static methods now for backwards compatibility
  //Once it is a standard, this will be redesigned
  //public static long FindWindowByName(String name);
  
  /**
   * Fetch window from title.
   * @param name title of the window
   * @param strict strict search only returns exact match. May be system dependent
   * @return Window instance or null if no window is found
   * @throws UnsupportedOperationException 
   */
  public static Window FindWindowByName(String name, boolean strict) throws UnsupportedOperationException {
    String sysname = System.getProperty("os.name");
    if(sysname.contains("Windows")) {
      return MSWindow.windowFromName(name, strict);
    }
    else {
      throw new UnsupportedOperationException("Window API not supported on this OS.");
    }
  }
  /**
   * Fetch windows based on the callback validator.
   * @param validator
   * @return Window instance or null if no window is found
   * @throws UnsupportedOperationException 
   */
  public static List<Window> FindWindows(WindowValidator validator) throws UnsupportedOperationException {
    String sysname = System.getProperty("os.name");
    if(sysname.contains("Windows")) {
      return MSWindow.windows(validator);
    }
    else {
      throw new UnsupportedOperationException("Window API not supported on this OS.");
    }
  }
}
