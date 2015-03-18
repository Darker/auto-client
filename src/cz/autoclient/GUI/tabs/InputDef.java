/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.tabs;

import cz.autoclient.settings.Settings;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class InputDef {
  private Settings setting;
  private JComponent field;
  public InputDef(JComponent f, Settings s) {
    setting = s;
    field = f;
  }
  public JComponent getField() {
    return field;
  }
}
