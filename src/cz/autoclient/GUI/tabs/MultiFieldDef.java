/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.tabs;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Jakub
 */
public class MultiFieldDef extends FieldDef {
  private ArrayList<InputDef> fields = new ArrayList<>();
  //All fields will be grouped into this panel
  private final JPanel group = new JPanel();
  public MultiFieldDef(String label) {
    super(label, null, null);
    container.add(group);
    //group.setBorder(BorderFactory.createLineBorder(Color.BLUE));
  }
  
  @Override
  @Deprecated
  public void addField(JComponent field) {
    throw new UnsupportedOperationException("Not supported for multiple inputs.");
  }
  public void addField(JComponent field, Settings set, Setnames setting_name, String tooltip) {
    InputDef f = new InputDef(field, set);
    if(tooltip!=null && !tooltip.isEmpty())
      field.setToolTipText(tooltip);
    if(set!=null)
      set.bindToInput(setting_name.name, field);
    group.add(field);
    fields.add(f);
  }
  public void packEven() {
    
    //Change the layout so that all elements are evenly distributed
    group.setLayout(new GridLayout(1, fields.size()));
  }
  public void packWeighted(double... weights) {
    GridBagLayout lay = new GridBagLayout();
    
    for(int i=0,l=fields.size(); i<l; i++) {
      JComponent field = fields.get(i).getField();
      
      GridBagConstraints c = new GridBagConstraints();
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Settings for "+field.getClass().getName()+" at "+i+":");
      if(weights.length>i && weights[i]>0) {
        c.weightx = weights[i]; 
        c.fill = GridBagConstraints.HORIZONTAL;
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      fill = GridBagConstraints.HORIZONTAL");
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      weightx = "+weights[i]);
      }
      else {
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      fill = GridBagConstraints.NONE");
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      weightx = 0.0");
      }
      c.gridx = i;
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "      gridx = "+i);
      
      lay.setConstraints(field, c);
    }
    group.setLayout(lay);
  }
  public void addField(JComponent field, Settings set, Setnames setting_name) {
    addField(field, set, setting_name, null);
  }
  @Override
  public void attachToSettings(Settings set) {
    throw new UnsupportedOperationException("Not supported for multiple inputs.");
  }
}
