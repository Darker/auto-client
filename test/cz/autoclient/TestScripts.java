/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.ScriptSymbol;

/**
 *
 * @author Jakub
 */
public class TestScripts {
   public static void main(String[] args) throws Exception
   {
     /*String[] testChars = new String[] {
       "d",
       "\\",
       "\"",
       "3",
       " ",
       "D",
       ">",
       "S>"
     };
     for(String s:testChars) {
       System.out.println("String \""+s+"\" identified as "+ScriptSymbol.identify(s));
       System.out.println("   Reverse check: "+ScriptSymbol.identify(s).is(s+"!0ds"));
     }*/
     String s1;
     /*String s1 = "S>bl,hh;aa,b";
     OneLineScript.parse(s1);*/
     
     /*s1 = "bl,hh;aa,b";
     OneLineScript.parse(s1);*/
     
     s1 = "S>bl,S>;aa,\\,\\d\\\\,;";
     OneLineScript.parse(s1);
     
     s1 = "S>echo,pokus bla bla;echo,druhy radek";
     OneLineScript s = OneLineScript.parse(s1);
     s.compile();
     s.run();
   }
}
