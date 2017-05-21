package cz.autoclient;

 // Call templates
import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.summoner_spells.ButtonSummonerSpellMaster;
import cz.autoclient.GUI.summoner_spells.InputSummonerSpell;
import cz.autoclient.GUI.updates.UpdateProgressWindow;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.logging.ConsoleLoggerBase;
import cz.autoclient.main_automation.Automat;
import cz.autoclient.main_automation.AutomatV2;
import cz.autoclient.robots.Robot;
import cz.autoclient.robots.exceptions.NoSuchRobotException;
import cz.autoclient.settings.InputHandlers;
import cz.autoclient.settings.Settings;
import cz.autoclient.settings.input_handlers.InputJCheckBox;
import cz.autoclient.settings.input_handlers.InputJCheckBoxMenuItem;
import cz.autoclient.settings.input_handlers.InputJComboBox;
import cz.autoclient.settings.input_handlers.InputJSpinner;
import cz.autoclient.settings.input_handlers.InputJTextField;
import cz.autoclient.settings.secure.PasswordFailedException;
import cz.autoclient.settings.secure.SecureSettings;
import cz.autoclient.settings.secure.UniqueID;
import cz.autoclient.updates.Updater;
import cz.autoclient.updates.VersionId;
import java.awt.Frame;
import java.beans.Expression;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
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


// S>s,top,3,400;d,2000;s,Hello everyone\, I go top :)
// S>s,top,3,400;d,2000;s,Hello everyone\, I go top :)
// S>s,top,3,400;d,2000;s,Hello everyone\, I go top :)
 public class Main
   implements IWMConsts, IMKConsts
 {
   //Some application constants
   public static final String SETTINGS_FILE = "data/settings.bin";
   private VersionId VERSION = new VersionId("0.0-error");
   public static final File BACKUP_DIR = new File("data/backup/");
   public static boolean debug = true;
   
   public Automat ac;
   public Gui gui;
   
   public Settings settings;
   public Updater updater;
   
   public final String[] runAsAdminPaths = new String[]{"run-as-admin.vbs", "release_aditional_files/run-as-admin.vbs", "admin.vbs"};
   
   public final ArrayList<Robot> robots = new ArrayList<>();
   
 
   
   public Main()
   {
     updater = new Updater("Darker/auto-client", getVersion(), new File("./updates/"));
     // Init logger
     {
        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        // Remove all old/default loggers
        Logger rootLogger = Logger.getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for(java.util.logging.Handler h: handlers)
            rootLogger.removeHandler(h);
        
        rootLogger.addHandler(new ConsoleLoggerBase());
     }
     
     Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Running auto client "+getVersion());
     //Normal program
     startGUI();
   }
   
   public final VersionId getVersion() {
     if(VERSION.affix.equals("error")) {
       InputStream in = Main.class.getResourceAsStream("/version");
       Scanner sc = new Scanner(in, "UTF-8");
       VERSION = new VersionId(sc.useDelimiter("\\A").next());
     }
     return VERSION;
   }

   
   public boolean ToolRunning() {
     return ac != null && ac.isAlive() && !ac.isInterrupted();
   }
   public void StopTool()
   {
     if ((ac != null) && (ac.isAlive()) && (!ac.isInterrupted()))
     {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Stopping tool..");
       ac.interrupt();
     }
     else {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Tried to stop tool while it wasn't running!");
     }
   }
   
   public void StartTool()
   {
     if ((ac == null) || (!ac.isAlive()))
     {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting tool..");
       ac = new AutomatV2(gui, settings);
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
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "No settings loaded, they will be re-created. Error:"+e);
     }
     catch(IOException e) {
       //This means the settings probably exist but are corrupted
       System.err.println("Settings corrupted. Error:"+e);
       e.printStackTrace();
       
       File setFile = new File(SETTINGS_FILE);
       
       if(setFile.isFile()) {
         File newFile = null;
         boolean copySuccess = false;
         Exception copyError = null;
         try {
           BACKUP_DIR.mkdirs();
           String filename = BACKUP_DIR.getAbsolutePath()+"/backup_";
           int iterator = 1;
           while((newFile = new File(filename+(iterator++)+".bin")).exists()) {
             //Sometimes you're so pro you do everything in the while condition
           }

           Files.copy(setFile.toPath(), newFile.toPath());
           copySuccess = true;
         }
         catch(Exception ex) {
           copyError = ex;
           newFile = null;
         }
         
         final String path = copySuccess?newFile.getAbsolutePath():copyError.toString();
         final boolean success = copySuccess;
         new Thread() {
           @Override
           public void run() {
             StringBuilder message = new StringBuilder("Your settings file was corrupted.\n");
             if(success) {
               message.append("Corrupted file was moved into ");
               message.append(path);
               message.append(" for backup and default settings will now be used.");
             }
             else {
               message.append("Additionally, a following error has occured when attempting to backup corrupted file:\n    ");
               message.append(path);  
               message.append("\nWe're sorry, all your settings are lost :(");
             }
             JOptionPane.showMessageDialog(null, 
                   message.toString()
                   , "Settings corrupted",JOptionPane.ERROR_MESSAGE);
           }
         }.start();
         
       }
     }
     SwingUtilities.invokeLater(new Runnable()
     {
       @Override
       public void run()
       {
         gui = new Gui(Main.this, settings);
         gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         gui.setVisible(true);
         if(updater!=null) {
           gui.setUpdateManager(updater);
           updater.setSettings(settings);
           if(settings.getBoolean(Setnames.UPDATES_AUTOCHECK.name, (Boolean)Setnames.UPDATES_AUTOCHECK.default_val))
             updater.checkForUpdates();
         }
         Dialogs.dialogInfoOnceAsync("Welcome to Auto Client designed "
             + "for the new LoL Client. Please take notice, that this release is <b>very "
             + "experimental and unreliable</b>! Please report any issues to our github account of facebook.<br />"
             + "<br />"
             + "Notable changes:<ul>"
             + "<li>The program will now move your mouse cursor. This is sad but unavoidable.</li>"
             + "<li>Runes, ARAM and masteries were all removed and are not finished yet</li>"
             + "<li>We now use process name to detect client and game, so more languages will be supported.</li></ol>",
             "New client info", gui, "DIALOG_NEW_CLIENT_BETA", settings, true);
       }
     });
     // If the application is configured to allways start as admin it will restart now
     // It's slow but it works without creating some other kind of settings
     if(settings.getBoolean(Setnames.START_AS_ADMIN.name, (boolean)Setnames.START_AS_ADMIN.default_val)) {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting as admin because of settings.");
       tryRestartAsAdmin(false);
       return;
     }
     
     //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "PW: "+settings.getSetting(Setnames.REMEMBER_PASSWORD.name));
     //Fill empty fields with default values
     Setnames.setDefaults(settings);
     //Initialise encryption
     SecureSettings encryptor = settings.getEncryptor();
     //Constant password.S-1-5-21-3630785732-580163861-2202204989-1004
     //Constant password.S-1-5-21-3630785732-580163861-2202204989-1004
     /*EncryptedSetting test = new EncryptedSetting(encryptor);
     settings.setSetting(Setnames.REMEMBER_PASSWORD.name, test);
     test.setEncryptedValue(new byte[] {0,56,64,32,44,55,66,99,88,77,88,55,66,33});*/
     
     encryptor.addPassword("Constant password.");
     encryptor.addPassword(UniqueID.WINDOWS_USER_SID);
     try {
       encryptor.getMergedPassword();
       //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Password key: "+encryptor.getMergedPassword());
       //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Encryption password: "+encryptor.getMergedPassword());
     } catch (PasswordFailedException ex) {
       Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
     }
   }
   private <T extends Robot> T createRobot(Class<T> type) throws NoSuchRobotException {
     try {
       T robot = (T)new Expression(type, "new", new Object[]{}).getValue();
       robot.setLogger(LogManager.getLogger(type.getName()));
       return robot;
     }
     catch(Exception e) {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error when creating robot: "+e.getMessage());
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
   public void Terminate(final boolean force, final boolean saveSettings) {
     destroyGui();
     //Stop tool thread if running
     if(ToolRunning())
       StopTool();
     //Save settings
     if(saveSettings)
       saveSettings();
     //Here, no non-deamon threads should be running (daemon thread does not prolong the applicatione execution).
     if(force)
       System.exit(0);
   }
   public void saveSettings() {
     if(settings!=null) {
       try {
         settings.loadSettingsFromBoundFields();
         settings.saveToFile(SETTINGS_FILE, false);
       }
       catch(IOException e) {
         System.err.println("Problem saving settings:");
         e.printStackTrace(System.err);
       }
     }
     else {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Settings is null!");
     }
     if(updater!=null)
       updater.saveAll();
   }
   public void destroyGui() {
     //gui is JFrame representing the application window
     SwingUtilities.invokeLater(
       ()->{
         if(gui!=null) {
           gui.setVisible(false);
           gui.teardown();
           gui = null;
         }
         for(Frame frame : JFrame.getFrames()) {
           //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Frame " + frame.getTitle());
           if(frame.isDisplayable()) {
             //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "  Destroying frame " + frame.getTitle());
             frame.dispose();
           }
         }
       }
     );
   }
   public void InstallUpdate() {
     if(updater!=null) {
       if(updater.getUpdates().installStepIs(Updater.InstallStep.CAN_COPY_FILES, Updater.InstallStep.CAN_UNPACK)) {
         updater.installUpdate();
         updater.terminateAfterAction();
         Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Installing updates, will terminate after that.");
       }
       else
         throw new IllegalStateException("Tried to install update but this is no time to install.");
     }
     else 
       throw new IllegalStateException("Tried to install update but updates are disabled.");
   }
   public void Terminate() {
     Terminate(false, true); 
   }
    public void Terminate(boolean force) {
     Terminate(force, true); 
   }
   public void Restart(boolean force, boolean saveSettings) {
     if(saveSettings)
       saveSettings();
      //Stop tool thread if running
     if(ToolRunning())
       StopTool();
     try {
       Runtime.getRuntime().exec("javaw -jar \""+getSelfPath()+"\" >restart.log");
     } catch (FileNotFoundException ex) {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot find the jar file.");
     } catch (IOException ex) {
       Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot execute the command.");
     }
     Terminate(force);
   }
   public void Restart(boolean force) {
     Restart(force, true);
   }
   public void Restart() {
     Restart(false, true);
   }
   public Thread RestartAsync(final boolean force, final boolean saveSettings) {
     Thread t = new Thread("Restart AutoClient") {
       @Override
       public void run() {
         Main.this.Restart(force, saveSettings);
       }
     };
     t.start();
     return t;
   }
   public Thread RestartAsync(boolean force) {
     return RestartAsync(force, true);
   }
   public Thread RestartAsync() {
     return RestartAsync(false, true);
   }
   public void UpdateAndRestart() {
     Thread restartThread = new Thread("UpdateAndRestart") {
       @Override
       public void run() {
         destroyGui();
         saveSettings();
         UpdateProgressWindow wnd = new UpdateProgressWindow(updater, ()->{
           destroyGui();
           //Stop tool thread if running
           if(ToolRunning())
             StopTool();
           try {
             Runtime.getRuntime().exec("javaw -jar \""+getSelfPath()+"\" >restart.log");
           } catch (FileNotFoundException ex) {
             Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot find the jar file.");
           } catch (IOException ex) {
             Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot execute the command.");
           }
         });
         //updater.setUpdateListener(wnd);
         InstallUpdate();
       }
     };
     restartThread.start();
   }
   
   public void RestartWithAdminRightsAsync() throws FileNotFoundException, IOException {
     RestartWithAdminRightsAsync(false);
   }
   public void RestartWithAdminRightsAsync(final boolean force) throws FileNotFoundException, IOException {
     if(!force)
       Terminate(force);
     StartWithAdminRights();
     if(force)
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
   public void tryRestartAsAdmin(final boolean force) {
      try {
        RestartWithAdminRightsAsync(force);
      }
      catch (FileNotFoundException ex) {
        Dialogs.dialogErrorAsync(ex.getMessage());
      }
      catch (IOException ex) {
        Dialogs.dialogErrorAsync("Cannot read or use the helper VBS file.\n    "+ex);
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

     String jarPath = getSelfPath();
     //The path to the helper script. This scripts takes 1 argument which is a Jar file full path
     File runAsAdmin = null;
     // Temporary file to check
     File fileTmp = null;
     // All possible paths will be checked until existing vbs file is found
     for(int i=0; i<runAsAdminPaths.length && runAsAdmin==null; i++) {
         fileTmp = new File(runAsAdminPaths[i]);
         if(fileTmp.exists()) {
           runAsAdmin = fileTmp; 
         }
     }
     if(runAsAdmin==null) {
       throw new FileNotFoundException(
             "The VBSScript used for elevation not found at "
             + new File(runAsAdminPaths[0]).getAbsolutePath()
       );
     }
     // If exception was not thrown the program can be launched
     String command = "cscript \""+runAsAdmin.getAbsolutePath()+"\" "+jarPath;
     Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Executing '"+command+"'");
     //Note that .exec is asynchronous
     //After it starts, you must terminate your program ASAP, or you'll have 2 instances running
     Runtime.getRuntime().exec(command);
   }
   
   public static String getSelfPath() throws FileNotFoundException {
     //Our 
     String jarPath;

     //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Current relative path is: " + s);
     
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
     return jarPath;
   }
 }


