 package sirius.core;
 
 import com.sun.jna.Native;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.win32.StdCallLibrary;
 import com.sun.jna.win32.W32APIOptions;
 
 public abstract interface DlgWin32API
   extends StdCallLibrary
 {
   public static final DlgWin32API INSTANCE = (DlgWin32API)Native.loadLibrary("user32.dll", DlgWin32API.class, W32APIOptions.DEFAULT_OPTIONS);
   
   public abstract long GetDialogBaseUnits();
   
   public abstract int GetDlgCtrlID(WinDef.HWND paramHWND);
   
   public abstract WinDef.HWND GetDlgItem(WinDef.HWND paramHWND, int paramInt);
   
   public abstract int GetDlgItemInt(WinDef.HWND paramHWND, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
   
   public abstract int GetDlgItemTextA(WinDef.HWND paramHWND, int paramInt1, char[] paramArrayOfChar, int paramInt2);
   
   public abstract int IsDlgButtonChecked(WinDef.HWND paramHWND, int paramInt);
   
   public abstract boolean SetDlgItemInt(WinDef.HWND paramHWND, int paramInt1, int paramInt2, boolean paramBoolean);
   
   public abstract boolean SetDlgItemTextA(WinDef.HWND paramHWND, int paramInt, char[] paramArrayOfChar);
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.DlgWin32API

 * JD-Core Version:    0.7.0.1

 */