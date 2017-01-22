/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.ComparablePixel;
import cz.autoclient.autoclick.PixelGroup;
import cz.autoclient.autoclick.PixelGroupSimple;
import cz.autoclient.autoclick.PixelGroupThreshold;
import cz.autoclient.autoclick.windows.Window;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class SituationDetector {
  public static enum LobbyType implements PixelGroup {
    ARAM(2, new PixelOffset[] {
       PixelOffset.ARAM_REROLL_BLUE,
       PixelOffset.ARAM_REROLL_WHITE,
       PixelOffset.ARAM_REROLL_GRAY
    }),
    NORMAL_BAN(new PixelOffset[] {
      PixelOffset.BAN_BANNING,
      PixelOffset.LobbyLockGray
    }),
    NORMAL_BLIND(new PixelGroup.AlwaysFalse());
    
    public final PixelGroup pixels;
    LobbyType() {
       pixels = null;
    }
    LobbyType(PixelGroup pixels) {
       this.pixels = pixels;
    }
    LobbyType(ComparablePixel[] pixels) {
       this.pixels = new PixelGroupSimple(
         pixels 
       );
    }
    LobbyType(int threshold, ComparablePixel[] pixels) {
       this.pixels = new PixelGroupThreshold(threshold, pixels);
    }
    @Override
    public boolean test(BufferedImage i) {
      if(pixels==null)
        return false;
      else
        return pixels.test(i);
    }

  }
  
  public static boolean IsAram(Window window) {
     return LobbyType.ARAM.test(window);
  }
  public static LobbyType loggyType(Window window) {
    BufferedImage screenshot = window.screenshot();
    for(LobbyType t:LobbyType.values()) {
      if(t.test(screenshot)) {
        return t; 
      }
    }
    return LobbyType.NORMAL_BLIND;
  }
  
  public static boolean waitForPoints(Window window, final int timeout, final int pollInterval) {
    return false;
  }
}
