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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;

/** Abstract representation of notification. Extend from this class and make a notification
 * that actually notifies users.
 * @author Jakub
 */
public abstract class Notification {
  protected final JCheckBoxMenuItem menu_item;
  protected final Settings settings;
  protected final Def definition;
  protected final String settingName;
  protected boolean defaultValue;
  /**
   * This constructor initiates the notification menu item that allows to toggle this 
   * notification on and off.
   * @param def
   * @param sets 
   */
  protected Notification(Def def, Settings sets) {
    settings = sets;
    definition = def;
    settingName = "notif_"+definition.path.replace(".", "_")+"_"+this.getClass().getSimpleName().replace("Notification", "").toLowerCase();
    // Try to lead default value from settings
    defaultValue = false;
    try {
      Setnames setting = Setnames.valueOf(settingName.toUpperCase());
      if(setting!=null) {
        if(setting.default_val!=null && setting.default_val instanceof Boolean) {
          defaultValue = (boolean)setting.default_val;
        }
      }
    }
    catch(IllegalArgumentException e) {}
    
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Notification setting name: \""+settingName+"\" (Setnames."+settingName.toUpperCase()+")");
    if(def.visible) {
      menu_item = new JCheckBoxMenuItem();
      settings.bindToInput(settingName, menu_item);
      menu_item.setState(settings.getBoolean(settingName, defaultValue));
      menu_item.setText(definition.name);
      menu_item.setToolTipText(definition.text);
    }
    else {
      menu_item = null; 
    }
  }
  /**
   * Creates a notification based on implementation. Can use the definition and operate on it.
   */
  public abstract void notification();
  
  public JCheckBoxMenuItem getMenuItem() {
    return menu_item; 
  }
  
  public boolean isEnabled() {
    return settings.getBoolean(settingName, defaultValue);
  }
  
  
  public enum Def {
    TB_GROUP_JOINED("TB: Group joined",
                 "tb.solo.group_joined",
                 "Teambuilder group was joined, waiting for ready button.",
                 TrayIcon.MessageType.INFO,
                 false
    ),
    TB_GAME_CAN_START("TB: Game can start",
                 "tb.captain.game_can_start",
                 "Everybody is ready. It's time to start the game!",
                 TrayIcon.MessageType.INFO,
                 false
    ),
    TB_PLAYER_JOINED("TB: A player joined the group",
                 "tb.captain.player_joined",
                 "A player has joined your group, you might want to check him out.",
                 TrayIcon.MessageType.INFO,
                 false
    ),
    BLIND_TEAM_JOINED("In lobby",
                 "blind.lobby.joined",
                 "You've been put in lobby with your teammates.",
                 TrayIcon.MessageType.INFO,
                 "/bell.wav"
    ),
    DRAFT_TEAM_JOINED("Draft lobby",
                 "draft.lobby.joined",
                 "You've been put in draft lobby!",
                 TrayIcon.MessageType.INFO,
                 "/bell.wav"
    ),
    UPDATE_AVAILABLE("Update available",
                 "app.update.exists",
                 "An update is available. Check Tools->Updates. Configure this notification on Notifications menu.",
                 TrayIcon.MessageType.INFO
    ),
    UPDATE_DOWNLOADED("Update downloaded",
                 "app.update.downloaded",
                 "An update is ready to install. Check Tools->Updates.",
                 TrayIcon.MessageType.INFO
    ),
    ;
    Def(String n, String p, String t, TrayIcon.MessageType type, String audioFile, boolean visible) {
      name = n;  
      text = t;
      path = p;
      this.audioFile = audioFile;
      this.type = type;
      this.visible = visible;
    }
    Def(String n, String p, String t, TrayIcon.MessageType type, boolean visible) {
      this(n,p,t,type, null, visible);
    }
    Def(String n, String p, String t, TrayIcon.MessageType type, String audioFile) {
      this(n,p,t,type, audioFile, true);
    }
    Def(String n, String p, String t, TrayIcon.MessageType type) {
      this(n,p,t,type, null, true);
    }
    
    public final String name;
    public final String text;
    public final String path;
    public final String audioFile;
    public final TrayIcon.MessageType type;
    public final boolean visible;
    
    public Notification createInstance(Class<? extends Notification> source, Settings set, Object... params) throws Exception
    {
      return instantiate(source, this, set, params);
    }
    public static Notification instantiate(Class<? extends Notification> source, Def def, Settings set, Object... params) throws Exception
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
        Throwable exception = ex.getTargetException();
        if(exception instanceof Exception)
          throw (Exception)exception;
        else
          throw new Exception("Shit goin' down, throwable thrown! WTF run!!!");
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
        catch(CantCreateNotification e) {
          Logger.getLogger(Notification.class.getName()).log(Level.INFO, "Cannot create "+d.name()+" because: "+e.getMessage());
        }
        catch(Exception e) {
          System.err.println("createAll failed for "+d.name()); 
          e.printStackTrace();
          //Nothing, just ignore it.
        }
      }
    }
  }
}
