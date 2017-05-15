/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.GUI.LazyLoadedImage;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.ImageFrameV2;
import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.RelativeRectangle;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.comvis.RectMatch;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.league_of_legends.SummonerSpell;
import static cz.autoclient.main_automation.AutomatV2.cropToSummonerSpellDialog;
import static cz.autoclient.main_automation.AutomatV2.findSummonerSpell;
import static cz.autoclient.main_automation.AutomatV2.normalizeRect;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public class NewSpellDetection {

  /**
   * @param args the command line arguments
   * @throws java.lang.Throwable
   */
  public static void main(String[] args) throws Throwable /*motherfucker**/ {
    String[] spells = {
      (String) "SummonerTeleport",
      (String) "SummonerFlash"
    };
    
    //System.out.println(s.name);
    BufferedImage testScreen = DebugDrawing.loadFromPath("C:\\MYSELF\\programing\\java\\AutoCall\\newclient\\LOBBY_BLIND_SPELLS.png");
    Rect winRect = new Rect(0, testScreen.getWidth(), testScreen.getHeight(), 0);   //window.getRect();
    double winSizeCoef = ConstData.sizeCoeficientInverted(winRect);
    Rect cropRect = null;
    dbgmsg("Window: "+winRect);
    RelativeRectangle[] spellButtonRectangles = {
      ImageFrameV2.Lobby_Spell1Button,
      ImageFrameV2.Lobby_Spell2Button
    };

    {
      BufferedImage dialog = cropToSummonerSpellDialog(testScreen, winRect);
      BufferedImage results = DebugDrawing.cloneImage(testScreen);
      //BufferedImage resultsSmall = DebugDrawing.cloneImage(dialog);
      for(SummonerSpell s : ConstData.lolData.getSummonerSpells().values()) {
        Rect pos = findSummonerSpell(s, dialog, 3);
        if(pos!=null) {
          Rect realPosition = normalizeRect(pos, ImageFrameV2.Lobby_SpellDialog.rect(winRect), winSizeCoef);
          DebugDrawing.drawResult(results, realPosition, Color.GREEN);
          DebugDrawing.drawText(results, realPosition.right, realPosition.top, s.name, Color.GREEN);
          
          //DebugDrawing.drawResult(resultsSmall, pos, Color.GREEN);
          //DebugDrawing.drawText(resultsSmall, pos.right, pos.top, s.name, Color.GREEN);
        }
        else {
          dbgmsg("WARNING: Spell "+s.name+" NOT FOUND!"); 
        }
      }
      //DebugDrawing.displayImage(resultsSmall, "BLE");
      DebugDrawing.displayImage(results, "BLE");
    }
    for (int i = 0; i < 2; i++) {
      SummonerSpell s = ConstData.lolData.getSummonerSpells().get(spells[i]);
      if (s == null) {
        dbgmsg("  Spell #" + (i + 1) + " is null.");
        continue;
      }
       // First check if the same spell is already selected
      // Get the screenshot of selected spells first
      //BufferedImage selected_spell = ScreenWatcher.resampleImage(
      //    window.screenshotCrop(spellButtonRectangles[i].rect(window)),
      //    winSizeCoef, winSizeCoef);
      
      Rect buttonCropRect = spellButtonRectangles[i].rect(winRect);
      BufferedImage selected_spell = ScreenWatcher.resampleImage(
          LazyLoadedImage.crop(testScreen, buttonCropRect),
          winSizeCoef, winSizeCoef
      );

      BufferedImage small_icon_no_crop = s.img.getScaledDiscardOriginal(ConstData.summonerSpellButtonSize, ConstData.summonerSpellButtonSize);
      BufferedImage small_icon = LazyLoadedImage.crop(small_icon_no_crop, 1);
      ScreenWatcher.changeHSB(small_icon, -0.0F, -0.114F);
      
      //DebugDrawing.displayImage(selected_spell, "Spell button #"+(i+1));
      //DebugDrawing.displayImage(small_icon_no_crop, "Required spell #"+(i+1));
      //DebugDrawing.displayImage(small_icon, "Required spell #"+(i+1));  

      RectMatch selected_spell_rect = ScreenWatcher.findByAvgColor(small_icon, selected_spell, 0.003f, false, null);
      // Spell found selected
      if (selected_spell_rect != null) {
        dbgmsg("  Spell #" + (i + 1) + " already selected, difference: "+selected_spell_rect.diff);
        continue;
      }
      dbgmsg("must select spell!");
      //Crop the icon - the GUI disorts the icon borders so I ignore them
      BufferedImage icon = s.img.getScaledDiscardOriginal(ConstData.summonerSpellChoseButtonSize, ConstData.summonerSpellChoseButtonSize);
      ScreenWatcher.changeHSB(icon, -0.01F, -0.25F);
      if (icon != null) {
        
//        //click(i == 0 ? PixelOffset.Blind_SumSpell1 : PixelOffset.Blind_SumSpell2);
//        //Wait till the launcher screen redraws
//        sleep(500L);
//        //Calculate crop rectangle 
        BufferedImage dialog = cropToSummonerSpellDialog(testScreen, winRect);
        Rect pos = findSummonerSpell(s, dialog, 3);
//
        if (pos != null) {
           dbgmsg("Original result: "+pos);
           BufferedImage clone = DebugDrawing.cloneImage(dialog);
           DebugDrawing.drawResult(clone, pos, Color.RED);
           DebugDrawing.displayImage(clone, "Non-normalized");
           // recalculate the relative position to the screen position
           Rect realPosition = normalizeRect(pos,  ImageFrameV2.Lobby_SpellDialog.rect(winRect), winSizeCoef);
           clone = DebugDrawing.cloneImage(testScreen);
           DebugDrawing.drawResult(clone, realPosition, Color.RED);
           DebugDrawing.displayImage(clone, "Non-normalized");
        } else {
          dbgmsg("  Spell #" + (i + 1) + " image corrupted.");
          BufferedImage clone = DebugDrawing.cloneImage(dialog);
          //DebugDrawing.drawResult(clone, pos, Color.RED);
          DebugDrawing.displayImage(clone, "Non-normalized");
        }
      }
    }
    DebugDrawing.displayImage(testScreen, "Original screenshot");
  }
  private static void dbgmsg(String string) {
   System.out.println(string);
  }
  
}
