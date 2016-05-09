/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.main_automation.ChampionImages;
import cz.autoclient.main_automation.ChampionImages.ColorInfo;
import java.awt.Color;
import java.io.File;
import java.util.Map.Entry;

/**
 *
 * @author Jakub
 */
public class ChampAvgColors {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    ChampionImages imgs = new ChampionImages(new File("LOLResources"));
    for(Entry<String, ColorInfo> e: imgs) {
      System.out.println(e.getKey()+" => "+e.getValue().toString());
    }
  }
  
}
