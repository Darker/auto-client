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
  public String getPassword();
  public default boolean equals(PasswordInitialiser p) {
    return p!=null && (p==this || p.getPassword().equals(this.getPassword()));
  }
  public default boolean equals(String p) {
    return p!=null && (p.equals(this.getPassword()));
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
    public boolean equals(PasswordInitialiser p) {
      return p!=null && (p==this || p.getPassword().equals(this.getPassword()));
    }
    @Override
    public int hashCode() {
      return password.hashCode();
    }
  }
}
