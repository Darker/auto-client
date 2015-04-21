/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.settings.secure.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import cz.autoclient.settings.secure.PasswordInitialiser.StringPasswordInitialiser;
import cz.autoclient.settings.secure.SecureSettings;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Jakub
 */
public class EncryptionTest {
  @Rule
  public final TemporaryFolder testFolder = new TemporaryFolder();
  private File path;
  @Before
  public void createTestData() throws IOException {
    path = testFolder.newFile("test_security.bin");
  }
  @Test
  public void TestSimpleSaveLoad() throws Exception {
    final String password = "password";
    final String value = "SECRET STRING";
    //System.out.println("MAC ADDRESS: " + SecureSettings.getMacAddress());
    {
      SecureSettings test = createTest(password);
      EncryptedSetting set = new EncryptedSetting(test);
      set.setDecryptedValue(value);
      //set.getEncryptedValue();
      ObjToFile(set, path);
    }

    if(path.isFile()) {
      SecureSettings test = createTest(password);
      
      EncryptedSetting set = (EncryptedSetting)ObjFromFile(path);
      set.setEncryptor(test);
      assertEquals("The decrypted value doesn't match the encrypted one: ", value, set.getDecryptedValue());
    }
    else
      fail("The encrypted file was not created.");
  }
  @Test
  public void sameInitialisersAreEqual() {
    assertTrue((new StringPasswordInitialiser("test")).equals(new StringPasswordInitialiser("test")));
  }
  @Test
  public void equalInitialisersCanBeRemoved() {
    SecureSettings test = createTest("test");
    test.removePassword(new StringPasswordInitialiser("test"));
    assertFalse("Equal password was expected to be removed and "
        + "SecureSettings should have no passwords.", test.hasPasswords());
    
    test.addPassword(UniqueID.WINDOWS_USER_SID);
    test.removePassword(UniqueID.WINDOWS_USER_SID);
    assertFalse("Same instance password should've been removed.", test.hasPasswords());
  }
  
  public static void main(String[] args) throws Exception
  {
    System.out.println(UniqueID.WINDOWS_USER_SID.getPassword());
    assertNotEmpty("User SID is empty!", UniqueID.WINDOWS_USER_SID.getPassword());
    
    
    
  }
  public static void assertNotEmpty(String message, String input) {
    if(input.isEmpty())
      throw new IllegalArgumentException(message);
  }
  
  public static void configureDefaultEncryptor(SecureSettings s) {
    //s.setUse_hwid(true);
    s.addPassword("Constant password.");
  }
  
  public static void ObjToFile(Object obj, File file) throws Exception {
      OutputStream filestr = new FileOutputStream(file);
      OutputStream buffer = new BufferedOutputStream(filestr);
      ObjectOutput output = new ObjectOutputStream(buffer);
      
      output.writeObject(obj);
      buffer.close();
      output.close();
  }
  public static Object ObjFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
      InputStream filestr = new FileInputStream(file);
      InputStream buffer = new BufferedInputStream(filestr);
      ObjectInput input = new ObjectInputStream (buffer);
      return input.readObject();
  }
  
  public static SecureSettings createTest() {
    return new SecureSettings(new PasswordInitialiser() {
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
    return new SecureSettings(password);
  }
}
