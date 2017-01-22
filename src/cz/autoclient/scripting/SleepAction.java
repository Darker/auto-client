/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import java.util.concurrent.Callable;

/**
 *
 * @author Jakub
 */
public interface SleepAction extends Callable<Boolean> {
  public long duration();
  public boolean done();
  
  public static class NoAction implements SleepAction {
    @Override
    public long duration() {
      return 0;
    }
    @Override
    public boolean done() {
      return true;
    }
    @Override
    public Boolean call() throws Exception {
      return true;
    }
  }
}
