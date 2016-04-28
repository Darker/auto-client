/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.league_of_legends.Champion;
import cz.autoclient.league_of_legends.LoLVersion;
import cz.autoclient.league_of_legends.SummonerSpell;
import cz.autoclient.league_of_legends.maps.Champions;
import cz.autoclient.league_of_legends.maps.GameObjectMap;
import cz.autoclient.league_of_legends.maps.SummonerSpells;
import java.io.File;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class TestGameObjectMap {

  public static void main(String[] args) throws Exception {
    LoLVersion version = new LoLVersion(LoLVersion.Realm.NA, new File("test_data"), true);
    
    GameObjectMap<Champion> champs = new Champions(version, true);
    
    int iter = 0;
    /*for(Champion ch : champs) {
      System.out.println(ch.name);
      if(iter++<5) {
        ch.img.getBufferedImage();
        if(ch.img.isFailed())
          System.out.println("Couldn't download image from "+ch.img.url);
        else
          DebugDrawing.displayImage(ch.img.getBufferedImage(), ch.name);
      }
      else break;
    }*/
    
    GameObjectMap<SummonerSpell> spells = new SummonerSpells(version, true);
    iter = 0;
    for(SummonerSpell s : spells) {
      System.out.println(s.name);
      if(iter++<50) {
        s.img.getBufferedImage();
        if(s.img.isFailed())
          System.out.println("Couldn't download image from "+s.img.url);
        else
          DebugDrawing.displayImage(s.img.getBufferedImage(), s.name);
      }
      else break;
    }
    
    //Test the caching of lists
    List<String> l1 = champs.enumValues(Champions.getName);
    List<String> l2 = champs.enumValues(Champions.getName, true);
    if(l1==l2) 
      throw new Error("Different list instances are equal!");
    if(l2!= champs.enumValues(Champions.getName, true))
      throw new Error("enumValues failed to cache the results.");
  }
  
}
