import automat_settings.Settings;
import com.incors.plaf.alloy.AlloyLookAndFeel;
import com.incors.plaf.alloy.AlloyTheme;
import com.incors.plaf.alloy.themes.glass.GlassTheme;
import java.io.IOException;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import sirius.constants.IMKConsts;
import sirius.constants.IWMConsts;
 
 
 public class Main
   implements IWMConsts, IMKConsts
 {
   //Some application constants
   public static final String SETTINGS_FILE = "data/settings.bin";
   
   public Thread ac;
   public Gui gui;
   private Settings settings;
   
 
   
   public Main()
     throws InterruptedException
   {
   
     //Normal program
     startGUI();
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
       //Do nothing, this is expected for first run 
     }
     
     gui = new Gui(this, settings);
     
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run()
       {
         AlloyLookAndFeel.setProperty("alloy.licenseCode", "a#Phyrum_Tea#1cl1e0j#9u3t2k");
         try
         {
           AlloyTheme al = new GlassTheme();
           new AlloyLookAndFeel().setTheme(al, true);
           LookAndFeel alloyLnF = new AlloyLookAndFeel(new GlassTheme());
           UIManager.setLookAndFeel(alloyLnF);
         }
         catch (UnsupportedLookAndFeelException ex) {}
         Main.this.gui.setDefaultCloseOperation(3);
         Main.this.gui.setVisible(true);
       }
     });
   }
   
   public static void main(String[] args)
     throws InterruptedException
   {
     Main ac = new Main();
   }
 }


