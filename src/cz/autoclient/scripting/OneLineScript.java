/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.scripting;

import cz.autoclient.scripting.exception.CommandException;
import cz.autoclient.scripting.exception.ScriptParseException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class OneLineScript implements Callable<Boolean> {
  private ArrayList<CommandBuilder> preCompiled;
  private ArrayList<ScriptCommand> compiled;
  private ScriptEnvironment environment;
  private OneLineScript(ArrayList<CommandBuilder> preCompiled) {
    this.preCompiled = preCompiled; 
  }
  public void compile() throws CommandException {
    if(compiled!=null)
      return;
    if(preCompiled==null)
      throw new IllegalStateException("No script to compile. This is strange error!");
    compiled = new ArrayList();
    for(CommandBuilder cb : preCompiled) {
      try {
        ScriptCommand c = cb.createCommand();
        c.setEnvironment(getEnvironment());
        c.parseArguments(cb.args);
        compiled.add(c);
      }
      catch(CommandException e) {
        //Clear the compiled data since it's useless
        compiled = null;
        throw e;
      }
    }
    preCompiled = null;
  }
  @Override
  public Boolean call() throws InterruptedException {
    run();
    return true;
  }
  public void run() throws InterruptedException {
    if(compiled==null)
      throw new IllegalStateException("Tried to run uncompiled script.");
    for(ScriptCommand cl : compiled) {
      try {
        cl.execute();
      } catch (Exception ex) {
        if(ex instanceof InterruptedException)
          throw (InterruptedException)ex;
      }
    }
  }
  /**
   * Configure environmentironment variable in the inner environmentironment.
   * @param name
   * @param value 
   */
  public void setenv(String name, Object value) {
    if(environment==null)
      environment = new ScriptEnvironment(name, value);
    else
      environment.set(name, value);
  }
  
  /**
   * Fetch inner environmentironment variable value.
   * @param <T> required variable type
   * @param name name of variable
   * @param type type class
   * @return  
   */
  public <T> T getenv(String name, Class<T> type) {
    if(environment==null)
      return null;
    else
      return environment.get(name, type);
  }

  public ScriptEnvironment getEnvironment() {
    return environment!=null?environment:(environment=new ScriptEnvironment());
  }
  
  
  
  public enum Section {START, COMMAND_NAME, COMMAND_ARGS, ESCAPING};
  public static OneLineScript parseAndCompile(String data) throws ScriptParseException, CommandException {
    OneLineScript scr = parse(data);
    scr.compile();
    return scr;
  }
  public static OneLineScript parse(String data) throws ScriptParseException {
    ArrayList<ScriptSymbol> expected = new ArrayList();
    ArrayList<CommandBuilder> commands = new ArrayList();
    
    expected.add(ScriptSymbol.SCRIPT_START);
    StringBuilder buffer = new StringBuilder();
    CommandBuilder b = new CommandBuilder();
    
    ScriptSymbol currentSymbol = null;

    Section section = Section.START;
    
    // Parsing character by character
    for(int i=0, l=data.length(); i<=l; i++) {
       if(i>=l) {
         currentSymbol = ScriptSymbol.END;
         if(!expected.contains(currentSymbol)) {
           throw unexpectedEx(expected, data, i);
         }
       }
       else if(section == Section.ESCAPING) {
         currentSymbol = ScriptSymbol.identify(data, i);
       }
       else if((currentSymbol=whichExpected(expected, data, i))==null)
         throw unexpectedEx(expected, data, i);
       if(i<l) {
         buffer.append(data.substring(i, i+currentSymbol.length));
         //Increment i for symbols longer than one character
         i+=currentSymbol.length-1;
       }
       
       if(section==Section.START) {
         expected.remove(ScriptSymbol.SCRIPT_START);
         expected.add(ScriptSymbol.LETTER);
         buffer = new StringBuilder();
         section=Section.COMMAND_NAME;
         System.out.println("Script started, waiting for first command.");
       }
       else if(section==Section.COMMAND_NAME) {
         //System.out.println("  building command name: \""+buffer+"\"");
         expected.remove(ScriptSymbol.SCRIPT_START);
         exAddOnce(expected, ScriptSymbol.LETTER);
         exAddOnce(expected, ScriptSymbol.PARAMETER_SEPARATOR);
         exAddOnce(expected, ScriptSymbol.COMMAND_SEPARATOR);
         exAddOnce(expected, ScriptSymbol.END);
         if(currentSymbol!=ScriptSymbol.LETTER) {
           //Remove the non-letter symbol
           if(currentSymbol!=ScriptSymbol.END)
             buffer.deleteCharAt(buffer.length()-currentSymbol.length);
           b.name = buffer.toString();
           buffer = new StringBuilder();
           System.out.println("Command name is '"+b.name+"'");
           //If there is parameter separator after command name
           if(currentSymbol==ScriptSymbol.PARAMETER_SEPARATOR) {
             System.out.println("  Expecting parameters...");
             section=Section.COMMAND_ARGS;
             exAddOnce(expected, ScriptSymbol.NUMBER);
             exAddOnce(expected, ScriptSymbol.UNKNOWN_SYMBOL);
             exAddOnce(expected, ScriptSymbol.ESCAPE);
             exAddOnce(expected, ScriptSymbol.SPACE);
             //exRemove_afterParamSeparator(expected);
           }
         }
       }
       else if(section==Section.COMMAND_ARGS) {
         exAddOnce(expected, ScriptSymbol.PARAMETER_SEPARATOR);
         exAddOnce(expected, ScriptSymbol.COMMAND_SEPARATOR);
         if(currentSymbol==ScriptSymbol.PARAMETER_SEPARATOR ||
            currentSymbol==ScriptSymbol.COMMAND_SEPARATOR ||
            currentSymbol==ScriptSymbol.END
             ) {
           //Remove separator
           if(currentSymbol!=ScriptSymbol.END)
             buffer.deleteCharAt(buffer.length()-currentSymbol.length);
           System.out.println("  Param: "+buffer.toString());
           b.args.add(buffer.toString());
           buffer = new StringBuilder();
           //if(currentSymbol==ScriptSymbol.PARAMETER_SEPARATOR)
             //exRemove_afterParamSeparator(expected);
         }
         else if(currentSymbol==ScriptSymbol.ESCAPE) {
           section=Section.ESCAPING;
         }
       }
       else if(section==Section.ESCAPING) {
         //If current symbol is escapable, the backslash will be consumed
         if(currentSymbol.escapable) {
           System.out.println("Escaping "+currentSymbol.prettyPrint(data, i));
           buffer.deleteCharAt(buffer.length()-1-currentSymbol.length);
         }
         else {
           System.out.println("No escape operation. Kept backlash as it is.");
         }
         //Return back to normal argument data parsing
         section=Section.COMMAND_ARGS;
       }
       
       
       if(currentSymbol==ScriptSymbol.COMMAND_SEPARATOR || currentSymbol == ScriptSymbol.END) {
         System.out.println("Command "+b.name+" closed.");
         
         commands.add(b);
         
         //If next step is end, we can end now, nothing is unfinished
         if(i>=l-1) {
           System.out.println("All commands parsed.");
           break;
         }
         
         b = new CommandBuilder();
         section=Section.COMMAND_NAME;
         expected.remove(ScriptSymbol.PARAMETER_SEPARATOR);
         expected.remove(ScriptSymbol.NUMBER);
         expected.remove(ScriptSymbol.UNKNOWN_SYMBOL);
         expected.remove(ScriptSymbol.ESCAPE);
         expected.remove(ScriptSymbol.SPACE);

         
         System.out.println("Expecting command or end.");
       }
       
    }
    return new OneLineScript(commands);
  }
  
  public static void exAddOnce(ArrayList<ScriptSymbol> expected, ScriptSymbol sym) {
    if(!expected.contains(sym))
      expected.add(sym);
  }
  public static void exRemove_afterParamSeparator(ArrayList<ScriptSymbol> expected) {
    //Two parameter separators in line are not allowed
    expected.remove(ScriptSymbol.PARAMETER_SEPARATOR);
    //Command separator after param separator is not allowed
    expected.remove(ScriptSymbol.COMMAND_SEPARATOR);
    //End forbiden after param separator
    expected.remove(ScriptSymbol.END);
  }
  public static boolean isScript(String data) {
    return ScriptSymbol.SCRIPT_START.is(data);
  }
  private static ScriptParseException unexpectedEx(ArrayList<ScriptSymbol> expected, String data, int offset) {
    StringBuilder build = new StringBuilder("Unexpected ");
    if(offset<data.length()) {
      String str = data.substring(offset);
      build.append(ScriptSymbol.identify(str).prettyPrint(str,0));
    }
    else {
      build.append(ScriptSymbol.END);
    }
    build.append(" expecting ");
    
    boolean first = true;
    for(ScriptSymbol s:expected) {
      if(first) {
        first = false; 
      }
      else {
        build.append(", ");
      }
      build.append(s.name());
    }
    build.append(" at character ");
    build.append(offset);
    build.append(".\n     ");
    //Add the debug
    build.append(data);
    build.append("\n     ");
    for(int i=0; i<offset; i++) {
      build.append('-');
    }
    build.append('|');
    
    return new ScriptParseException(build.toString());
  }
  private static ScriptSymbol whichExpected(ArrayList<ScriptSymbol> expected, String data, int offset) {
    for(ScriptSymbol s : expected) {
      if(s.is(data, offset))
        return s;
    }
    return null;
    
  }
}
