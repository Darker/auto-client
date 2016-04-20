/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.ms_windows;

/**
 *
 * @author Jakub
 */
public enum MouseEventParam {
  CONTROL(0x0008),
  LBUTTON(0x0001),
  MBUTTON(0x0010),
  RBUTTON(0x0002),
  SHIFT(0x0004),
  XBUTTON1(0x0020),
  MK_XBUTTON2(0x0040);
  
  public final int code;
  MouseEventParam(int c) {
    code = c;
  }
  public static int MakeMouseEventparam(boolean ctrl, boolean shift) {
    int result = 0;
    if(ctrl)
      result&=CONTROL.code;
    if(shift)
      result&=SHIFT.code;
    return result;
  }
}
