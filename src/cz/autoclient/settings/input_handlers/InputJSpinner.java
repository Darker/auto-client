/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.input_handlers;

import cz.autoclient.settings.Input;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.ValueChanged;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jakub
 */
public class InputJSpinner implements Input {
  private final JSpinner field;
  private final ValueChanged onchange;
  private SettingsInputVerifier<Object> verifier;
  

  //Indicate whether events have been bound to input
  private boolean bound = false;
  public InputJSpinner(JSpinner input, ValueChanged onchange, SettingsInputVerifier<Object> subverifier) {
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
    //Event to be called if new value is valid
    final ValueChanged onchange = this.onchange;
    //Only works when you leave the field
    /*field.setInputVerifier(new InputVerifier() {
      @Override
      public boolean verify(JComponent in) {
        if(verif==null) {
          onchange.changed((Boolean)((JSpinner)in).isSelected());
          return true;
        }
        //If verification fails, return false and ignore the value
        if(!verif.verify(in))
          return false;
        //Sucessful verification means we get the value and update it
        onchange.changed(verif.value(in));
        return true;
      }
    });*/
    
    field.addChangeListener(
      new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          if(verif==null) {
            onchange.changed((Number)field.getValue());
            return;
          }
          //If verification fails, return false and ignore the value
          if(!verif.verify(field))
            return;
          //Sucessful verification means we get the value and update it
          onchange.changed(verif.value(field));
        }
      }
    );
    
  }
  @Override
  public JComponent getField() {
    return field;
  }

  @Override
  public Object getValue() {
    if(verifier==null)
      return field.getValue();
    else if(validate())
      return verifier.value(field);
    else
      return null;
  }
  @Override
  public void setValue(Object value) {
    if(value instanceof Number) 
      field.setValue(value);
  }

  @Override
  public boolean validate() {
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


  
}
