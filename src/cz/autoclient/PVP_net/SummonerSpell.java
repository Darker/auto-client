/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.GUI.LazyLoadedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 */
public enum SummonerSpell {
  Ignite("Ignite", "SummonerDot.png"),
  Heal("Heal", "SummonerHeal.png"),
  Barrier("Barrier", "SummonerBarrier.png"),
  Boost("Cleanse", "SummonerBoost.png"),
  Clairvoyance("Clairvoyance", "SummonerClairvoyance.png"),
  Exhaust("Exhaust", "SummonerExhaust.png"),
  Flash("Flash", "SummonerFlash.png"),
  Ghost("Ghost", "SummonerHaste.png"),
  Clarity("Clarity", "SummonerMana.png"),
  Garrison("Garrison", "OdinGarrison.png"),
  Smite("Smite", "SummonerSmite.png"),
  Teleport("Teleport", "SummonerTeleport.png"),
  ;
  public static final String dir = "images/";
  public final String name;
  public final String filename;
  public final LazyLoadedImage image;
  SummonerSpell(String name, String filename) {
    this.name = name;
    this.filename = filename;
    image = new LazyLoadedImage(dir+filename);
  }
  public ImageIcon getIcon() {
    return image.getIcon();
  }
  
  public static SummonerSpell byName(String name) {
    SummonerSpell[] values = SummonerSpell.values();
    for(SummonerSpell value:values) {
      if(name.equalsIgnoreCase(value.name))
        return value;
    }
    return null;
  }
}
