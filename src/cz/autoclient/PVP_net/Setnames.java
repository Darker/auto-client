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
  //Whether the program should automatically start a game if possible
  TEAMBUILDER_AUTOSTART_ENABLED("tb_cap_autostart_enabled", false),
  INVITE_ENABLED("invite_enabled", false),
  TRAY_ICON_ENABLED("tray_enabled", true),
  TRAY_ICON_MINIMIZE("tray_minimize", false),
  
  BLIND_SUMMONER1("sumspell1", SummonerSpell.Teleport),
  BLIND_SUMMONER2("sumspell2", SummonerSpell.Flash),
  /**NOTFICATONS**/
  NOTIF_MENU_TB_GROUP_JOINED("not_menu_tb_gr_join", false),
  
  //Notify when everybody is ready and the game can start (or was started, if auto start is enabled)
  NOTIF_MENU_TB_READY_TO_START("not_menu_tb_game_canstart", true),
  NOTIF_MENU_TB_PLAYER_JOINED("not_menu_tb_player_joined", false)
  
  
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
