/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.helpers;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This extends basic watcher but additionally requires the value to be changed to something.
 * @author Jakub
 */
public class ValueChangeToWatcher<T> extends ValueChangeWatcher<T> {
  public final T requiredValue;
  public ValueChangeToWatcher() {
    requiredValue = null;
  };
  public ValueChangeToWatcher(T inital) {
    super(inital);
    //I'd call this() here but java is retarded
    requiredValue = null;
  };
  public ValueChangeToWatcher(T inital, T val) {
    super(inital);
    requiredValue = val;
  };
  @Override
  public boolean checkValueChanged(T value) {
    boolean state = (oldvalue==null && value==null && requiredValue==null)
                 || (value!=null && !value.equals(oldvalue) && value.equals(requiredValue));
    changed = state || changed;
    oldvalue = value;
    return state;
  }
  
  public boolean checkValueChangedDebug(T value) {
    if((oldvalue!=null && value==null) || (value!=null && !value.equals(oldvalue))) {
      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Value "+(value==null?oldvalue.getClass().getName():value.getClass().getName())+" changed from "+oldvalue+" to "+value);
    }
    
    boolean state = (oldvalue!=null && value==null && requiredValue==null)
                 || (value!=null && !value.equals(oldvalue) && value.equals(requiredValue));
    oldvalue = value;
    changed = state || changed;
    return state;
  }
}
