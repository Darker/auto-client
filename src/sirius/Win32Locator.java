 package sirius;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.WinDef;
 
 public class Win32Locator
 {
   private long hwnd;
   private long parent;
   private String winClass;
   private String caption;
   private int index;
   
   public Win32Locator()
   {
     this.hwnd = 0L;
     this.parent = 0L;
     this.winClass = "(.*)";
     this.caption = "(.*)";
     this.index = 0;
   }
   
   public Win32Locator(String winClass, String caption, int index)
   {
     this.winClass = winClass;
     this.caption = caption;
     this.index = index;
   }
   
   public Win32Locator(long parent, String winClass, String caption, int index)
   {
     this.parent = parent;
     this.winClass = winClass;
     this.caption = caption;
     this.index = index;
   }
   
   public final String getCaption()
   {
     return this.caption;
   }
   
   public final long getHwnd()
   {
     return this.hwnd;
   }
   
   public final int getIndex()
   {
     return this.index;
   }
   
   public final long getParent()
   {
     return this.parent;
   }
   
   public final String getWinClass()
   {
     return this.winClass;
   }
   
   public final void setCaption(String captionVal)
   {
     this.caption = captionVal;
   }
   
   public final void setHwnd(WinDef.HWND hwndVal)
   {
     this.hwnd = Pointer.nativeValue(hwndVal.getPointer());
   }
   
   public final void setHwnd(long hwndVal)
   {
     this.hwnd = hwndVal;
   }
   
   public final void setIndex(int indexVal)
   {
     this.index = indexVal;
   }
   
   public final void setParent(WinDef.HWND parentVal)
   {
     this.parent = parentVal.getPointer().getLong(0L);
   }
   
   public final void setParent(long parentVal)
   {
     this.parent = parentVal;
   }
   
   public final void setWinClass(String winClassVal)
   {
     this.winClass = winClassVal;
   }
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.Win32Locator

 * JD-Core Version:    0.7.0.1

 */