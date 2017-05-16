/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrame;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.ColorPixel;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.GraphicPredicate;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.exceptions.APIException;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class WindowTools {
   public static void enter(Window window, int delay)
   {
     window.keyDown(13);
     if(delay>0) {
       try {
         Thread.sleep(delay);
       }
       catch(InterruptedException e) {};
     }
     window.keyUp(13);
   }
   public static void enter(Window window)
   {
     enter(window, 0);
   }
   public static void click(Window window, PixelOffset pos)  throws APIException {
 
     Rect rect = window.getRect();
     window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));

   }
   public static void click(Window window, ColorPixel pos) throws APIException {
   
     Rect rect = window.getRect();
     window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
 
   }

   public static boolean checkPoint(Window window, ComparablePixel point) throws APIException {
     if(point.getColor()==null)
       return false;
     if(point.getTolerance()<1) {
       Rect rect = window.getRect();
       if(rect.width==0 || rect.height==0)
         return false;
       //Return false automatically if the point is outside the window scope
       if(rect.width<point.getX() || rect.height<point.getY())
         return false;
       
       int x = (int)(rect.width * point.getX());
       int y = (int)(rect.height * point.getY());
       
       return point.getColor().equals(
                  window.getColor(
                      x,
                      y
                  )
       );
     }
     else {
       return checkPoint(window, point, point.getTolerance());
     }
   }
   public static int checkPoint(Window window, ComparablePixel... points) throws APIException {
     return checkPoint(window.screenshot(), points);
   }
   public static int checkPoint(BufferedImage img, ComparablePixel... points) throws APIException {
     int matches = 0;
     for(ComparablePixel point:points) {
       if(checkPoint(img, point))
         matches++;
       //else 
       //  System.out.println("Point "+point+" failed.");
     }
     return matches;
   }
   public static boolean checkPoint(Window window, ComparablePixel point, int tolerance) throws APIException {
     return checkPoint(window, point, tolerance, null);
   }
   public static boolean checkPoint(Window window, ComparablePixel point, int tolerance, String debug) throws APIException {
     Color b = point.getColor();
     if(b==null)
       return false;
     
     Rect rect = window.getRect();
     //When the client is minimized...
     if(rect.width<1 || rect.height < 1)
       return false;
     
     Color a = window.getColor((int)Math.round(rect.width * point.getX()), (int)Math.round(rect.height * point.getY()));
     
     if(debug!=null) {
       dbg("DEBUG#"+debug+" checkPoint("+point.toSource()+"), "+tolerance+")");
       dbg("   Comparing to: "+a);
       dbg("    R: "+Math.abs(a.getRed() - b.getRed())+" => "+(Math.abs(a.getRed() - b.getRed()) < tolerance));
       dbg("    G: "+Math.abs(a.getGreen() - b.getGreen())+" => "+(Math.abs(a.getGreen() - b.getGreen()) < tolerance));
       dbg("    B: "+Math.abs(a.getBlue() - b.getBlue())+" => "+(Math.abs(a.getBlue() - b.getBlue()) < tolerance));
     }
     return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
            (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
            (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
     

   }
   public static boolean checkPoint(BufferedImage img, ComparablePixel point) throws APIException {
     return checkPoint(img, point, point.getTolerance());
   }
   public static boolean checkPoint(BufferedImage img, ComparablePixel point, int tolerance) throws APIException {
     Color b = point.getColor();
     if(b==null)
       return false;
 
     Color a = getColor(img, (int)point.realX(img.getWidth()), (int)point.realY(img.getHeight()));

     return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
            (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
            (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
   }

   public static boolean checkPoint(BufferedImage img, PixelOffset point, int tolerance) throws APIException {
     return checkPoint(img, point.offset(0, 0), tolerance);
   }
   public static Color getColor(BufferedImage img, int x, int y) {
     int pixel = img.getRGB(x, y);
     return new Color(((pixel&0x00FF0000)>>16),((pixel&0x0000FF00)>>8), (pixel&0x000000FF)); 
   }
   public static int[] diffPoint(Window window, ColorPixel point) throws APIException {
     if(point.color==null)
       return new int[] {255,255,255};
     
     Rect rect = window.getRect();
     Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
     Color b = point.color;

     return new int[] {(int)Math.abs(a.getRed() -   b.getRed()),
                       (int)Math.abs(a.getGreen() - b.getGreen()),
                       (int)Math.abs(a.getBlue() -  b.getBlue())};
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel p, int tolerance) throws APIException {
     Rect preal = p.toRect(img.getWidth(), img.getHeight());
     boolean result = checkPoint(img, p, tolerance);

     DebugDrawing.drawPoint(
        img, 
        preal.left, 
        preal.top, 
        5,
        result?java.awt.Color.GREEN:java.awt.Color.RED,
        (p instanceof Enum)?((Enum)p).name() : null
     );
   }
   public static boolean waitForPixel(Window window, ComparablePixel pixelOffset, int timeout)
   throws InterruptedException
   {
     long timeStarted = System.currentTimeMillis();
     long time = timeStarted;
     final int sleepBaseTime = 30;
     
     while(timeout<0 || time-timeStarted<timeout) {
       //BufferedImage screenshot = window.screenshot();
       if(checkPoint(window, pixelOffset)) {
         return true;
       }
       else {
         //dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - pixel failed.");
         //drawCheckPoint(screenshot, pixelOffset);
         //DebugDrawing.displayImage(screenshot);
       }
       time = System.currentTimeMillis();
       // Calculate time to sleep so that it never goes over timeout
       long sleep = time+sleepBaseTime-timeStarted<timeout?sleepBaseTime:timeout-(time-timeStarted);
       //dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - sleep for "+sleep+"ms");
       if(sleep>0) {
         Thread.sleep(sleep);
         time = System.currentTimeMillis();
       }
       else
         break;
     }
     dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - timeout returning false for "+pixelOffset.toSource());
     return false;
   }
   
   public static boolean waitForPredicate(Window window, GraphicPredicate predicate, int timeout)
   throws InterruptedException
   {
     long timeStarted = System.currentTimeMillis();
     long time = timeStarted;
     final int sleepBaseTime = 30;
     
     while(timeout<0 || time-timeStarted<timeout) {
       if(predicate.test(window)) {
         return true;
       }
       else {
         //dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - pixel failed.");
         //drawCheckPoint(screenshot, pixelOffset);
         //DebugDrawing.displayImage(screenshot);
       }
       time = System.currentTimeMillis();
       // Calculate time to sleep so that it never goes over timeout
       long sleep = time+sleepBaseTime-timeStarted<timeout?sleepBaseTime:timeout-(time-timeStarted);
       //dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - sleep for "+sleep+"ms");
       if(sleep>0) {
         Thread.sleep(sleep);
         time = System.currentTimeMillis();
       }
       else
         break;
     }
     dbg("T: "+ (System.currentTimeMillis()-timeStarted)+" - timeout returning false for predicate.");
     return false;
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel pixelOffset) throws APIException {
     drawCheckPoint(img, pixelOffset, pixelOffset.getTolerance());
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel[] pixels) throws APIException {
     for(ComparablePixel p : pixels) {
       drawCheckPoint(img, p);
     }
   }
   public static void showDrawCheckPoint(BufferedImage img, ComparablePixel[] pixels) throws APIException, InterruptedException {
     drawCheckPoint(img, pixels);
     DebugDrawing.displayImage(img);
   }
   
   public static BufferedImage getNormalizedScreenshot(Window window) {
      Rect winRect = window.getRect();
      double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
      return normalizeImage(
          window.screenshot(),
          winSizeCoef);
   }
   
   public static BufferedImage getNormalizedScreenshot(Window window, ImageFrame crop) {
      Rect winRect = window.getRect();
      Rect cropRect = crop.multiplyBySize(winRect);
      double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
      return normalizeImage(
          window.screenshotCrop(cropRect),
          winSizeCoef);
   }
   
   public static BufferedImage normalizeImage(BufferedImage i, double coefficient) {
     return ScreenWatcher.resampleImage(
          i,
          coefficient, coefficient);
   }

   public static void say(Window w, String text, Rect field) throws APIException, InterruptedException {
     if(text==null || text.isEmpty())
       return;
     w.slowClick(field, 30);
     w.typeString(text);
     w.keyDown(KeyEvent.VK_ENTER);
     w.keyUp(KeyEvent.VK_ENTER);
   }
   public static void say(Window w, String text, PixelOffset field) throws APIException, InterruptedException {
     if(text==null || text.isEmpty())
       return;
     say(w, text, field.toRect(w.getRect()));
   }
   
   public static void dbg(String str) {
     System.out.println("[WINDOW-TOOLS] "+str); 
   }
}
