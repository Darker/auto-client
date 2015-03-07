package cz.autoclient;

import cz.autoclient.GUI.StateGuard;
import cz.autoclient.automat_settings.InputHandlers;
import cz.autoclient.automat_settings.Settings;
import cz.autoclient.automat_settings.input_handlers.InputJCheckBox;
import cz.autoclient.automat_settings.input_handlers.InputJTextField;

import java.io.IOException;
import javax.swing.JCheckBox;
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
     catch(IOException e) {
       //Do nothing, this is expected for first run, before the settings file is created 
     }
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
     //Start program
     Main ac = new Main();
   }
 }


