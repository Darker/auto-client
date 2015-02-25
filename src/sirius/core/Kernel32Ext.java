 package sirius.core;
 
 import com.sun.jna.Native;
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.Kernel32;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.platform.win32.WinNT;
 import com.sun.jna.win32.W32APIOptions;
 
 public abstract interface Kernel32Ext
   extends Kernel32
 {
   public static final Kernel32Ext INSTANCE = (Kernel32Ext)Native.loadLibrary("kernel32.dll", Kernel32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
   
   public abstract Pointer VirtualAllocEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, int paramInt, WinDef.DWORD paramDWORD1, WinDef.DWORD paramDWORD2);
   
   public abstract boolean VirtualFreeEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, int paramInt, WinDef.DWORD paramDWORD);
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.Kernel32Ext

 * JD-Core Version:    0.7.0.1

 */