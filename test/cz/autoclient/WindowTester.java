package cz.autoclient;

import cz.autoclient.PVP_net.Constants;
import cz.autoclient.autoclick.MSWindow;
import cz.autoclient.autoclick.MouseButton;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.Window;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.autoclick.exceptions.APIError;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
   public static void main(String[] args) throws APIError
   {
     MSWindow test = MSWindow.windowFromName("PVP.net", false);
     System.out.println(test==null?"Fail.":"Success");
     if(test!=null) {
       //test.typeString("PoJus.'\\#`&[]*-č");
       Rect rect = test.getRect();
       System.out.println(rect);
       System.out.println("Ratio = " + rect.right + "/" + Constants.normalSize.right + " = " + Constants.sizeCoeficient(test.getRect()));
    
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
       catch(APIError e) {
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
