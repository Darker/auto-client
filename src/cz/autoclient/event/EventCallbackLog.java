/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * This class logs all added callbacks and allows you to remove them 
 * all later on. This is important to prevent memory leaks.
 * @author Jakub
 */
public class EventCallbackLog {
  public final EventEmitter emitter;
  public final ArrayList<EventDescriptor> events = new ArrayList();
  public EventCallbackLog(EventEmitter emitter) {
    this.emitter = emitter;
  }
  public void addEventListener(String name, EventCallback event) {
    emitter.addEventListener(name, event);
    events.add(new EventDescriptor(name, event));
  }
  
  public void removeAll() {
    for(EventDescriptor e: events) {
      emitter.removeEventListener(e.name, e.cb);
    }
    events.clear();
  } 
  public void removeAll(String name) {
    events.removeIf(new RemoveIfString(name));
  } 
  public class RemoveIfString implements Predicate<EventDescriptor> {
    public final String name;
    public RemoveIfString(String name) {
      this.name = name;
    }
    
    @Override
    public boolean test(EventDescriptor e) {
      if(e.name.equals(name)) {
        emitter.removeEventListener(e.name, e.cb);
        return true;
      }
      else
        return false;
    }
        
  }
  
  public void addEventListener(String name, EventEmitter.ArgsNone i) {
    addEventListener(name, new EventEmitter.LambdaNone(i));
  }
  public <T> void addEventListener(String name, EventEmitter.ArgsOne<T> i) {
    addEventListener(name, new EventEmitter.LambdaOne(i));
  }
  public <T1, T2> void addEventListener(String name, EventEmitter.ArgsTwo<T1, T2> i) {
    addEventListener(name, new EventEmitter.LambdaTwo(i));
  }
  public <T1, T2, T3> void addEventListener(String name, EventEmitter.ArgsThree<T1, T2, T3> i) {
    addEventListener(name, new EventEmitter.LambdaThree(i));
  }
  public <T1, T2, T3, T4> void addEventListener(String name, EventEmitter.ArgsFour<T1, T2, T3, T4> i) {
    addEventListener(name, new EventEmitter.LambdaFour(i));
  }
  
  public static class EventDescriptor {
    public EventDescriptor(String name, EventCallback cb) {
      this.name = name;
      this.cb = cb;
    }
    public final String name;
    public final EventCallback cb;
    
  }
}
