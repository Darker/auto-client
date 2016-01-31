/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Jakub
 */
public class RunnableScript {
  public RunnableScript(File file) {
    this.file = file;
  }
  protected final File file;
  public Process run() throws IOException {
    ProcessBuilder pb = new ProcessBuilder("cmd", "/K "+file.getAbsolutePath()+">update_test.txt");
    Process p = pb.start();
    return p;
  }
}
