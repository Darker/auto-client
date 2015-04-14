/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.cache.WindowCache;
import cz.autoclient.autoclick.windows.cache.title.CacheByTitle;



/**
 *
 * @author Jakub
 */

public class TestWindowCache {
  public static void main(String[] args) throws Exception {
    WindowCache cache = new CacheByTitle();
    Window netbeans = cache.getWindow("testwin");
    while(netbeans!=null) {
      System.out.print("Got window "+netbeans.getTitle());
      System.in.read();
      netbeans = cache.getWindow("testwin");
    }
    System.out.println("Window lost...");
  }
}
