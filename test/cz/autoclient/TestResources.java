/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.GUI.ImageResources;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author Jakub
 */
public class TestResources {
   public static void main(String[] args) throws Exception
   {
     //Remember if everything was OK
     boolean ok = true;
     
     System.out.println("Checking resources: \n");
     for(ImageResources ir : ImageResources.values()) {
       Image im = ir.getImage();
       ImageIcon ico = ir.getIcon();
       System.out.println("  ["+ir.name()+"]\n  - path: "+ir.getClasspath());
       if(im==null || ico==null) {
         System.out.println("    - ERROR!");
         if(im==null)
           System.out.println("      Image failed.");
         if(ico==null)
           System.out.println("      ImageIcon failed.");
         ok = false;
       }
       else
         System.out.println("    - OK.");
     }
     if(!ok) {
       throw new Exception("There were broken resources! Remove them from enum or add them to .jar.");
     }
   }
}
