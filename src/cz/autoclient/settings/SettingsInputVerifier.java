/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings;

import java.lang.reflect.ParameterizedType;

/**
 *
 * @author Jakub
 * @param <T> object that the value should be converted to
 */
public abstract class SettingsInputVerifier<T> extends javax.swing.InputVerifier {
  public abstract T value(javax.swing.JComponent comp);
  
  //Invalid verifier passed if no verifier defined
  public final static SettingsInputVerifier INVALID_VERIFIER = new SettingsInputVerifier<String>() {
     @Override
     public String value(javax.swing.JComponent comp) {
       if(comp instanceof javax.swing.JTextField)
         return ((javax.swing.JTextField)comp).getText();
       else 
         return null;
     }
     @Override
     public boolean verify(javax.swing.JComponent comp) {
       return true; 
     }
  };
  
  public Class getType() {
    return (Class)
     ((ParameterizedType)getClass().getGenericSuperclass())
       .getActualTypeArguments()[0];
  }
}
