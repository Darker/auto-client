/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autoclick;

import autoclick.windows.Messages;

/**
 *
 * @author Jakub
 */
public enum MouseButton {
  Left, Middle, Right;
  
  public final int win_message_id_down;
  public final int win_message_id_up;
  public final int win_message_id_double;
  MouseButton() {
    switch(this.ordinal()) 
    {
     case 1: 
       win_message_id_double = Messages.MBUTTONDBLCLK.code;
       win_message_id_up = Messages.MBUTTONUP.code;
       win_message_id_down = Messages.MBUTTONDOWN.code;
       break;
     case 2: 
       win_message_id_double = Messages.RBUTTONDBLCLK.code;
       win_message_id_up = Messages.RBUTTONUP.code;
       win_message_id_down = Messages.RBUTTONDOWN.code;
       break;
     case 0: 
     default: 
       win_message_id_double = Messages.LBUTTONDBLCLK.code;
       win_message_id_up = Messages.LBUTTONUP.code;
       win_message_id_down = Messages.LBUTTONDOWN.code;
     }
  }

}
