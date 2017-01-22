/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.autoclick.windows.Window;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class MemoryLeakCheck {
  public static final Runtime runtime = Runtime.getRuntime();
  public static final double mb = 1024*1024;
  public static void main(String[] args) throws Exception {
    Window win;

    while((win=MSWindow.windowFromName("666BL", false))==null);
    String title = win.getTitle();
    //Remember the starting memory for the final comparison
    double usedMemoryStart = getMBUsed();
    System.out.println("Starting with "+String.format("%4.1f", usedMemoryStart));
    
    //This will be updated to keep track of changes during test
    double lastMemory = usedMemoryStart;
    
    int dummy = 0;
    //Memory change threshold - once the change is greater than this, info will appear in console
    final double threshold = 10;
    
    while(win.isValid() && win.getTitle().equals(title)) {
      win.getRect();
      //Run the tested operation
      BufferedImage scrnshot = win.screenshot();
      //Do not kill the CPU
      Thread.sleep(200);
      //Ensure the image is used
      if(scrnshot.getRGB(20, 30)==500) {
        dummy++;
      }
      //Calculate memory changes
      double mbnew = getMBUsed();
      double diff = mbnew-lastMemory;
      
      if(diff>=threshold || diff<=-threshold) {
        System.out.println((diff>0?"+":"-")+" "+String.format("%3.3f", diff*(diff>0?01.0D:-1.0D)));
        System.out.println("            = "+String.format("%4.1f", mbnew));
        //Update lastMemory to keep track of the next change
        lastMemory = mbnew;
      }
    }
    //Final change sum
    double mbnew = getMBUsed();
    double diff = mbnew-usedMemoryStart;
    System.out.println("Overall diff: "+String.format("%4.1f", diff));
  }
  /** Will return used memory in MBytes as double. Calculates from sum
   *  of total and free memory.
   * 
   * @return used memory in MBytes.
   */
  public static final double getMBUsed() {
    return (runtime.totalMemory())/mb; // + runtime.freeMemory()
  }
}
