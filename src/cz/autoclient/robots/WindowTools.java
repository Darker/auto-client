/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots;

import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.ColorPixel;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.exceptions.APIError;
import java.awt.Color;
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
   public static void click(Window window, PixelOffset pos)  throws APIError {
 
     Rect rect = window.getRect();
     window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));

   }
   public static void click(Window window, ColorPixel pos) throws APIError {
   
     Rect rect = window.getRect();
     window.click((int)(rect.width * pos.x), (int)(rect.height * pos.y));
 
   }

   public static boolean checkPoint(Window window, ComparablePixel point) throws APIError {
     if(point.getColor()==null)
       return false;
     if(point.getTolerance()<1) {
       Rect rect = window.getRect();
       return point.getColor().equals(
                  window.getColor(
                      (int)(rect.width * point.getX()),
                      (int)(rect.height * point.getY())
                  )
       );
     }
     else {
       return checkPoint(window, point, point.getTolerance());
     }
   }
   public static int checkPoint(Window window, ComparablePixel... points) throws APIError {
     int matches = 0;
     if(points.length<3) {
       for(ComparablePixel point:points) {
         if(checkPoint(window, point))
           matches++;
       }
     }
     else {
       BufferedImage img = window.screenshot();
       for(ComparablePixel point:points) {
         if(checkPoint(img, point, point.getTolerance()))
           matches++;
       }      
     }
     return matches;
   }
   
   public static boolean checkPoint(Window window, ComparablePixel point, int tolerance) throws APIError {
     return checkPoint(window, point, tolerance, null);
   }
   public static boolean checkPoint(Window window, ComparablePixel point, int tolerance, String debug) throws APIError {
     Color b = point.getColor();
     if(b==null)
       return false;
     
     Rect rect = window.getRect();
     
     Color a = window.getColor((int)(rect.width * point.getX()), (int)(rect.height * point.getY()));
     
     if(debug!=null) {
       System.out.println("DEBUG#"+debug+" checkPoint("+point.toSource()+"), "+tolerance+")");
       System.out.println("   Comparing to: "+a);
       System.out.println("    R: "+Math.abs(a.getRed() - b.getRed())+" => "+(Math.abs(a.getRed() - b.getRed()) < tolerance));
       System.out.println("    G: "+Math.abs(a.getGreen() - b.getGreen())+" => "+(Math.abs(a.getGreen() - b.getGreen()) < tolerance));
       System.out.println("    B: "+Math.abs(a.getBlue() - b.getBlue())+" => "+(Math.abs(a.getBlue() - b.getBlue()) < tolerance));
     }
     return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
            (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
            (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
     

   }
   public static boolean checkPoint(BufferedImage img, ComparablePixel point) throws APIError {
     return checkPoint(img, point, point.getTolerance());
   }
   public static boolean checkPoint(BufferedImage img, ComparablePixel point, int tolerance) throws APIError {
     Color b = point.getColor();
     if(b==null)
       return false;
 
     Color a = getColor(img, (int)point.realX(img.getWidth()), (int)point.realY(img.getHeight()));

     return (Math.abs(a.getRed() -   b.getRed())   < tolerance) &&
            (Math.abs(a.getGreen() - b.getGreen()) < tolerance) &&
            (Math.abs(a.getBlue() -  b.getBlue())  < tolerance);
   }

   public static boolean checkPoint(BufferedImage img, PixelOffset point, int tolerance) throws APIError {
     return checkPoint(img, point.offset(0, 0), tolerance);
   }
   public static Color getColor(BufferedImage img, int x, int y) {
     int pixel = img.getRGB(x, y);
     return new Color(((pixel&0x00FF0000)>>16),((pixel&0x0000FF00)>>8), (pixel&0x000000FF)); 
   }
   public static int[] diffPoint(Window window, ColorPixel point) throws APIError {
     if(point.color==null)
       return new int[] {255,255,255};
     
     Rect rect = window.getRect();
     Color a = window.getColor((int)(rect.width * point.x), (int)(rect.height * point.y));
     Color b = point.color;

     return new int[] {(int)Math.abs(a.getRed() -   b.getRed()),
                       (int)Math.abs(a.getGreen() - b.getGreen()),
                       (int)Math.abs(a.getBlue() -  b.getBlue())};
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel p, int tolerance) throws APIError {
     Rect preal = p.toRect(img.getWidth(), img.getHeight());
     DebugDrawing.drawPoint(
        img, 
        preal.left, 
        preal.top, 
        5,
        checkPoint(img, p, tolerance)?java.awt.Color.GREEN:java.awt.Color.RED,
        (p instanceof Enum)?((Enum)p).name() : null
     );
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel pixelOffset) throws APIError {
     drawCheckPoint(img, pixelOffset, pixelOffset.getTolerance());
   }
   public static void drawCheckPoint(BufferedImage img, ComparablePixel[] pixels) throws APIError {
     for(ComparablePixel p : pixels) {
       drawCheckPoint(img, p);
     }
   }
   public static void showDrawCheckPoint(BufferedImage img, ComparablePixel[] pixels) throws APIError, InterruptedException {
     drawCheckPoint(img, pixels);
     DebugDrawing.displayImage(img);
   }
   
   public static void say(Window w, String text, Rect field) throws APIError, InterruptedException {
     if(text==null || text.isEmpty())
       return;
     w.slowClick(field, 30);
     w.typeString(text);
     w.keyDown(13);
     w.keyUp(13);
   }
   public static void say(Window w, String text, PixelOffset field) throws APIError, InterruptedException {
     if(text==null || text.isEmpty())
       return;
     say(w, text, field.toRect(w.getRect()));
   }


}
