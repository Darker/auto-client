/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import java.io.File;
import java.io.PrintWriter;

/**
 *
 * @author Jakub
 */
public class WaitForBatch {

  /**
   * Get your program totally stuck in three simple steps:
   */
  public static void main(String[] args) throws Throwable {
    /** 
     *  1. Create a simple batch file.
     */
    String batch = "echo Hello world!"; //\r\nexit
    File file = new File("hello_world.bat");
    file.createNewFile();
    try (PrintWriter out = new PrintWriter(file)) {
      out.print(batch);
    }
    /**
     * 2. Execute that batch file
     */
    ProcessBuilder pb = new ProcessBuilder("cmd", "/C "+file.getAbsolutePath()+">hello_world_log.txt");
    Process p = pb.start();
    /**
     * 3. Wait for the file like an idiot forever
     */
    p.waitFor();
  }
  
}
