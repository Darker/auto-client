/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Jakub
 */
public class EncryptedSetting implements Serializable {
  public transient SecureSettings encryptor;
  private transient Serializable decryptedValue = null;
  private byte[] encryptedValue = null;
  private boolean hasValue = false;

  
  
  public EncryptedSetting(SecureSettings encryptor) {
    this.encryptor = encryptor;
  }
  public Serializable getDecryptedValue() throws InvalidPasswordException {
    if(decryptedValue==null) {
      if(encryptedValue==null) {
        if(!hasValue)
          return null;
        else
          throw new IllegalStateException("Neither encrypted or decrypted values are known. Can't get decrypted value.");
      }
      
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
      //System.out.println("Encrypted value: "+encryptedValue);
    }
    return encryptedValue;
  }
  public void setEncryptedValue(byte[] encryptedValue) {
    this.encryptedValue = encryptedValue;
    decryptedValue = null;
  }
  private void writeObject(ObjectOutputStream oos) throws IOException {
    if(encryptor!=null) {
      getEncryptedValue();
    }
    oos.defaultWriteObject();
    //oos.writeObject(this);
  }
}
