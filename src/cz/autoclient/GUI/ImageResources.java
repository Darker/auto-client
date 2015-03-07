/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Enum of resources used for GUI. These resources are packed in .jar and are for internal use.
 * Provides lazy-loaded Image and ImageIcon for comfort.
 * @author Jakub
 */
public enum ImageResources {
  ICON("IconHighRes.png");
  //So according to [this guy](http://stackoverflow.com/a/17007533/607407) I
  //  should enter classpath beginning with slash to make sure it's absolute path from
  //  the root of my .jar
  public static final String basepath = "/cz/autoclient/resources/";
  //Cache everything to have less letters to write
  public static final ClassLoader loader = ImageResources.class.getClassLoader();
  public static final Class leclass = ImageResources.class;
  //String is immutable so it's ok to make it a public constant
  public final String path;
  //These will fill up on demand when needed
  private ImageIcon icon;
  private Image image = null;
  //If image has failed, we'll not try to load it again and will return null straight away
  private boolean image_failed = false;
  //Constructor concatenates the individual path with the global path
  ImageResources(String path) {
    this.path = basepath+path;
  }
  
  public ImageIcon getIcon() {
    if(icon==null) {
      if(image!=null) {
        icon = new ImageIcon(image);
      }
      else {
        icon = new ImageIcon(loader.getResource(path));
      }
    }
    return icon;
  }
  /** Loads, or just retrieves from cache, the image.
   *  @return Image (not necesarily a BufferedImage) or null on failure
  */
  public Image getImage() {
    //Lazy load...
    if(image==null) {
      //Since the .jar is constant (it's packed) we can
      //Remember the image is unavailable
      if(image_failed)
        return null;
      //Use whatever is stored in Icon if we have it
      if(icon!=null) {
        image = icon.getImage();
      }
      //Load from .jar
      else {
        try {
          image = ImageIO.read(leclass.getResourceAsStream("/images/grass.png"));
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
