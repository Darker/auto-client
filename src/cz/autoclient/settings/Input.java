/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;

import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public interface Input {
  /** Retrieve the field associated with this abstract input.
   * @return JComponent field. Use `instanceof` to check for type.
   */
  public JComponent getField();
  /**
   * Will put the best possible representation of the value in the input.
   * @param value to appear in the input
   */
  public void setValue(Object value);
  /** Parse input field value and turn it into object.
   * @return Object representing parsed value of the input field
   */
  public Object getValue();
  /**
   * Check if the value in JComponent is valid using the associated verifier.
   * @return true if the value is valid and can be turned into type <T>
   */
  public boolean validate();
  /**
   * Retrieve the internal verifier.
   * @return SettingsInputVerifier
   */
  public SettingsInputVerifier<Object> getVerifier();
  /**
   * Change the internal verifier.
   * @param ver verifier to replace the original input verifier. Pass null to skip value verification.
   */
  public void setVerifier(SettingsInputVerifier<Object> ver);
  /**
   * Add the verifier
   */
  public void bind();
  /**
   * Remove the verifier from input, do not call onchange event any more
   */
  public void unbind();

}
