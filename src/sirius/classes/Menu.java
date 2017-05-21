 package sirius.classes;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.WinDef;
 import javax.jws.WebService;
 import sirius.constants.IMenuFlag;
 import sirius.constants.IWMConsts;
 import sirius.core.types.WinDefExt;

 
 @WebService
 public class Menu
   extends Common
   implements IMenuFlag, IWMConsts, WinDef
 {
   public int getMenuDefaultItem(long hMenu, int fByPos, int gmdiFlags)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuDefaultItem(handle, fByPos, gmdiFlags);
   }
   
   public boolean getMenuInfo(long hmenu, WinDefExt.MENUINFO lpcmi)
   {
     WinDef.HMENU handle = longToHmenu(hmenu);
     return getUser32().GetMenuInfo(handle, lpcmi);
   }
   
   public int getMenuItemCount(long hMenu)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuItemCount(handle);
   }
   
   public int getMenuItemID(long hMenu, int nPos)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuItemID(handle, nPos);
   }
   
   public boolean getMenuItemInfo(long hMenu, int uItem, boolean fByPosition, WinDefExt.MENUITEMINFO lpmii)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuItemInfo(handle, uItem, fByPosition, lpmii);
   }
   
   public boolean getMenuItemRect(long hWnd, long hMenu, int uItem, WinDef.RECT rect)
   {
     WinDef.HWND handle = longToHwnd(hWnd);
     WinDef.HMENU menuHandle = longToHmenu(hMenu);
     return getUser32().GetMenuItemRect(handle, menuHandle, uItem, rect);
   }
   
   public int getMenuState(long hMenu, int uId, int uFlags)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuState(handle, uId, uFlags);
   }
   
   public int getMenuString(long hMenu, int uIDItem, char[] lpString, int nMaxCount, int uFlag)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().GetMenuString(handle, uIDItem, lpString, nMaxCount, uFlag);
   }
   
   public long getSubMenu(long hMenu, int nPos)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return Pointer.nativeValue(getUser32().GetSubMenu(handle, nPos).getPointer());
   }
   
   public boolean isMenu(long hMenu)
   {
     WinDef.HMENU handle = longToHmenu(hMenu);
     return getUser32().IsMenu(handle);
   }
   
   public boolean pickItem(long hwnd, long hMenu, int iPos)
   {
     int count = getMenuItemCount(hMenu);
     if (count < iPos) {
       return false;
     }
     WinDef.HWND handle = longToHwnd(hwnd);
     
     WinDef.WPARAM wParam = new WinDef.WPARAM();
     WinDef.LPARAM lParam = new WinDef.LPARAM();
     int id = getMenuItemID(hMenu, iPos);
     
     wParam.setValue(id);
     lParam.setValue(0L);
     
     getUser32().PostMessage(handle, 273, wParam, lParam);
     
     return true;
   }
 }

