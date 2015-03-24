 package sirius.core;
 
 import com.sun.jna.Native;
 import com.sun.jna.platform.win32.User32;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.win32.W32APIOptions;
 import sirius.core.types.WinDefExt;
 
 public abstract interface User32Ext
   extends User32
 {
   public static final User32Ext INSTANCE = (User32Ext)Native.loadLibrary("user32.dll", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
   
   public abstract boolean GetClientRect(WinDef.HWND paramHWND, WinDef.RECT paramRECT);
   
   //public abstract boolean DeleteObject(WinDef.HGDIOBJ paramRECT);
   
   public abstract WinDef.HMENU GetMenu(WinDef.HWND paramHWND);
   
   public abstract boolean GetMenuBarInfo(WinDef.HWND paramHWND, long paramLong1, long paramLong2, WinDefExt.MENUBARINFO paramMENUBARINFO);
   
   public abstract long GetMenuCheckMarkDimensions();
   
   public abstract int GetMenuDefaultItem(WinDef.HMENU paramHMENU, int paramInt1, int paramInt2);
   
   public abstract boolean GetMenuInfo(WinDef.HMENU paramHMENU, WinDefExt.MENUINFO paramMENUINFO);
   
   public abstract int GetMenuItemCount(WinDef.HMENU paramHMENU);
   
   public abstract int GetMenuItemID(WinDef.HMENU paramHMENU, int paramInt);
   
   public abstract boolean GetMenuItemInfo(WinDef.HMENU paramHMENU, int paramInt, boolean paramBoolean, WinDefExt.MENUITEMINFO paramMENUITEMINFO);
   
   public abstract boolean GetMenuItemRect(WinDef.HWND paramHWND, WinDef.HMENU paramHMENU, int paramInt, WinDef.RECT paramRECT);
   
   public abstract int GetMenuState(WinDef.HMENU paramHMENU, int paramInt1, int paramInt2);
   
   public abstract int GetMenuString(WinDef.HMENU paramHMENU, int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3);
   
   public abstract WinDef.HWND GetParent(WinDef.HWND paramHWND);
   
   public abstract WinDef.HMENU GetSubMenu(WinDef.HMENU paramHMENU, int paramInt);
   
   public abstract WinDef.HMENU GetSystemMenu(WinDef.HWND paramHWND, boolean paramBoolean);
   
   public abstract WinDef.HDC GetWindowDC(WinDef.HWND paramHWND);
   
   public abstract boolean GetWindowPlacement(WinDef.HWND paramHWND, WinDefExt.WINDOWPLACEMENT paramWINDOWPLACEMENT);
   
   public abstract boolean IsIconic(WinDef.HWND paramHWND);
   
   public abstract boolean IsMenu(WinDef.HMENU paramHMENU);
   
   public abstract boolean IsRectEmpty(WinDef.RECT paramRECT);
   
   public abstract boolean IsWindow(WinDef.HWND paramHWND);
   
   public abstract boolean IsWindowEnabled(WinDef.HWND paramHWND);
   
   public abstract boolean IsWindowUnicode(WinDef.HWND paramHWND);
   
   public abstract int SendMessage(WinDef.HWND paramHWND, int paramInt, WinDef.WPARAM paramWPARAM, WinDef.LPARAM paramLPARAM);
   
   public abstract boolean SetWindowText(WinDef.HWND paramHWND, char[] paramArrayOfChar);
   
   public abstract boolean EnableWindow(WinDef.HWND paramHWND, boolean enable);
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.User32Ext

 * JD-Core Version:    0.7.0.1

 */