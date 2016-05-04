/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Jakub
 */
public class EventCallbackLambda extends EventCallback {
  public final AnyCallbackInterface cb;
  public EventCallbackLambda(AnyCallbackInterface i) {
    this.cb = i;
  }
  @Override
  public void event(Object... parameters) throws Exception {
    java.lang.reflect.Method method = null;
    if(parameters==null)
      parameters = new Object[] {};
    try {
      method = cb.getClass().getMethod("event", EventEmitter.objectsToClasses(parameters));
    } catch (NoSuchMethodException ex) {
      throw new NoCallbackException("No valid callback was found for parameter types:"
          +EventEmitter.classesToStrings(EventEmitter.objectsToClasses(parameters))
          +"\n"
          +EventEmitter.listCallbacks(cb)
       );
    } catch (SecurityException ex) {
      throw new NoCallbackException("Security exception when calling callback.", ex);
    }
    try {
      method.invoke(cb, parameters);
    } catch (IllegalAccessException ex) {
      throw new NoCallbackException("IllegalAccessException when calling callback.", ex);
    } catch (IllegalArgumentException ex) {
      throw new NoCallbackException("IllegalArgumentException when calling callback.", ex);
    } catch (InvocationTargetException ex) {
      throw new NoCallbackException("InvocationTargetException when calling callback.", ex);
    }
  }
}
