/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

/**
 *
 * @author Jakub
 */
public enum AcceptedGameType {
  NORMAL(15),
  DRAFT(21),
  UNKNOWN;
  
  public final int timeout;
  AcceptedGameType(int timeout) {
    this.timeout = timeout;
  }
  AcceptedGameType() {
    this(12);
  }
}
