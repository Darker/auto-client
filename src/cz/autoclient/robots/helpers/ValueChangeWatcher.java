/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.helpers;

/**
 * Using given methods, this class will allow you to check whether a value
 * has changed. Robots use this to trigger self when something changes.
 * @author Jakub
 * @param <T> Object type to be watcher for changes
 */
public class ValueChangeWatcher<T> {
  /**
   * Remembering inital value allows the class to {@link ValueChangeWatcher#reset()} later on.
   */
  public final T inital;
  public ValueChangeWatcher() {inital = null;}
  public ValueChangeWatcher(T inital) {
    this.inital = inital;
    oldvalue = inital; 
  }
  /** Remembered old value which will be compared to any given value and 
   *  then re-assigned.
   */
  protected T oldvalue = null;
  /**
   * Reset the class to original state.
   */
  public void reset() {
    oldvalue = inital;
    changed = false;    
  }
  
  
  protected boolean changed = false;
  /** Will return true IFF the value changed now during this call. Will return false 
   *  even if changed=true.
   * 
   * @param value value to compare
   * @return true if value is not equal to oldvalue
   */
  public boolean checkValueChanged(T value) {
    boolean state = (oldvalue!=null && value==null) || (value!=null && !value.equals(oldvalue));
    changed = state || changed;
    oldvalue = value;
    return state;
  }
  /** Will return true if value changed now or in the past.
   * 
   * @param value
   * @return 
   */
  public boolean checkValueChangedSometime(T value) {
    return checkValueChanged(value) || changed;
  }
  /**
   * After calling this, {@link ValueChangeWatcher#hasChanged()} will
   * return false again. Usually you'll want to call this after you perform 
   * the onchange action.
   */
  public void resetChanged() {
    changed = false;
    
  }
  public boolean hasChanged() {
    return changed; 
  }
  
}
