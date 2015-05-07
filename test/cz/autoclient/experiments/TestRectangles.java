/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.autoclick.Rect;

/**
 *
 * @author Jakub
 */
public class TestRectangles {
   public static void main(String[] args) throws Exception
   {
     Rect pos = new Rect(96, 304, 134, 266);
     Rect region = new Rect(133, 688, 371, 361);
     System.out.println(pos.move(region.left, region.top));
   }
}
