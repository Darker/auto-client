/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.settings.Input;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.ValueChanged;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public abstract class AutomatSettingListener implements Input {
  SettingsInputVerifier<Object> verif;

  
  @Override
  public JComponent getField() {
    return null;
  }

  @Override
  public abstract Object getValue();

  @Override
  public boolean validate() {
    return false;
  }

  @Override
  public SettingsInputVerifier<Object> getVerifier() {
    return verif;
  }

  @Override
  public void setVerifier(SettingsInputVerifier<Object> ver) {
    verif=ver;
  }
  // These two do nothing as this class never invokes any changes
  @Override
  public void bind() {}

  @Override
  public void unbind() {}
  
}
