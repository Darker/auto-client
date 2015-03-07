/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.GUI.ImageResources;
import java.awt.Image;

/**
 *
 * @author Jakub
 */
public class TestResources {
   public static void main(String[] args) throws Exception
   {
     Image im = ImageResources.ICON.getImage();
     
     if(im==null)
       throw new Exception("Resources do not work!");
     else
       System.out.println("Resources OK.");
   }
}
