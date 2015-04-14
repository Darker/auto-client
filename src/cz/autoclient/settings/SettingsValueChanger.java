/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;

/**
 *
 * @author Jakub
 */
public interface SettingsValueChanger {
  /**
   * Set value to this input. Called when settings are loading values on input.
   * Allows you to convert objects to values on input fields.
   * @param comp
   * @param value 
   */
  public void setValue(javax.swing.JComponent comp, Object value);
}
