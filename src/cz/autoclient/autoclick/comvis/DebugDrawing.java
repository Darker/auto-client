/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.comvis;

import cz.autoclient.autoclick.Rect;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Jakub
 */
public class DebugDrawing {
   public static void drawResult(BufferedImage target, Rect rect, Color color) {
     //silent fail for invalid result
     if(rect==null)
       return;
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.drawRect((int)rect.left, (int)rect.top, (int)rect.width, (int)rect.height);
     graph.dispose();
   }
   public static void drawResult(BufferedImage target, Rect rect, Color color, Color centerColor) {
     //silent fail for invalid result
     if(rect==null)
       return;
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.drawRect((int)rect.left, (int)rect.top, (int)rect.width, (int)rect.height);
     
     Rect mid = rect.middle();
     DebugDrawing.drawPoint(target, mid.left, mid.top, mid.width/3, color);
     graph.dispose();
   }
   public static void drawResult(BufferedImage target, ArrayList<Rect> rect, Color color) {
     for(int i=0,l=rect.size(); i<l; i++) {
       drawResult(target, rect.get(i), color); 
     }
   }
  public static void filledRect(BufferedImage target, Rect rect, Color color) {
     //silent fail for invalid result
     if(rect==null)
       return;
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.fillRect((int)rect.left, (int)rect.top, (int)rect.width, (int)rect.height);
     graph.dispose();
   }
   public static void drawText(BufferedImage target, int x, int y, String string, Color color) {
        Graphics2D g2d = target.createGraphics();

        g2d.setPaint(color);
        g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        y = y+fm.getHeight();
        g2d.drawString(string, x, y);
        g2d.dispose();
   }
   public static void drawPoint(BufferedImage target, int x, int y, int size, Color color, String name) {
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.drawLine(x-size, y, x+size, y);
     graph.drawLine(x, y-size, x, y+size);
     if(name!=null && name.length()>0) {
        graph.setFont(new Font("Courier New", Font.PLAIN, 12));
        FontMetrics fm = graph.getFontMetrics();
        y = y+fm.getHeight();
        x+=size+2;
        graph.drawString(name, x, y);
     }
     graph.dispose();
   }
   public static void drawPoint(BufferedImage target, int x, int y, int size, Color color) {
     drawPoint(target, x, y, size, color, null);
   }
   public static void drawPointOrRect(BufferedImage target, Rect rect, Color color) {
     if(rect.width>0 || rect.height>0) {
       drawResult(target, rect, color);
     }
     else {
       drawPoint(target, rect.left, rect.top, 5, color); 
     }
   }
   public static void drawPointOrRect(BufferedImage target,  Color color, Rect... rects) {
     for(Rect r:rects) {
       drawPointOrRect(target, r, color);
     }
   }
   public static void drawPointOrRect(BufferedImage target,  Color color, Iterable<? extends Rect> rects) {
     for(Rect r:rects) {
       drawPointOrRect(target, r, color);
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
   public static boolean saveToPath(String path, BufferedImage image) {
     File img = new File(path);
     try {
       ImageIO.write(image, "png", new File(path));
     } catch (IOException ex) {
       System.err.println("Failed to save image as '"+path+"'. Error:"+ex);
       return false;
     }
     return true;
   }
   public static void displayImage(final Image image, String message, boolean synchronous) throws InterruptedException {
     if(image==null)
       throw new IllegalArgumentException("No image to draw. Given image is null.");

     //The window
     JFrame frame = new JFrame();
     //Topmost component of the window
     Container main = frame.getContentPane();
     //Turns out this is probably the simplest way to render image on screen 
     //with guaranteed 1:1 aspect ratio
     JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
        @Override
        public Dimension getPreferredSize(){
            return new Dimension(image.getWidth(null), image.getHeight(null));
        }
     };
     panel.setSize(image.getWidth(null), image.getHeight(null));
     //Put the image drawer in the topmost window component
     main.add(panel);
     //System.out.println(image.getWidth(null)+", "+image.getHeight(null));
     //frame.pack();
     frame.setTitle(message+" ["+image.getWidth(null)+" x "+image.getHeight(null)+"]");
     //Set window size to the image size plus some padding dimensions
     frame.pack();
     frame.setVisible(true);
     final Thread t = Thread.currentThread();
     frame.addWindowListener(new WindowListener() {
       @Override
       public void windowOpened(WindowEvent e) {}
       @Override
       public void windowClosing(WindowEvent e) {
         synchronized(t) {t.notify();}
         //System.out.println("Closing");
         frame.dispose();
       }
       @Override
       public void windowClosed(WindowEvent e) {
         //System.out.println("Closed");
         //synchronized(t) {
           //t.notify();
         //}
       }
       @Override
       public void windowIconified(WindowEvent e) {}
       @Override
       public void windowDeiconified(WindowEvent e) {}
       @Override
       public void windowActivated(WindowEvent e) {}
       @Override
       public void windowDeactivated(WindowEvent e) {}
     });
     if(synchronous) {
       synchronized(t) {
         t.wait();
         //System.out.println("Wait over.");
       }
     }
     //JOptionPane.showMessageDialog(null, scrollPane, message, javax.swing.JOptionPane.INFORMATION_MESSAGE);
   }
   public static void displayImage(Image image) throws InterruptedException {
     displayImage(image, "Debug", true);
   }
   public static void displayImage(Image image, String title) throws InterruptedException {
     displayImage(image, title, true);
   }
   public static BufferedImage cloneImage(BufferedImage bi) {
     ColorModel cm = bi.getColorModel();
     boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
     WritableRaster raster = bi.copyData(null);
     return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
   }
}
