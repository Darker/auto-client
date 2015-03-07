/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 */
public enum ImageResources {
  ICON("IconHighRes.png");
  
  public static final String basepath = "resources/";
  public static final ClassLoader loader = ImageResources.class.getClassLoader();
  public static final Class leclass = ImageResources.class;
  
  public final String path;
  private ImageIcon icon;
  private Image image = null;
  private boolean image_failed = false;
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
  public Image getImage() {
    if(image==null) {
      if(image_failed)
        return null;
      if(icon!=null) {
        image = icon.getImage();
      }
      else {
        try {
          image = ImageIO.read(leclass.getResourceAsStream("/images/grass.png"));
        }
        catch(IOException e) {
          image_failed = true;
        }
      }
    }
    return image;
  }
}
