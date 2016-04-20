 package sirius;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.platform.win32.WinUser;
 import com.sun.jna.ptr.IntByReference;
 import javax.jws.WebService;
 import sirius.classes.Common;
 import sirius.core.User32Ext;
 
 @WebService
 public class Win32Utils
   extends Common
 {
   public class WNDENUMPROC
     implements WinUser.WNDENUMPROC
   {
     private final int maxLength = 128;
     private int currIndex;
     private final Win32Locator locator;
     
     public WNDENUMPROC(Win32Locator locatorVal)
     {
       this.locator = locatorVal;
       this.currIndex = 0;
     }
     
     public final boolean callback(WinDef.HWND arg0, Pointer arg1)
     {
       User32Ext user32 = User32Ext.INSTANCE;
       int length = user32.GetWindowTextLength(arg0) + 1;
       char[] buf = new char[length];
       
       user32.GetWindowText(arg0, buf, length);
       String text = String.valueOf(buf).trim();
       if (!text.matches(this.locator.getCaption())) {
         return true;
       }
       buf = null;
       buf = new char[''];
       
       user32.GetClassName(arg0, buf, 128);
       String clazz = String.valueOf(buf).trim();
       if (!clazz.matches(this.locator.getWinClass())) {
         return true;
       }
       if (this.currIndex < this.locator.getIndex()) {
         this.currIndex += 1;
       } else {
         this.locator.setHwnd(arg0);
       }
       if (this.locator.getHwnd() == 0L) {
         return true;
       }
       if (!user32.IsWindow(Win32Utils.this.longToHwnd(this.locator.getHwnd())))
       {
         this.locator.setHwnd(0L);
         return true;
       }
       buf = null;
       this.locator.setCaption(text);
       this.locator.setWinClass(clazz);
       
       return false;
     }
     
     public final Win32Locator getLocator()
     {
       return this.locator;
     }
   }
   
   public long searchSameThreadWindow(long baseHwnd, Win32Locator locator)
   {
     User32Ext user32 = User32Ext.INSTANCE;
     
     WinDef.HWND hWnd = new WinDef.HWND();
     hWnd.setPointer(Pointer.createConstant(baseHwnd));
     
     IntByReference lpdwProcessId = new IntByReference();
     int threadID = user32.GetWindowThreadProcessId(hWnd, lpdwProcessId);
     
     Pointer pt = Pointer.NULL;
     locator.setHwnd(0L);
     
     WNDENUMPROC enumProc = new WNDENUMPROC(locator);
     user32.EnumThreadWindows(threadID, enumProc, pt);
     
     return enumProc.getLocator().getHwnd();
   }
   
   public long searchWindow(Win32Locator locator)
   {
     User32Ext user32 = User32Ext.INSTANCE;
     
     locator.setHwnd(0L);
     WNDENUMPROC enumProc = new WNDENUMPROC(locator);
     Pointer pt = Pointer.NULL;
     if (locator.getParent() == 0L)
     {
       user32.EnumWindows(enumProc, pt);
     }
     else
     {
       WinDef.HWND hWnd = new WinDef.HWND();
       hWnd.setPointer(Pointer.createConstant(locator.getParent()));
       user32.EnumChildWindows(hWnd, enumProc, pt);
     }
     return enumProc.getLocator().getHwnd();
   }
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.Win32Utils

 * JD-Core Version:    0.7.0.1

 */