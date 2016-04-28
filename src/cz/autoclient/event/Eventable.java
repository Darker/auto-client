/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub
 */
public interface Eventable {
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
        //oh yeah, put these random parameters in!
        //Surelly no NullPointer exceptions will occur!
        callback.event(parameters);
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
}
