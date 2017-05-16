/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation.macros;

import cz.autoclient.PVP_net.PixelOffsetV2;
import cz.autoclient.autoclick.PixelGroup;
import cz.autoclient.autoclick.PixelGroupSimple;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.main_automation.Automat;
import cz.autoclient.main_automation.AutomatCallable;
import cz.autoclient.main_automation.WindowTools;

/**
 *
 * @author Jakub
 */
public class MacroMakeCustomGame extends AutomatCallable {

  public MacroMakeCustomGame(Automat myAutomat) {
    super(myAutomat);
  }
  public MacroMakeCustomGame() {}
  @Override
  public Boolean call() throws Exception {
    Window w = myAutomat.getWindow();
    click(PixelOffsetV2.Main_HomeButton);
    Thread.sleep(1000);
    click(PixelOffsetV2.Main_PlayButton);
    PixelGroupSimple playPixels = new PixelGroupSimple(
        PixelOffsetV2.Play_CancelTop,
        PixelOffsetV2.Play_ConfirmTop
    );
    if(!WindowTools.waitForPredicate(w, playPixels, 5000))
      throw new IllegalStateException("Cannot get to the play menu!");
    click(PixelOffsetV2.Play_CreateCustom);
    if(!WindowTools.waitForPixel(w, PixelOffsetV2.Play_Custom_GameName, 5000))
      throw new IllegalStateException("Cannot get to the custom game menu!");
    click(PixelOffsetV2.Play_Confirm);
    PixelGroupSimple customLobbyPixels = new PixelGroupSimple(
        PixelOffsetV2.Play_Custom_Lobby_Chat,
        PixelOffsetV2.Play_Custom_Lobby_Invite
    );
    if(!WindowTools.waitForPredicate(w, customLobbyPixels, 5000))
      throw new IllegalStateException("Cannot get to the custom game lobby!");
    click(PixelOffsetV2.Play_Custom_Lobby_Start);
    PixelGroupSimple dialogPixels = new PixelGroupSimple(
        PixelOffsetV2.Play_Custom_Lobby_NoXPDialog_Background,
        PixelOffsetV2.Play_Custom_Lobby_NoXPDialog_Yes
    );
    if(!WindowTools.waitForPredicate(w, dialogPixels, 5000))
      throw new IllegalStateException("Cannot start the custom game!");
    click(PixelOffsetV2.Play_Custom_Lobby_NoXPDialog_Yes);
    
    this.myAutomat.simulateAccepted();
    return null;
  }
}
