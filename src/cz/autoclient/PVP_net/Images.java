/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.comvis.ScreenWatcher;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 * This enum represents images used in the automated application - buttons, icons etc.
 */
public enum Images {

  //Green text indicating player has accepted invite to game
  INVITE_ACCEPTED("images/search_objects/accepted.png"),
  //Same as above but cropped from the right side where friend list is
  INVITE_ACCEPTED_SMALL("images/search_objects/accepted_small.png"),
  //Green text indicating player has not yet accepted or declined invite to game
  INVITE_PENDING("images/search_objects/pending.png"),

  VOID(null);
  
  public final String path;
  private BufferedImage img = null;
  private ImageIcon icon = null;
  private HashMap<Float, BufferedImage> scaled;
  private double color_sum[] = null;
  
  Images(String path) {
    this.path = path;
  }
  
  public BufferedImage getImg() throws IOException {
    if(img==null) {
       File file = new File(path);
       img = ImageIO.read(file);
    }
    return img;    
  }
  public ImageIcon getIcon() throws IOException {
    if(img==null) {
       getImg();
    }
    if(img!=null && icon==null) {
      icon = new ImageIcon(img);
    }
    return icon; 
  }
  public double[] getColorSum() throws IOException {
    if(color_sum==null)
      color_sum = ScreenWatcher.colorSum(this.getImg());
    return color_sum;
  }
  public int getWidth() {
    try {
      return this.getImg().getWidth();
    }
    catch(IOException e) {
      return -1; 
    }
  }
  public int getHeight() {
    try {
      return this.getImg().getHeight();
    }
    catch(IOException e) {
      return -1; 
    }
  }
  @Override
  public String toString() {
    return name()+"["+getWidth()+" x "+getHeight()+"](\""+path+"\")";
    
  }
  public BufferedImage getImgScaled(float scale) throws IOException {
    if(scaled.containsKey(scale)) {
      return scaled.get(scale);
    }
    getImg();
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    
    
    BufferedImage img_scaled = scaleOp.filter(img, after);
    scaled.put(scale, img_scaled);
    return img_scaled;
  }
  
}
