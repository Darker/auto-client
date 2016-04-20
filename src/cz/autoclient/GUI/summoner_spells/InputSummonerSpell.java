/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

import cz.autoclient.event.EventCallback;
import cz.autoclient.settings.Input;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.ValueChanged;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class InputSummonerSpell implements Input {
  private final ButtonSummonerSpellMaster field;
  private final ValueChanged onchange;
  private SettingsInputVerifier<Object> verifier;
  

  //Indicate whether events have been bound to input
  private boolean bound = false;
  public InputSummonerSpell(ButtonSummonerSpellMaster input, ValueChanged onchange, SettingsInputVerifier<Object> subverifier) {
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

    field.addEventListener("change", new EventCallback() {
      @Override
      public void event(Object... parameters) {
        if(parameters[0] instanceof String || parameters[0]==null) {
          //SummonerSpell value = (SummonerSpell)parameters[0];  
          onchange.changed(parameters[0]);
        }
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
      return field.getSpell();
    else if(validate())
      return verifier.value(field);
    else
      return null;
  }
  @Override
  public void setValue(Object value) {
    if(value instanceof String) {
      field.setSpell((String)value);
    }
    else 
      field.setSpell(null);
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
