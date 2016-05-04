/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.summoner_spells;

import cz.autoclient.event.EventCallback;
import cz.autoclient.event.EventEmitter;
import cz.autoclient.settings.Settings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub
 */
public class ButtonSummonerSpellMaster extends ButtonSummonerSpell implements EventEmitter {
  private FrameSummonerSpells popup;
  protected Settings settings;
  private ButtonSummonerSpellMaster twin;
  public ButtonSummonerSpellMaster(String spell, Settings set) {
    super(spell);
    this.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showPopup();
      }
    });
  }
  public void setTwin(ButtonSummonerSpellMaster t) {
    twin = t;
    t.setTwinNoRecursion(this);
  }
  /*
  * Does not call setTwin on it's twin. This is obviously needed to prevent endless setTwin recursion
  */
  protected void setTwinNoRecursion(ButtonSummonerSpellMaster t) {
    twin = t;
  }
  public ButtonSummonerSpellMaster getTwin() {
    return twin; 
  }
  /** Check whether the twin has the same value as is the value that is about to be assigned.
   *  If it's so, it will give the twin it's value.  
   *    Example:
   *      THIS is about to turn to Flash from Heal
   *      TWIN is Flash too
   *      TWIN will become Heal
   *      THIS will become Flash
   * @param value The new spell used by this button
   */
  public void setSpellSafe(String value) {
    if(twin!=null && value!=null) {
      String twin_value = twin.getSpell();
      if(value.equals(twin_value)) {
        twin.setSpell(spell); 
      }
    }
    setSpell(value);
  }
  /** Override setSpell to allow event dispatching when value changes.
   *  Event is dispatched after the value changes.
   * @param value the new value just as in the original function
   */
  @Override
  public void setSpell(String value) {
    super.setSpell(value);
    dispatchEvent("change", value);
  }
  /** Silent set spell. Does not trigger the event.
   * @param value the new value just as in the original function
   */
  public void setSpellSilent(String value) {
    super.setSpell(value);
  }
  
  private void showPopup() {
    //Create the popup if needed
    if(popup==null)
      createPopup();
    popup.setVisible(true);
  }
  private void createPopup() {
    popup = new FrameSummonerSpells(this, null);    
  } 

  Map<String, List<EventCallback>> listeners = new HashMap<String, List<EventCallback>>();
  @Override
  public Map<String, List<EventCallback>> getListeners() {
    return listeners;
  }
}
