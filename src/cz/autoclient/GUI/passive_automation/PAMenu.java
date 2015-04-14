/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.passive_automation;

import cz.autoclient.GUI.ImageResources;
import cz.autoclient.robots.BotActionListener;
import cz.autoclient.robots.Robot;
import cz.autoclient.robots.RobotManager;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.SettingsValueChanger;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Passive automation menu - the basic menu that handles enabling, starting and disabling of
 * a passive automation robot.
 * @author Jakub
 */
public class PAMenu {
  public static final ImageResources disabled_icon = ImageResources.PA_BOT_STOPPED;
  public static final ImageResources enabled_icon = ImageResources.PA_BOT_PAUSED;
  public static final ImageResources running_icon = ImageResources.PA_BOT_RUNNING;
  
  private static Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

  public final Robot robot;
  protected RobotManager robots;
  public final Settings settings;
  public final JMenu root;
  public final JCheckBoxMenuItem enable;
  //Other GUI
  protected JMenuItem aboutLink;
  //Null until populated
  private PAConfigWindow config_window;
  
  protected String name;
  protected String settingName;
  public PAMenu(Robot robot, Settings settings, String name, String settingName) {
    this.robot = robot;
    this.settings = settings;
    this.name = name;
    this.settingName = settingName;
    //Create GUI
    root = new JMenu();
    enable = new JCheckBoxMenuItem();
    initGui();
    //Assign bot callback
    robot.setListener(new BOTStateUpdater());
  }
  public PAMenu(Robot robot, Settings settings, String name) {
    this(robot, settings, name, "PA_" + name.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_]", "").toUpperCase());
  }
  protected void initGui() {
    //Set main node text
    root.setText(name);
    
    //Configure the enable/disable item
    boolean enabled = settings.getBoolean(settingName, false);
    enable.setState(enabled);
    enable.setText("Enable/disable");
    //Remember the last state for further updates
    lastState = enabled;
    setIcon(enabled);
    root.add(enable);
    
    
    //Prepare about item
    aboutLink = new JMenuItem();
    aboutLink.setVisible(false);
    root.add(aboutLink);
    /** Assign callbacks **/
    settings.bindToInput(settingName, enable, new EnabledStateUpdater());
  }
  public void setAboutLink(final URL link) {
    if(desktop == null || !desktop.isSupported(Desktop.Action.BROWSE))
      return;
    //Make the about item visible
    aboutLink.setVisible(true);
    aboutLink.setText("About");
    //aboutLink.setIcon(ImageResources.INTERNET.getIcon());
    //Add the click-redirect listener
    aboutLink.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        displayAboutPage(link);
      }
    });
  }
  public void setAboutLink(String link) {
    try {
      setAboutLink(new URL(link));
    }
    catch(MalformedURLException e) {};
  }
  public static void displayAboutPage(URL page) {
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(page.toURI());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  protected void setIcon(boolean enabled, boolean running) {
   if(running)
     root.setIcon(running_icon.getIcon());
   else
     root.setIcon(enabled?enabled_icon.getIcon():disabled_icon.getIcon());
  }
  protected void setIcon(boolean enabled) {
    setIcon(enabled, robot.isRunning());
  }
  protected void setIcon() {
    setIcon(enable.getState(), robot.isRunning());
  }

  public RobotManager getRobots() {
    return robots;
  }
  public void setRobots(RobotManager robots) {
    if(enable.getState()) {
      robots.addRobot(robot);
    }
    this.robots = robots;
  }
  
  protected boolean lastState = false;
  protected void updateEnabledState(boolean enabled) {
    if(lastState != enabled) {
      setIcon(enabled);
      if(enabled) {
        if(robots!=null)
          robots.addRobot(robot);
        System.out.println("Bot "+name+" enabled.");
      }
      else {
        if(robots!=null)
          robots.removeRobot(robot);
        else
          robot.stop();
        System.out.println("Bot "+name+" disabled.");
      }
      lastState = enabled;
    }
  }
  /** CONFIG WINDOW **/
  public PAConfigWindow createConfigWindow() {
    if(config_window==null) {
      config_window = new PAConfigWindow(this, settings);
      root.add(config_window.actuator, 1);
    }   
    return config_window;
  }
  public PAConfigWindow createConfigWindow(Window parent) {
    if(config_window==null) {
      config_window = new PAConfigWindow(this, settings, parent);
      root.add(config_window.actuator, 1);
    }   
    return config_window;
  }
  
  
  protected class BOTStateUpdater implements BotActionListener {
    @Override
    public void started() {
      setIcon();
      System.out.println("Bot "+name+" started.");
    };
    @Override
    public void terminated(Exception e) {
      setIcon(enable.getState(), false);
      System.out.println("Bot "+name+" terminated.");
    };
  }
  protected class EnabledStateUpdater extends SettingsInputVerifier implements SettingsValueChanger {
    @Override
    public Boolean value(JComponent comp) {
      boolean enabled = ((JCheckBoxMenuItem)comp).getState();
      updateEnabledState(enabled);
      return enabled;
    }
    @Override
    public boolean verify(JComponent input) {return true;}
    @Override
    public void setValue(JComponent comp, Object value) {
      boolean enabled = value instanceof Boolean? (Boolean)value:false;
      updateEnabledState(enabled);
      ((JCheckBoxMenuItem)comp).setState(enabled);
    }
  }
}
