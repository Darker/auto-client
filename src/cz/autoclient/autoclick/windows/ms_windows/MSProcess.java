/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows.ms_windows;

/**
 *
 * @author Jakub
 */
public class MSProcess implements cz.autoclient.autoclick.Process {
  public  long pid;
  public  String name;
  public  String username;
  
  /**
   * Widnows tasklist uses localised version ov Not available sentence for not available entries.
   * We need to recognize those entries.
   */
  private static String NOT_AVAILABLE;
  
  @Override
  public boolean terminate() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public long getPID() {
    return pid;
  }

  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public String getUser() {
    return username;
  }

  @Override
  public boolean isAdmin() {
    return username==null;
  }

  @Override
  public int getMemory() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
