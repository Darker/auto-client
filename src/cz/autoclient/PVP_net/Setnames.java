/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.settings.Settings;

/**
 * Holds setting names and default values. This allows us to easily refactor settings whenever we want.
 * @author Jakub
 */
public enum Setnames {
  TEAMBUILDER_ENABLED("tb_enabled", false),
  INVITE_ENABLED("invite_enabled", false),
  TRAY_ICON_ENABLED("tray_enabled", true),
  TRAY_ICON_MINIMIZE("tray_minimize", false)
  ;
  
  public final String name;
  public final Object default_val;
  Setnames(String n, Object d) {
    name = n;
    default_val = d;    
  }
  public static void setDefaults(Settings settings) {
    for(Setnames set : Setnames.values()) {
      settings.setSettingDefault(set.name, set.default_val);
    }
  }
}
