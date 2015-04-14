/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;

import java.beans.Expression;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class InputHandlers {
  /** Array of available class types:**/
  private static final Map<Class<? extends JComponent>, Class<? extends Input>> handlers = new HashMap<>();
  /** Register new handler type
   * @param handler class that handles the new input type, must have propert constructor
   * @param type type of input that can be handled by that class
  **/
  public static void register(Class<? extends Input> handler, Class<? extends JComponent> type) {
    handlers.put(type, handler);
  }
  /** Find handler for given input and instantiate it. 
   * 
   * @param comp JComponent that represents some kind of user input
   * @param onchange callable to be called when input value chages
   * @param subverifier verifier that can convert the data in input to some object represantation used by application
   * @return Input instance, never null - on failure, exception is thrown
   * @throws NoSuchMethodException when there's no such constructor or handler of when the constructor throws some other exception
   */
  public static Input fromJComponent(JComponent comp, ValueChanged onchange, SettingsInputVerifier<Object> subverifier) throws NoSuchMethodException {
    //Find the handler class
    Class<? extends Input> handler = handlers.get(comp.getClass());
   
    Input result;
    
    if(handler==null) {
      throw new NoSuchMethodException("No input handler for "+comp.getClass().getName()+" input type.");
    }
    try {
      result = (Input)new Expression(handler, "new", new Object[]{comp, onchange, subverifier}).getValue();
    }
    catch(Exception e) {
      throw new NoSuchMethodException("Constructor for "+handler.getName()+" failed with exception:"+e);
    }
    //Reflection removed, it can't evalueate complicated expressions (supertypes etc)
    /*Class[] arguments = new Class[3];
    arguments[0] = comp.getClass(); 
    arguments[1] = ValueChanged.class;
    arguments[2] = SettingsInputVerifier.class;
          
    try {   
      result = handler.getDeclaredConstructor(arguments).newInstance(comp, onchange, subverifier);
    } catch (InstantiationException ex) {
      throw new NoSuchMethodException("Instantiation exception: "+ex);
    } catch (IllegalAccessException ex) {
      throw new NoSuchMethodException("Illegal access exception: "+ex);
    } catch (IllegalArgumentException ex) {
      throw new NoSuchMethodException("Illegal argument exception: "+ex);
    } catch (InvocationTargetException ex) {
      throw new NoSuchMethodException("InvocationTargetException: "+ex);
    }*/

    /*catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new NoSuchMethodException("No valid handler for this input type.");
    }*/
    return result;
  }
}
