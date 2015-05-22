/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.ScriptCommand;
import cz.autoclient.scripting.ScriptEnvironment;
import cz.autoclient.scripting.ScriptSymbol;
import cz.autoclient.scripting.exception.IllegalCmdArgumentException;

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
     
     ScriptCommand.setCommand("ex", CommandException.class);
     s = OneLineScript.parse("S>echo,Test systemovych promennych.;ex");
     s.compile();
     s.setenv("exception", new RuntimeException("Vse je v poradku."));
     System.out.println("Chyba: "+s.getenv("exception", RuntimeException.class));
     s.run();
   }
   public static class CommandException extends ScriptCommand {
     @Override
     public void parseArguments(Iterable<String> args) throws IllegalCmdArgumentException {
     }

     @Override
     public boolean execute() {
       throw environment.get("exception", RuntimeException.class);
     }
   }
}
