/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.tabs;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
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
  }
  
  @Override
  public void addField(JComponent field) {
    throw new UnsupportedOperationException("Not supported for multiple inputs.");
  }
  public void addField(JComponent field, Settings set, Setnames setting_name, String tooltip) {
    InputDef f = new InputDef(field, set);
    if(tooltip!=null && !tooltip.isEmpty())
      field.setToolTipText(tooltip);
    set.bindToInput(setting_name.name, field);
    group.add(field);
    fields.add(f);
  }
  public void addField(JComponent field, Settings set, Setnames setting_name) {
    addField(field, set, setting_name, null);
  }
  @Override
  public void attachToSettings(Settings set) {
    throw new UnsupportedOperationException("Not supported for multiple inputs.");
  }
}
