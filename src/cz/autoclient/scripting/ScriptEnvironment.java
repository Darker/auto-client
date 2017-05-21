/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Jakub
 */
public class ScriptEnvironment {
  private final Map variables = new HashMap<String, Object>();
  private final ArrayList<SleepAction> paralelActions = new ArrayList();
 
  public ScriptEnvironment() {}
  /**
   * Construct environment from given variables and their names. 
   * As described in {@link #set(java.lang.Object...) }.
   * @throws IllegalArgumentException as described in {@link #set(java.lang.Object...) }
   * @param data
   */
  public ScriptEnvironment(Object... data) throws IllegalArgumentException {
    set(data);
  }
  /**
   * Add variables to environment. Data must be given in format:
   *   {String name1, Object value1, String name2, Object value2 ... }
   * @throws IllegalArgumentException when odd entry is not type of string or when 
   *         the number of entries isn't even.
   * @param data
   */
  public void set(Object... data) throws IllegalArgumentException {
    //Do not throw on no data as it doesn't matter. If it does, program will break later.
    if(data==null||data.length == 0)
      return;
    if(data.length%2!=0)
      throw new IllegalArgumentException("Odd number of arguments means there's more names than values.");
    
    for(int i=0,l=data.length-1; i<l; i+=2) {
      if(data[i] instanceof String) {
        if(data[i]!=null) {
          variables.put((String)data[i], data[i+1]);
        }
        else {
          throw new IllegalArgumentException("NULL given as name."); 
        }
      }
      else {
        throw new IllegalArgumentException("Non-string argument given as name."); 
      }
    }
  }
  /**
   * Retrieves a variable by given type. Returns null if the value of that type isn't
   * available.
   * @param <T>
   * @param name variable name
   * @param type class, for example Object.class
   * @return null or object of type T
   */
  public <T> T get(String name, Class<T> type) {
    Object tmp = variables.get(name);
    if(type.isInstance(tmp)) {
      return (T)tmp;
    }
    return null;
  }
  
  public Object get(String name) {
    return variables.get(name);
  }
  /**
   * Retrieves environment variable but throws {@link NoSuchEnvVariableException} if the
   * variable is not available, that means, if it's not set or different type.
   * @param <T> type to retrieve
   * @param name
   * @param type
   * @return 
   */
  public <T> T getOrThrow(String name, Class<T> type) throws NoSuchEnvVariableException {
    if(!variables.containsKey(name))
      throw new NoSuchEnvVariableException("Variable '"+name+"' is not set.", name);
    Object tmp = variables.get(name);
    if(type.isInstance(tmp)) {
      return (T)tmp;
    }
    else {
      throw new NoSuchEnvVariableException("Variable '"+name+"' is "+tmp.getClass().getName()+" not "+type.getName()+".", name);
    }
  }
  public static class NoSuchEnvVariableException extends RuntimeException {
    public final String variableName;

    public NoSuchEnvVariableException(String message, String variableName) {
      super(message);
      this.variableName = variableName;
    }
    public NoSuchEnvVariableException(String message) {
      this(message, null);
    }
  }
  public void addSleepAction(SleepAction s) {
    synchronized(paralelActions) {
      paralelActions.add(s);
    }
  }
  public void addSleepAction(Callable<Boolean> s) {
    addSleepAction(new SleepActionLambda(s));
  }
  public void addSleepAction(Callable<Boolean> s, long time) {
    addSleepAction(new SleepActionLambda(s, time));
  }
  public void addSleepActions(SleepAction[] s) {
    synchronized(paralelActions) {
      for(SleepAction a:s) {
        paralelActions.add(a);
      }
    }
  }
  public void sleep(long millis) throws InterruptedException {
    //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[Script] Sleep for: "+millis);
    long start = System.currentTimeMillis();
    long now = start;
    long end = now+millis;
    synchronized(paralelActions) {
      for (Iterator<SleepAction> iterator = paralelActions.iterator(); iterator.hasNext();) {
        // get time and end if less than 50ms remains
        now = System.currentTimeMillis();
        if(now+50>=end)
          break;
        SleepAction a = iterator.next();
        if (a.duration()+now+50<end) {
          // Remove the current element from the iterator and the list.
          // this is done before calling
          // this means of exception is thrown, the program will not 
          // retry to call this action
          iterator.remove();
          try {
            a.call();
          }
          catch(InterruptedException e) {
            throw e; 
          }
          catch(Exception e) {
            e.printStackTrace(); 
          }
          // Calculate duration
          /*System.out.println("  [Script] Action duration real/expected: "+
               (System.currentTimeMillis()-now)+
               "/"+a.duration()
          );*/
          
        }
      }
    }
    now = System.currentTimeMillis();
    long sleep = end-now;
    if(sleep>0)
      Thread.sleep(sleep);
    
    /*System.out.println("[Script] Slept for: "+
         (System.currentTimeMillis()-start)+" instead of "+millis
    );*/
  }
  
}
