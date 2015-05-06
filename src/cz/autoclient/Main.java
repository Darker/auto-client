package cz.autoclient;

import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.summoner_spells.ButtonSummonerSpellMaster;
import cz.autoclient.GUI.summoner_spells.InputSummonerSpell;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.robots.Robot;
import cz.autoclient.robots.exceptions.NoSuchRobotException;
import cz.autoclient.settings.InputHandlers;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.input_handlers.*;
import cz.autoclient.settings.secure.SecureSettings;
import cz.autoclient.settings.secure.UniqueID;
import java.beans.Expression;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import sirius.constants.IMKConsts;
import sirius.constants.IWMConsts;
 
 
 public class Main
   implements IWMConsts, IMKConsts
 {
   //Some application constants
   public static final String SETTINGS_FILE = "data/settings.bin";
   public static boolean debug = true;
   
   public Automat ac;
   public Gui gui;
   
   private Settings settings;
   
   public final ArrayList<Robot> robots = new ArrayList<>();
   
 
   
   public Main()
   {
     //Normal program
     startGUI();
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
     catch(FileNotFoundException e) {
       //Do nothing, this is expected for first run, before the settings file is created 
       System.out.println("No settings loaded, they will be re-created. Error:"+e);
     }
     catch(IOException e) {
       //This means the settings probably exist but are corrupted
       System.err.println("Settings corrupted. Error:"+e);
       if((new File(SETTINGS_FILE)).exists()) {
         new Thread() {
           @Override
           public void run() {
             JOptionPane.showMessageDialog(null, "Your settings file was corrupted. Defaults will be loaded."
                 //+ "It will be moved into data/backup/ directory and default settings will be used."
                 , "Error",JOptionPane.ERROR_MESSAGE);
           }
         }.start();
         e.printStackTrace();
         
       }
     }
     //System.out.println("PW: "+settings.getSetting(Setnames.REMEMBER_PASSWORD.name));
     //Fill empty fields with default values
     Setnames.setDefaults(settings);
     //Initialise encryption
     SecureSettings encryptor = settings.getEncryptor();
     
     /*EncryptedSetting test = new EncryptedSetting(encryptor);
     settings.setSetting(Setnames.REMEMBER_PASSWORD.name, test);
     test.setEncryptedValue(new byte[] {0,56,64,32,44,55,66,99,88,77,88,55,66,33});*/
     
     encryptor.addPassword("Constant password.");
     encryptor.addPassword(UniqueID.WINDOWS_USER_SID);
     
     //System.out.println("Encryption password: "+encryptor.getMergedPassword());
     
     gui = new Gui(this, settings);
 
     SwingUtilities.invokeLater(new Runnable()
     {
       @Override
       public void run()
       {
         gui.setDefaultCloseOperation(3);
         gui.setVisible(true);
       }
     });
   }
   private <T extends Robot> T createRobot(Class<T> type) throws NoSuchRobotException {
     try {
       T robot = (T)new Expression(type, "new", new Object[]{}).getValue();
       robot.setLogger(LogManager.getLogger(type.getName()));
       return robot;
     }
     catch(Exception e) {
       throw new NoSuchRobotException("Constructor has failed.");
     }
   }
   public <T extends Robot> T findRobot(Class<T> type) throws NoSuchRobotException {
     for(Robot r : robots) {
       if(type.isInstance(r))
         return (T)r;
     }
     return createRobot(type);
   }
   
   public static void main(String[] args)
   {
     //Register GUI settings handlers
     InputHandlers.register(InputJTextField.class,  javax.swing.JPasswordField.class);
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


