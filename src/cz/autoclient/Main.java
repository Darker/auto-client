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
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
   
   public Settings settings;
   
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
   public Thread TerminateAsync(final boolean force) {
     Thread t = new Thread("Terminate AutoClient") {
       @Override
       public void run() {
         Main.this.Terminate(force);
       }
     };
     t.start();
     return t;
   }
   public Thread TerminateAsync() {
     return TerminateAsync(false);
   }
   /**
    * Terminates the whole program while saving settings.
   * @param force if true, terminates the program using System.exit(0)
    */
   public void Terminate(final boolean force) {
     //gui is JFrame representing the application window
     gui.setVisible(false);
     gui.destroyTray();
     gui.dispose();
     gui.robots.interrupt();
     
     for(Frame frame : JFrame.getFrames()) {
       //System.out.println("Frame " + frame.getTitle());
       if(frame.isDisplayable()) {
         //System.out.println("  Destroying frame " + frame.getTitle());
         frame.dispose();
       }
     }
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

     //Here, no non-deamon threads should be running (daemon thread does not prolong the applicatione execution).
     if(force)
       System.exit(0);
   }
   public void Terminate() {
     Terminate(false); 
   }
   
   public void RestartWithAdminRightsAsync() throws FileNotFoundException, IOException {
     RestartWithAdminRightsAsync(false);
   }
   public void RestartWithAdminRightsAsync(final boolean force) throws FileNotFoundException, IOException {
     StartWithAdminRights();
     Terminate(force);
   }
   private static volatile Boolean isAdmin = null;
   private static final Object isAdmin_mutex = new Object();
   public static boolean isAdmin(){
     Boolean is = isAdmin;
     if(is!=null)
       return is;
     synchronized(isAdmin_mutex) {
       if((is = isAdmin)!=null)
         return is;
       PrintStream systemErr = System.err;
       Preferences prefs = Preferences.systemRoot();
       synchronized(systemErr) {    // better synchroize to avoid problems with other threads that access System.err
         System.setErr(null);
         try{
           prefs.put("foo", "bar"); // SecurityException on Windows
           prefs.remove("foo");
           prefs.flush(); // BackingStoreException on Linux
           return isAdmin = true;
         }
         catch(Exception e) {
           return isAdmin = false;
         }
         finally {
           System.setErr(systemErr);
         }
       }
     }
   }
   /**
    * Start this very jar file elevated on Windows. It is strongly recommended to close any existing IO
    * before calling this method and avoid writing anything more to files. The new instance of this same
    * program will be started and simultaneous write/write or read/write would cause errors.
    * @throws FileNotFoundException if the helper vbs script was not found
    * @throws IOException if there was another failure inboking VBS script
    */
   public void StartWithAdminRights() throws FileNotFoundException, IOException {
     //The path to the helper script. This scripts takes 1 argument which is a Jar file full path
     File runAsAdmin = new File("run-as-admin.vbs");;
     //Our 
     String jarPath;

     //System.out.println("Current relative path is: " + s);
     
     try {
       jarPath = "\""+new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath()+"\"";
     } catch (URISyntaxException ex) {
       throw new FileNotFoundException("Could not fetch the path to the current jar file. Got this URISyntax exception:"+ex);
     }
     //If the jar path was created but doesn't contain .jar, we're (most likely) not running from jar
     //typically this happens when running the program from IDE
     //These 4 lines just serve as a fallback in testing, should be deleted in production
     //code and replaced with another FileNotFoundException
     if(!jarPath.contains(".jar")) {
       Path currentRelativePath = Paths.get("");
       jarPath = "\""+currentRelativePath.toAbsolutePath().toString()+"\\AutoClient.jar\"";
     }
     //Now we check if the path to vbs script exists, if it does we execute it
     if(runAsAdmin.exists()) {
       String command = "cscript \""+runAsAdmin.getAbsolutePath()+"\" "+jarPath;
       System.out.println("Executing '"+command+"'");
       //Note that .exec is asynchronous
       //After it starts, you must terminate your program ASAP, or you'll have 2 instances running
       Runtime.getRuntime().exec(command);
       
     }
     else
       throw new FileNotFoundException("The VBSScript used for elevation not found at "+runAsAdmin.getAbsolutePath());
   }
 }


