 package sirius.core.types;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.Structure;
 import com.sun.jna.platform.win32.BaseTSD;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.platform.win32.WinNT;
 import java.util.Arrays;
 import java.util.List;
 
 public abstract interface WinDefExt
   extends WinDef
 {
   public static class HBRUSH
     extends WinNT.HANDLE
   {
     HBRUSH() {}
     
     HBRUSH(Pointer p)
     {
       super();
     }
   }
   
   public static class MENUBARINFO
     extends Structure
   {
     public int cbSize = size();
     public WinDef.RECT rcBar;
     public WinDef.HMENU hMenu;
     public WinDef.HWND hwndMenu;
     public boolean fBarFocused;
     public boolean fFocused;
     
     protected List getFieldOrder()
     {
       return Arrays.asList(new String[] { "cbSize", "rcBar", "hMenu", "hwndMenu", "fBarFocused", "fFocused" });
     }
   }
   
   public static class MENUINFO
     extends Structure
   {
     public int cbSize = size();
     public WinDef.DWORD fMask;
     public WinDef.DWORD dwStyle;
     public WinDefExt.UINT cyMax;
     public WinDefExt.HBRUSH hbrBack;
     public WinDef.DWORD dwContextHelpID;
     public BaseTSD.ULONG_PTR dwMenuData;
     
     protected List getFieldOrder()
     {
       return Arrays.asList(new String[] { "cbSize", "fMask", "dwStyle", "cyMax", "hbrBack", "dwContextHelpID", "dwMenuData" });
     }
   }
   
   public static class MENUITEMINFO
     extends Structure
   {
     public int cbSize = size();
     public WinDefExt.UINT fMask;
     public WinDefExt.UINT fType;
     public WinDefExt.UINT fState;
     public WinDefExt.UINT wID;
     public WinDef.HMENU hSubMenu;
     public WinDef.HBITMAP hbmpChecked;
     public WinDef.HBITMAP hbmpUnchecked;
     public BaseTSD.ULONG_PTR dwItemData;
     public String dwTypeData;
     public WinDefExt.UINT cch;
     public WinDef.HBITMAP hbmpItem;
     
     protected List getFieldOrder()
     {
       return Arrays.asList(new String[] { "cbSize", "fMask", "fType", "fState", "wID", "hSubMenu", "hbmpChecked", "hbmpUnchecked", "dwItemData", "dwTypeData", "cch", "hbmpItem" });
     }
   }
   
   public static class UINT
     extends WinDef.DWORD
   {
     private static final long serialVersionUID = 1L;
     
     UINT() {}
     
     UINT(long arg0)
     {
       super();
     }
   }
   
   public static class WINDOWPLACEMENT
     extends Structure
   {
     public int length;
     public int flags;
     public int showCmd;
     public WinDef.POINT ptMinPosition;
     public WinDef.POINT ptMaxPosition;
     public WinDef.RECT rcNormalPosition;
     
     protected List getFieldOrder()
     {
       return Arrays.asList(new String[] { "length", "flags", "showCmd", "ptMinPosition", "ptMaxPosition", "rcNormalPosition" });
     }
   }
 }



/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar

 * Qualified Name:     sirius.core.types.WinDefExt

 * JD-Core Version:    0.7.0.1

 */