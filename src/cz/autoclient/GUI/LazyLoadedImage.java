/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import cz.autoclient.autoclick.comvis.DebugDrawing;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 */
public class LazyLoadedImage {
  public enum Type {
    RESOURCE, FILE; 
  }
  public final String path;
  public final Type type;
  /**
   * Represent file location of this image. Null if this is resource image.
   */
  public final File file;
  

  //Cache class refference and use the same class refference every time
  //  this should allow JVM to get some non-static functions optimized better
  public static final Class leclass = Type.FILE.getClass();
  //These will fill up on demand when needed
  protected ImageIcon icon = null;
  protected Image image = null;
  protected BufferedImage b_image = null;
  //If image has failed, we'll not try to load it again and will return null straight away
  protected boolean image_failed = false;
  
  private final String imageMutex = "ddd";
  
  private HashMap<Dimensions, BufferedImage> scaledInstances;
  
  private static class Dimensions {
    public final int x,y;

    public Dimensions(int x,int y){
      this.x = x;
      this.y = y;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result += prime * result + x;
      result += prime * result + y;
      return result;
    }

    @Override
    public boolean equals(Object o){
      if(o instanceof Dimensions){
        Dimensions p = (Dimensions) o;
        return p.x == x && p.y == y;
      }else{
        return false;
      }
    }
  }
  
  public LazyLoadedImage(String path, Type type) {
    this.path = path;
    this.type = type;
    if(type==Type.FILE)
      file = new File(path);
    else
      file = null;
  }
  public LazyLoadedImage(String path) {
    this(path, Type.FILE);
  }

  public boolean isFailed() {
    return image_failed;
  }
  
  
  public URL getClasspath() {
    return leclass.getResource(path);
  }
  public ImageIcon getIcon() {
    return type==Type.FILE?getIconFile():getIconResource(); 
  }
  public Image getImage() {
    return type==Type.FILE?getImageFile():getImageResource(); 
  }
  public BufferedImage getBufferedImage() {
    if(b_image!=null)
      return b_image;
    
    Image im = this.getImage();
    
    if(im instanceof BufferedImage) {
      return b_image = (BufferedImage)im;
    }
    else if(im!=null) {
      // Create a buffered image with transparency
      b_image = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);

      // Draw the image on to the buffered image
      Graphics2D bGr = b_image.createGraphics();
      bGr.drawImage(im, 0, 0, null);
      bGr.dispose();

      // Return the buffered image
      return b_image;
    }
    return null;
    //return type==Type.FILE?new BufferedImage(getImageFile()):new BufferedImage(getImageResource()); 
  }
  public BufferedImage getCropped(int frame) {
    BufferedImage src = getBufferedImage();
    return src!=null? src.getSubimage(frame, frame, src.getWidth()-2*frame, src.getHeight()-2*frame):null;
  }
  public BufferedImage getScaled(int width, int height, boolean saveToCache) {
    BufferedImage src = getBufferedImage();
    if(src!=null) {
      Dimensions d = new Dimensions(width, height);
      System.out.println("Scaling icon.");
      try {
        DebugDrawing.displayImage(src);
      } catch (InterruptedException ex) {
        Logger.getLogger(LazyLoadedImage.class.getName()).log(Level.SEVERE, null, ex);
      }
      if(scaledInstances!=null) {
        if(scaledInstances.containsKey(d)) {
          System.out.println("Already cached. Returning cache.");
          return scaledInstances.get(d);
        }
      }
      BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      AffineTransform at = new AffineTransform();
      at.scale(src.getWidth()/width, src.getHeight()/height);
      //System.out.println("["+xscale+", "+yscale+"]");
      AffineTransformOp scaleOp = 
         new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
      scaled = scaleOp.filter(src, scaled);
      if(saveToCache) {
        if(scaledInstances==null)
          scaledInstances = new HashMap();
        System.out.println("Saving to cache.");
        scaledInstances.put(d, scaled);
      }      
      return scaled;
    }
    return null;
  }
  public Image getImageFile() {
    if(image==null && !image_failed) {
      synchronized(imageMutex) {
        if(image==null) {
          File file = new File(path);
          try {
            image = ImageIO.read(file);
          }
          catch(IOException e) {
            image=null;
            image_failed = true;
          }
        }
      }
     }
    return image;    
  }
  public ImageIcon getIconFile() {
    if(image==null) {
       getImageFile();
    }
    if(image!=null && icon==null) {
      icon = new ImageIcon(image);
    }
    return icon; 
  }
  private synchronized ImageIcon getIconResource() {
    if(icon==null) {
      if(image!=null) {
        icon = new ImageIcon(image);
      }
      else {
        try {
          icon = new ImageIcon(leclass.getResource(path));
        }
        catch(NullPointerException e) {
          icon = null; 
        }
      }
    }
    return icon;
  }
  /** Loads, or just retrieves from cache, the image.
   *  @return Image (not necesarily a BufferedImage) or null on failure
  */
  private synchronized Image getImageResource() {
    //Lazy load...
    if(image==null) {
      //Since the .jar is constant (it's packed) we can
      //Remember the image is unavailable
      if(image_failed)
        return null;
      //System.out.println("  LoadImage(\""+leclass.getResource(".")+"\")");
      //Use whatever is stored in Icon if we have it
      if(icon!=null) {
        image = icon.getImage();
      }
      //Load from .jar
      else {
        try {
          image = ImageIO.read(leclass.getResourceAsStream(path));
        }
        //While only IOException is reported it also can throw InvalidArgumentException
        // when read() argument is null
        catch(Exception e) {
          image_failed = true;
        }
      }
    }
    return image;
  }
}
