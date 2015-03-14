/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Jakub
 */
public class Settings implements java.io.Serializable {
  //Settings can be of any type
  protected Map<String, Object> settings = new HashMap<>();
  /**
   * Map of input fields that are mapped to certain settings. This allows to automatically update the settings both ways.
   */
  protected final Map<String, Input> boundInputs = new HashMap<>();
  /**
   * Should never change from true to false! Indicates that settings have changed and should be saved in file.
   */
  private boolean changed = false;

  /**
   * Retrieve setting that IS Integer. If the setting does not exist or isn't Integer, returns default value.
   * @param name name of the setting
   * @param defaultValue value to return if the setting is nonexistent or not Integer
   * @return defaultValue on failure
   */
  public int getInt(String name, final int defaultValue) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof Integer) {
        return (Integer)value; 
      }
    }
    return defaultValue;
  }

  public float getFloat(String name, final float defaultValue) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof Float) {
        return (Float)value; 
      }
    }
    return defaultValue;
  }
  public boolean getBoolean(String name, final boolean defaultValue) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof Boolean) {
        return (Boolean)value; 
      }
    }
    return defaultValue;
  }
  public String getString(String name) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof String) {
        return (String)value; 
      }
    }
    return null;
  }
  public String getStringEquivalent(String name) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof String) {
        return (String)value; 
      }
      else
        return value.toString();
    }
    return "";
  }
  public Object getSetting(String name) {
    return settings.get(name);
  }
  public boolean empty(String name) {
    Object val = settings.get(name);
    if(val!=null) {
      return false;
    }
    return true;
  } 
  /** Set setting to any object value. 
   * @param name setting name
   * @param value setting value
   * @return old setting value. If the setting didn't exist before, returns null. Note that null is also valid setting value
   */
  public Object setSetting(String name, Object value) {
    Object old = settings.get(name);
    if(old!=null && old.equals(value)) {
      return old;   
    }
    changed = true;
    settings.put(name, value);
    System.out.println("AutomatSettings[\""+name+"\"] = "+value.toString());
    return old;
  }
  /** Set default value of that setting. This must be set every time after loading settings (it's not saved).
   * In fact, this function just adds setting if setting is not set. This also does not trigger
   * changed flag - when you save settings, these changes will not force save, but will be saved if
   * save operation is performed.
   * @param name setting name
   * @param value setting value
   * @return true if something was changed, false if the value was already set
   */
  public boolean setSettingDefault(String name, Object value) {
    if(settings.containsKey(name)) {
      return false;
    }
    settings.put(name, value);
    System.out.println("default AutomatSettings[\""+name+"\"] = "+value.toString());
    return true;
  }
  /** Automatically update setting value as user types.
   * @param setting_name What is the name of associated setting?
   * @param input JTextComponent (like JTextField) to listen on for events
   */
  public void listenOnInput(final String setting_name, final JTextField input) {
    //Debug output
    //System.out.println("Settings[\""+setting_name+"\"] automatically updates on input change.");
    //Only works if you press enter
    /*input.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //Set setting to current value
        setSetting(setting_name, input.getText());
      }
    });*/
    //Only works when you leave the field
    input.setInputVerifier(new InputVerifier() {
      @Override
      public boolean verify(JComponent in) {
        //Set setting to current value
        setSetting(setting_name, input.getText());
        return true;
      }
    });
  }

  public void bindToInput(final String setting_name, final JComponent input, final SettingsInputVerifier<Object> verif) {    
    //boundInputs.put(setting_name, input);
    Input in;
    try {
    in = InputHandlers.fromJComponent(input, 
      new ValueChanged() {
        @Override
        public void changed(Object o) {
          //System.out.println("Change event!");
          setSetting(setting_name, o);
        }
      },
      verif
    );}
    //Silent fail
    catch(NoSuchMethodException e) {
      System.err.println("No handler for this input field!");
      return;
    };
    
    boundInputs.put(setting_name, in);
    if(verif!=null) {
      if(verif!=SettingsInputVerifier.INVALID_VERIFIER) {
        in.bind();
      }
      //Invalid verifier means no verification is needed (eg. for any string)
      else {
        in.setVerifier(null);
        in.bind();
      }
    }
  }
  public void bindToInput(final String setting_name, final JComponent input, final boolean auto_update) {
    bindToInput(setting_name, input, auto_update?SettingsInputVerifier.INVALID_VERIFIER:null);
  }
  public void bindToInput(final String setting_name, final JComponent input) {
    bindToInput(setting_name, input, SettingsInputVerifier.INVALID_VERIFIER);
  }
  
  public void displaySettingsOnBoundFields() {
    for (Map.Entry pair : boundInputs.entrySet()) {
      String name = (String)pair.getKey();
      //System.out.println("Trying to load setting '"+name+"' on input.");
      if(settings.containsKey(name)) {
        Input input = (Input)pair.getValue();
        //System.out.println("   value='"+settings.get(name)+"'");
        input.setValue(settings.get(name));
      }
    }
  }
  public void loadSettingsFromBoundFields() {
    for (Map.Entry pair : boundInputs.entrySet()) {
      
      
      String name = (String)pair.getKey();
      Input input = (Input)pair.getValue();
      
      //System.out.println("Trying to load setting '"+name+"' from input.");
      //System.out.println("   value='"+input.getValue()+"'");
      setSetting(name, input.getValue());
    }
  }
  
  public void saveToFile(String path) throws IOException {
    OutputStream file = new FileOutputStream(path);
    OutputStream buffer = new BufferedOutputStream(file);
    ObjectOutput output = new ObjectOutputStream(buffer);
    System.out.println("Saving settings HashMap - "+settings.size()+" fields.");
    output.writeObject(settings);
    
    buffer.close();
    output.close();
  }
  public void saveToFile(String path, boolean forced) throws IOException {
    if(forced || changed)
      saveToFile(path);
  }
  public void loadFromFile(String path) throws IOException {
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream (buffer);
    //Get the properties out of the object
    try {
      Map set = (Map)input.readObject();
      System.out.println(set.size()+" settings loaded from file.");
      this.settings.putAll(set);
    }
    catch(ClassNotFoundException e) {};  
  }
}
