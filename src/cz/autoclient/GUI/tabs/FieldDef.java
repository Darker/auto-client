/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.tabs;

import cz.autoclient.settings.Settings;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jakub
 */
public class FieldDef {
  public final String setting_name;
  public final String title;
  public final String label_text;
  public final JPanel container;
  public final JLabel label;
  private JComponent field = null;
  public FieldDef(String lab,  String tt, String sn) {
    setting_name = sn;
    title = tt;
    label_text = lab;
    container = new JPanel();
    container.setLayout(new GridLayout(1, 1));
    container.setBorder(BorderFactory.createLineBorder(Color.red));

    label = new JLabel(lab);
    label.setHorizontalAlignment(JLabel.CENTER);
    container.add(label);
  }
  public void addField(JComponent field) {
    if(this.field!=null) {
      container.remove(this.field); 
    }
    field.setToolTipText(title);
    
    container.add(field);
    this.field = field;
  }
  public void attachToSettings(Settings set) {
    if(field!=null && setting_name!=null) {
      set.bindToInput(setting_name, field, true);
    }   
  }
}
