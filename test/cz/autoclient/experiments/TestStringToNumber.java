/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.github.html.ReleaseFileHtml;

/**
 *
 * @author Jakub
 */
public class TestStringToNumber {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    System.out.println(ReleaseFileHtml.sizeToNumber("4.33 MB"));
  }
  
}
