/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick;

/**
 *
 * @author Jakub
 */
public abstract class RecursiveGroup implements PixelGroup {
  protected final PixelGroup[] groups;

  public RecursiveGroup(PixelGroup... groups) {
    this.groups = groups;
  }
}
