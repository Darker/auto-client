/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

/**
 *
 * @author Jakub
 */
public interface Process {
  public boolean terminate();
  //public Window showConsole();
  public long getPID();
  public String getName();
  public String getUser();
  public boolean isAdmin();
  public int getMemory();
}
