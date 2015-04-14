/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Jakub
 */
public class SecureSettings {
  private String password;
  private PasswordInitialiser latePassword;
  private String hwid;
  private boolean use_hwid;
  private boolean use_password = true;



  
  
  private Cipher cipher;
  private IvParameterSpec ivSpec;
  private SecretKeySpec key;
  
  protected boolean initialized = false;

  public boolean isInitialized() {
    return initialized;
  }
  public SecureSettings() {
    this(true, "12345");
  }
  public SecureSettings(boolean use_hwid, String password) {
    this.password = password;
    this.use_hwid = use_hwid;
  }
  public SecureSettings(boolean use_hwid, PasswordInitialiser callback) {
    latePassword = callback;
    this.use_hwid = use_hwid;
  }
  public void init() {
    if(initialized)
      return;
    String data = "";
    if(password!=null) {
      data+=password;
    }
    else if(latePassword!=null) {
      password = latePassword.getPassword();
    }
    if(password==null&&latePassword!=null) {
      password = latePassword.getPassword();
    }
    if(password!=null) {
      data+=password;
    }
    else if(latePassword!=null) {
      password = latePassword.getPassword();
    }
    if(use_hwid) {
      if(hwid==null)
        hwid = getMacAddress(); 
      if(hwid!=null) {
        data+=hwid; 
      }
    }
    byte[] bytes;
    try {
      MessageDigest sha= MessageDigest.getInstance("MD5");
      bytes = sha.digest(data.getBytes());
    }
    catch (NoSuchAlgorithmException e) {
      bytes = new byte[16];
      byte[] bytes2 = data.getBytes();
      for(int i=0, l=Math.min(bytes2.length, bytes.length); i<l; i++) {
        bytes[i] = bytes2[i];
      }
    }
    //System.out.println("Key size: "+bytes.length);
    
    key = new SecretKeySpec(bytes, "AES");
    ivSpec = new IvParameterSpec(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    //ivSpec = new IvParameterSpec(bytes);
    // create the cipher with the algorithm you choose
    // see javadoc for Cipher class for more info, e.g.
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      
    } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
      System.out.println("Cipher is null!");
      cipher = null; 
    }
    initialized = true;
  }
  public String decrypt(String data) {
    try {
      return new String(decrypt(data.getBytes()));
    } catch (Exception ex) {
      return null;
    }
  }
  public Serializable decrypt(EncryptedSetting set) throws InvalidPasswordException {
    set.encryptor = this;
    return set.getDecryptedValue();
  }
  public byte[] encrypt(EncryptedSetting set) throws InvalidPasswordException {
    set.encryptor = this;
    return set.getEncryptedValue();
  }
  
  public Serializable decrypt(byte[] data, Class<? extends Serializable> className) {
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ObjectInputStream is;
    try {
      is = new ObjectInputStream(in);
      Serializable o = (Serializable)is.readObject();
      if(className.isInstance(o))
        return o;
      else
        return null;
    } catch (IOException | ClassNotFoundException ex) {
      System.out.println("DECRYPT FAILED!");
    }
    return null;
  }
  public Serializable decryptObject(byte[] data) throws InvalidPasswordException {
    
    ByteArrayInputStream in = null;
    try {
      in = new ByteArrayInputStream(decrypt(data));
    } catch (InvalidKeyException | ShortBufferException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
      throw new InvalidPasswordException("Decryption algorithm has failed to decrypt binary data properly.");
    }

    ObjectInputStream is;
    try {
      is = new ObjectInputStream(in);
      Serializable o = (Serializable)is.readObject();
      return o;
    } catch (IOException | ClassNotFoundException ex) {
      System.out.println("DECRYPT FAILED: "+ex);
      ex.printStackTrace();
      throw new InvalidPasswordException("The decrypted stream was mapformed - your key is invalid. Original error: "+ex);
    }
  }
  public byte[] encrypt(Serializable obj) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream os;
    try {
      os = new ObjectOutputStream(out);
      os.writeObject(obj);
      return encrypt(out.toByteArray());
    } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | ShortBufferException | IllegalBlockSizeException | BadPaddingException ex) {
      System.out.println("ENCRYPT FAILED: "+ex);
      ex.printStackTrace();
    }
    return null;
  }
  
  public byte[] encrypt(byte[] data) throws InvalidAlgorithmParameterException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    init();
    
    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
    byte[] encrypted = new byte[cipher.getOutputSize(data.length)];
    int enc_len = cipher.update(data, 0, data.length, encrypted, 0);
    enc_len += cipher.doFinal(encrypted, enc_len);
    return encrypted;
  }
  public byte[] decrypt(byte[] data) throws InvalidKeyException, ShortBufferException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    init();
    
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
    byte[] decrypted = new byte[cipher.getOutputSize(data.length)];
    int dec_len = cipher.update(data, 0, data.length, decrypted, 0);
    dec_len += cipher.doFinal(decrypted, dec_len);
    return decrypted;
  }

  public static String getHWID() {
    return UUID.randomUUID().toString();
  }
  public static String getMacAddress() {
    byte[] mac = null;
    try {
      Enumeration<NetworkInterface> infs = NetworkInterface.getNetworkInterfaces();
      //mac = .nextElement().getHardwareAddress();
      for (;infs.hasMoreElements();) {
        NetworkInterface d = infs.nextElement();
        /*System.out.println("Network inf: ");
        System.out.println("             Name: "+d.getDisplayName());
        System.out.println("             Virtual: "+d.isVirtual());
        System.out.println("             MAC: "+MAC2String(d.getHardwareAddress()));*/
        byte[] addr = d.getHardwareAddress();
        if(addr!=null) {
          mac = addr;
          break;
        }
      }
    } catch (SocketException ex) {
      System.out.println(ex);
      return "";
      //Logger.getLogger(SecureSettings.class.getName()).log(Level.SEVERE, null, ex);
    } catch(NullPointerException e) {
      System.out.println(e);
      e.printStackTrace();
      return ""; 
    }
    
    if(mac==null) {
      System.out.println("MAC is null.");
      return "";
    }
    return MAC2String(mac);
  }
  public static String MAC2String(byte[] mac) {
    if(mac==null)
      return "";
    StringBuilder b = new StringBuilder();
    
    for (int i = 0; i < mac.length; i++) {
      if(i>0)
        b.append('-');
      b.append(String.format("%02X", (int)(mac[i]&0xFF)));
    }
    return b.toString();
  }
  public boolean doesUse_hwid() {
    return use_hwid;
  }

  public void setUse_hwid(boolean use_hwid) {
    //If the keys were generated, they will need to be generated again
    if(use_hwid!=this.use_hwid)
      initialized = false;
    this.use_hwid = use_hwid;

  }

  public void setPassword(String password) {
    //If the keys were generated, they will need to be generated again
    if(!password.equals(this.password))
      initialized = false;
    this.password = password;
  }
  
  public boolean doesUse_password() {
    return use_password;
  }

  public void setUse_password(boolean use_password) {
    //If the keys were generated, they will need to be generated again
    if(use_password!=this.use_password)
      initialized = false;
    this.use_password = use_password;

  }

}
