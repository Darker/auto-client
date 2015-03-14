/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;
import cz.autoclient.Main;
import cz.autoclient.autoclick.MSWindow;
import cz.autoclient.autoclick.Window;
import cz.autoclient.PVP_net.Constants;
import cz.autoclient.threads.Pauseable;
/**
 * This class is designed to ensure GUI reflects current situation. It checks whether 
 * PVP.net is running at this moment.
 * @author Jakub
 */
public class StateGuard extends Pauseable {
  //Remember last state f PVP.net window
  private boolean pvp_net_running;
  //Remember the last state of the thread
  private boolean automation_thread_running;
  //Main thread refference
  private Main main;
  //GUI refference
  private Gui gui;
  
  public StateGuard(Main main_thread, Gui gui) {
    main = main_thread; 
    this.gui = gui;

  }
  @Override
  public void run() {
    //Pvp net state for the current itteration
    boolean pvp_net;
    //Start with negative state:
    pvp_net_changed(false);
    
    Window win = null;
    boolean thread_running;
    while(!isInterrupted()||true) {
      //Check whether window is running
      if(win==null) {
        win = MSWindow.windowFromName(Constants.window_title, false);
        //if(win==null)
        //  System.out.println("Window from name failed...");
      }
      else if(!win.isValid()) {
        win = null;
        //System.out.println("Window is invalid...");
      }
      pvp_net = win!=null;
      //On an change, update GUI
      if(pvp_net!=pvp_net_running) {
        pvp_net_changed(pvp_net);
      }
      
      /*thread_running = main.ToolRunning();
      if(thread_running!=automation_thread_running)
        thread_changed(thread_running);*/
      
      try {
        sleep(800L);
        //Wait some more if paused
        waitPause();
      }
      catch(InterruptedException e) {
        break; 
      }
    }    
  }

  private void pvp_net_changed(boolean newState) {
    pvp_net_running = newState;
    //System.out.println("New state: "+newState);
    gui.displayClientAvailable(newState);
    if(newState) {
      
    }
    else {
      if(main.ToolRunning())
        main.StopTool();
      gui.displayDllStatus(false);
    } 
  }


}
