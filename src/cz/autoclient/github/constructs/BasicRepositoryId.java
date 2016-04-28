/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.constructs;

import cz.autoclient.github.interfaces.RepositoryId;

/**
 *
 * @author Jakub
 */
public class BasicRepositoryId implements RepositoryId {
  public BasicRepositoryId(String username, String name) {
    this.username = username;
    this.name = name;
  }
  public BasicRepositoryId(String compoundName) {
    String[] splat = compoundName.split("/");
    if(splat.length==2) {
      username = splat[0];
      name = splat[1];
    }
    else
      throw new IllegalArgumentException("Invalid github path: "+compoundName);
  }
 
  @Override
  public String getUsername() {
    return username;
  }
  @Override
  public String getName() {
    return name;
  }
  @Override
  public String toString() {
    return username+"/"+name; 
  }
  public final String name;
  public final String username;
}
