/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.helpers;

/**
 *
 * @author Jakub
 */
public class IterationLimiter {
  private int iteration = 0;
  public boolean limitIteration(int limit) {
    return limit<=iteration++; 
  }
}
