/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.input_handlers;

import cz.autoclient.settings.Input;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.ValueChanged;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class InputJComboBox implements Input {
  private final JComboBox field;
  private final ValueChanged onchange;
  private SettingsInputVerifier<Object> verifier;
  

  //Indicate whether events have been bound to input
  private boolean bound = false;
  public InputJComboBox(JComboBox input, ValueChanged onchange, SettingsInputVerifier<Object> subverifier) {
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
    field.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        changed();
      }
    });
    
    field.addActionListener (new ActionListener () {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed();
        }
    });
  }
  protected void changed() {
    if(verifier==null) {
      onchange.changed(field.getEditor().getItem());
    }
    //If verification fails, return false and ignore the value
    else if(verifier.verify(field))
      onchange.changed(verifier.value(field));

  }
  @Override
  public JComponent getField() {
    return field;
  }

  @Override
  public Object getValue() {
    if(verifier==null)
      return field.getEditor().getItem();
    else if(validate())
      return verifier.value(field);
    else
      return null;
  }
  @Override
  public void setValue(Object value) {
    field.getEditor().setItem(value);
    /*if(value!=null)
      System.out.println("Setting "+field.getClass().getName()+
                         " value to "+
                         value.getClass().getName()+" ("+value+")");
    else
      System.out.println("Setting "+field.getClass().getName()+
                         " value to null");
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " -> Value: "+getValue());*/
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
  }


  
}
