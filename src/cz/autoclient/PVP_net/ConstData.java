/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowValidator;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.league_of_legends.LoLVersion;
import java.io.File;

/**
 * Contains various constant data linked to PVP.net client and League of Legends
 * @author Jakub
 */
public class ConstData {
  public static final String window_title = "League of Legends";
  public static final String window_title_part = "League of Legends";
  public static final String process_name = "LeagueClientUx.exe";
  public static final String game_process_name = "";
  public static final String test_process_name = "Annoyance.exe";
 
  public static final String patcher_window_title = "LoL Patcher";
  
  public static final String game_window_title = "League of Legends";
  public static final Rect normalSize = new Rect(0, 1152, 720, 0);
  public static final Rect smallestSize = new Rect(0, 1024, 640, 0);
  public static final LoLVersion lolData = new LoLVersion(LoLVersion.Realm.NA, new File("LOLResources"), true);
  
  public static final Window getClientWindow() {
    return MSWindow.findWindow(new WindowValidator.CompositeValidatorAND(new WindowValidator[] {
      new WindowValidator.ExactTitleValidator("League of Legends"),
      new WindowValidator.ProcessNameValidator(ConstData.process_name)
    })
    );
  }
  public static double sizeCoeficient(Rect size) {
    //With cold blood, I'll assume these Riot idiots will never allow you to change Client aspect ratio
    return size.right/(double)smallestSize.right;
  }
  
  public static double sizeCoeficientInverted(Rect size) {
    //With cold blood, I'll assume these Riot idiots will never allow you to change Client aspect ratio
    return (double)smallestSize.right/size.right;
  }
   /** Denormalize rectangle. 
    * 
    * @param in Rectangle in normalized coordinates. This means Rect using the smallest window coordinates - ConstData.smallestSize
   * @param window Window actual dimensions
   * @return De-normalised rectangle - that is, rectangle valid on the window
   */
   public static Rect deNormalize(Rect in, Rect window) {
     return in.multiply(sizeCoeficient(window));
   }
   /** Normalize rectangle. 
    * 
    * @param in Rectangle in non-normalized coordinates. This means Rect 
    *           using current window coordinates.
   * @param window Window dimensions
   * @return Normalised rectangle - that is, rectangle valid relative to most saved images
   */
   public static Rect normalize(Rect in, Rect window) {
     return in.multiply(sizeCoeficientInverted(window));
   }
   
   public static Rect deNormalize(ImageFrame in, Rect window) {
     return in.multiplyBySize(window);
   }
}
