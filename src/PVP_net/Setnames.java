/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PVP_net;

/**
 *
 * @author Jakub
 */
public enum Setnames {
  TEAMBUILDER_ENABLED("tb_enabled", false);
  
  public final String name;
  public final Object def;
  Setnames(String n, Object d) {
    name = n;
    def = d;    
  }
}
