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
public class NotificationTrayBaloon extends Notification {
  private final TrayIcon tray_icon;
  public NotificationTrayBaloon(Def def, Settings sets, TrayIcon icon) {
    super(def, sets);
    tray_icon = icon;
  }
  @Override
  public void notification() {
    tray_icon.displayMessage(definition.name, definition.text, TrayIcon.MessageType.INFO);
  }
}
