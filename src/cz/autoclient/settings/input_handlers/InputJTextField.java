/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.input_handlers;

import cz.autoclient.settings.Input;
import cz.autoclient.settings.InputSecure;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.ValueChanged;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Jakub
 */
public class InputJTextField implements Input, InputSecure {
  private final JTextField field;
  private final ValueChanged onchange;
  private SettingsInputVerifier<Object> verifier;
  
  protected boolean secure = false;

  //Indicate whether events have been bound to input
  private boolean bound = false;
  public InputJTextField(JTextField input, ValueChanged onchange, SettingsInputVerifier<Object> subverifier) {
    field = input;
    this.onchange = onchange;
    verifier = subverifier;
  }
  /**
   * Binds the events to the field using InputVerifier
   */
  @Override
  public void bind() {
    bound = true;
    //Internal verifier
    final SettingsInputVerifier<Object> verif = this.verifier;
    //Only works when you leave the field
    field.setInputVerifier(new InputVerifier() {
      @Override
      public boolean verify(JComponent in) {
        if(verif==null) {
          onchange.changed((String)((JTextField)in).getText());
          return true;
        }
        //If verification fails, return false and ignore the value
        if(!verif.verify(in, false))
          return false;
        //Sucessful verification means we get the value and update it
        onchange.changed(verif.value(in));
        return true;
      }
    });
  }
  @Override
  public JComponent getField() {
    return field;
  }

  @Override
  public Object getValue() {
    if(verifier==null)
      return field.getText();
    else if(validate())
      return verifier.value(field);
    else
      return null;
  }
  /** Converts given object to string. Sets value to empty string if null is given.
   * 
   * @param value anything
   */
  @Override
  public void setValue(Object value) {
    if(value!=null)
      field.setText(value.toString());
    else
      field.setText("");
  }

  @Override
  public boolean validate() {
    //throw new UnsupportedOperationException("BLE");
    return verifier!=null?verifier.verify(field):true;
  }

  @Override
  public SettingsInputVerifier<Object> getVerifier() {
    return verifier;
  }
  /**
   * Change the internal verifier.
   */
  @Override
  public void setVerifier(SettingsInputVerifier<Object> ver) {
    verifier = ver;
    if(bound) {
      //Re-bind the event listener
      bind();
    }
  }
  
  
  @Override
  public void unbind() {
    bound = false;
    field.setInputVerifier(null);
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  @Override
  public void setSecure(boolean secure) {
    this.secure = secure;
  }  
}
