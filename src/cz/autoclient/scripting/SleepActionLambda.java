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
public class SleepActionLambda implements SleepAction {
  public final long duration;
  public final Callable<Boolean> lambda;
  public boolean done = false;
  public SleepActionLambda(Callable<Boolean> lambda, long duration) {
    this.duration = duration;
    this.lambda = lambda;
  }
  public SleepActionLambda(Callable<Boolean> lambda) {
    this(lambda, 1000);
  }
  @Override
  public long duration() {
    return duration; 
  }
  @Override
  public Boolean call() throws Exception {
    return done = lambda.call() || done;
  }

  @Override
  public boolean done() {
    return done;
  }
}
