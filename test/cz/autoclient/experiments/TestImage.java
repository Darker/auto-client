/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.experiments.ScreenWatcherMain;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Jakub
 */
public class TestImage {
   public static void main(String[] args) throws Exception
   {
     String path = ScreenWatcherMain.path + "LOBBY-SPELL_1.png";
     File img = new File(path);
     BufferedImage image = null;
     try {
       image = ImageIO.read(img);
     }
     catch(IOException e) {
       throw new Error("Can't read '"+img+"': "+e);
     }
     int width = image.getWidth(),
         height = image.getHeight();
     final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
     if(pixels.length==0)
       throw new Error("No pixels.");
     //If there's entry for every R, G, B, and A
     System.out.println("width*height*4 = "+(width*height)+"*4 = "+(width*height*4));
     //If R,G and B are stored separatelly, no alpha
     System.out.println("width*height*3 = "+(width*height*3));
     //If every pixel is represented by one integer
     System.out.println("width*height = "+(width*height));
     //The actual length
     System.out.println("pixels.length = " + pixels.length);
     
     int first = pixels[0];
     for(int i=0,l=pixels.length; i<l; i++) {
       if(pixels[i]!=first)
         throw new Error("Found a difference!");
     }
     System.out.println("The array is same through and through.");
     
     
   }
}
