/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.comvis;

import cz.autoclient.autoclick.Rect;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
   public static void drawPoint(BufferedImage target, int x, int y, int size, Color color) {
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.drawLine(x-size, y, x+size, y);
     graph.drawLine(x, y-size, x, y+size);
     graph.dispose();
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
   public static void displayImage(Image image, String message) {
     JLabel label = new JLabel(new ImageIcon(image));

     JPanel panel = new JPanel();
     panel.add(label);

     JScrollPane scrollPane = new JScrollPane(panel);
     
     JOptionPane.showMessageDialog(null, scrollPane, message, javax.swing.JOptionPane.INFORMATION_MESSAGE);
   }
   public static void displayImage(Image image) {
     displayImage(image, "Debug");
   }
   static BufferedImage cloneImage(BufferedImage bi) {
     ColorModel cm = bi.getColorModel();
     boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
     WritableRaster raster = bi.copyData(null);
     return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
   }
}
