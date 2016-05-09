/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.windows.Window;

/**
 *
 * @author Jakub
 */
public class SituationDetector {
  public static boolean IsAram(Window window) {
     PixelOffset[] aram_points = new PixelOffset[] {
       PixelOffset.ARAM_REROLL_BLUE,
       PixelOffset.ARAM_REROLL_WHITE,
       PixelOffset.ARAM_REROLL_GRAY
     };
     return WindowTools.checkPoint(window, aram_points)>=2;
  }
  
  public static boolean waitForPoints(Window window, final int timeout, final int pollInterval) {
    return false;
  }
}
