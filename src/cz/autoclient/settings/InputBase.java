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
public abstract class InputBase implements Input {

  protected ValueChanged onchange;
  protected SettingsInputVerifier<Object> verifier;
  //Indicate whether events have been bound to input
  private boolean bound = false;

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
}
