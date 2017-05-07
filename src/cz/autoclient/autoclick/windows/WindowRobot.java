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
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class WindowRobot implements Window {

  public WindowRobot(Window innerWindow) {
    this.innerWindow = innerWindow;
    try {
      this.robot = new Robot();
      robot.setAutoDelay(1);
    } catch (AWTException ex) {
      System.out.println("Robot cannot be created!");
    }
  }
  public FocusMode focusMode = FocusMode.FORCE_FOCUS;
  protected Window innerWindow;
  protected Robot robot;
  /** This enum describes how should program behave based on the 
   * window focus state.
   */
  public enum FocusMode {
    /**
     * Do not read focus state and perform all actions normally.
     */
    IGNORE_FOCUS,
    /**
     * If window isn't focused, try to focus it before performing action(s)
     */
    FORCE_FOCUS,
    /**
     * If window isn't focused, loop while waiting for focus.
     */
    WAIT_FOCUS,
    /**
     * If the window isn't focused, do not perferm the actions.
     */
    SKIP_NONFOCUS
  }
  /**
   * 
   * @return false if nothing should be done (eg. focus failed, thread interrupted)
   */
  boolean performFocusActions() {
    switch(focusMode) {
      case FORCE_FOCUS: {
        if(innerWindow.isForeground())
          return true;
        innerWindow.focus();
        int i=5;
        while(i-->0) {
          if(innerWindow.isForeground()) {
            return true;
          }
          try {
            Thread.sleep(40);
          } catch (InterruptedException ex) {
            return false;
          }
        }
        return false;
      }
      case WAIT_FOCUS: {
        while(!innerWindow.isForeground()) {
          try {
            Thread.sleep(40);
          } catch (InterruptedException ex) {
            return false;
          }
        }
        break;
      }
      case SKIP_NONFOCUS: {
        if(!innerWindow.isForeground()) {
          return false;
        }
        break;
      }
    }
    return true;
  }
  Rect transposeCoordinates(int x, int y) {
    Rect rect = innerWindow.getRect();
    return new Rect(rect.left+x, rect.top+y);
  }
  
  @Override
  public void mouseDown(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException {
    if(performFocusActions()) {
      Rect rect = transposeCoordinates(x,y);
      robot.mouseMove(rect.left, rect.top);
      robot.mousePress(button.java_input_event_button_num);
    }
  }

  @Override
  public void mouseUp(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException {
    if(performFocusActions()) {
      Rect rect = transposeCoordinates(x,y);
      robot.mouseMove(rect.left, rect.top);
      robot.mouseRelease(button.java_input_event_button_num);
    }
  }

  @Override
  public void click(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException {
    if(performFocusActions()) {
      Rect rect = transposeCoordinates(x,y);
      robot.mouseMove(rect.left, rect.top);
      robot.mousePress(button.java_input_event_button_num);
      robot.delay(30);
      robot.mouseRelease(button.java_input_event_button_num);
    }
  }

  @Override
  public void doubleclick(int x, int y, MouseButton button, boolean isControl, boolean isAlt, boolean isShift) throws WindowAccessDeniedException {
    if(performFocusActions()) {
      Rect rect = transposeCoordinates(x,y);
      robot.mouseMove(rect.left, rect.top);
      robot.mousePress(button.java_input_event_button_num);
      robot.delay(30);
      robot.mouseRelease(button.java_input_event_button_num);
      robot.delay(30);
      robot.mousePress(button.java_input_event_button_num);
      robot.delay(30);
      robot.mouseRelease(button.java_input_event_button_num);
    }
  }

  @Override
  public void mouseOver(int x, int y) throws WindowAccessDeniedException {
    if(performFocusActions()) {
      Rect rect = transposeCoordinates(x,y);
      robot.mouseMove(rect.left, rect.top);
    }
  }

  @Override
  public void keyDown(int key) {
    if(performFocusActions()) {
      try {
        robot.keyPress(key);
      }
      catch(IllegalArgumentException e) {
        System.out.println("WARNING - illegal char code: "+key);
      }
    }
  }

  @Override
  public void keyPress(int key) {
    if(performFocusActions()) {
      try {
        robot.keyPress(key);
        robot.delay(30);
        robot.keyRelease(key);
      }
      catch(IllegalArgumentException e) {
        System.out.println("WARNING - illegal char code: "+key);
      }
    }
  }

  @Override
  public void keyUp(int key) {
    if(performFocusActions()) {
      try {
        robot.keyRelease(key);
      }
      catch(IllegalArgumentException e) {
        System.out.println("WARNING - illegal char code: "+key);
      }
    }
  }

  @Override
  public void typeString(String text) {
    if(performFocusActions()) {
      for(int character: text.toCharArray()) {
        try {
          // Special handling of letters
          if(Character.isLetter(character)) {
            int charKey = Character.toUpperCase(character);
            boolean upper = Character.isUpperCase(character);
            if(upper) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.delay(10);
            }
            robot.keyPress(charKey);
            robot.delay(20);
            robot.keyRelease(charKey);
            robot.delay(5);
            if(upper)
                robot.keyRelease(KeyEvent.VK_SHIFT);
          }
          else if(character == ('\n') || character==13) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(20);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(5);
          }
          else {
            robot.keyPress(character);
            robot.delay(30);
            robot.keyRelease(character);
            robot.delay(5);
          }
        }
        catch(IllegalArgumentException e) {
          System.out.println("WARNING - illegal char code: "+character);
        }
      }
    }
  }
  
  @Override
  public void maximize() {
    innerWindow.maximize();
  }

  @Override
  public void minimize() {
    innerWindow.minimize();
  }

  @Override
  public void minimize(boolean force) {
    innerWindow.minimize(force);
  }

  @Override
  public void restore() {
    innerWindow.restore();
  }

  @Override
  public void restoreNoActivate() {
    innerWindow.restoreNoActivate();
  }

  @Override
  public void hide() {
    innerWindow.hide();
  }

  @Override
  public void focus() {
    innerWindow.focus();
  }

  @Override
  public void close() {
    innerWindow.close();
  }

  @Override
  public void disable() {
    innerWindow.disable();
  }

  @Override
  public void enable() {
    innerWindow.enable();
  }

  @Override
  public boolean isEnabled() {
    return innerWindow.isEnabled();
  }

  @Override
  public boolean isMinimized() {
    return innerWindow.isMinimized();
  }

  @Override
  public boolean isVisible() {
    return innerWindow.isVisible();
  }

  @Override
  public boolean isForeground() {
    return innerWindow.isForeground();
  }

  @Override
  public boolean isValid() {
    return innerWindow.isValid();
  }

  @Override
  public void move(int x, int y) throws APIException {
    innerWindow.move(x, y);
  }

  @Override
  public void resize(int w, int h) throws APIException {
    innerWindow.resize(w, h);
  }

  @Override
  public void repaint() {
    innerWindow.repaint();
  }

  @Override
  public void moveresize(int x, int y, int w, int h) throws APIException {
    innerWindow.moveresize(x, y, w, h);
  }

  @Override
  public Color getColor(int x, int y) {
    return innerWindow.getColor(x, y);
  }

  @Override
  public Color getAvgColor(int x, int y, int w, int h) {
    return innerWindow.getAvgColor(x, y, w, h);
  }

  @Override
  public BufferedImage screenshot() throws APIException {
    return innerWindow.screenshot();
  }

  @Override
  public BufferedImage screenshotCrop(int x, int y, int w, int h) throws APIException {
    return innerWindow.screenshotCrop(x, y, w, h);
  }

  @Override
  public BufferedImage screenshotCrop(RectInterface rect) {
    return innerWindow.screenshotCrop(rect);
  }

  @Override
  public Rect getRect() throws APIException {
    return innerWindow.getRect();
  }

  @Override
  public String getTitle() {
    return innerWindow.getTitle();
  }

  @Override
  public boolean isAdmin() {
    return innerWindow.isAdmin();
  }

  @Override
  public Process getProcess() {
    return innerWindow.getProcess();
  }
  @Override
  public String getProcessName() {
    return innerWindow.getProcessName();
  }
  @Override
  public List<Window> getChildWindows() {
    return innerWindow.getChildWindows();
  }

  @Override
  public void everyChild(WindowCallback c, boolean recursive) {
    innerWindow.everyChild(c, recursive);
  }

  @Override
  public void everyChild(WindowCallback c) {
    innerWindow.everyChild(c);
  }
}
