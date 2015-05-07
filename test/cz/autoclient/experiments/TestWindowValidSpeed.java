/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.autoclick.windows.Window;

/**
 *
 * @author Jakub
 */
public class TestWindowValidSpeed  {
   private Window window;
   private boolean valid = false;
   //Prepare properties and do some warmupt to force JVM compile the bytecode
   //@Override
   protected void setUp() throws Exception {
     window = MSWindow.windowFromName("NetBeans", false);
     if(window.isValid()) {
       valid = true;       
     }
     
     
     if(!window.isValid() || MSWindow.windowFromName("NetBeans", false)==null)
       valid = false;
     if(!valid)
       throw new Exception("WINDOW \"NetBeans\" DOESN'T EXIST, can't benchmark.");
   }
   //Native way
   public void timeIS_VALID(final int reps) {
     for (int i = 0; i < reps; i++) {
       if(!window.isValid())
         valid = false;
     }
   }
   //Java way
   public void timeGET_BY_NAME(final int reps) {
     for (int i = 0; i < reps; i++) {
       if(MSWindow.windowFromName("NetBeans", false)==null)
         valid = false;
     }
   }
   //The main function that starts Caliper and does the tests
   /*public static void main(String[] args) {
     CaliperMain.main(TestWindowValidSpeed.class, args);
   }*/
}
