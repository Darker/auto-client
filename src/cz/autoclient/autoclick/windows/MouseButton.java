/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows;

import cz.autoclient.autoclick.windows.ms_windows.Messages;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Jakub
 */
public enum MouseButton {
  Left, Middle, Right, Invalid;
  
  public final Messages win_message_id_down;
  public final Messages win_message_id_up;
  public final Messages win_message_id_double;
  public final int java_input_event_button_num;
  MouseButton() {
    switch(this.ordinal()) 
    {
     case 0: 
       win_message_id_double = Messages.LBUTTONDBLCLK;
       win_message_id_up = Messages.LBUTTONUP;
       win_message_id_down = Messages.LBUTTONDOWN;
       java_input_event_button_num = InputEvent.BUTTON1_MASK;
       break;
     case 1: 
       win_message_id_double = Messages.MBUTTONDBLCLK;
       win_message_id_up = Messages.MBUTTONUP;
       win_message_id_down = Messages.MBUTTONDOWN;
       java_input_event_button_num = InputEvent.BUTTON2_MASK;
       break;
     case 2: 
       win_message_id_double = Messages.RBUTTONDBLCLK;
       win_message_id_up = Messages.RBUTTONUP;
       win_message_id_down = Messages.RBUTTONDOWN;
       java_input_event_button_num = InputEvent.BUTTON3_MASK;
       break;
     default: 
       win_message_id_double = null;
       win_message_id_up = null;
       win_message_id_down = null;
       java_input_event_button_num = -1;
     }
  }
  public static MouseButton fromJavaAwtMouseEvent(MouseEvent e) {
    switch(e.getButton()) 
    {
     case 2: 
       return Middle;
     case 3: 
       return Right;
     case 1: 
       return Left;
     default: 
       return Invalid;
     }
  }

}
