/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.passive_automation;

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
import static cz.autoclient.GUI.ImageResources.PA_BOT_DISABLED_ERROR;
import static cz.autoclient.GUI.ImageResources.PA_BOT_STOPPED;
import static cz.autoclient.GUI.ImageResources.PA_BOT_PAUSED;
import static cz.autoclient.GUI.ImageResources.PA_BOT_RUNNING;
import cz.autoclient.settings.input_handlers.InputJCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Passive automation menu - the basic menu that handles enabling, starting and disabling of
 * a passive automation robot.
 * @author Jakub
 */
public class PAMenu {  
  
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
    /*root.setIcon(PA_BOT_DISABLED_ERROR.getIcon());
    if(true)
      return;*/
    if(running)
      root.setIcon(PA_BOT_RUNNING.getIcon());
    else {
      if(enabled) 
        root.setIcon(PA_BOT_PAUSED.getIcon());
      else
        root.setIcon(robot.isErrorDisabled()?PA_BOT_DISABLED_ERROR.getIcon():PA_BOT_STOPPED.getIcon());
    } 
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
        //Reset the robots data, it has not been running and data may be outdated
        robot.reset();
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
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            setIcon();
            System.out.println("Bot "+name+" started.");
          }
        }
      );
    };
    @Override
    public void terminated(Throwable e) {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            setIcon(enable.getState(), false);
            if(e==null)
              System.out.println("Bot "+name+" terminated.");
            else
              System.out.println("Bot "+name+" terminated with error: \n     "+e);
          }
        }
      );
    };
    @Override
    public void disabledByError(Throwable e) {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
              //updateEnabledState(false);
              if(enable.getState()==true) {
                //Simulate Click on the menu item to trigger any callbacks
                ActionEvent event = new ActionEvent(enable, 666, "die");
                enable.setState(false);
                for(ActionListener l :enable.getActionListeners()) {
                  l.actionPerformed(event);
                  //Remove the settings action listener
                  if(l instanceof InputJCheckBoxMenuItem.ChangeListener) {
                    //Remember the listener to be able to put it back
                    enableBlocker.oldListener = l;
                    enable.removeActionListener(l);
                  }
                }
                enableBlocker.error = e;
                //Add the listener with warning popup
                enable.addActionListener(enableBlocker);
              }
              //Just update icon if already disabled (though this should not occur)
              else {
                setIcon(); 
              }
          }
        }
      );
    }
  }
  public final EnableBlocker enableBlocker = new EnableBlocker(null);
  protected class EnableBlocker implements ActionListener {
    public Throwable error;
    public ActionListener oldListener;
    public EnableBlocker(Throwable error) {
      this.error = error; 
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      if(error==null)
        return;
      JCheckBoxMenuItem comp;
      try {
        comp = (JCheckBoxMenuItem)e.getSource();
      }
      catch(ClassCastException ex) {
        System.err.println("Invalid action source.");
        return;
      }
      boolean enabled = comp.getState();
      if(enabled && robot.isErrorDisabled()) {
        int state = JOptionPane.showConfirmDialog(PAMenu.this.enable, "This automat was "
            + "disabled due to following exception:\n"
            + "   "+robot.getLastError()+"\n"
            + "Are you sure you want to run it again?", "Robot disabled by error",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE);
        if(state==1) {
          ((JCheckBoxMenuItem)comp).setState(false);
          
          //updateEnabledState(false);
        }
        else {
          robot.forgetErrors();
          oldListener.actionPerformed(e);
          //Put the action listener back
          comp.addActionListener(oldListener);
        }
      }
    }
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
