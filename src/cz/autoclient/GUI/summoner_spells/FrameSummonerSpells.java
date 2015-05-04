/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

import cz.autoclient.GUI.ToolTipTimer;
import cz.autoclient.settings.Settings;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.league_of_legends.SummonerSpell;
import cz.autoclient.league_of_legends.maps.SummonerSpells;

/**
 *
 * @author Jakub
 */
public class FrameSummonerSpells extends JDialog {
  private static final javax.swing.border.Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
  
  protected final Settings settings;
  protected final ButtonSummonerSpellMaster parent;
  public FrameSummonerSpells(ButtonSummonerSpellMaster par, Settings set) {
    super(SwingUtilities.getWindowAncestor(par));
    
    parent = par;
    settings = set;
    // Remove title bar, close buttons...
    setUndecorated(true); 
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    //Set some styles
    JPanel contentPane = (JPanel)getContentPane();
    contentPane.setBorder(border);
    //Will populate the selection of objects in grid layout
    createButtons();
    //Will make the frame as large as the content
    pack();
    //Do not appear by default
    super.setVisible(false); 
    //Hide when deactivated
    this.addWindowListener(new WindowAdapter()
     {
       @Override
       public void windowDeactivated(WindowEvent e) {
         FrameSummonerSpells.this.setVisible(false);
       }
     });
  }
  @Override
  public void setVisible(boolean visible) {
    if(visible) {
      //Get location of the button
      Point location = parent.getLocationOnScreen();
      //Move the popup at the location of the
      //Also move vertically so it appears UNDER the button
      setLocation(location.x, location.y+parent.getSize().height);
    }
    super.setVisible(visible); 
  }
  private void createButtons() {
    //Get all existing SummonerSpells
    SummonerSpells spells = ConstData.lolData.getSummonerSpells();
    int length = spells.size();
    //cz.autoclient.PVP_net.SummonerSpell[] values = cz.autoclient.PVP_net.SummonerSpell.values();
   
    //int length = values.length;
    //Calculate number of rows and cells for the frame grid
    //We add 1 to length because there will be allways the NULL button
    int width = (int)Math.ceil(Math.sqrt(length+1)),
        height = (int)Math.round(Math.sqrt(length+1));
    //Layout will automatically distribute spells evenly in the grid
    GridLayout layout = new GridLayout(height, width, 5, 5);
    //Get content panel to add buttons in
    Container contentPane = getContentPane();
    contentPane.setLayout(layout);
    //Temporary variable
    ButtonSummonerSpell b;
    //Listener for clicks
    ActionListener list = new buttonOnclick();
    //Create all the buttons
    for(SummonerSpell spell : spells) {
      b = new ButtonSummonerSpell(spell.jsonKey);
      b.addActionListener(list);
      b.addMouseListener(ToolTipTimer.INSTANT_TOOLTIP);
      b.setToolTipText(spell.name);
      contentPane.add(b);
    }
    /*for(int i=0, l=values.length; i<l; i++) {
      b = new ButtonSummonerSpell(values[i]);
      b.addActionListener(list);
      b.addMouseListener(ToolTipTimer.INSTANT_TOOLTIP);
      b.setToolTipText(values[i].name);
      contentPane.add(b);
    }*/
    b = new ButtonSummonerSpell(null);
    b.addActionListener(list);
    b.addMouseListener(ToolTipTimer.INSTANT_TOOLTIP);
    b.setToolTipText("Do not change spell");
    contentPane.add(b);

    
  }
  private class buttonOnclick implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      parent.setSpellSafe(((ButtonSummonerSpell)e.getSource()).getSpell());
      FrameSummonerSpells.this.setVisible(false);
    }
  }
}
