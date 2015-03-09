/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author Jakub
 */
public abstract class Notification {
  private final JCheckBoxMenuItem menu_item;
  private final Settings settings;
  private final Definition definition;
  public Notification(Definition def, Settings sets) {
    settings = sets;
    definition = def;
    
    menu_item = new JCheckBoxMenuItem();
    settings.bindToInput(definition.setting.name, menu_item);
    menu_item.setState(settings.getBoolean(definition.setting.name, (boolean)definition.setting.default_val));
  }
  /**
   * Creates a notification based on implementation. Can use the definition and operate on it.
   */
  public abstract void notification();
  
  
  public enum Definition {
    ;
    Definition(String n, String t, Setnames setting) {
      name = n;  
      text = t;
      this.setting = setting;
    }
    public final String name;
    public final String text;
    public final Setnames setting;
  }
}
