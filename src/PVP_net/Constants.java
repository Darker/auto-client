/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PVP_net;

import autoclick.Rect;

/**
 *
 * @author Jakub
 */
public class Constants {
  public static Rect normalSize = new Rect(0, 1152, 720, 0);
  public static double sizeCoeficient(Rect size) {
    //With cold blood, I'll assume these idiots will never allow you to change Client aspect ratio
    return size.right/normalSize.right;
  }
}
