/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenu;

/**
 * A collection of notifications. Can be used for casual mass operations on notifications as 
 * well as for issuing notifications easily.
 * @author Jakub
 */
public class Notifications {
  private Map<Notification.Def, ArrayList<Notification>> notifications = new HashMap<>();
  public void addNotification(Notification notif) {
    Notification.Def def = notif.definition;
    ArrayList<Notification> notifs;
    if(!notifications.containsKey(def)) {
      notifs = new ArrayList<>();
      notifications.put(def, notifs);
    }
    else {
      notifs = notifications.get(def);
    }
    notifs.add(notif);
  }
  /**
   * Issue notification based on definition. All kinds of notifications with this definition will be
   * dispatched.
   * @param names 
   */
  public void notification(Notification.Def... names) {
    ArrayList<Notification> notifs;
    for (Notification.Def name : names) {
      notifs = notifications.get(name);
      for(Notification notif : notifs) {
        notif.notification();
      }
    }    
  }
  /**
   * Loop through notifications and add notifications of one type to the menu.
   * @param menu Jmenu that will be filled with notification toggle buttons
   * @param template class type required for the notifications to be added. Use this to filter notifications.
   */
  public void addToJMenu(JMenu menu, Class<? extends Notification> template) {
    for (Map.Entry pair : notifications.entrySet()) {
      ArrayList<Notification> list = (ArrayList)pair.getValue();
      for(Notification notif : list) {
        if(template.isInstance(notif)) {
          menu.add(notif.getMenuItem());
        }
      }
    }
  }
}
