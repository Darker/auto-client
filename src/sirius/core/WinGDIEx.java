 package sirius.core;
 
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.platform.win32.WinGDI;
 
 public abstract interface WinGDIEx
   extends WinGDI
 {
   public static final WinDef.DWORD SRCCOPY = new WinDef.DWORD(13369376L);
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.WinGDIEx

 * JD-Core Version:    0.7.0.1

 */