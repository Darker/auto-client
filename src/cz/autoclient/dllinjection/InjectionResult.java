/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.dllinjection;

/**
 *
 * @author Jakub
 */
public abstract class InjectionResult  {
  public void run(boolean result) {
    if(result)
      run(true, null);
    else
      run(false, "Unknown error.");
  }
  public abstract void run(boolean result, String fail_reason);
  
  public static final InjectionResult DUMMY = new InjectionResult() {
    @Override
    public void run(boolean result, String fail_reason) {}
  };
}
