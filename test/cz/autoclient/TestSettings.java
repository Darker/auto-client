/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.InvalidPasswordException;
import cz.autoclient.settings.secure.SecureSettings;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;
/**
 *
 * @author Jakub
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSettings {
  private final File path;
  public TestSettings() {
    path = new File("test_settings.bin");
  }
  
  
  Settings set;
  @Test
  public void testInit() {
    set = new Settings();
  }
  @Test
  public void testEntry() {
    set.setSetting("int", 666);
    set.setSetting("String", "hello");
    set.setSetting("null", null);
    //Insert sub setting node
    Settings subset = new Settings();
    set.copyTo(subset);
    
    set.setSetting("Settings", subset);
    assertEquals("Settings getInt doesn't return default value properly.", set.getInt("invalid", 0), 0);
  }
  @Test
  public void testEncryption() {
    SecureSettings encr = set.getEncryptor();
    encr.setPassword("PW");
    encr.setUse_hwid(true);
    
    set.setEncrypted("encrypted string", "hello");
  }
  
  @Test
  public void testSave() throws IOException {
    path.mkdirs();
    set.saveToFile(path);
  }
  
  
  @Test
  public void testLoad() throws IOException {
    set = new Settings();
    
    set.loadFromFile(path);
    
  }
  @Test
  public void testSavedValues() {
    assertEquals("int value malformed", set.getInt("int", 0), 666);
    assertEquals("string value malformed", set.getString("String"), "hello");
    assertEquals("null value malformed", set.getSetting("null"), null);
    //assertEquals("null value malformed", set.getEncrypted("encrypted string"), null);
    assertEquals("Settings value malformed", 
        set.getSetting("Settings", Settings.class).getSetting("String"),
        "hello");
  }
  @Test(expected = IllegalStateException.class)
  public void testDecryptionStateException() {
    set.getEncrypted("encrypted string");
  }
  @Test(expected = InvalidPasswordException.class)
  public void testDecryptionPasswordException() {
    SecureSettings encr = set.getEncryptor();
    encr.setPassword("PW_WRONG");
    encr.setUse_hwid(true);
    set.getEncrypted("encrypted string");
  }
  @Test(expected = InvalidPasswordException.class)
  public void testDecryptionHWIDException() {
    SecureSettings encr = set.getEncryptor();
    encr.setPassword("PW");
    encr.setUse_hwid(false);
    set.getEncrypted("encrypted string");
  }
  @Test
  public void testDecryption() {
    SecureSettings encr = set.getEncryptor();
    encr.setPassword("PW");
    encr.setUse_hwid(true);
    
    assertEquals("encrypted string value malformed", set.getEncrypted("encrypted string"), "hello");
  }
}
