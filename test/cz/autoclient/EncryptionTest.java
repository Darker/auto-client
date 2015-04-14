/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.secure.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Jakub
 */
public class EncryptionTest {
  public static void main(String[] args) throws Exception
  {

    File test_file = new File("test.bin");
    System.out.println("MAC ADDRESS: " + SecureSettings.getMacAddress());

    {
      SecureSettings test = createTest("password");
      EncryptedSetting set = new EncryptedSetting(test);
      set.setDecryptedValue("SECRET STRING");
      //set.getEncryptedValue();
      ObjToFile(set, test_file);
      

    }
   
    if(test_file.isFile()) {
      SecureSettings test = createTest("password");
      
      EncryptedSetting set = (EncryptedSetting)ObjFromFile(test_file);
      set.encryptor = test;
      System.out.println(set.getDecryptedValue());
    }
    else
      throw new FileNotFoundException("No file was generated!");
    
    
    /** Test with settings **/
    //WRITE
    {
      Settings s = new Settings();
      //Force init default encryptor
      configureDefaultEncryptor(s.getEncryptor());
      //Save value
      s.setEncrypted("SETTING", "SECRET_SETTING");
      //Save to file
      s.saveToFile(test_file.getAbsolutePath());
    }
    //READ
    {
      Settings s = new Settings();
      //Load data
      s.loadFromFile(test_file.getAbsolutePath());
      //Force init default encryptor
      configureDefaultEncryptor(s.getEncryptor());
      //Save value
      System.out.println("DECRYPTED SETTING: "+s.getEncrypted("SETTING"));
    }
  }
  public static void configureDefaultEncryptor(SecureSettings s) {
    s.setUse_hwid(true);
    s.setPassword("Constant password.");
  }
  
  public static void ObjToFile(Object obj, File file) throws Exception {
      OutputStream filestr = new FileOutputStream(file);
      OutputStream buffer = new BufferedOutputStream(filestr);
      ObjectOutput output = new ObjectOutputStream(buffer);
      
      output.writeObject(obj);
      buffer.close();
      output.close();
  }
  public static Object ObjFromFile(File file) throws Exception {
      InputStream filestr = new FileInputStream(file);
      InputStream buffer = new BufferedInputStream(filestr);
      ObjectInput input = new ObjectInputStream (buffer);
      return input.readObject();
  }
  
  public static SecureSettings createTest() {
    return new SecureSettings(true, new PasswordInitialiser() {
     @Override
     public String getPassword() {
       return (String)JOptionPane.showInputDialog(
                    null,
                    "Enter password:\n",
                    "Master password required",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
     }
    });
  }
  public static SecureSettings createTest(String password) {
    return new SecureSettings(true, password);
  }
}
