/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

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
  
  private static final Border emptyBorder = BorderFactory.createEmptyBorder();
  protected SummonerSpell spell;

  
  public ButtonSummonerSpell(SummonerSpell spell) {
    super(spell.getIcon());
    this.spell = spell;
    this.setBorder(emptyBorder);
    this.setContentAreaFilled(false);
  }
  
  public void setSpell(SummonerSpell spell) {
    this.setIcon(spell.getIcon());
    this.spell = spell; 
  }
  public SummonerSpell getSpell() {
    return spell;
  }
}
