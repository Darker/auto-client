/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.sound.LazyLoadedSound;

/**
 *
 * @author Jakub
 */
public class TestSound {
   public static void main(String[] args) throws Exception
   {
     LazyLoadedSound sound = new LazyLoadedSound("sounds/notice.mp3");
     if(sound.isValid()) {
       sound.play();
     }
     else {
       System.out.println("Sound invalid!");
     }
   }
}
