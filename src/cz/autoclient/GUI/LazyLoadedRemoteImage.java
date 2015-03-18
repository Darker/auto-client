/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 */
public class LazyLoadedRemoteImage extends LazyLoadedImage {
  public final URL url;
  public LazyLoadedRemoteImage(String path, URL url) {
    super(path, LazyLoadedImage.Type.FILE);
    this.url = url;
  }
  public LazyLoadedRemoteImage(String path, String url) {
    this(path, url_or_null_java_is_very_retarded(url));
  }
  public static URL url_or_null_java_is_very_retarded(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException ex) {
      return null;
    }
  }
  
  @Override
  public ImageIcon getIcon() {
    if(file.exists()) {
      return getIconFile(); 
    }
    else
      return getIconUrl();
  }
  @Override
  public Image getImage() {
    if(file.exists()) {
      return getImageFile(); 
    }
    else
      return getImageUrl();
  } 

  private Image getImageUrl() {
    if(image==null) {
      if(image_failed)
        return null;
      try {  
        image = ImageIO.read(url);
      } catch (IOException ex) {
        image=null;
        image_failed = true;
        return null;
      }
      try {
        file.getParentFile().mkdirs();
        ImageIO.write((RenderedImage)image, "png", file);
      } catch (IOException ex) {
        //Can't save image, will download it every time
      }
    }
    return image;
  }

  private ImageIcon getIconUrl() {
    if(icon==null) {
      getImage();
      if(image!=null)
        icon = new ImageIcon(image);
      else 
        return null;
    }
    return icon;
  }
}
