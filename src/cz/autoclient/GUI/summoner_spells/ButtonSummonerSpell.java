/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

import cz.autoclient.GUI.ImageResources;
import cz.autoclient.PVP_net.SummonerSpell;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 *
 * @author Jakub
 */
public class ButtonSummonerSpell extends JButton {
  
  public static final Border emptyBorder = BorderFactory.createEmptyBorder();
  protected SummonerSpell spell;

  
  public ButtonSummonerSpell(SummonerSpell spell) {
    super(spell!=null?spell.getIcon():ImageResources.SUMMONER_SPELLS_NOSPELL.getIcon());
      
    this.spell = spell;
    this.setBorder(emptyBorder);
    this.setContentAreaFilled(false);
  }
  
  public void setSpell(SummonerSpell spell) {
    if(spell!=null)
      this.setIcon(spell.getIcon());
    else 
      this.setIcon(ImageResources.SUMMONER_SPELLS_NOSPELL.getIcon());
    this.spell = spell; 
  }
  public SummonerSpell getSpell() {
    return spell;
  }
}
