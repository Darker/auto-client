/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub
 */
public interface EventEmitter {
  /** Call all callbacks for given event name.
   * 
   * @param name string name of the event, such as "click", "mouseover" or "valueChanged".
   * @param parameters Anything. You yourself have to check for arguments... Bad!
   */
  public default void dispatchEvent(String name, Object... parameters) {
    //Retrieve the map of events
    Map<String, List<EventCallback>> events = getListeners();
    //Retrieve list of callbacks that corresponds to given String name
    List<EventCallback> callbacks = events.get(name);
    //If such list exists...
    if(callbacks!=null) {
      //...call every callback's `event()` with given parameters
      for(EventCallback callback : callbacks) {
        try {
          //oh yeah, put these random parameters in!
          //Surelly no NullPointer exceptions will occur!
          callback.event(parameters);
        } catch (Exception ex) {
          System.out.println("EVENT failed!");
          ex.printStackTrace();
        }
      }
    }
  }
  public default void addEventListener(String name, EventCallback event) {
    Map<String, List<EventCallback>> events = getListeners();
    List<EventCallback> callbacks = events.get(name);
    if(callbacks==null) {
      callbacks = new ArrayList<EventCallback>();
      events.put(name, callbacks);
    }
    event.setSource(this);
    callbacks.add(event);
  }
  public default void addEventListener(String name, ArgsNone i) {
    addEventListener(name, new LambdaNone(i));
  }
  public default <T> void addEventListener(String name, ArgsOne<T> i) {
    addEventListener(name, new LambdaOne(i));
  }
  public default <T1, T2> void addEventListener(String name, ArgsTwo<T1, T2> i) {
    addEventListener(name, new LambdaTwo(i));
  }
  public default <T1, T2, T3> void addEventListener(String name, ArgsThree<T1, T2, T3> i) {
    addEventListener(name, new LambdaThree(i));
  }
  public default <T1, T2, T3, T4> void addEventListener(String name, ArgsFour<T1, T2, T3, T4> i) {
    addEventListener(name, new LambdaFour(i));
  }
  
  public default void removeEventListener(String name, EventCallback event) {
    Map<String, List<EventCallback>> events = getListeners();
    List<EventCallback> callbacks = events.get(name);
    if(callbacks!=null) {
      for (Iterator<EventCallback> iterator = callbacks.iterator(); iterator.hasNext();) {
        if(iterator.next()==event)
          iterator.remove();
      }
    }
  }

  
  public Map<String, List<EventCallback>> getListeners();
  
  public static Class[] objectsToClasses(Object[] array) {
    Class[] classes = new Class[array.length];
    for(int i=0,l=array.length; i<l; i++) {
      classes[i] = array[i]!=null?array[i].getClass():null;
    }
    return classes;
  }
  public static String classesToStrings(final Class[] array) {
    StringBuilder b = new StringBuilder();
    for(Class c: array) {
      if(b.length()>0) 
        b.append(", ");
      b.append(c.getName());
    }
    return b.toString();
  }
  public static String listCallbacks(Object callback) {
    StringBuilder b = new StringBuilder();
    Method[] methods = callback.getClass().getMethods();
    for(Method m: methods) {
      if(b.length()>0) 
        b.append("\n");
      b.append("    ");
      b.append(m.getReturnType().getName());
      b.append(" event( ");
      if("event".equals(m.getName())) {
        Class[] types = m.getParameterTypes();
        b.append(classesToStrings(types));
      }
      b.append(")");
    }
    return b.toString();
  }
  public static Object[] fillToSize(Object[] array, int desiredSize) {
    if(array.length>=desiredSize)
      return array;
    else {
      Object[] newArray = new Object[desiredSize];
      for(int i=0,l=array.length; i<l; i++) {
        newArray[i] = array[i];
      }
      return newArray;
    }
  }
  public static interface ArgsNone extends AnyCallbackInterface {
    void event() throws Exception; 
  }
  public static interface ArgsOne<T1> extends AnyCallbackInterface {
    void event(T1 param) throws Exception; 
  }
  public static interface ArgsTwo<T1, T2> extends AnyCallbackInterface {
    void event(T1 param, T2 param2) throws Exception; 
  }
  public static interface ArgsThree<T1, T2, T3> extends AnyCallbackInterface {
    void event(T1 param, T2 param2, T3 param3) throws Exception; 
  }
  public static interface ArgsFour<T1, T2, T3, T4> extends AnyCallbackInterface {
    void event(T1 param, T2 param2, T3 param3, T4 param4) throws Exception; 
  }
  
  public static class LambdaNone extends EventCallback {
    public final ArgsNone cb;
    public LambdaNone(ArgsNone i) {
      this.cb = i;
    }
    @Override
    public void event(Object... parameters) throws Exception {  
      cb.event();
    }
  }
  public static class LambdaOne<T1> extends EventCallback {
    public final ArgsOne<T1> cb;
    public LambdaOne(ArgsOne<T1> i) {
      this.cb = i;
    }
    @Override
    public void event(Object... parameters) throws Exception { 
      parameters = fillToSize(parameters, 1);
      try {cb.event((T1)parameters[0]);}
      catch(ClassCastException e) {throw new NoCallbackException(parameters);}
    }
  }
  public static class LambdaTwo<T1, T2> extends EventCallback {
    public final ArgsTwo<T1, T2> cb;
    public LambdaTwo(ArgsTwo<T1, T2> i) {
      this.cb = i;
    }
    @Override
    public void event(Object... parameters) throws Exception { 
      parameters = fillToSize(parameters, 2);
      try {cb.event((T1)parameters[0], (T2)parameters[1]);}
      catch(ClassCastException e) {throw new NoCallbackException(parameters);}
    }
  }
  public static class LambdaThree<T1, T2, T3> extends EventCallback {
    public final ArgsThree<T1, T2, T3> cb;
    public LambdaThree(ArgsThree<T1, T2, T3> i) {
      this.cb = i;
    }
    @Override
    public void event(Object... parameters) throws Exception { 
      parameters = fillToSize(parameters, 3);
      try {cb.event((T1)parameters[0], (T2)parameters[1], (T3)parameters[2]);}
      catch(ClassCastException e) {throw new NoCallbackException(parameters);}
    }
  }
  public static class LambdaFour<T1, T2, T3, T4> extends EventCallback {
    public final ArgsFour<T1, T2, T3, T4> cb;
    public LambdaFour(ArgsFour<T1, T2, T3, T4> i) {
      this.cb = i;
    }
    @Override
    public void event(Object... parameters) throws Exception {  
      parameters = fillToSize(parameters, 4);
      try {cb.event((T1)parameters[0], (T2)parameters[1], (T3)parameters[2], (T4)parameters[3]);}
      catch(ClassCastException e) {throw new NoCallbackException(parameters);}
    }
  }
}
