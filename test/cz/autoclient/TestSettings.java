/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.EncryptedSetting;
import cz.autoclient.settings.secure.InvalidPasswordException;
import cz.autoclient.settings.secure.SecureSettings;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.TemporaryFolder;
/**
 *
 * @author Jakub
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSettings {
  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();
  
  private File path;
  private File path2;
  private final static Settings inputSet = new Settings();
  private final static Settings outputSet = new Settings();
  public TestSettings() throws IOException {
    
    //inputSet = new Settings();
    //outputSet = new Settings();
  }
  @Before
  public void createTestData() throws IOException {
    path = testFolder.newFile("test_settings.bin");//new File("./test_settings.bin");
    path2 = testFolder.newFile("test_settings2.bin");
  }
  
  public static final int intval = 666;
  public static final String stringval = "hello";
  
  public static final int intval2 = 13;
  
  /*@Test
  public void test01_Init() {
    inputSet = new Settings();
  }*/
  @Test
  public void test02_Entry() {
    
    inputSet.setSetting("int", intval);
    inputSet.setSetting("String", stringval);
    inputSet.setSetting("null", null);
    //Insert sub setting node
    Settings subset = new Settings();
    inputSet.copyTo(subset);
    
    inputSet.setSetting("Settings", subset);
    assertEquals("Settings getInt doesn't return default value properly.", inputSet.getInt("invalid", 0), 0);
  }
  @Test
  public void test03_Encryption() {
    SecureSettings encr = inputSet.getEncryptor();
    encr.addPassword("PW");
    
    inputSet.setEncrypted("encrypted string", stringval);
    inputSet.setEncrypted("encrypted int", intval);
  }
  
  @Test
  public void test04_SaveLoad() throws IOException {
    path.mkdirs();
    inputSet.saveToFile(path);
    assertNotEquals("File "+path.getAbsolutePath()+" is empty! Size: ", 0, path.length());
    //System.out.println("File size: "+path.length());
    outputSet.loadFromFile(path);
    assertFalse("The instances in the settings loaded from file are the same. That's suspicious!", 
                inputSet.getSetting("String")==outputSet.getSetting("String"));
    
  }
  

  @Test
  public void test06_SavedValues() {
    assertEquals("int value malformed", intval, outputSet.getInt("int", 0));
    assertEquals("string value malformed", stringval, outputSet.getString("String"));
    assertEquals("null value malformed", null, outputSet.getSetting("null"));
    //assertEquals("null value malformed", inputSet.getEncrypted("encrypted string"), null);
    assertEquals("Settings value malformed", stringval,
        outputSet.getSetting("Settings", Settings.class).getSetting("String"));
    
    //Add some too
    outputSet.setSetting("VERSION", 2.0);
  }
  @Test(expected = IllegalStateException.class)
  public void test07_DecryptionStateException() {
    outputSet.getEncrypted("encrypted string");
  }
  @Test(expected = InvalidPasswordException.class)
  public void test08_DecryptionPasswordException() {
    SecureSettings encr = outputSet.getEncryptor();
    encr.addPassword("PW_WRONG");
    outputSet.getEncrypted("encrypted string");
  }
  /*@Test(expected = InvalidPasswordException.class)
  public void test09_DecryptionHWIDException() {
    SecureSettings encr = outputSet.getEncryptor();
    encr.clearPasswords();
    encr.addPassword("PW");
    
    outputSet.getEncrypted("encrypted string");
  }*/
  @Test
  public void test10_Decryption() {
    SecureSettings encr = outputSet.getEncryptor();
    encr.clearPasswords();
    encr.addPassword("PW");
    
    assertEquals("encrypted string value malformed", stringval, outputSet.getEncrypted("encrypted string"));
  }
  @Test
  public void test11_ChangePassword() {
    assertTrue("Just a moment ago, settings had encryptor class.", outputSet.hasEncryptor());
    outputSet.prepareForEncryptionChange();
    SecureSettings encr = outputSet.getEncryptor();
    //System.out.println("Old password: "+encr.getMergedPassword());
    encr.clearPasswords();
    encr.addPassword("PW_NEW");
    //System.out.println("New password: "+encr.getMergedPassword());
    assertTrue("The instance of encryptor in and out of settings must be same.", 
        outputSet.getSetting("encrypted int", EncryptedSetting.class).getEncryptor()==encr);
    //System.out.println("    Single item password: "+outputSet.getSetting("encrypted int", EncryptedSetting.class).getEncryptor().getMergedPassword());
    //Changeone item
    outputSet.setEncrypted("encrypted int", intval2);
    assertEquals("The setting value was supposed to change to new value!", intval2, outputSet.getEncrypted("encrypted int"));
    //System.out.println("    Single item value: "+outputSet.getEncrypted("encrypted int"));
  }
  
  
  @Test
  public void test12_SaveLoadBack() throws IOException {
    path2.mkdirs();
    //System.out.println("Saving with password: "+outputSet.getEncryptor().getMergedPassword());
    //System.out.println("    Single item password: "+outputSet.getSetting("encrypted int", EncryptedSetting.class).getEncryptor().getMergedPassword());
    //System.out.println("    Single item value: "+outputSet.getEncrypted("encrypted int"));
  
    outputSet.saveToFile(path2);
    assertNotEquals("File "+path2.getAbsolutePath()+" is empty! Size: ", 0, path2.length());
    //ystem.out.println("File size: "+path.length());
    inputSet.clearSettings();
    assertEquals("Settings should be empty now!", 0, inputSet.size());
    //System.out.println("Loading with password: "+inputSet.getEncryptor().getMergedPassword());
    
    inputSet.loadFromFile(path2);
    assertEquals("The added setting was lost.", 2.0, inputSet.getSetting("VERSION"));
    //System.out.println("Decrypted int: " + outputSet.getEncrypted("encrypted int"));
    //System.out.println("Decrypted int: " + inputSet.getEncrypted("encrypted int"));
    
  }
  
  @Test(expected = NullPointerException.class)
  public void test13_noEncryptorAfterDeserialize() {
    assertEquals("The added setting was lost.", 2.0, inputSet.getSetting("VERSION"));
    System.out.println("Single item password: "+inputSet.getSetting("encrypted int", EncryptedSetting.class).getEncryptor().getMergedPassword());
  }
  
  @Test(expected = InvalidPasswordException.class)
  public void test14_DecryptWithChangedPassword() {
    //Output set has still set the original "PW" password
    System.out.println("Decrypted int: " + inputSet.getEncrypted("encrypted int"));
    //System.out.println("    Using password: "+inputSet.getSetting("encrypted int", EncryptedSetting.class).getEncryptor().getMergedPassword());
  }
  @Test(expected = InvalidPasswordException.class)
  public void test15_DecryptWithChangedPassword() {
    //Output set has still set the original "PW" password
    System.out.println("Decrypted string: " + inputSet.getEncrypted("encrypted string"));
    //System.out.println("    Using password: "+inputSet.getSetting("encrypted string", EncryptedSetting.class).getEncryptor().getMergedPassword());
  }
  
  public void test16_DecryptWithChangedCorrectPassword() {
    SecureSettings encr = inputSet.getEncryptor();
    encr.replacePassword("PW_NEW", "PW");
    //Output set has still set the original "PW" password
    assertEquals("Failed to decrypt encrypted integer properly.", intval2, inputSet.getEncrypted("encrypted int"));
    assertEquals("Failed to decrypt encrypted string properly.", stringval, inputSet.getEncrypted("encrypted string"));
  }
}
