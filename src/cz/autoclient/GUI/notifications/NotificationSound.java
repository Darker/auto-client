/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

import cz.autoclient.settings.Settings;
import java.awt.TrayIcon;

/**
 *
 * @author Jakub
 */
public class NotificationSound extends Notification {
  public NotificationSound(Notification.Def def, Settings sets, TrayIcon icon) {
    super(def, sets);
  }
  @Override
  public void notification() {
  
  }
  
  public enum Def {
    ;
   /* public final 
    Def(String soundPath) {
      
    }*/
    
  }
}
