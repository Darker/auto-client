/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PVP_net;

import autoclick.comvis.ScreenWatcher;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Jakub
 * This enum represents images used in the automated application - buttons, icons etc.
 */
public enum Images {

  //Green text indicating player has accepted invite to game
  INVITE_ACCEPTED("launcher_screens/search_objects/accepted.png"),
  //Green text indicating player has not yet accepted or declined invite to game
  INVITE_PENDING("launcher_screens/search_objects/pending.png"),
  VOID(null);
  
  public final String path;
  private BufferedImage img = null;
  private double color_sum[] = null;
  
  Images(String path) {
    this.path = path;
  }
  
  public BufferedImage getImg() throws IOException {
    if(this.img==null) {
       File file = new File(path);
       BufferedImage thing = null;
       this.img = ImageIO.read(file);
    }
    return img;    
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
  public BufferedImage getImgScaled(float scale) throws IOException {
    getImg();
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    return scaleOp.filter(img, after);
  }
  
}
