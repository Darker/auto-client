/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging;

/**
 *
 * @author Jakub
 */
public enum Level {
  //Most of the garbage debug data
  DEBUG,
  //Things that are strange but likelly to happen
  NOTICE,
  //Some step of the program had to be ommited due to a problem
  WARNING,
  //Vital function will not happen at all
  ERROR,
  //Program crashed
  FATAL_ERROR;
  
  
}
