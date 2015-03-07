/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.dllinjection.NativeProcess;
import java.io.File;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Jakub
 */
public class DLLInject {
  public static void main(String[] args) throws Exception
  {
    String procName = "PSPad.exe";     // process name
    File dll = new File("./stop_flashing/StopThat.dll"); // injectee DLL path
    if(!dll.exists()) {
      System.out.println("Dll not found in "+dll.getAbsolutePath());
      return;
    }

    // signature of method to be run after DLL is injected
    //Signature signature = new Signature("TestNamespace", "Program", "Main"); 
    System.out.println("Finding PID for program:");
    int pid = NativeProcess.getProcessId(procName);     // get process ID
    System.out.println("   - "+pid);
    if(pid==-1) {
      throw new Exception("Process does not exist.");
    }

    File injector = new File("./stop_flashing/RemoteDLLInjector32.exe");
    //Injector.getInstance().inject(pid, dll, signature); // inject DLL into process
    Process p = Runtime.getRuntime().exec(injector.getAbsolutePath()+" "+pid+" \""+dll.getAbsolutePath()+"\"");
    byte[] buffer = new byte[255];
    boolean last_iteration = true;
    while(p.isAlive() || last_iteration) {
      if(!p.isAlive())
        last_iteration = false;
      
      int read = p.getInputStream().read(buffer);
      for(int i=0; i<read; i++) {
        System.out.print((char)buffer[i]);
      }
      if(read>0) {
        last_iteration = true;
      }
      p.waitFor(50, TimeUnit.MILLISECONDS);
    }
    System.out.println("Return value: "+p.exitValue());
  }
}
