 package sirius.classes;
 
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.Shell32;
 import com.sun.jna.platform.win32.WinDef;
 import sirius.constants.IICCConsts;
 import sirius.core.CommCtl;
 import sirius.core.DlgWin32API;
 import sirius.core.GDI32Ext;
 import sirius.core.Kernel32Ext;
 import sirius.core.User32Ext;
 
 public class Common
   implements IICCConsts
 {
   private final int wordShift = 16;
   private Shell32 shell32 = Shell32.INSTANCE;
   private User32Ext user32 = User32Ext.INSTANCE;
   private GDI32Ext gdi32 = GDI32Ext.INSTANCE;
   private DlgWin32API dlg32 = DlgWin32API.INSTANCE;
   private Kernel32Ext kernel32 = Kernel32Ext.INSTANCE;
   private CommCtl commCtl32 = CommCtl.INSTANCE;
   
   public final Shell32 getShell32()
   {
     return this.shell32;
   }
   
   public final GDI32Ext getGDI32()
   {
     return this.gdi32;
   }
   
   public final User32Ext getUser32()
   {
     return this.user32;
   }
   
   public final DlgWin32API getDlg32()
   {
     return this.dlg32;
   }
   
   public final Kernel32Ext getKernel32()
   {
     return this.kernel32;
   }
   
   public final CommCtl getCommCtl32()
   {
     return this.commCtl32;
   }
   
   public final void initCommonControls()
   {
     CommCtl.INITCOMMONCONTROLSEX lpInitCtrls = new CommCtl.INITCOMMONCONTROLSEX();
     lpInitCtrls.dwICC = 65535;
     this.commCtl32.InitCommonControlsEx(lpInitCtrls);
   }
   
   public final WinDef.HMENU longToHmenu(long input)
   {
     WinDef.HMENU handle = new WinDef.HMENU();
     handle.setPointer(Pointer.createConstant(input));
     return handle;
   }
   
   public final WinDef.HWND longToHwnd(long input)
   {
     WinDef.HWND handle = new WinDef.HWND();
     handle.setPointer(Pointer.createConstant(input));
     return handle;
   }
   
   public final long makeLong(int a, int b)
   {
     return a | b << 16;
   }
   
   public final WinDef.LPARAM makeLParam(int a, int b)
   {
     WinDef.LPARAM lParam = new WinDef.LPARAM(makeLong(a, b));
     return lParam;
   }
   
   public final WinDef.WPARAM makeWParam(int a, int b)
   {
     WinDef.WPARAM wParam = new WinDef.WPARAM(makeLong(a, b));
     return wParam;
   }
   
   public final void postMessage(long hwnd, int msg, int wparam, int lparam)
   {
     this.user32.PostMessage(longToHwnd(hwnd), msg, new WinDef.WPARAM(wparam), new WinDef.LPARAM(lparam));
   }
   
   public final int sendMessage(long hwnd, int msg, int wparam, int lparam)
   {
     return this.user32.SendMessage(longToHwnd(hwnd), msg, new WinDef.WPARAM(wparam), new WinDef.LPARAM(lparam));
   }
 }



