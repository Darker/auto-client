/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.scripting.OneLineScript;
import cz.autoclient.scripting.ScriptCommand;
import cz.autoclient.scripting.exception.CommandException;
import cz.autoclient.scripting.exception.IllegalCmdArgumentException;
import cz.autoclient.scripting.exception.ScriptParseException;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author Jakub
 */
public class TestScripting {
  //@Rule
  
  
  @Before
  public void createTestData() throws IOException {
    ScriptCommand.setCommand("envex", CommandEnvException.class);
    ScriptCommand.setCommand("sbecho", EchoToStringBuilder.class);
  }
  @Test
  public void TestEnvironment() throws ScriptParseException {
    OneLineScript test = OneLineScript.parse("S>echo,d");
    test.setenv("sb", new StringBuilder());
    test.getenv("sb", StringBuilder.class).append("ddd");
    assertEquals("Enviroment variables are unreliable.", "ddd", test.getenv("sb", StringBuilder.class).toString());
  }
  @Test
  public void TestParseEscaping() throws ScriptParseException, CommandException {
    OneLineScript test = OneLineScript.parseAndCompile("S>sbecho,S>;sbecho,\\,\\d\\\\,;");
    test.setenv("sb", new StringBuilder());
    test.run();
    assertEquals("Escaping doesn't work properly!", "S>,\\d\\", test.getenv("sb", StringBuilder.class).toString());
    //new TestException("Exception thrown by command.")
  }
  @Test(expected = ScriptParseException.class)
  public void TestParseNoCommandName() throws ScriptParseException, CommandException {
    OneLineScript.parse("S>,sss;");
  }
  
  public static class EchoToStringBuilder extends ScriptCommand {
    private String data;
    @Override
    public void parseArguments(Iterable<String> args) throws IllegalCmdArgumentException {
      StringBuilder s = new StringBuilder();
      for(String str:args) {
        s.append(str);
      }
      data = s.toString();
    }

    @Override
    public boolean execute() {
      StringBuilder sb = environment.getOrThrow("sb", StringBuilder.class);
      if(sb!=null) {
        sb.append(data);
        return true;
      }
      throw new RuntimeException("No string builder available.");
    }
    
  }
  public static class TestException extends RuntimeException {}
  public static class CommandEnvException extends ScriptCommand {
    @Override
    public void parseArguments(Iterable<String> args) throws IllegalCmdArgumentException {
      new Exception().printStackTrace();
    }
    
    @Override
    public boolean execute() {
      throw environment.get("exception", TestException.class);
    }
  }
  
}
