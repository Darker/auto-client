package cz.autoclient.experiments;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import cz.autoclient.Main;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrame;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.autoclick.windows.MouseButton;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.ClickProxy;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub
 */
public class WindowTester {
   public static void main(String[] args) throws Exception
   {
    /*JComboBox<String> combo = new JComboBox<>(new String[] {"bar", "item"});
    combo.setEditable(true);

    JButton button = new JButton("Get");
    button.addActionListener((ActionEvent e) -> {
        System.out.println(combo.getSelectedItem());
    });

    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.getContentPane().add(combo);
    frame.getContentPane().add(button);
    frame.pack();
    frame.setVisible(true);*/
     
     // "LoL Patcher" "firefox"  
     MSWindow test = MSWindow.windowFromName("[GUIDE] Suppress PVP.net", false);
     System.out.println(test==null?"Fail.":"Success");
     if(test!=null) {
       //test.typeString("PoJus.'\\#`&[]*-č");
       /*Rect rect = test.getRect();
       System.out.println(rect);
       System.out.println("Ratio = " + rect.right + "/" + ConstData.smallestSize.right + " = " + ConstData.sizeCoeficient(test.getRect()));
     */
       //test.slowClick(288, 114, 100);
       //wintree(test);
       //9, 643, 62, 496
       //Rect testrect = Rect.byWidthHeight(100, 100, 100, 50);
       /*BufferedImage scrn = test.screenshot();
       DebugDrawing.drawPointOrRect(scrn, testrect, Color.red);
       DebugDrawing.displayImage(scrn);*/
       //DebugDrawing.displayImage(test.screenshotCrop(testrect));
       /*while(true) {
         System.out.println("Taking screenshot:");
         //DebugDrawing.displayImage(ScreenWatcher.resampleImage(MSWindow.screenshotAll(), 0.8, 0.8));
         
         //Thread.sleep(4000);
       }*/
       /*WinUser.HOOKPROC hkprc = new WinUser.HOOKPROC() {
         
       };
       test.UserExt.SetWindowsHookEx(7, hkprc, null, test.UserExt.GetWindowThreadProcessId(test.hwnd, null));*/
       //testPercentRect(test);
       ClickProxy proxy = new ClickProxy(test);
       proxy.start();
       while(proxy.isRunning()) {
         System.out.println("Minimized: "+test.isMinimized());
         System.out.println("Visible: "+test.isVisible());
         System.out.println("---------------------------");
         if(test.isMinimized()) {
           test.restoreNoActivate();
         }
         
         Thread.sleep(1000);
         //test.close();
       }
     }
     System.out.println("Main over.");
     //TestGetByPID(6568);
   }
   private static void testPercentRect(Window window) throws Exception {
     BufferedImage scrn = window.screenshot();
     Rect testrect = ConstData.deNormalize(ImageFrame.ble, window.getRect());
     System.out.println("Created rectangle: "+testrect);
     DebugDrawing.drawPointOrRect(scrn, testrect, Color.red);
     DebugDrawing.displayImage(scrn);
     DebugDrawing.displayImage(window.screenshotCrop(testrect));
     
   }
   private static void wintree(Window window, int indent) {
     for(int i=0;i<indent; i++)
       System.out.print(" ");
     String title = window.getTitle();
     String w_class = MSWindow.getWindowClass(((MSWindow)window).hwnd);
     System.out.println(title+ " ["+w_class+"]");
     

     List<Window> windows = window.getChildWindows();
     for(Window child:windows) {
       wintree(child, indent+2);
     }
   }
   private static void wintree(Window window) {
     wintree(window, 0);
   }
   private interface LowLevelMouseProc extends HOOKPROC {
     LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam);
   }
   public class Point extends Structure {

     @Override
     protected List getFieldOrder() {
       throw new UnsupportedOperationException("Not supported yet.");
     }
     public class ByReference extends Point implements
       Structure.ByReference {
     };

     public NativeLong x;
     public NativeLong y;
   }

   public static class MOUSEHOOKSTRUCT extends Structure {
     @Override
     protected List getFieldOrder() {
       throw new UnsupportedOperationException("Not supported yet.");
     }
     public static class ByReference extends MOUSEHOOKSTRUCT implements
       Structure.ByReference {
     };

     public POINT pt;
     public HWND hwnd;
     public int wHitTestCode;
     public ULONG_PTR dwExtraInfo;
   }     
           
   public static void TestGetByPID(int pid) throws Exception {
     MSWindow test = MSWindow.windowFromPID(pid);
     System.out.println(test==null?"Fail.":"Success");
     if(test!=null) {
       //test.typeString("PoJus.'\\#`&[]*-č");
       if(test.isMinimized())
         test.maximize();
       else
         test.minimize(true);
     }
   }
   public static void TestScreenshots(Window test) {
       long startTime = System.nanoTime();
       //BufferedImage crop_screen = test.screenshotCrop(80,80,50,50);
       BufferedImage screenshot = null;
       if(test.isMinimized()) {
         System.out.println("Restoring the window because it's minimised.");
         test.restoreNoActivate();
         test.repaint();
       }
       try {
         screenshot = test.screenshot();
       }
       catch(APIException e) {
         System.err.println("No screenshot. Error: "+e);
       }      
       if(screenshot!=null) {
         //Try to click on the thing given by an image:
         File img = new File("thing.png");
         BufferedImage thing = null;
         try {
           thing = ImageIO.read(img);
         }
         catch(IOException e) {
           System.err.println("Can't read thing.png: "+e);
         }
         if(thing!=null) {
           Rect pos = ScreenWatcher.findByExactMatch(thing, screenshot);
           if(pos!=null) {
              System.out.println("Found object: "+pos);

              test.click((int)pos.top+2, (int)pos.left+2, MouseButton.Left);
              //Draw rectangle on discovered position
              Graphics2D graph = screenshot.createGraphics();
              graph.setColor(Color.RED);

              graph.drawRect((int)pos.top, (int)pos.left, (int)pos.width, (int)pos.height);
              graph.dispose();
           }
           else
             System.err.println("Couln't find object!");
           try {
             ImageIO.write(screenshot, "png", new File("WindowTester.output.png"));
           } catch (IOException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
           }
         }
       }
   }
   public static BufferedImage loadFromPath(String path) {
     File img = new File(path);
     BufferedImage thing = null;
     try {
       thing = ImageIO.read(img);
     }
     catch(IOException e) {
       System.err.println("Can't read '"+path+"': "+e);
     }
     return thing;
   }
}
