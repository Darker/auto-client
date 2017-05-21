/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class EncryptedSetting implements Serializable {
  private transient SecureSettings encryptor;
  private transient Serializable decryptedValue = null;
  private byte[] encryptedValue = null;
  private boolean hasValue = false;

  public SecureSettings getEncryptor() {
    return encryptor;
  }

  public boolean doesHaveValue() {
    return hasValue;
  }

  public void setEncryptor(SecureSettings encryptor) {
    if(encryptor==null) {
      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "NUL ENCRYPTOR OH NO");
      new Exception().printStackTrace(System.out);
    }
    this.encryptor = encryptor;
  }

  public EncryptedSetting(SecureSettings encryptor) {
    setEncryptor(encryptor);
  }
  public Serializable getDecryptedValue() throws InvalidPasswordException {
    if(decryptedValue==null) {
      if(encryptedValue==null) {
        if(!hasValue)
          return null;
        else
          throw new IllegalStateException("Neither encrypted or decrypted values are known. Can't get decrypted value.");
      }
      if(encryptor==null)
        throw new IllegalStateException("Encrypting class is null, nothing can be decrypted.");
      decryptedValue = encryptor.decryptObject(encryptedValue);
      if(decryptedValue==null) {
        throw new InvalidPasswordException("Failed to decrypt value - seems your password is incorrect."); 
      }
    }
    return decryptedValue;
  }
  public void setDecryptedValue(Serializable unencryptedValue) {
    decryptedValue = unencryptedValue;
    hasValue = unencryptedValue!=null;
    encryptedValue = null;
  }
  public byte[] getEncryptedValue() {
    if(encryptedValue==null) {
      //Null encrypts back to null
      if(decryptedValue==null) {
        return null;
        //throw new IllegalStateException("Neither encrypted or decrypted values are known. Can't get encrypted value.");
      }
      encryptedValue = encryptor.encrypt(decryptedValue);
      //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Encrypted value: "+encryptedValue);
    }
    return encryptedValue;
  }
  public void setEncryptedValue(byte[] encryptedValue) {
    this.encryptedValue = encryptedValue;
    decryptedValue = null;
  }
  public void forgetEncryptedValue() {
    if(decryptedValue==null && hasValue) {
      if(encryptor!=null)
        getDecryptedValue();
      else
        throw new IllegalStateException("Loosing data. This instance needs to be decrypted before you discard encrypted data.");
    }
    encryptedValue = null;
  }
  private void writeObject(ObjectOutputStream oos) throws IOException {
    if(encryptor!=null) {
      getEncryptedValue();
    }
    else if(encryptedValue==null && hasValue) {
      throw new IllegalStateException("This setting needs to be encrypted before serializing!"); 
    }
    oos.defaultWriteObject();
    //oos.writeObject(this);
  }
}
