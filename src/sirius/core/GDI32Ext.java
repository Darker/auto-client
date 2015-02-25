 package sirius.core;
 
 import com.sun.jna.Native;
 import com.sun.jna.platform.win32.GDI32;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.win32.W32APIOptions;
 
 public abstract interface GDI32Ext
   extends GDI32
 {
   public static final GDI32Ext INSTANCE = (GDI32Ext)Native.loadLibrary("gdi32", GDI32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
   
   public abstract boolean BitBlt(WinDef.HDC paramHDC1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, WinDef.HDC paramHDC2, int paramInt5, int paramInt6, WinDef.DWORD paramDWORD);
   
   public abstract int GetPixel(WinDef.HDC paramHDC, int paramInt1, int paramInt2);
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.GDI32Ext

 * JD-Core Version:    0.7.0.1

 */