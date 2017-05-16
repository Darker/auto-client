/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.autoclick.ComparablePixel;
import java.util.concurrent.Callable;

/**
 *
 * @author Jakub
 */
public abstract class AutomatCallable implements Callable<Boolean> {

  public AutomatCallable(AutomatInterface myAutomat) {
    this.myAutomat = myAutomat;
  }
  public AutomatCallable() {
    myAutomat = null;
  }
  public AutomatInterface myAutomat;
  public void setMyAutomat(AutomatInterface myAutomat) {
    this.myAutomat = myAutomat;
  }

  @Override
  public Boolean call() throws Exception {
    return false;
  }
  
  protected void slowClick(ComparablePixel point, int timeout) throws InterruptedException {
    if(myAutomat != null) {
      myAutomat.slowClick(point, timeout);
    }
  }
  protected void click(ComparablePixel point) throws InterruptedException {
    if(myAutomat != null) {
      myAutomat.click(point);
    }
  }
}
