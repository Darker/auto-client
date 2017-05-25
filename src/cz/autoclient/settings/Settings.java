/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;


import cz.autoclient.settings.secure.EncryptedSetting;
import cz.autoclient.settings.secure.InvalidPasswordException;
import cz.autoclient.settings.secure.SecureSettings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Jakub
 */
public class Settings implements java.io.Serializable {
  /** 
   * Prevent any incompatible class bullshit errors
   */
  private static final long serialVersionUID = 666;
  //Settings can be of any type
  protected Map<String, Object> settings = new HashMap<>();
  /**
   * Map of input fields that are mapped to certain settings. This allows to automatically update the settings both ways.
   */
  protected transient final Map<String, Input> boundInputs = new HashMap<>();
  /**
   * Should never change from true to false! Indicates that settings have changed and should be saved in file.
   */
  private transient boolean changed = false;
  /**
   * If using encrypted settings, you must set the encryptor/decryptor first! Otherwise, 
   * {@link java.lang.IllegalStateException} will be thrown.
   */
  protected transient SecureSettings encryptor;


  /**
   * Retrieve setting that IS Integer. If the setting does not exist or isn't Integer, returns default value.
   * @param name name of the setting
   * @param defaultValue value to return if the setting is nonexistent or not Integer
   * @return defaultValue on failure
   */
  public int getInt(String name, final int defaultValue) {
    if(settings.containsKey(name)) {
      Object value = settings.get(name);
      if(value instanceof Number) {
        return ((Number)value).intValue();
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
      if(value instanceof String) 
        return (String)value; 
      else if(value!=null)
        return value.toString();
      else
        return "";
    }
    return "";
  }
  public Object getSetting(String name) {
    return settings.get(name);
  }
  /** Retrieves value only if it's instance of given type. 
   * 
   * @param <T> 
   * @param name name of the setting
   * @param type Class of required return type.
   * @return Setting cast to required type or null if this type is not present.
   */
  public <T> T getSetting(String name, Class<T> type) {
    Object val = settings.get(name);
    if(val!=null && type.isInstance(val))
      return (T)val;
    return null;
  }
  public Object getEncrypted(String name) throws InvalidPasswordException {
    Object s = settings.get(name);
    if(s instanceof EncryptedSetting) {
      //Prevent null pointer exception
      if(encryptor==null) {
        throw new IllegalStateException("The SecureSettings class must be initialised before using encrypted"
            + "settings!"); 
      }
      return encryptor.decrypt((EncryptedSetting)s);
    }
    //If the setting is not saved as encrypted, return the unencrypted value
    else 
      return s;
  }
  public void setEncrypted(String name, java.io.Serializable value) {
    if(encryptor==null) {
      throw new IllegalStateException("The SecureSettings class must be initialised before using encrypted"
          + "settings!"); 
    }
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ENCRYPTED: AutomatSettings[\""+name+"\"] = "+value/*(value!=null?"[value hidden]":"null")*/);
    changed = true;
    Object s = settings.get(name);
    if(s!=null && s instanceof EncryptedSetting) {
      ((EncryptedSetting)s).setEncryptor(encryptor);
      ((EncryptedSetting)s).setDecryptedValue(value);
    }
    else {
      EncryptedSetting e = new EncryptedSetting(encryptor);
      e.setDecryptedValue(value);
      settings.put(name, e);
    }
  }
  
  public boolean empty(String name) {
    Object val = settings.get(name);

    return val==null;
  } 
  /** 
   * Check if setting with given name and type exists. This can be used to prevent 
   * type conversion errors when invalid type is supplied.
   * @param name setting name
   * @param type required type of the value
   * @return true if value exists, is not null and represents given type
   */
  public boolean exists(String name, Class type) {
    Object val = settings.get(name);
    if(val!=null) {
      return type.isInstance(val);
    }
    return false;
  }
  /** 
   * Check if setting with given name exists.
   * @param name setting name
   * @return true if value exists, 
   */
  public boolean exists(String name) {
    return settings.containsKey(name);
  }
  /** 
   * Return the size of the settings array.
   * @return number of elements in the internal HashMap
   */
  public int size() {
    return settings.size();
  }
  /** Set setting to any object value. 
   * @param name setting name
   * @param value setting value
   * @param force forces the setting to be written even if old value equals the new
   * @return old setting value. If the setting didn't exist before, returns null. Note that null is also valid setting value
   */
  public Object setSetting(String name, Object value, boolean force) {
    Object old = settings.get(name);
    if(!force) {
      if(old==value || old!=null&&old.equals(value)) {
        return old;   
      }
    }
    changed = true;
    settings.put(name, value);
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AutomatSettings[\""+name+"\"] = "+(value!=null?value.toString():"null"));
    //Thread.dumpStack();
    return old;
  }
  
  public Object setSetting(String name, Object value) {
    return setSetting(name, value, false);
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
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "default AutomatSettings[\""+name+"\"] = "+(value!=null?value.toString():"null"));
    return true;
  }
  /** Clear all settings.
   * 
   * @return number of values deleted from hashmap
   */
  public int clearSettings() {
    int len = settings.size();
    settings.clear();
    return len;
  }
  /** Automatically update setting value as user types.
   * @param setting_name What is the name of associated setting?
   * @param input JTextComponent (like JTextField) to listen on for events
   */
  public void listenOnInput(final String setting_name, final JTextField input) {
    //Debug output
    //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Settings[\""+setting_name+"\"] automatically updates on input change.");
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
        new ValueChangedListener(setting_name),
        verif
      );
    }
    catch(NoSuchMethodException e) {
      //System.err.println("No handler for "+input.getClass().getName());
      throw new NoInputHandlerException(e.getMessage(), input);
    }
    
    boundInputs.put(setting_name, in);
    useVerifier(in, verif);
  }
  public void bindToInput(final String setting_name, final Input input, final SettingsInputVerifier<Object> verif) {    
    boundInputs.put(setting_name, input);
    useVerifier(input, verif);
  }
  public void bindToInputSecure(final String setting_name, final JComponent input, final SettingsInputVerifier<Object> verif) {    
    //boundInputs.put(setting_name, input);
    Input in;
    try {
      in = InputHandlers.fromJComponent(input, 
        new SecureValueChangedListener(setting_name),
        verif
      );
    }

    //Silent fail
    catch(NoSuchMethodException e) {
      System.err.println("No handler for "+input.getClass().getName()+" \n   Error:"+e);
      return;
    }
    if(in instanceof InputSecure) {
      ((InputSecure)in).setSecure(true);
    }
    boundInputs.put(setting_name, in);
    useVerifier(in, verif);
  }
  private void useVerifier(final Input in, final SettingsInputVerifier<Object> verif) {
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
  
  public class ValueChangedListener extends ValueChanged {
    public final String setting_name;
    public ValueChangedListener(String n) {
      setting_name = n; 
    }
    @Override
    public void changed(Object o) {
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Change event!");
      setSetting(setting_name, o);
    }
  }
  public class SecureValueChangedListener extends ValueChanged {
    public final String setting_name;
    public SecureValueChangedListener(String n) {
      setting_name = n; 
    }
    @Override
    public void changed(Object o) {
      //Can't encrypt unserializable objects
      if(!(o instanceof java.io.Serializable))
        return;
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Change event!");
      setEncrypted(setting_name, (java.io.Serializable)o);
    }
  }
  
  public void copyTo(Settings target) {
    if(target==this)
      throw new IllegalArgumentException("Can't copy to self. Also, it makes no sense!");
    for (Map.Entry pair : settings.entrySet()) {
      target.setSetting((String)pair.getKey(), pair.getValue());
    }
  }
  public void copyTo(Settings target, List<String> filter) {
    if(target==this)
      throw new IllegalArgumentException("Can't copy to self. Also, it makes no sense!");
    for (Map.Entry pair : settings.entrySet()) {
      String name = (String)pair.getKey();
      if(filter.contains(name))
        target.setSetting(name, pair.getValue());
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
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Trying to load setting '"+name+"' on input.");
      if(settings.containsKey(name)) {
        Input input = (Input)pair.getValue();
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   value='"+settings.get(name)+"'");
        input.setValue(settings.get(name));
      }
    }
  }
  /**
   * Filtered display of settings. Use this if you just changed known set of settings and you want to
   * update displayed values.
   * @param filter names of settings to be updated
   */
  public void displaySettingsOnBoundFields(List<String> filter) {
    for(String name: filter) {
      if(boundInputs.containsKey(name)&&settings.containsKey(name)) {
        boundInputs.get(name).setValue(settings.get(name));
      }
    }
  }
  public void loadSettingsFromBoundFields() {
    for (Map.Entry pair : boundInputs.entrySet()) {
      String name = (String)pair.getKey();
      Input input = (Input)pair.getValue();
      
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Trying to load setting '"+name+"' from input.");
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "   value='"+input.getValue()+"'");
      if(input instanceof InputSecure && ((InputSecure)input).isSecure())
        this.setEncrypted(name, (java.io.Serializable)input.getValue());
      else
        setSetting(name, input.getValue());
    }
  }
  
  public void saveToFile(File path) throws IOException {
    
    //File f = new File(path);
    if(!path.getParentFile().exists()&&!path.getParentFile().mkdirs()) {
      throw new IOException("Can't create folders in "+path.getAbsolutePath());
    }
    path.createNewFile();
    //Ensure the settings will try to be saved encrypted
    assignEncryptorToAllEncryptedSettings();
    
    OutputStream file = new FileOutputStream(path);
    ObjectOutput output;
    try (OutputStream buffer = new BufferedOutputStream(file)) {
      output = new ObjectOutputStream(buffer);
      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Saving settings HashMap - "+settings.size()+" fields.");
      output.writeObject(settings);
    }
    output.close();
  }
  public void saveToFile(String path) throws IOException {
    saveToFile(new File(path)); 
  }
  public void saveToFile(String path, boolean forced) throws IOException {
    saveToFile(new File(path), forced); 
  }
  public void saveToFile(File path, boolean forced) throws IOException {
    if(forced || changed)
      saveToFile(path);
  }
  
  public void loadFromFile(File path) throws IOException {    
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream (buffer);
    //Get the properties out of the object
    try {
      Map set = (Map)input.readObject();
      Logger.getLogger(this.getClass().getName()).log(Level.INFO, set.size()+" settings loaded from file.");
      this.settings.putAll(set);
    }
    catch(ClassNotFoundException e) {
    
    }
    finally {
      input.close();
      buffer.close();
      file.close();
    };  

  }
  public void loadFromFile(String path) throws IOException {   
    loadFromFile(new File(path)); 
  }
  
  /**Custom serialization - only write the hash map, everything else is irrelevant **/
  private void writeObject(ObjectOutputStream oos) throws IOException {
    assignEncryptorToAllEncryptedSettings();
    oos.writeObject(settings);
  }
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    settings = (HashMap<String, Object>)ois.readObject(); 
  }

  public SecureSettings getEncryptor() {
    if(encryptor==null) {
      encryptor = new SecureSettings(); 
    }
    return encryptor;
  }
  public boolean hasEncryptor() {
    return encryptor!=null; 
  }

  public synchronized void setEncryptor(SecureSettings encryptor) {
    this.encryptor = encryptor;
  }
  /** Loops through all secure settings and sets the own encryptor to them.
   * 
   */
  private synchronized void assignEncryptorToAllEncryptedSettings() {
    //Unlike other functions, this one is internal and fails silently
    if(encryptor==null)
      return;
    for (Map.Entry pair : settings.entrySet()) {
      if(pair.getValue() instanceof EncryptedSetting) {
         encryptor.encrypt((EncryptedSetting)pair.getValue());
      }
    }
  }
  /** If this method is called, all settings will be decrypted. After that, the
   *  any encryption keys can be changed and the encrypted data will not be lost.
   * 
   */
  public void prepareForEncryptionChange() {
    if(encryptor==null) {
      throw new IllegalStateException("The SecureSettings class must be initialised before using encrypted"
          + "settings!"); 
    }
    for (Map.Entry pair : settings.entrySet()) {
      if(pair.getValue() instanceof EncryptedSetting) {
         EncryptedSetting e = (EncryptedSetting)pair.getValue();
         encryptor.decrypt(e);
         e.forgetEncryptedValue();
      }
    }
  }
}
