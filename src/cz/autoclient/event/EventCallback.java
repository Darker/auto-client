/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

/**
 * Simple event callback that can be passed to event dispatched (my Eventable interface)
 * @author Jakub
 */
public abstract class EventCallback {
  /**
   * Object to which is this listener assigned. But as we have public setSource, it can be anything.
   */
  private Eventable source;
  public void setSource(Eventable src) {
    source = src; 
  }
  /** Retrieve object to which this event listener is assigned.
   * @return can be any object that implements Eventable
   */
  public Eventable getSource() {
    return source; 
  }
  /** This function will be called when the event is dispatched. Any parameters can be passed :(
   * 
   * @param parameters anything, even nothing. This is really dangerous and unreliable!
   */
  public abstract void event(Object... parameters);
}
