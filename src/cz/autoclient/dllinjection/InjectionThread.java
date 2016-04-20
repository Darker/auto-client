/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.dllinjection;

import java.io.IOException;

/**
 *
 * @author Jakub
 */
public class InjectionThread extends Thread {
  private final InjectionResult callback;
  private Process cmd = null;
  public InjectionThread() {
    callback = InjectionResult.DUMMY;
  }
  public InjectionThread(InjectionResult cb) {
    callback = cb==null?InjectionResult.DUMMY:cb;
  }
  @Override
  public void run() {

      try {
        cmd = DLLInjector.injectNow();
      }
      catch(IOException e) {
        callback.run(false, "Dll injection executable not found!");
        return;
      }
      catch(ProcessNotFoundException e) {
        callback.run(false, "Process '"+e.name+"' not found!"); 
        return;
      }
      String output = NativeProcess.readWholeOutput(cmd);
      try {
        cmd.waitFor();
      }
      catch(InterruptedException e) {
        callback.run(false, "Thread interrupted externally.");
        return;
      }
      //System.out.println("Injection output:\n "+output);
      //System.out.println("Injection result: "+cmd.exitValue());
      callback.run(true);
      //Unfortunatelly, the binary allways returns 0, even on error
      //callback.run(p.exitValue()==0, "Error in binary API.");
      
      
  }
}
