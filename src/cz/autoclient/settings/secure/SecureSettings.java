/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;
import static cz.autoclient.settings.secure.UniqueID.getMacAddress;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

  private final List<PasswordInitialiser> passwords = new ArrayList<>();
  
  private Cipher cipher;
  private IvParameterSpec ivSpec;
  private SecretKeySpec key;
  
  protected boolean initialized = false;

  public boolean isInitialized() {
    return initialized;
  }
  public SecureSettings() {
    //System.out.println("Creating new encryptor: " + hashCode());
  }
  public SecureSettings(String password) {
    this();
    passwords.add(new PasswordInitialiser.StringPasswordInitialiser(password));
  }
  public SecureSettings(PasswordInitialiser callback) {
    this();
    passwords.add(callback);
  }
  public SecureSettings(PasswordInitialiser... callbacks) {
    this();
    passwords.addAll(Arrays.asList(callbacks));
    //for(PasswordInitialiser cb : callbacks) 
    //  passwords.add(cb);
  }
  public void addPassword(PasswordInitialiser callback) {
    passwords.add(callback);
    initialized = false;
  }
  public void addPassword(String password) {
    passwords.add(new PasswordInitialiser.StringPasswordInitialiser(password));
    initialized = false;
  }
  public void removePassword(PasswordInitialiser callback) {
    for(int i=0,l=passwords.size(); i<l; i++) {
      if(passwords.get(i).equals(callback)) {
        passwords.remove(i);
        break;
      }
    }
    initialized = false;
  }
  public void replacePassword(PasswordInitialiser newpw, PasswordInitialiser toReplace) {
    for(int i=0,l=passwords.size(); i<l; i++) {
      if(passwords.get(i).equals(toReplace)) {
        passwords.set(i, newpw);
        break;
      }
    }
    initialized = false;
  }
  public void replacePassword(String newpw, String toReplace) {
    for(int i=0,l=passwords.size(); i<l; i++) {
      if(passwords.get(i).equals(toReplace)) {
        passwords.set(i, new PasswordInitialiser.StringPasswordInitialiser(newpw));
        break;
      }
    }
    initialized = false;
  }
  public boolean hasPasswords() {
    return !passwords.isEmpty();
  }
  public void clearPasswords() {
    passwords.clear();
    initialized = false;
  }
  
  public String getMergedPassword() {
    StringBuilder data = new StringBuilder();
    for(PasswordInitialiser intz : passwords) {
      data.append(intz.getPassword());
    }
    return data.toString();
  }
  
  public void init() {
    if(initialized)
      return;

    byte[] data_bytes = getMergedPassword().getBytes();
    byte[] bytes;
    try {
      MessageDigest sha= MessageDigest.getInstance("MD5");
      bytes = sha.digest(data_bytes);
    }
    catch (NoSuchAlgorithmException e) {
      bytes = new byte[16];
      byte[] bytes2 = data_bytes;
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
    set.setEncryptor(this);
    return set.getDecryptedValue();
  }
  public byte[] encrypt(EncryptedSetting set) throws InvalidPasswordException {
    set.setEncryptor(this);
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
}
