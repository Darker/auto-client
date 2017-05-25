/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

import cz.autoclient.GUI.ImageResources;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.league_of_legends.SummonerSpell;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 *
 * @author Jakub
 */
public class ButtonSummonerSpell extends JButton {
  
  public static final Border emptyBorder = BorderFactory.createEmptyBorder();
  protected String spell;

  
  public ButtonSummonerSpell(String spell) {
    super(spell!=null?findSpellIcon(spell):ImageResources.SUMMONER_SPELLS_NOSPELL.getIcon());
      
    this.spell = spell;
    this.setBorder(emptyBorder);
    this.setContentAreaFilled(false);
  }
  private static ImageIcon findSpellIcon(String name) {
    try {
      //return ConstData.lolData.getSummonerSpells().find(SummonerSpell.GET_NAME, name).img.getIcon();
      SummonerSpell spell = ConstData.lolData.getSummonerSpells().get(name);
      if(spell!=null) {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Spell: "+spell.img.url);
        return new ImageIcon(spell.img.getScaledDiscardOriginal(48,48));
      }
      else {
        /*Logger.getLogger(this.getClass().getName()).log(Level.INFO, "No spell at "+name+"\n   Available names: ");
        for(String sname:ConstData.lolData.getSummonerSpells().keySet()) {
           Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      "+sname);
        }*/
        return null;
      }
    }
    catch(NullPointerException e) {
      return null; 
    }
  }
  public void setSpell(String spell) {
    if(spell!=null)
      this.setIcon(findSpellIcon(spell));
    else 
      this.setIcon(ImageResources.SUMMONER_SPELLS_NOSPELL.getIcon());
    this.spell = spell; 
  }
  public String getSpell() {
    return spell;
  }
}
