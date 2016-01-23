/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
import java.awt.TrayIcon;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JCheckBoxMenuItem;

/** Abstract representation of notification. Extend from this class and make a notification
 * that actually notifies users.
 * @author Jakub
 */
public abstract class Notification {
  protected final JCheckBoxMenuItem menu_item;
  protected final Settings settings;
  protected final Def definition;
  /**
   * This constructor initiates the notification menu item that allows to toggle this 
   * notification on and off.
   * @param def
   * @param sets 
   */
  protected Notification(Def def, Settings sets) {
    settings = sets;
    definition = def;
    
    menu_item = new JCheckBoxMenuItem();
    settings.bindToInput(definition.setting.name, menu_item);
    menu_item.setState(settings.getBoolean(definition.setting.name, (boolean)definition.setting.default_val));
    menu_item.setText(definition.name);
    menu_item.setToolTipText(definition.text);
  }
  /**
   * Creates a notification based on implementation. Can use the definition and operate on it.
   */
  public abstract void notification();
  
  public JCheckBoxMenuItem getMenuItem() {
    return menu_item; 
  }
  
  
  public enum Def {
    TB_GROUP_JOINED("TB: Group joined",
                 "tb.solo.group_joined",
                 "Teambuilder group was joined, waiting for ready button.",
                 Setnames.NOTIF_MENU_TB_GROUP_JOINED,
                 TrayIcon.MessageType.INFO
    ),
    TB_GAME_CAN_START("TB: Game can start",
                 "tb.captain.game_can_start",
                 "Everybody is ready. It's time to start the game!",
                 Setnames.NOTIF_MENU_TB_READY_TO_START,
                 TrayIcon.MessageType.INFO
    ),
    TB_PLAYER_JOINED("TB: A player joined the group",
                 "tb.captain.player_joined",
                 "A player has joined your group, you might want to check him out.",
                 Setnames.NOTIF_MENU_TB_PLAYER_JOINED,
                 TrayIcon.MessageType.INFO
    ),
    BLIND_TEAM_JOINED("In lobby",
                 "blind.lobby.joined",
                 "You've been put in lobby with your teammates.",
                 Setnames.NOTIF_MENU_BLIND_IN_LOBBY,
                 TrayIcon.MessageType.INFO
    ),
    UPDATE_AVAILABLE("Update available",
                 "app.update.exists",
                 "An update is available. Check Tools->Updates. Configure this notification on Notifications menu.",
                 Setnames.NOTIF_MENU_UPDATE_EXISTS,
                 TrayIcon.MessageType.INFO
    ),
    ;
    Def(String n, String p, String t, Setnames setting, TrayIcon.MessageType type) {
      name = n;  
      text = t;
      path = p;
      this.setting = setting;
      this.type = type;
    }
    public final String name;
    public final String text;
    public final String path;
    public final Setnames setting;
    public final TrayIcon.MessageType type;
    
    public Notification createInstance(Class<? extends Notification> source, Settings set, Object... params) throws NoSuchMethodException
    {
      return instantiate(source, this, set, params);
    }
    public static Notification instantiate(Class<? extends Notification> source, Def def, Settings set, Object... params) throws NoSuchMethodException
    {
      //Copying ARGUMENT TYPES:
      Class[] argument_types = new Class[2+params.length];
      argument_types[0] = def.getClass(); 
      argument_types[1] = set.getClass();
      //Copy rest of the argument_types
      for(int i=0;i<params.length; i++) {
        if(params[i]==null) {
          System.err.println("PLZ NO NULL!");
          argument_types[i+2] = Object.class;
        }
        else 
         argument_types[i+2] = params[i].getClass();      
      }
      //Copying ARGMENT VALUES:
      Object[] arguments = new Object[2+params.length];
      arguments[0] = def; 
      arguments[1] = set;
      System.arraycopy(params, 0, arguments, 2, params.length);

      
      //Variable that is returned if no exception triggers, which is likely to happen.
      Notification result;
      try {   
        result = source.getDeclaredConstructor(argument_types).newInstance(arguments);
      } catch (InstantiationException ex) {
        throw new NoSuchMethodException("Instantiation exception.");
      } catch (IllegalAccessException ex) {
        throw new NoSuchMethodException("Illegal access exception.");
      } catch (IllegalArgumentException ex) {
        throw new NoSuchMethodException("Illegal argument exception.");
      } catch (InvocationTargetException ex) {
        throw new NoSuchMethodException("InvocationTargetException");
      }

      /*catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw new NoSuchMethodException("No valid handler for this input type.");
      }*/
      return result;
    }
    public static void createAll(Notifications target, Class<? extends Notification> source, Settings set, Object... params) {
      for(Def d : values()) {
        try {
          target.addNotification(instantiate(source, d, set, params));
        }
        catch(NoSuchMethodException e) {
          System.err.println("createAll failed for "+d.name()); 
          e.printStackTrace();
          //Nothing, just ignore it.
        }
      }
    }

  }
}
