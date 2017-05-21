 package sirius.classes;
 
 import com.sun.jna.Memory;
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.GDI32;
 import com.sun.jna.platform.win32.User32;
 import com.sun.jna.platform.win32.WinDef;
 import com.sun.jna.platform.win32.WinGDI;
 import com.sun.jna.platform.win32.WinNT;
 import com.sun.jna.platform.win32.WinUser;
 import java.awt.Color;
 import java.awt.image.BufferedImage;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.InputStreamReader;
 import java.util.logging.Level;
 import java.util.logging.Logger;
import sirius.constants.IMKConsts;
import sirius.constants.IWMConsts;
 import sirius.core.GDI32Ext;
 import sirius.core.User32Ext;
 import sirius.core.types.WinDefExt;
 
 public class Window
   extends Common
   implements IWMConsts, IMKConsts
 {
   public void activate(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().SetForegroundWindow(handle);
   }
   
   public void click(long hwnd, int button, int x, int y, boolean isControl, boolean isAlt, boolean isShift)
   {
     mouseDown(hwnd, button, x, y, isControl, isAlt, isShift);
     mouseUp(hwnd, button, x, y, isControl, isAlt, isShift);
   }
   
   public void click(long hwnd, int button, double x, double y, boolean isControl, boolean isAlt, boolean isShift)
   {
     mouseDown(hwnd, button, (int)x, (int)y, isControl, isAlt, isShift);
     mouseUp(hwnd, button, (int)x, (int)y, isControl, isAlt, isShift);
   }
   
   public void close(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDef.WPARAM wParam = new WinDef.WPARAM();
     WinDef.LPARAM lParam = new WinDef.LPARAM();
     
     wParam.setValue(0L);
     lParam.setValue(0L);
     getUser32().PostMessage(handle, 16, wParam, lParam);
   }
   
   public BufferedImage capture(long hWnd)
   {
     WinDef.DWORD SRCCOPY = new WinDef.DWORD(13369376L);
     getUser32();WinDef.HDC hdcWindow = User32Ext.INSTANCE.GetDC(longToHwnd(hWnd));
     WinDef.HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);
     WinDef.RECT bounds = getClientRect(hWnd);
     int width = bounds.right - bounds.left;
     int height = bounds.bottom - bounds.top;
     WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);
     WinNT.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
     getGDI32();GDI32Ext.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, SRCCOPY);
     GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
     GDI32.INSTANCE.DeleteDC(hdcMemDC);
     WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
     bmi.bmiHeader.biWidth = width;
     bmi.bmiHeader.biHeight = (-height);
     bmi.bmiHeader.biPlanes = 1;
     bmi.bmiHeader.biBitCount = 32;
     bmi.bmiHeader.biCompression = 0;
     Memory buffer = new Memory(width * height * 4);
     GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, 0);
     BufferedImage image = new BufferedImage(width, height, 1);
     image.setRGB(0, 0, width, height, buffer.getIntArray(0L, width * height), 0, width);
     GDI32.INSTANCE.DeleteObject(hBitmap);
     User32.INSTANCE.ReleaseDC(longToHwnd(hWnd), hdcWindow);
     return image;
   }
   
   public BufferedImage capture(long hWnd, int w, int h)
   {
     WinDef.DWORD SRCCOPY = new WinDef.DWORD(13369376L);
     getUser32();WinDef.HDC hdcWindow = User32Ext.INSTANCE.GetDC(longToHwnd(hWnd));
     WinDef.HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);
     WinDef.RECT bounds = getClientRect(hWnd);
     int width = w;
     int height = h;
     WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);
     WinNT.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
     getGDI32();GDI32Ext.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, SRCCOPY);
     GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
     GDI32.INSTANCE.DeleteDC(hdcMemDC);
     WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
     bmi.bmiHeader.biWidth = width;
     bmi.bmiHeader.biHeight = (-height);
     bmi.bmiHeader.biPlanes = 1;
     bmi.bmiHeader.biBitCount = 32;
     bmi.bmiHeader.biCompression = 0;
     Memory buffer = new Memory(width * height * 4);
     GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, 0);
     BufferedImage image = new BufferedImage(width, height, 1);
     image.setRGB(0, 0, width, height, buffer.getIntArray(0L, width * height), 0, width);
     GDI32.INSTANCE.DeleteObject(hBitmap);
     User32.INSTANCE.ReleaseDC(longToHwnd(hWnd), hdcWindow);
     return image;
   }
   
   public boolean isRunning(String process)
   {
     boolean found = false;
     try
     {
       File file = File.createTempFile("process", ".vbs");
       file.deleteOnExit();
       FileWriter fw = new FileWriter(file);
       
       String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\nSet locator = CreateObject(\"WbemScripting.SWbemLocator\")\nSet service = locator.ConnectServer()\nSet processes = service.ExecQuery _\n (\"select * from Win32_Process where name='" + process + "'\")\n" + "For Each process in processes\n" + "wscript.echo process.Name \n" + "Next\n" + "Set WSHShell = Nothing\n";
       
 
 
 
 
 
 
 
 
       fw.write(vbs);
       fw.close();
       Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
       BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
       
 
 
       String line = input.readLine();
       if ((line != null) && 
         (line.equals(process))) {
         found = true;
       }
       input.close();
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
     return found;
   }
   
   public void doubleClick(long hwnd, int button, int x, int y, boolean isControl, boolean isAlt, boolean isShift)
   {
     int message = 0;
     int flags = 0;
     switch (button)
     {
     case 0: 
       message = 515;
       break;
     case 1: 
       message = 518;
       break;
     case 2: 
       message = 521;
       break;
     default: 
       message = 515;
     }
     if (isControl) {
       flags |= 0x8;
     }
     if (isShift) {
       flags |= 0x4;
     }
     sendMessage(hwnd, message, flags, makeLParam(x, y).intValue());
   }
   
   public WinDef.RECT getClientRect(long hwnd)
   {
     WinDef.RECT result = new WinDef.RECT();
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().GetClientRect(handle, result);
     return result;
   }
   
   public long getDesktopWindow()
   {
     return 0L;
   }
   
   public long getMenu(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDef.HMENU hmenu = getUser32().GetMenu(handle);
     return Pointer.nativeValue(hmenu.getPointer());
   }
   
   private Color convertRGB(Color rgb)
   {
     return new Color(rgb.getBlue(), rgb.getGreen(), rgb.getRed());
   }
   
   public Color getColor(long cid, int x, int y)
   {
     BufferedImage buf = new BufferedImage(2, 2, 5);
     getUser32();WinDef.HDC hdcWindow = User32Ext.INSTANCE.GetDC(longToHwnd(cid));
     buf.setRGB(1, 1, getGDI32().GetPixel(hdcWindow, x, y));
     Color cRet = convertRGB(new Color(buf.getRGB(1, 1)));
     buf.flush();
     return cRet;
   }
   
   public Color getColor(long cid, double x, double y)
   {
     BufferedImage buf = capture(cid);
     Color cRet = new Color(buf.getRGB((int)x, (int)y));
     buf.flush();
     return cRet;
   }
   
   public Color getColor(double x, double y, BufferedImage buf)
   {
     Color cRet = new Color(buf.getRGB((int)x, (int)y));
     
     return cRet;
   }
   
   public boolean screenCheck(Color rgb, int x, int y, int tolerance, BufferedImage buf)
   {
     Color pixel = getColor(x, y, buf);
     if ((pixel != null) && 
       (Math.abs(pixel.getRed() - rgb.getRed()) < tolerance) && (Math.abs(pixel.getGreen() - rgb.getGreen()) < tolerance) && (Math.abs(pixel.getBlue() - rgb.getBlue()) < tolerance)) {
       return true;
     }
     return false;
   }
   
   public boolean pixelCheck(long cid, Color rgb, int x, int y, int tolerance)
   {
     Color pixel = getColor(cid, x, y);
     if ((pixel != null) && 
       (Math.abs(pixel.getRed() - rgb.getRed()) < tolerance) && (Math.abs(pixel.getGreen() - rgb.getGreen()) < tolerance) && (Math.abs(pixel.getBlue() - rgb.getBlue()) < tolerance)) {
       return true;
     }
     return false;
   }
   
   public WinDef.RECT getRect(long hwnd)
   {
     WinDef.RECT result = new WinDef.RECT();
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().GetWindowRect(handle, result);
     return result;
   }
   
   public long getSystemMenu(long hwnd, boolean revert)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDef.HMENU hmenu = getUser32().GetSystemMenu(handle, revert);
     return Pointer.nativeValue(hmenu.getPointer());
   }
   
   public String getText(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     int length = getUser32().GetWindowTextLength(handle) + 1;
     char[] buf = new char[length];
     
     getUser32().GetWindowText(handle, buf, length);
     String text = String.valueOf(buf).trim();
     return text;
   }
   
   public boolean getWindowPlacement(long hwnd, WinDefExt.WINDOWPLACEMENT placement)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     return getUser32().GetWindowPlacement(handle, placement);
   }
   
   public boolean isEnabled(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     return getUser32().IsWindowEnabled(handle);
   }
   
   public boolean isMaximized(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDefExt.WINDOWPLACEMENT placement = new WinDefExt.WINDOWPLACEMENT();
     getUser32().GetWindowPlacement(handle, placement);
     return placement.showCmd == 3;
   }
   
   public boolean isMinimized(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDefExt.WINDOWPLACEMENT placement = new WinDefExt.WINDOWPLACEMENT();
     getUser32().GetWindowPlacement(handle, placement);
     return placement.showCmd == 2;
   }
   
   public boolean isNormal(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDefExt.WINDOWPLACEMENT placement = new WinDefExt.WINDOWPLACEMENT();
     getUser32().GetWindowPlacement(handle, placement);
     return placement.showCmd == 1;
   }
   
   public boolean isUnicode(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     return getUser32().IsWindowUnicode(handle);
   }
   
   public boolean isVisible(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     return getUser32().IsWindowVisible(handle);
   }
   
   public boolean isWindow(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     return getUser32().IsWindow(handle);
   }
   
   public void keyDown(long hwnd, int key)
   {
     sendMessage(hwnd, 256, key, 0);
   }
   
   public void keyPress(long hwnd, int key)
   {
     sendMessage(hwnd, 258, key, 0);
   }
   
   public void stringPress(long hwnd, String text)
   {
     for (char key : text.toCharArray()) {
       sendMessage(hwnd, 258, key, 0);
     }
   }
   
   public void keyUp(long hwnd, int key)
   {
     sendMessage(hwnd, 257, key, 0);
   }
   
   public long FindWindow(final String name)
   {
     final long[] WindowID = { 0L };
     getUser32().EnumWindows(new WinUser.WNDENUMPROC()
     {
       int count = 0;
       
       public boolean callback(WinDef.HWND hWnd, Pointer arg1)
       {
         String wText = Window.this.getText(hWnd.hashCode());
         if ((WindowID[0] != 0L) || (wText.isEmpty()) || (!wText.contains(name)) || (!Window.this.isWindow(hWnd.hashCode()))) {
           return true;
         }
         WindowID[0] = hWnd.hashCode();
         Logger.getLogger(Window.class.getName()).log(Level.INFO, "Found");
         return true;
       }
     }, null);
     
 
     return WindowID[0];
   }
   
   public void maximize(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().ShowWindow(handle, 3);
   }
   
   public void minimize(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().ShowWindow(handle, 6);
   }
   
   public void mouseDown(long hwnd, int button, int x, int y, boolean isControl, boolean isAlt, boolean isShift)
   {
     int message = 0;
     int flags = 0;
     switch (button)
     {
     case 0: 
       message = 513;
       break;
     case 1: 
       message = 516;
       break;
     case 2: 
       message = 519;
       break;
     default: 
       message = 513;
     }
     if (isControl) {
       flags |= 0x8;
     }
     if (isShift) {
       flags |= 0x4;
     }
     sendMessage(hwnd, message, flags, makeLParam(x, y).intValue());
   }
   
   public void mouseUp(long hwnd, int button, int x, int y, boolean isControl, boolean isAlt, boolean isShift)
   {
     int message = 0;
     int flags = 0;
     switch (button)
     {
     case 0: 
       message = 514;
       break;
     case 1: 
       message = 517;
       break;
     case 2: 
       message = 520;
       break;
     default: 
       message = 514;
     }
     if (isControl) {
       flags |= 0x8;
     }
     if (isShift) {
       flags |= 0x4;
     }
     sendMessage(hwnd, message, flags, makeLParam(x, y).intValue());
   }
   
   public void move(long hwnd, int x, int y, int width, int height)
   {
     moveTo(hwnd, x, y);
     sizeTo(hwnd, width, height);
   }
   
   public void moveTo(long hwnd, int x, int y)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDef.RECT rc = getRect(hwnd);
     getUser32().MoveWindow(handle, x, y, rc.right - rc.left, rc.bottom - rc.top, true);
   }
   
   public void restore(long hwnd)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     getUser32().ShowWindow(handle, 1);
   }
   
   public void sizeTo(long hwnd, int width, int height)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     WinDef.RECT rc = getRect(hwnd);
     getUser32().MoveWindow(handle, rc.left, rc.top, width, height, true);
   }
   
   public void start(long hwnd, String command, String params, String workingDir)
   {
     WinDef.HWND handle = longToHwnd(hwnd);
     getShell32().ShellExecute(handle, null, command, params, workingDir, 5);
   }
 }



