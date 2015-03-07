/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.dllinjection;
//I'll make my own functions later on

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Jakub
 * 
 * This file is designed to allow me to inject my dll into client. The dll will suppress any 
 * taskbar flashing or jumping in focus.
 */
public class DLLInjector {
  public static final String DLL_32 = "StopThat.dll";
  public static final String INJECTOR_32 = "RemoteDLLInjector32.exe";
  public static final String DLL_64 = "StopThat_64.dll";
  public static final String INJECTOR_64 = "RemoteDLLInjector64.exe";
  
  private static String command_line_pattern = "$PID \"$DLL_PATH\"";
  private static String directory = "./stop_flashing";
  private static int version = 32;
  
  //Paths to dll file and the injection program
  private static File dll;
  private static File injector;
  
  public static void setDirectory(String path) {
    if(!path.equals(directory)) {
      directory = path;
      //Clear paths
      dll = null;
      injector = null;
    }
  }
  
  private static void createPaths() {
    if(version==32) {
      dll = new File(directory+"/"+DLL_32);
      injector = new File(directory+"/"+INJECTOR_32);
    }
    else {
      dll = new File(directory+"/"+DLL_64);
      injector = new File(directory+"/"+INJECTOR_64);
    }
  }
  
  public static boolean available() {
    createPaths();
    return dll.exists() && injector.exists();
  }
  public static void inject() {
    if(available()) {
      new InjectionThread().start();
    }    
  }
  public static void inject(InjectionResult res) {
    if(available()) {
      new InjectionThread(res).start();
    }
    else
      res.run(false, "Injection files are missing.");
  }
  public static Process injectNow() throws ProcessNotFoundException, IOException {
    int pid = NativeProcess.getProcessId(cz.autoclient.PVP_net.Constants.process_name);
    if(pid>0) {
      String command_line = DLLInjector.getCommandLine(pid);
      return Runtime.getRuntime().exec(command_line);
    }
    else 
      throw new ProcessNotFoundException("Process not seen in tasklist.", cz.autoclient.PVP_net.Constants.process_name);
  }
  public static String getCommandLine(int pid) {
    return injector.getAbsolutePath()+
                            " "+
                            command_line_pattern.replace("$PID", ""+pid)
                                                .replace("$PNAME", cz.autoclient.PVP_net.Constants.process_name)
                                                .replace("$DLL_PATH", dll.getAbsolutePath());
    
  }
  
}
