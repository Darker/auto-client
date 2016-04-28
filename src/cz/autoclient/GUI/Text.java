/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

/**
 *
 * @author Jakub
 */
public enum Text {
  TITLE_STOPPED("Not running."),
  
  TOGGLE_BUTTON_TITLE_DISABLED("Can't start because Client is not running."),
  TOGGLE_BUTTON_TITLE_ENABLED("Start/stop automation"),
  
  MENU_DLL_LOADED("DLL injected."),
  MENU_DLL_LOAD("Disable Client attention requests"),
  MENU_DLL_TITLE("Disables taskbar flashing and focus stealing using DLL injection"),
  MENU_DLL_TITLE_UNAVAILABLE("This requires additional binary files. Read help to learn more."),
;
  
  public final String text;
  Text(String text) {
    this.text = text;
  }
  @Override
  public String toString() {
    return text; 
  }
}
