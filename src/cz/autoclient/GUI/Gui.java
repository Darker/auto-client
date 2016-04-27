package cz.autoclient.GUI;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import cz.autoclient.GUI.champion.ConfigurationManager;
import cz.autoclient.GUI.tabs.TabbedWindow;
import cz.autoclient.GUI.tabs.FieldDef;
import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.GUI.notifications.NotificationTrayBaloon;
import cz.autoclient.GUI.notifications.Notifications;
import cz.autoclient.GUI.passive_automation.PAConfigWindow;
import cz.autoclient.GUI.passive_automation.PAMenu;
import cz.autoclient.GUI.summoner_spells.ButtonSummonerSpellMaster;
import cz.autoclient.GUI.tabs.MultiFieldDef;
import cz.autoclient.GUI.updates.UpdateMenuItem;
import cz.autoclient.Main;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.windows.ClickProxy;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;
import cz.autoclient.settings.Settings;
import cz.autoclient.dllinjection.DLLInjector;
import cz.autoclient.dllinjection.InjectionResult;
import cz.autoclient.league_of_legends.maps.Champions;
import cz.autoclient.robots.AutoLoginBot;
import cz.autoclient.robots.AutoQueueBot;
import cz.autoclient.robots.LaunchBot;
import cz.autoclient.robots.RobotManager;
import cz.autoclient.robots.exceptions.NoSuchRobotException;
import cz.autoclient.settings.SettingsInputVerifier;
import cz.autoclient.settings.SettingsValueChanger;
import cz.autoclient.settings.secure.EncryptedSetting;
import cz.autoclient.updates.Progress;
import cz.autoclient.updates.UpdateInfo;
import cz.autoclient.updates.UpdateInfoListener;
import cz.autoclient.updates.Updater;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
 
 public class Gui
   extends JFrame
 {
   private Main ac;
   private StateGuard guard;
   private Settings settings;
   private Notifications notifications = new Notifications();
   //Is PVP.net window available?
   private boolean pvp_net_window = false;
   //Was our DLL loaded by the user?
   private boolean dll_loaded = false;
   /** GUI ELEMENTS **/
   
   private JMenuItem menu_threadcontrol_pretend_accepted;
   private JMenuItem menu_dll_additions;
   private JCheckBoxMenuItem menu_tray_enabled;
   private JCheckBoxMenuItem menu_tray_minimize;
   
   UpdateMenuItem updateMenuItem;
   
   //TRAY ICON STUFF
   private TrayIcon tray_icon;
   //Remember whether tray icon has been added or not
   private boolean tray_added = false;
   private final SystemTray tray;
   
   //Robot management
   public RobotManager robots;
   
   /**
    * Is set to true if the AutoClient anti-annoyance functions are in place
    */
   private boolean anoyance_disabled = false;
   private ConfigurationManager champ_config;
   private Updater updater;
   
   
   public String[] getSelectedMode()
   {
     return new String[0];//(String[]) (selected != null ? selected : "");
   }
   public static Gui inst;
   
   public Gui(Main acmain, final Settings settings)
   {
     if(inst!=null)
       throw new IllegalStateException("Only one Gui instance allowed.");
     
     this.ac = acmain;
     this.settings = settings;
     
     inst = this;
     
     //The tray is final so it must be initialised in constructor
     if(SystemTray.isSupported())
       tray = SystemTray.getSystemTray();
     else
       tray = null;
     
     robots = new RobotManager(1200);
         // Create menu and stuff
     initMenu();
     initComponents();
     //There is many factors that determine whether the icon will be shown
     // it must be both supported and enabled
     displayTrayEnabled();
     final Thread finalize = new Thread("Starting GUI threads") {
       @Override
       public void run() {
         setIconImage(getIcon().getImage());

         // Display settings:
         settings.displaySettingsOnBoundFields();
         // Start guarding
         guard = new StateGuard(Gui.this.ac, Gui.this);
         guard.start();
         // Create robot menu
         initRobotMenu();
         //Start passive automation
         robots.start();
       }
     };
     //notification(Notification.Def.TB_GAME_CAN_START);
     this.addWindowListener(new WindowAdapter()
     {
        @Override
        public void windowOpened(WindowEvent event)
        {
          System.out.println("Window opened.");
        }
        @Override
        public void windowClosing(WindowEvent event)
        {
          System.out.println("Window is closing!");
          //Terminate the program
          ac.TerminateAsync();
        }
        @Override
        public void windowClosed(WindowEvent event)
        {
          //System.out.println("This function is never called.");
        }
        @Override
        public void windowIconified(WindowEvent event) {
          if(Gui.this.canTray()) {
            if(settings.getBoolean(Setnames.TRAY_ICON_MINIMIZE.name, false)) {
              //Gui.this.setType(javax.swing.JFrame.Type.UTILITY);
              Gui.this.setState(Frame.ICONIFIED);
              Gui.this.setVisible(false);
            }            
          }
        }
     });
     finalize.start();
     setSize(500, 300);
   }
   // Called from Main.java to ensure threads and shit are
   // destroyed, killed, disintegrated etc...
   public void teardown() {
     //Stop updating GUI
     if(guard!=null)
       guard.interrupt();
     destroyTray();
     dispose();
     robots.interrupt();
   }
   //
   // AUTOMATION INDICATION HERE!
   public void displayToolAction(boolean state) {
     buttonStartStop.setText(state ? "Stop" : "Start");
     buttonStartStop.setSelected(state);
     buttonStartStop.setBackground(state? Color.RED:null);
     //Enable/disable thread control
     menu_threadcontrol_pretend_accepted.setEnabled(state);
     
     tray_icon.setImage((state?getIconRunning():getIcon()).getImage());
     
     if(!state) {
       setTitle("Stopped."); 
     }
   }
   public void displayToolAction() {
     displayToolAction(ac.ToolRunning());
   }
   
   private boolean ToolAction()
   {
     boolean toolState = ac.ToolRunning();
     displayToolAction(!toolState);
     
     if (!toolState) {
       ac.StartTool();
     } else {
       ac.StopTool();
     }
     return !toolState;
   }

   private boolean ToolAction(ActionEvent e) {
     return ToolAction();
   }
   
   public void displayClientAvailable(boolean available) {
     if(!SwingUtilities.isEventDispatchThread()) {
       new Exception("Error: GUI operation outside EDT!").printStackTrace();
       displayClientAvailableAsync(available);
       return;
     }
     buttonStartStop.setEnabled(available);
     pvp_net_window = available;
     if(available) {
       buttonStartStop.setToolTipText(Text.TOGGLE_BUTTON_TITLE_ENABLED.text);
       displayDllStatus(dll_loaded);
     }
     else {
       buttonStartStop.setToolTipText(Text.TOGGLE_BUTTON_TITLE_DISABLED.text);
       menu_dll_additions.setEnabled(false);
     } 
   }
   public void displayClientAvailableAsync(final boolean available) {
     SwingUtilities.invokeLater(() -> Gui.this.displayClientAvailable(available));
   }
   public void displayTrayEnabled(boolean enabled, boolean minimize, boolean do_not_change_state) {
     if(!SwingUtilities.isEventDispatchThread()) {
       new Exception("Error: GUI operation outside EDT!").printStackTrace();
       displayTrayEnabledAsync(enabled, minimize, do_not_change_state);
       return;
     }
     if(tray==null || !SystemTray.isSupported()) {
       enabled = minimize = false;
     }
     if(!do_not_change_state) {
       menu_tray_enabled.setState(enabled);         
       menu_tray_minimize.setState(enabled&&minimize); 
     }
     menu_tray_minimize.setEnabled(enabled);
     if(enabled) {
       showTrayIcon(true);
     }
     else {
       showTrayIcon(false);
     }
   }
   public void displayTrayEnabledAsync(final boolean enabled, final boolean minimize, final boolean do_not_change_state) {
     SwingUtilities.invokeLater(() -> displayTrayEnabled(enabled, minimize, do_not_change_state));
   }
   public void displayTrayEnabled(boolean enabled, boolean minimize) {
     displayTrayEnabled(enabled, minimize, false);
   }
   public void displayTrayEnabled(boolean enabled) {
     displayTrayEnabled(enabled, settings.getBoolean(Setnames.TRAY_ICON_MINIMIZE.name, (Boolean)Setnames.TRAY_ICON_MINIMIZE.default_val), false);
   }
   
   public void displayTrayEnabled() {
     displayTrayEnabled(
         settings.getBoolean(Setnames.TRAY_ICON_ENABLED.name, (Boolean)Setnames.TRAY_ICON_ENABLED.default_val),
         settings.getBoolean(Setnames.TRAY_ICON_MINIMIZE.name, (Boolean)Setnames.TRAY_ICON_MINIMIZE.default_val),
         false);
   }
   /**
    * Hides the tray icon.
    */
   public void destroyTray() {
     //Hide tray icon
     if(canTray()) {
       showTrayIcon(false);
     }
   }
   public void displayDllStatus(boolean loaded) {
     dll_loaded = loaded;
     if(loaded) {
       menu_dll_additions.setText(Text.MENU_DLL_LOADED.text);
       menu_dll_additions.setEnabled(false);
     }
     else {
       menu_dll_additions.setText(Text.MENU_DLL_LOAD.text);
       if(DLLInjector.available()) {
         menu_dll_additions.setEnabled(pvp_net_window);
         menu_dll_additions.setToolTipText(Text.MENU_DLL_TITLE.text);
       }
       else {
         menu_dll_additions.setToolTipText(Text.MENU_DLL_TITLE_UNAVAILABLE.text);
         menu_dll_additions.setEnabled(false);
       }
     }
   }
   
   public JToggleButton getToggleButton1()
   {
     return this.buttonStartStop;
   }
   
   public void notification(Notification.Def... names) {
     notifications.notification(names);
   }
   public ImageResources getIcon() {
     return ImageResources.ICON_NO_COPYRIGHT;
   }
   public ImageResources getIconRunning() {
     return ImageResources.ICON_RUNNING;
   }
   public ImageResources getTrayIcon() {
     return ac.ToolRunning()?getIconRunning():getIcon();
   }
   //Can't write [C:\MYSELF\programing\java\AutoCall\AutoClient\target\autoclient-2.0-shrunk.jar](Can't read [C:\MYSELF\programing\java\AutoCall\AutoClient\target\autoclient-2.0-jar-with-dependencies.jar](Duplicate zip entry [autoclient-2.0-jar-with-dependencies.jar:cz/autoclient/autoclick/ColorPixel.class]))
   private void initTrayIcon() {
     if(tray_icon!=null)
       return;
     if (SystemTray.isSupported() && settings.getBoolean(Setnames.TRAY_ICON_ENABLED.name, true)) {
       tray_icon = new TrayIcon(getTrayIcon().getImage());
       tray_icon.setImageAutoSize(true);
       tray_icon.addMouseListener(new MouseListener() {
         @Override
         public void mouseClicked( MouseEvent e ) {
           switch(e.getButton()) {
             case MouseEvent.BUTTON1 : 
                          Gui.this.setVisible(true);
                          Gui.this.setState (Frame.NORMAL);
             break;
             case MouseEvent.BUTTON2 : 
               if(Gui.this.pvp_net_window)
                 Gui.this.ToolAction();
               
             
           }
           //Gui.this.setType(java.awt.Window.Type.NORMAL);
         }
         @Override
         public void mousePressed( MouseEvent e ) {}
         @Override
         public void mouseExited(MouseEvent e) {}
         @Override
         public void mouseReleased(MouseEvent e) {}
         @Override
         public void mouseEntered(MouseEvent e) {}
       });
     }
   }
   private void showTrayIcon(boolean state) {
     //Just make sure tray icon was created
     initTrayIcon();
     //If tray is not supported, return
     if(!canTray())
       return;
     if(state && !tray_added) {
       try {
         tray.add(tray_icon);
       } catch (AWTException e) {
         //No tray icon
         tray_icon = null;
       }
       if(tray_icon!=null)
         tray_added = true;
     }
     else if(!state && tray_added) {
       tray.remove(tray_icon); 
       tray_added = false;
     }
   }
   private boolean canTray() {
     return tray!=null && tray_icon!=null; 
   }
   private void initMenu() {
     menuBar1 = new JMenuBar();
     //======== menuTools ========
     {
       menuTools = new JMenu();

       menuTools.setText("Tools");

       //---- menuItem1 ----
       /*menuItem1 = new JMenuItem();
       menuItem1.setText("Set Chat Delay");
       menuItem1.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               DelaySelected(e);
           }
       });
       menuTools.add(menuItem1);*/

       final JMenuItem menuItem2 = new JMenuItem();
       menuItem2.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               menuItem2.setEnabled(false);
               menuItem2.setText("Injecting...");

               DLLInjector.inject(new InjectionResult() {
                   @Override
                   public void run(boolean result, String fail_reason) {
                      if(result)
                        menuItem2.setText(Text.MENU_DLL_LOADED.text);
                      else {
                        menuItem2.setEnabled(true);
                        menuItem2.setText("Injection failed: "+fail_reason);
                      }
                   }
               });
           }
       });
       menuTools.add(menuItem2);
       menu_dll_additions = menuItem2;
       /** Run as administrator**/
       {
         final JMenuItem item = new JMenuItem();
         item.setText("Restart as administrator...");
         item.setToolTipText("If you run PVP.net Client under admin account, you must run this tool elevated too.");
         item.setEnabled(true);
         
         item.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
               Gui.this.tryRestartAsAdmin();
             }
         });
         menuTools.add(item);
         //Later, check if this menuAutomation item should be enabled
         SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
             if(Main.isAdmin()) {
               item.setEnabled(false);
               item.setToolTipText("Already running with adiministrator privilegies.");
             }
           }
         });
       }
       //Prevent accidental PVP.net minimization
       JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem("Prevent Client minimize");
       checkBox.setToolTipText("If you accidentally minimize PVP.net window when robot is running it will be restored on background.");
       settings.bindToInput(Setnames.PREVENT_CLIENT_MINIMIZE.name, checkBox, true);
       menuTools.add(checkBox); 

       //Update dll aditions status
       displayDllStatus(false);
       //======== Updates ========
       {
         JMenu menu = new JMenu();
         menu.setText("Updates");
         
         checkBox = new JCheckBoxMenuItem("Auto-check for updates");
         checkBox.setToolTipText("Automatically check for updates over the internet.");
         settings.bindToInput(Setnames.UPDATES_AUTOCHECK.name, checkBox, true);
         menu.add(checkBox);
         
         checkBox = new JCheckBoxMenuItem("Auto-download");
         checkBox.setToolTipText("Downloads update if it's found.");
         settings.bindToInput(Setnames.UPDATES_AUTODOWNLOAD.name, checkBox, true);
         menu.add(checkBox);
         
         updateMenuItem = new UpdateMenuItem();
         updateMenuItem.setUnknown(ac.getVersion());
 
         updateMenuItem.setText("Initializing.");
         
         updateMenuItem.setFocusable(false);
         updateMenuItem.setFocusPainted(false);
         updateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              System.out.println("Update clicked!");
              if(updater.getUpdates().installStepIs(Updater.InstallStep.CAN_COPY_FILES, Updater.InstallStep.CAN_UNPACK))
                ac.UpdateAndRestart();
              else
                updater.takeNextAction();
              return;
            }
         });
         
         menu.add(updateMenuItem);
         menuTools.add(menu);
       }
       //======== Debug ========
       {
         JMenu menu = new JMenu();
         menu.setText("Debug");
         menu_threadcontrol_pretend_accepted = new JMenuItem();
         menu_threadcontrol_pretend_accepted.setText("Detect game lobby");
         menu_threadcontrol_pretend_accepted.setToolTipText("If the lobby has been already invoked, skip accept phase.");
         menu_threadcontrol_pretend_accepted.setEnabled(false);
         menu_threadcontrol_pretend_accepted.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
               Gui.this.ac.ac.simulateAccepted();
             }
         });
         menu.add(menu_threadcontrol_pretend_accepted);
         JMenuItem item = new JMenuItem();
         item.setText("Show current screenshot.");
         item.setToolTipText("Serves as debug feature to check whether Winapi is working.");
         item.setEnabled(true);
         item.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
               Window window = CacheByTitle.initalInst.getWindow(ConstData.window_title_part);
               if(window!=null) {
                 try {
                   DebugDrawing.displayImage(window.screenshot(), "Screnshot", false);
                 }
                 catch(InterruptedException ex) {
                   
                 }
                 catch(APIException ex) {
                   dialogErrorAsync("Window can't return screenshot. This is the last error: \n"+ex);
                 }
               }
               else {
                 dialogErrorAsync("No window found that contains '"+ConstData.window_title_part+"' in title.");
               }
             }
         });
         menu.add(item);
         
         item = new JMenuItem();
         item.setText("Create remote window.");
         item.setToolTipText("Creates a window that mimics everything you see in PVP.net launcher and passes your clicks.");
         item.setEnabled(true);
         final ClickProxy proxy = new ClickProxy(null);
         item.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
               if(!proxy.isRunning()) {
                 proxy.setWindow(CacheByTitle.initalInst.getWindow(ConstData.window_title_part));
                 try {
                   proxy.start();
                 }
                 catch(IllegalStateException er) {
                   Gui.this.dialogErrorAsync(er.getMessage(), "Cannot create the click proxy.");
                 }
               }
             }
         });
         menu.add(item);
         
         menuTools.add(menu);
       }
       
     }
     menuBar1.add(menuTools);
     //======== Display========
     {
       menuDisplay = new JMenu();
       menuDisplay.setText("Display");

       menu_tray_enabled = new JCheckBoxMenuItem("Show in system tray");
       menu_tray_enabled.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
             boolean enabled = menu_tray_enabled.getState();
             boolean minimize = menu_tray_minimize.getState();
             if(!enabled)
               menu_tray_minimize.setState(false);
             displayTrayEnabled(enabled, minimize, true);
             if(!enabled && minimize) {
               //Send event to inform other checkbox that it's been disabled
               e.setSource(menu_tray_minimize);
               for(ActionListener a: menu_tray_minimize.getActionListeners()) {
                 a.actionPerformed(e);
               }
             }
           }
       });
       menuDisplay.add(menu_tray_enabled);
       settings.bindToInput(Setnames.TRAY_ICON_ENABLED.name,menu_tray_enabled, true);


       menu_tray_minimize = new JCheckBoxMenuItem("Minimize to tray");
       menu_tray_minimize.setToolTipText("When minimized, the application will disappear from task bar.");
       menu_tray_minimize.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
             displayTrayEnabled(menu_tray_enabled.getState(), menu_tray_minimize.getState(), true);
           }
       });
       menuDisplay.add(menu_tray_minimize);
       settings.bindToInput(Setnames.TRAY_ICON_MINIMIZE.name, menu_tray_minimize, true);
       //Add this menuAutomation to the bar
       menuBar1.add(menuDisplay);
     }
     //======== Notifications menuAutomation ========
     initTrayIcon();
     {
       menuNotifications = new JMenu();
       menuNotifications.setText("Notifications");
       Notification.Def.createAll(notifications, NotificationTrayBaloon.class, settings, tray_icon);
       //======== Tray Notifications menuAutomation ========
       {
         JMenu tray_notifs = new JMenu();
         tray_notifs.setText("Tray bubble");
         notifications.addToJMenu(tray_notifs, NotificationTrayBaloon.class);
         tray_notifs.setEnabled(canTray());
         menuNotifications.add(tray_notifs);
       }
       menuBar1.add(menuNotifications);
     }
     //======== Passive automation menuAutomation ========
     //if(false)
     {
        menuAutomation = new JMenu();
        menuAutomation.setText("Passive Automation");
        menuAutomation.setEnabled(false);
        menuAutomation.setToolTipText("Loading...");

        //A menuAutomation item with link to help
        {
          //System.out.println(ImageResources.PA_BOT_ENABLED.path);
          menuAutomation.add(new URLMenuItem(
                      "What's this?",
                      "Link to help page on github",
                      "https://github.com/Darker/auto-client/wiki/Passive-automation",
                      ImageResources.HELP)
          );          
        }
        menuBar1.add(menuAutomation);
      }
      //======== Help menu ========
      {
        JMenu help = new JMenu();
        help.setText("Help");
        help.add(new URLMenuItem(
                      "Report issue (GitHub)",
                      "Requires GitHub acount",
                      "https://github.com/Darker/auto-client/issues/new",
                      ImageResources.GITHUB)
        );
        
        help.add(new URLMenuItem(
                      "Facebook",
                      "You can contact me there",
                      "https://www.facebook.com/autoclient/",
                      ImageResources.FACEBOOK)
        );
        
        JMenuItem item = new JMenuItem();
        item.setText("About & Contact");
        item.addActionListener((ActionEvent e)->{
           Dialogs.dialogInfoAsync(
                 "Version: <tt>"+ac.getVersion()+"</tt><br />"
               + "e-mail: <a href=\"mailto: autoclient@hmamail.com\">"
               + "autoclient@hmamail.com</a><br />"
               + "<a href=\"https://www.facebook.com/autoclient/\">facebook/autoclient"
               + "</a><br />"
               ,
               
               "About", rootPane);
        });
        help.add(item);

        menuBar1.add(help);
      }
      setJMenuBar(menuBar1);

      //---- buttonStartStop ----
      buttonStartStop = new JToggleButton();
      buttonStartStop.setText("Start");
      buttonStartStop.setToolTipText(Text.TOGGLE_BUTTON_TITLE_DISABLED.text);
      buttonStartStop.setEnabled(false);
      buttonStartStop.setFocusable(true);
      buttonStartStop.setFocusPainted(false);
      buttonStartStop.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ToolAction();
          }
      });
      menuBar1.add(buttonStartStop);
      System.out.println("Menu created!");
    }

   public void setUpdateManager(final Updater updater) {
     updater.setUpdateListener(new UpdateVisual(this, updateMenuItem, updater));
     this.updater = updater;
    }
    protected class PasswordFieldVerifier extends SettingsInputVerifier implements SettingsValueChanger {
      private String password;
      @Override
      public Object value(JComponent comp) {
        System.out.println("Password: "+new String(((JPasswordField)comp).getPassword()));
        return new String(((JPasswordField)comp).getPassword());
      }

      @Override
      public void setValue(JComponent comp, Object value) {
        ((JPasswordField)comp).setText(value.toString());
      }

      @Override
      public boolean verify(JComponent input) {
        return true;
      }
      @Override
      public boolean verify(JComponent input, boolean silent) {
        if(!silent) {
          JPasswordField i = (JPasswordField)input;
          String PW = new String(i.getPassword());
          String check = (String)JOptionPane.showInputDialog(
                null,
                "Enter password again:\n",
                "Confirm password",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
          boolean state = PW.equals(check);
          if(!state) {
            i.setText("");
          }
          return state;
        }
        return verify(input);
      }

    }
    // This is expected to be called from another thread
    private void initRobotMenu() {
      GUIAppendList list = new GUIAppendList();
      //======== Auto Launch ========
      {
        try {
          PAMenu auto_launch = new PAMenu(ac.findRobot(LaunchBot.class), settings, "Auto launch");
          auto_launch.setRobots(robots);
          auto_launch.setAboutLink("https://github.com/Darker/auto-client/wiki/Passive-automation#auto-launch");
          auto_launch.root.setToolTipText("Automatically press launch in patcher.");
          list.addAt(menuAutomation, auto_launch.root, 0);
        }
        catch(NoSuchRobotException e) {}
      }

      //======== Auto queue ========
      {
        AutoQueueBot rur = new AutoQueueBot(settings);
        rur.requeued = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ToolAction();
          }
        };
        PAMenu auto_queue = new PAMenu(rur, settings, "Auto queue");

        //Set up setting window
        PAConfigWindow setwin = auto_queue.createConfigWindow(this);

        FieldDef field = new FieldDef(
            "Say after game:",
            "Optionally, bot can say something when the game is over.",
            "SAY_AFTER");
        field.addField(new JTextField());
        setwin.addLine(field);

        auto_queue.setRobots(robots);
        auto_queue.setAboutLink("https://github.com/Darker/auto-client/wiki/Passive-automation#auto-queue");
        auto_queue.root.setToolTipText("Requeue when the game is over.");

        list.addAt(menuAutomation, auto_queue.root, 0);
      }
      //======== Auto login ========
      {
        AutoLoginBot rur = new AutoLoginBot(settings);

        PAMenu auto_login = new PAMenu(rur, settings, "Auto login");

        //Set up setting window
        PAConfigWindow setwin = auto_login.createConfigWindow(this);

        FieldDef field = new FieldDef(
            "Password:",
            "Password to enter.",
            null);
        final JPasswordField pw = new JPasswordField();
        if(settings.exists(Setnames.REMEMBER_PASSWORD.name, EncryptedSetting.class)) {
          if(settings.getSetting(Setnames.REMEMBER_PASSWORD.name, EncryptedSetting.class).doesHaveValue())
            pw.setText("****");
          else
            pw.setText("");
        }

        field.addField(pw);
        setwin.addLine(field);
        field = new FieldDef(
            "Save password: ",
            "Click button to encrypt and save the password.",
            null);
        JButton button = new JButton("Save");
        button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String PW = new String(pw.getPassword());
            /*String check = (String)JOptionPane.showInputDialog(
                  null,
                  "Enter password again:\n",
                  "Confirm password",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  null,
                  "");*/
            boolean state = true; //PW.equals(check);
            if(!state) {
              JOptionPane.showMessageDialog(Gui.this, "Passwords didn't match.");
              pw.setText("");
            }
            else {
              pw.setText("****");
              settings.setEncrypted(Setnames.REMEMBER_PASSWORD.name, PW);
              //System.out.println("DECRYPTED: "+settings.getEncrypted(Setnames.REMEMBER_PASSWORD.name));
              new Thread() {
                @Override
                public void run() {
                  JOptionPane.showMessageDialog(Gui.this, "Password saved and encrypted.", "Password saved",JOptionPane.INFORMATION_MESSAGE);
                }
              }.start();
            }
          }
        });
        field.addField(button);
        setwin.addLine(field);
        /*field = new FieldDef(
            "Master password:",
            "Password to protect your game password.",
            null);
        final JPasswordField pw2 = new JPasswordField();*/
        /*settings.bindToInputSecure(Setnames.REMEMBER_PASSWORD.name, pw2, new PasswordFieldVerifier() {
          @Override
          public Object value(JComponent c) {
            String v = (String)super.value(c);
            settings.getEncryptor().setPassword(v);
            return v;
          }
        });*/

        /*field.addField(pw2);
        setwin.addLine(field);*/

        /*field = new FieldDef(
            "Use master password:",
            "Prompts for master password to protect your password.",
            Setnames.ENCRYPTION_USE_PW.name);
        final JCheckBox use_pw = new JCheckBox();
        ActionListener updateUsePW = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SecureSettings encr = settings.getEncryptor();
            boolean value = use_pw.isSelected();
            encr.setUse_password(value);
            pw2.setEnabled(value);
          }
        };
        use_pw.addActionListener(updateUsePW);
        //Set inital values
        boolean value = settings.getBoolean(setwin.settingName(Setnames.ENCRYPTION_USE_PW.name), false);
        settings.getEncryptor().setUse_password(value);
        pw2.setEnabled(value);*/

        //field.addField(use_pw);
        //setwin.addLine(field);


        /*field = new FieldDef(
            "Encrypt with hardware ID:",
            "Will use unique key of this computer to encrypt the password.",
            Setnames.ENCRYPTION_USE_HWID.name);
        final JCheckBox use_hwid = new JCheckBox();
        ActionListener updateUseHWID = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //settings.getEncryptor().setUse_hwid(use_hwid.isSelected());
          }
        };
        use_hwid.addActionListener(updateUseHWID);
        //settings.getEncryptor().setUse_hwid(settings.getBoolean(Setnames.ENCRYPTION_USE_HWID.name, true));
        field.addField(use_hwid);
        setwin.addLine(field);*/

        setwin.setSize(333, 130);

        auto_login.setRobots(robots);
        auto_login.setAboutLink("https://github.com/Darker/auto-client/wiki/Passive-automation#auto-login");
        auto_login.root.setToolTipText("Login automatically.");

        list.addAt(menuAutomation, auto_login.root, 0);
      }
      list.after(new Runnable() {
        @Override
        public void run() {
          Gui.this.menuAutomation.setEnabled(true);
          Gui.this.menuAutomation.setToolTipText("Tools running in background.");
        }
      });
      list.create();
    }
    private void initComponents() {
        //======== this ========
        setTitle("Application - stopped");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        createTabs(contentPane);

 
        //contentPane.add(buttonStartStop);
        //toggleButton1.setBounds(142, 151, 69, 19);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());



    }
    //http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
    public void createTabs(Container pane) {
        pane.setLayout(new GridLayout(1, 1));
        TabbedWindow win = new TabbedWindow();
        
        win.newTab("Blind pick lobby", "Lobby where lane is called and champion is picked");
        
        
        MultiFieldDef multifield = new MultiFieldDef("Champion:");
        JComboBox champion = new JComboBox();        
        champion.enableInputMethods(true);
        champion.setEditable(true);
        initChampField(champion);
        
        multifield.addField(/*new JTextField()*/champion, settings, Setnames.BLIND_CHAMP_NAME);
        
        champ_config = new ConfigurationManager(champion, settings);
        champ_config.setChampion(settings.getStringEquivalent(Setnames.BLIND_CHAMP_NAME.name));
        multifield.addField(champ_config.save, null, null);
        multifield.addField(champ_config.delete, null, null);
    
        
        multifield.packWeighted(0.5,0,0);
        win.addLine(multifield);
        
        FieldDef field = new FieldDef("Call text:", "Enter text to say after entering lobby.", Setnames.BLIND_CALL_TEXT.name);
        field.addField(new JTextField());
        field.attachToSettings(settings);
        win.addLine(field);
        
        System.out.println("Creating summoner spells.");
        multifield = new MultiFieldDef("Summoner spells:");
        ButtonSummonerSpellMaster spell1 = new ButtonSummonerSpellMaster(null, settings);
        ButtonSummonerSpellMaster spell2 = new ButtonSummonerSpellMaster(null, settings); 
        spell1.setTwin(spell2);
        multifield.addField(spell1, settings, Setnames.BLIND_SUMMONER1);
        multifield.addField(spell2, settings, Setnames.BLIND_SUMMONER2);
        win.addLine(multifield);
        System.out.println("Creating summoner Masteries.");
        
        field = new FieldDef("Mastery page (0=ignore):", "Index of mastery page to be selected:", Setnames.BLIND_MASTERY.name);
        JSpinner masteries = new JSpinner();
        masteries.setModel(new SpinnerNumberModel(0.0, 0.0, 20.0, 1.0));
        field.addField(masteries);
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Rune page (0=ignore):", "Index of rune page to be selected:", Setnames.BLIND_RUNE.name);
        JSpinner runes = new JSpinner();
        runes.setModel(new SpinnerNumberModel(0.0, 0.0, 20.0, 1.0));
        field.addField(runes);
        field.attachToSettings(settings);
        win.addLine(field);

        /*
        win.newTab("Team builder", "All teambuilder automation");
        
        field = new FieldDef("Enabled:", "Enable or disable this function.", Setnames.TEAMBUILDER_ENABLED.name);
        field.addField(new JCheckBox());
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Auto-Start:", "Start a game when everybody is ready.", Setnames.TEAMBUILDER_AUTOSTART_ENABLED.name);
        field.addField(new JCheckBox());
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Greet new player:", "As captain, you'll automatically call this to newcomers", "tb_cap_greet");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Lock call:", "Sentence you call when everybody should lock in.", "tb_cap_lock");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        win.addLine(field);
        */
        
        win.newTab("Invite friends", "Start automatically when everybody accepts.");
        
        field = new FieldDef("Auto start:", "Enable or disable this function.", Setnames.INVITE_ENABLED.name);
        field.addField(new JCheckBox());
        field.attachToSettings(settings);
        win.addLine(field);

        pane.add(win.container);
        win.close();   
        System.out.println("Inner window created.");
   }
   protected void initChampField(final JComboBox field) {
     SwingUtilities.invokeLater(new Runnable()
     {
       @Override
       public void run()
       {
         //Object[] elements = new Object[] {"Cat", "Dog", "Lion", "Mouse"};
         String[] elements = ConstData.lolData.getChampions().enumValues(Champions.getName, true).toArray(new String[0]);
         //System.out.println("Champions_old loaded: "+elements.length);
         //Safe to unload all JSON data now
         ConstData.lolData.getChampions().unloadData();
         
         AutoCompleteSupport.install(field, GlazedLists.eventListOf((Object[])elements));
       }
     });
   }
   protected static JPanel newLine() {
     JPanel line = new JPanel();
     line.setLayout(new GridLayout(1, 1));
     line.setBorder(BorderFactory.createLineBorder(Color.red));
     return line;
   }
   protected JPanel textLine(String label, String title, String setting) {
     JPanel line = newLine();
     line.add(makeTextPanel("Champion:"));
     JTextField field = new JTextField();
     field.setToolTipText("Enter champion name");
     if(setting!=null) {
       settings.bindToInput(setting, field);
     }
     line.add(field);
     return line;
   }
   protected static void endLine(ParallelGroup h, SequentialGroup v, JComponent line) {
     h.addComponent(line);
     v.addComponent(line, GroupLayout.PREFERRED_SIZE,
            GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
     v.addGap(10);
   }
    protected static JComponent makeTextPanel(String text) {
        //JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        return filler;
        //panel.setLayout(new GridLayout(1, 1));
        //panel.add(filler);
        //return panel;
    }
    private static void debugInspectElement(Container c, int recur) {
      Component[] comps = c.getComponents();
      if(recur==0)
        System.out.println("Inspecting "+c.getClass().getName()+": ");
      for(Component comp:comps) {
        System.out.print("  ");
        if(recur>0) {
          if(comp==comps[comps.length-1])
            System.out.print("╚");
          else
            System.out.print("╠");
          for(int i=0;i<recur; i++)
            System.out.print(" ");
        }
        System.out.println(comp.getClass().getName());
        if(comp instanceof Container) {
          //System.out.println(" );
          debugInspectElement((Container)comp, recur+2); 
        }
      }
    }
    public static void debugInspectElement(Container c) {
      debugInspectElement(c, 0);
    }
    
    
    
    // GLOBAL GUI ELEMENT VARIABLES
    private JMenuBar menuBar1;
    private JMenu menuTools;
    private JMenu menuDisplay;
    private JMenu menuAutomation;
    private JMenu menuNotifications;
    



    //private JTextField champField;
    //private JTextField textField2;
    private JToggleButton buttonStartStop;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    
    //Dialogs
    public void dialogErrorAsync(String message, String title) {
      Dialogs.dialogErrorAsync(message, title, this);
    }
    public void dialogErrorAsync(String message) {
      dialogErrorAsync(message, "Error");
    }
    
    public void tryRestartAsAdmin(final boolean force) {
      try {
        Gui.this.ac.RestartWithAdminRightsAsync(force);
      }
      catch (FileNotFoundException ex) {
        dialogErrorAsync(ex.getMessage());
      }
      catch (IOException ex) {
        dialogErrorAsync("Cannot read or use the helper VBS file.\n    "+ex);
      } 
    }
    public void tryRestartAsAdmin() {
      tryRestartAsAdmin(false);
    }
    

  
    private final Object dialogElevateMutex = new Object();
    private boolean elevateDialogIgnore = false;
    public void dialogElevateAsync() {
        new Thread("ElevationDialog") {
          @Override
          public void run() {
            //Custom button text
            Object[] options = {"Restart as administrator.",
                                "Exit program",
                                "Ignore this problem"};
            int n = JOptionPane.showOptionDialog(Gui.this,
                "It has been noticed that this program cannot "
                    + "send windows messages to the PVP.net Client. Most often "
                    + "this happens,\n because PVP.net Client is ran under administrator account.\n\n"
                    + "It is recommended that you run PVP.net Client under normal account. If "
                    + "you need to run it under administrator account,\n you will have "
                    + "to elevate this application. Read more in FAQ.",
                "Administrator access required.",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[2]);
            if(n==0) {
              tryRestartAsAdmin(true);
            }
            else if(n==1) {
              Gui.this.ac.TerminateAsync(true);
            }
          }
        }.start();
    }
    public void restart(boolean force) {
      ac.RestartAsync(force);
    }
 }

