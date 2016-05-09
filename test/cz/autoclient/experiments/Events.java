/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub
 */
public class Events implements EventEmitter {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    Events e = new Events();
    e.doStuff();
    e.testLambdas();
    
  }
  public void doStuff() throws Exception {
    EventCallback cb = new StringCallback();
    cb.event("TEST");
    this.addEventListener("test", cb);

    this.dispatchEvent("test", "TEST");
       
    // Invoke
    this.addEventListener("test2", (String test)->{System.out.println("Callback: "+test);});

    
    this.dispatchEvent("test2", "test!");
    this.dispatchEvent("test2", null, null);
    this.dispatchEvent("test2", new Object[]{});
    
    EventCallbackLog l = new EventCallbackLog(this);
    l.addEventListener("test2", (String test)->{System.out.println("Shouldn't happen! "+test);});
    this.dispatchEvent("test2", "test!");
    l.removeAll();
    this.dispatchEvent("test2", "test!");
    //this.dispatchEvent("test2", 666);
  }
  
  public void testLambdas() {
    Runnable a = this::nothing;
    Runnable b = this::nothing;
    if(a!=b)
      throw new RuntimeException("FUCK");
  }
  
  public Runnable makeRunnable(Runnable r) {
    return r; 
  }
  public void nothing() {}
  
  
  public static class StringCallback extends EventCallback {
      /*@Override
      public void event(Object... parameters) {
        System.out.println("Generic callback.");
      }*/
      public void event(String test) {
        System.out.println("String callback.");
      }
  }
  
  private HashMap<String, List<EventCallback>> events = new HashMap();
  @Override
  public Map<String, List<EventCallback>> getListeners() {
    return events;
  }
  
}
