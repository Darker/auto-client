/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.league_of_legends.SummonerSpell;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * Used to calculate how much must be summoner spell images desaturated to match the images in game lobby.
 * @author Jakub
 */
public class CalculateDesaturateRatio {
  public static class ValueRange implements Iterator<Double> {

    public ValueRange(double start, double end, double step) {
      if(step==0)
        throw new IllegalArgumentException("Cannot create iterable range with 0 step.");
      this.step = Math.abs(step);
      this.start = Math.min(start, end);
      this.end = Math.max(start, end);
      current = start;
    }
    public final double start;
    public final double end;
    public final double step;
    protected double current;

    @Override
    public boolean hasNext() {
      return current+step<=end;
    }

    @Override
    public Double next() {
      double tmp = current;
      current+=step;
      return tmp;
    }
  }
  /**
   * @param args the command line arguments
   */
//  public static String srcImage = "C:\\MYSELF\\programing\\java\\AutoCall\\newclient\\TELEPORT_DESATURATED.png";
//  public static String srcSpell = "SummonerTeleport";
  public static String srcImage = "C:\\MYSELF\\programing\\java\\AutoCall\\newclient\\HEAL_SMALL_DARK.png";
  public static String srcSpell = "SummonerHeal";
  
  public static void main(String[] args) throws Throwable {
    BufferedImage teleport = DebugDrawing.loadFromPath(srcImage);
    SummonerSpell s = ConstData.lolData.getSummonerSpells().get(srcSpell);
    BufferedImage small_icon_no_crop = s.img.getScaledDiscardOriginal(teleport.getWidth(), teleport.getHeight());
    
    double[] teleportColorSum = ScreenWatcher.colorSum(teleport);
    final float start = 0.05F;
    final float end = 0.116F;
    final float step = 0.0001F;
    final float multiplier = -1f;
    float current = start;
    
    ValueRange brightness = new ValueRange(0.0, 0.4, 0.01);

    
    double minDiff = Double.MAX_VALUE;
    double bestSChange = 0;
    double bestBChange = 0;
    BufferedImage bestImage = null;
    while(brightness.hasNext()) {
      double brightnessChange = (-1.0)*brightness.next();
      ValueRange saturation = new ValueRange(0.0, 0.4, 0.01);   
      while(saturation.hasNext()) {
        double saturationChange = (-1.0)*saturation.next();

        BufferedImage clone = DebugDrawing.cloneImage(small_icon_no_crop);
        ScreenWatcher.changeHSB(clone, (float)saturationChange, (float)brightnessChange);
        //DebugDrawing.displayImage(clone);
        //DebugDrawing.displayImage(teleport);
        double dist = dist(teleportColorSum, ScreenWatcher.colorSum(clone));
        //System.out.println("DIST: "+dist+" ["+saturationChange+", "+brightnessChange+"]");
        if(minDiff>dist) {
          minDiff = dist;
          bestSChange = saturationChange;
          bestBChange = brightnessChange;
          bestImage = clone;
        }
      }
    }
    System.out.println("BEST: DIST: "+minDiff+" ["+bestSChange+", "+bestBChange+"]");
    BufferedImage demonstration = new BufferedImage(teleport.getWidth()*2+1, teleport.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D graph = demonstration.createGraphics();
    graph.setColor(Color.RED);
    graph.drawLine(teleport.getWidth(), 0, teleport.getWidth(), teleport.getHeight());
    graph.drawImage(teleport, null, 0, 0);
    graph.drawImage(bestImage, null, teleport.getWidth()+1, 0);
    graph.dispose();
    DebugDrawing.displayImage(demonstration);
  }

  public static double dist(double[] color1, double[] color2) {
    double dist = 0;
    for(int i=0; i<3; ++i) {
      dist+=(color1[i]-color2[i])*(color1[i]-color2[i]);
    }
    return dist;
  }
  
}
