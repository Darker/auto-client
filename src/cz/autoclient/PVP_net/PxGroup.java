/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

import cz.autoclient.autoclick.PixelGroup;
import cz.autoclient.autoclick.PixelGroupSimple;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jakub
 */
public enum PxGroup implements PixelGroup {
  DRAFT_ACCEPT(new PixelGroupSimple(
    PixelOffset.Draft_Accept_1,
    PixelOffset.Draft_Accept_2  
  )),
  NORMAL_LOBBY(new PixelGroupSimple(
    PixelOffset.LobbyChat,
    PixelOffset.LobbyChat2,
    PixelOffset.LobbyChatBlueTopFrame
  )),
  DRAFT_LOBBY(new PixelGroupSimple(
    PixelOffset.Draft_Lobby_Chat,
    PixelOffset.Draft_Lobby_MainBar,
    PixelOffset.Draft_Lobby_TopBar
  )),
  ;
  
  public final PixelGroup g;
  private PxGroup(PixelGroup g) {
    this.g = g;
  }

  @Override
  public boolean test(BufferedImage i) {
    return g.test(i);
  }
}
