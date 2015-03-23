package cz.autoclient;

import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.ImageResources;
import cz.autoclient.GUI.summoner_spells.ButtonSummonerSpellMaster;
import cz.autoclient.GUI.summoner_spells.InputSummonerSpell;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.robots.LaunchBot;
import cz.autoclient.robots.Robot;
import cz.autoclient.robots.RobotManager;
import cz.autoclient.settings.InputHandlers;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.input_handlers.*;
import java.awt.Image;

import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import sirius.constants.IMKConsts;
import sirius.constants.IWMConsts;
 
 
 public class Main
   implements IWMConsts, IMKConsts
 {
   //Some application constants
   public static final String SETTINGS_FILE = "data/settings.bin";
   
   public Automat ac;
   public Gui gui;
   
   private Settings settings;
   RobotManager robots;
 
   
   public Main()
   {
     //Normal program
     startGUI();
     //Start passive automation
     startRobots();
   }
   public void startRobots() {
     if(robots!=null)
       return;
     /*Robot rur = new LaunchBot();
     robots = new RobotManager(1000);
     robots.addRobot(rur);
     robots.start();*/
     //rurs.join();
   }
   
   public boolean ToolRunning() {
     return ac != null && ac.isAlive() && !ac.isInterrupted();
   }
   public void StopTool()
   {
    
     if ((ac != null) && (ac.isAlive()) && (!ac.isInterrupted()))
     {
       System.out.println("Stopping tool..");
       ac.interrupt();
     }
     else {
       System.out.println("Tried to stop tool while it wasn't running!");
     }
   }
   
   public void StartTool()
   {
     if ((ac == null) || (!ac.isAlive()))
     {
       System.out.println("Starting tool..");
       ac = new Automat(gui, settings);
       ac.start();
     }
   }
   
   private void startGUI()
   {
     settings = new Settings();
     try {
       settings.loadFromFile(SETTINGS_FILE);
     }
     catch(IOException e) {
       //Do nothing, this is expected for first run, before the settings file is created 
     }
     //Fill empty fields with default values
     Setnames.setDefaults(settings);
     gui = new Gui(this, settings);
 
     SwingUtilities.invokeLater(new Runnable()
     {
       @Override
       public void run()
       {
         /*AlloyLookAndFeel.setProperty("alloy.licenseCode", "a#Phyrum_Tea#1cl1e0j#9u3t2k");
         try
         {
           AlloyTheme al = new GlassTheme();
           new AlloyLookAndFeel().setTheme(al, true);
           LookAndFeel alloyLnF = new AlloyLookAndFeel(new GlassTheme());
           UIManager.setLookAndFeel(alloyLnF);
         }
         catch (UnsupportedLookAndFeelException ex) {}*/
         
         gui.setDefaultCloseOperation(3);
         gui.setVisible(true);
       }
     });
   }
   
   public static void main(String[] args)
   {
     //Register GUI settings handlers
     InputHandlers.register(InputJTextField.class,  JTextField.class);
     InputHandlers.register(InputJCheckBox.class,  JCheckBox.class);
     InputHandlers.register(InputJCheckBoxMenuItem.class,  JCheckBoxMenuItem.class);
     InputHandlers.register(InputJSpinner.class,  JSpinner.class);
     InputHandlers.register(InputJComboBox.class,  JComboBox.class);
     //My GUI handler for the Summoner Spell buttons
     InputHandlers.register(InputSummonerSpell.class,  ButtonSummonerSpellMaster.class);
     //Start program
     Main ac = new Main();
   }
 }


