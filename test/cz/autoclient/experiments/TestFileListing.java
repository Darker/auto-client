/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.updates.UpdateInfo;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class TestFileListing {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Throwable{
    File self = new File(UpdateInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    for(File file:listFileChildren(self)){
      System.out.println(file.getPath());
    }
  }
  /** Returns list of all child files, recursively. **/
  public static Iterable<File> listFileChildren(File parent, ArrayList<File> list) {
    File[] files = parent.listFiles();
    for(File file:files) {
      list.add(file);
      if(file.isDirectory())
        listFileChildren(file, list);
    }
    return list;
  }
  public static Iterable<File> listFileChildren(File parent) {
    return listFileChildren(parent, new ArrayList());
  }
}
