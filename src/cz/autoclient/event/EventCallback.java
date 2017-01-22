/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

import java.lang.reflect.InvocationTargetException;

/**
 * Simple event callback that can be passed to event dispatched (my EventEmitter interface)
 * @author Jakub
 */
public class EventCallback {
  /**
   * Object to which is this listener assigned. But as we have public setSource, it can be anything.
   */
  private EventEmitter source;
  public void setSource(EventEmitter src) {
    source = src; 
  }
  /** Retrieve object to which this event listener is assigned.
   * @return can be any object that implements EventEmitter
   */
  public EventEmitter getSource() {
    return source; 
  }
  /** This function will be called when the event is dispatched. Any parameters can be passed :(
   * This function will try to find useable callback and throw when fails.
   * @param parameters anything, even nothing. This is really dangerous and unreliable!
   * @throws java.lang.Exception if anything fails
   */
  public void event(Object... parameters) throws Exception {
    java.lang.reflect.Method method = null;
    if(parameters==null)
      parameters = new Object[] {};
    try {
      method = getClass().getMethod("event", EventEmitter.objectsToClasses(parameters));
    } catch (NoSuchMethodException ex) {
      throw new NoCallbackException("No valid callback was found for parameter types.");
    } catch (SecurityException ex) {
      throw new NoCallbackException("Security exception when calling callback.", ex);
    }
    try {
      method.invoke(this, parameters);
    } catch (IllegalAccessException ex) {
      throw new NoCallbackException("IllegalAccessException when calling callback.", ex);
    } catch (IllegalArgumentException ex) {
      throw new NoCallbackException("IllegalArgumentException when calling callback.", ex);
    } catch (InvocationTargetException ex) {
      throw new NoCallbackException("InvocationTargetException when calling callback.", ex);
    }
  }
}
