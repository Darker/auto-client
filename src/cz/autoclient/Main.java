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
import java.awt.Frame;
import java.beans.Expression;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
         gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
     T result = null;
     for(Robot r : robots) {
       if(type.isInstance(r)) {
         result = (T)r;
         break;
       }
     }
     if(result==null) {
       result = createRobot(type);
       robots.add(result);
     }
     return result;
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
   public Thread TerminateAsync() {
     Thread t = new Thread() {
       @Override
       public void run() {
         Main.this.Terminate();
       }
     };
     t.start();
     return t;
   }
   /**
    * Terminates the whole program while saving settings.
    */
   public void Terminate() {
     //gui is JFrame representing the application window
     gui.setVisible(false);
     gui.destroyTray();
     gui.dispose();
     gui.robots.interrupt();
     //Stop tool thread if running
     if(ToolRunning())
       StopTool();
     //Save settings
     if(settings==null) {
       System.out.println("Settings is null!");
       return;
     }
     try {
       settings.loadSettingsFromBoundFields();
       settings.saveToFile(SETTINGS_FILE, false);
     }
     catch(IOException e) {
       System.err.println("Problem saving settings:");
       e.printStackTrace(System.err);
     }
     for(Frame frame : JFrame.getFrames()) {
       //System.out.println("Frame " + frame.getTitle());
       if(frame.isDisplayable()) {
         //System.out.println("  Destroying frame " + frame.getTitle());
         frame.dispose();
       }
     }
     //Here, no non-deamon threads should be running (daemon thread does not prolong the applicatione execution).
     //System.exit(0);
   }
   public void RestartWithAdminRightsAsync() throws FileNotFoundException, IOException {
     StartWithAdminRights();
     Terminate();
     System.exit(0);
   }
   private static Boolean isAdmin = null;
   public static boolean isAdmin(){
     if(isAdmin!=null)
       return isAdmin;
     Preferences prefs = Preferences.systemRoot();
     try {
       prefs.put("foo", "bar"); // SecurityException on Windows
       prefs.remove("foo");
       prefs.flush(); // BackingStoreException on Linux
       return isAdmin = true;
     }
     catch(Exception e){
       return isAdmin = false;
     }
  }
   public void StartWithAdminRights() throws FileNotFoundException, IOException {
     
     File runAsAdmin = new File("run-as-admin.vbs");;
     String param1;

     //System.out.println("Current relative path is: " + s);
     try {
       param1 = "\""+new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath()+"\"";
     } catch (URISyntaxException ex) {
       throw new FileNotFoundException("Could not fetch the path to the current jar file.");
     }
     if(!param1.contains(".jar")) {
       Path currentRelativePath = Paths.get("");
       param1 = "\""+currentRelativePath.toAbsolutePath().toString()+"\\AutoClient.jar\"";
     }
     
     if(runAsAdmin.exists()) {
       String command = "cscript \""+runAsAdmin.getAbsolutePath()+"\" "+param1;
       System.out.println("Executing '"+command+"'");
       Runtime.getRuntime().exec(command);
       
     }
     else
       throw new FileNotFoundException("The VBSScript used for elevation not found at "+runAsAdmin.getAbsolutePath());
     //Terminate();
     //System.exit(0);
   }
 }


