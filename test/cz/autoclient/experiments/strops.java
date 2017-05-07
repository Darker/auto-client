/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

/**
 *
 * @author Jakub
 */
public class strops {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String filename = "ddd/ddd\\ddd.exe";
    String[] separators = new String[] {"\\", "/"};
    for(int i=0; i<separators.length; ++i) {
      int index = filename.indexOf(separators[i]);
      if(index!=-1) {
        filename = filename.substring(index+1);
      }
    }

    System.out.println(filename);
  }
  
}
