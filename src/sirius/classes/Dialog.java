 package sirius.classes;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.WinDef;
 import javax.jws.WebService;
 
 @WebService
 public class Dialog
   extends Common
 {
   public long getDialogBaseUnits()
   {
     return getDlg32().GetDialogBaseUnits();
   }
   
   public int getDlgCtrlID(long hWnd)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return getDlg32().GetDlgCtrlID(handle);
   }
   
   public long getDlgItem(long hWnd, int itemId)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return Pointer.nativeValue(getDlg32().GetDlgItem(handle, itemId).getPointer());
   }
   
   public int getDlgItemInt(long hWnd, int itemId, boolean pbool, boolean flag2)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return getDlg32().GetDlgItemInt(handle, itemId, pbool, flag2);
   }
   
   public String getDlgItemTextA(long hWnd, int itemId)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     
     int length = 255;
     char[] buf = new char[length];
     
     getDlg32().GetDlgItemTextA(handle, itemId, buf, length);
     String text = String.valueOf(buf).trim();
     return text;
   }
   
   public int isDlgButtonChecked(long hWnd, int itemId)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return getDlg32().IsDlgButtonChecked(handle, itemId);
   }
   
   public boolean setDlgItemInt(long hWnd, int itenId, int intValue, boolean flag)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return getDlg32().SetDlgItemInt(handle, itenId, intValue, flag);
   }
   
   public boolean setDlgItemText(long hWnd, int itemId, String text)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     return getDlg32().SetDlgItemTextA(handle, itemId, text.toCharArray());
   }
 }



