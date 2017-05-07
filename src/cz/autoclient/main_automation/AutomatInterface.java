/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.scripting.SleepAction;

/**
 *
 * @author Jakub
 */
public interface AutomatInterface {

  void callText(final String message) throws InterruptedException;

  /**
   * Call text to chat in standard lobby. Supports scripts.
   *
   * @param message text to call or script to process
   * @param actions actions to perform when sleeping if message is script
   * @throws InterruptedException
   */
  void callText(final String message, SleepAction[] actions) throws InterruptedException;

  void simulateAccepted();
  
}
