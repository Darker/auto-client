/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;

/**
 *
 * @author Jakub
 */
public interface PasswordInitialiser {
  public String getPassword() throws PasswordFailedException;
  public default boolean equals(PasswordInitialiser p) {
    try {
        return p!=null && (p==this || p.getPassword().equals(this.getPassword()));
    }
    catch(PasswordFailedException e) {
      return false; 
    }
  }
  public default boolean equals(String p) {
    try {
      return p!=null && (p.equals(this.getPassword()));
    }
    catch(PasswordFailedException e) {
      return false; 
    }
  }
  
  public static class StringPasswordInitialiser implements PasswordInitialiser {
    private final String password;
    public StringPasswordInitialiser(String password) {
      this.password = password; 
    }
    @Override
    public String getPassword() {
      return password;
    }
    @Override
    public int hashCode() {
      return password.hashCode();
    }
  }
}
