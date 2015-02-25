package PVP_net;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub
 */
public enum TeamBuilderPlayerSlot {
  //Nothing in this slot
  Empty,
  //A player is trying to join, please accept or reject
  Joining,
  //Player was accepted but isn't in lobby yet
  Accepted,
  //Player is in the lobby now
  Occupied(false, true),
  //Player is ready
  Ready(false, true),
  //Slot didn't match anything sensible
  Error(false, false),  
  //Errorneous player entry - exists to allow distinguishing between errors
  ErrorPlayer(false, false)  
  ;
  
  public final boolean isEmpty;
  public final boolean isJoined;
  TeamBuilderPlayerSlot() {
    isEmpty = true;
    isJoined = false;
  }
  TeamBuilderPlayerSlot(boolean empty, boolean joined) {
    isEmpty = empty;
    isJoined = joined;
  }
  
  public double timeAccepted = -1;
  
  @Override
  public String toString() {
    return this.name();
  }
}
