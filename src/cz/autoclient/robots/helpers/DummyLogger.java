/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.robots.helpers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/**
 * Dummy logger that doesn't do anything. It will also properly
 * report as log with all loglevels turned off.
 * @author Jakub Mareda
 */
public class DummyLogger implements Logger {
  public static final DummyLogger inst = new DummyLogger();
  @Override
  public void catching(Level level, Throwable t) {}

  @Override
  public void catching(Throwable t) {}

  @Override
  public void debug(Marker marker, Message msg) {}

  @Override
  public void debug(Marker marker, Message msg, Throwable t) {}

  @Override
  public void debug(Marker marker, Object message) {}

  @Override
  public void debug(Marker marker, Object message, Throwable t) {}

  @Override
  public void debug(Marker marker, String message) {}

  @Override
  public void debug(Marker marker, String message, Object... params) {}

  @Override
  public void debug(Marker marker, String message, Throwable t) {}

  @Override
  public void debug(Message msg) {}

  @Override
  public void debug(Message msg, Throwable t) {}

  @Override
  public void debug(Object message) {}

  @Override
  public void debug(Object message, Throwable t) {}

  @Override
  public void debug(String message) {}

  @Override
  public void debug(String message, Object... params) {}

  @Override
  public void debug(String message, Throwable t) {}

  @Override
  public void entry() {}

  @Override
  public void entry(Object... params) {}

  @Override
  public void error(Marker marker, Message msg) {}

  @Override
  public void error(Marker marker, Message msg, Throwable t) {}

  @Override
  public void error(Marker marker, Object message) {}

  @Override
  public void error(Marker marker, Object message, Throwable t) {}

  @Override
  public void error(Marker marker, String message) {}

  @Override
  public void error(Marker marker, String message, Object... params) {}

  @Override
  public void error(Marker marker, String message, Throwable t) {}

  @Override
  public void error(Message msg) {}

  @Override
  public void error(Message msg, Throwable t) {}

  @Override
  public void error(Object message) {}

  @Override
  public void error(Object message, Throwable t) {}

  @Override
  public void error(String message) {}

  @Override
  public void error(String message, Object... params) {}

  @Override
  public void error(String message, Throwable t) {}

  @Override
  public void exit() {}

  @Override
  public <R> R exit(R arg0) {return arg0;}

  @Override
  public void fatal(Marker arg0, Message arg1) {}

  @Override
  public void fatal(Marker arg0, Message arg1, Throwable arg2) {}

  @Override
  public void fatal(Marker arg0, Object arg1) {}

  @Override
  public void fatal(Marker arg0, Object arg1, Throwable arg2) {}

  @Override
  public void fatal(Marker arg0, String arg1) {}

  @Override
  public void fatal(Marker arg0, String arg1, Object... arg2) {}

  @Override
  public void fatal(Marker arg0, String arg1, Throwable arg2) {}

  @Override
  public void fatal(Message arg0) {}

  @Override
  public void fatal(Message arg0, Throwable arg1) {}

  @Override
  public void fatal(Object arg0) {}

  @Override
  public void fatal(Object arg0, Throwable arg1) {}

  @Override
  public void fatal(String arg0) {}

  @Override
  public void fatal(String arg0, Object... arg1) {}

  @Override
  public void fatal(String arg0, Throwable arg1) {}

  @Override
  public Level getLevel() {return Level.OFF;}

  @Override
  public MessageFactory getMessageFactory() {return null;}

  @Override
  public String getName() {return "Dummy logger.";}

  @Override
  public void info(Marker arg0, Message arg1) {}

  @Override
  public void info(Marker arg0, Message arg1, Throwable arg2) {}

  @Override
  public void info(Marker arg0, Object arg1) {}

  @Override
  public void info(Marker arg0, Object arg1, Throwable arg2) {}

  @Override
  public void info(Marker arg0, String arg1) {}

  @Override
  public void info(Marker arg0, String arg1, Object... arg2) {}

  @Override
  public void info(Marker arg0, String arg1, Throwable arg2) {}

  @Override
  public void info(Message arg0) {}

  @Override
  public void info(Message arg0, Throwable arg1) {}

  @Override
  public void info(Object arg0) {}

  @Override
  public void info(Object arg0, Throwable arg1) {}

  @Override
  public void info(String arg0) {}

  @Override
  public void info(String arg0, Object... arg1) {}

  @Override
  public void info(String arg0, Throwable arg1) {}

  @Override
  public boolean isDebugEnabled() {return false;}

  @Override
  public boolean isDebugEnabled(Marker arg0) {return false;}

  @Override
  public boolean isEnabled(Level arg0) {return false;}

  @Override
  public boolean isEnabled(Level arg0, Marker arg1) {return false;};

  @Override
  public boolean isErrorEnabled() {return false;}

  @Override
  public boolean isErrorEnabled(Marker arg0) {return false;}

  @Override
  public boolean isFatalEnabled() {return false;}

  @Override
  public boolean isFatalEnabled(Marker arg0) {return false;}

  @Override
  public boolean isInfoEnabled() {return false;}

  @Override
  public boolean isInfoEnabled(Marker arg0) {return false;}

  @Override
  public boolean isTraceEnabled() {return false;}

  @Override
  public boolean isTraceEnabled(Marker arg0) {return false;}

  @Override
  public boolean isWarnEnabled() {return false;}

  @Override
  public boolean isWarnEnabled(Marker arg0) {return false;}

  @Override
  public void log(Level arg0, Marker arg1, Message arg2) {}

  @Override
  public void log(Level arg0, Marker arg1, Message arg2, Throwable arg3) {}

  @Override
  public void log(Level arg0, Marker arg1, Object arg2) {}

  @Override
  public void log(Level arg0, Marker arg1, Object arg2, Throwable arg3) {}

  @Override
  public void log(Level arg0, Marker arg1, String arg2) {}

  @Override
  public void log(Level arg0, Marker arg1, String arg2, Object... arg3) {}

  @Override
  public void log(Level arg0, Marker arg1, String arg2, Throwable arg3) {}

  @Override
  public void log(Level arg0, Message arg1) {}

  @Override
  public void log(Level arg0, Message arg1, Throwable arg2) {}

  @Override
  public void log(Level arg0, Object arg1) {}

  @Override
  public void log(Level arg0, Object arg1, Throwable arg2) {}

  @Override
  public void log(Level arg0, String arg1) {}

  @Override
  public void log(Level arg0, String arg1, Object... arg2) {}

  @Override
  public void log(Level arg0, String arg1, Throwable arg2) {}

  @Override
  public void printf(Level arg0, Marker arg1, String arg2, Object... arg3) {}

  @Override
  public void printf(Level arg0, String arg1, Object... arg2) {}

  @Override
  public <T extends Throwable> T throwing(Level arg0, T arg1) {return null;}

  @Override
  public <T extends Throwable> T throwing(T arg0) {return null;}

  @Override
  public void trace(Marker arg0, Message arg1) {}

  @Override
  public void trace(Marker arg0, Message arg1, Throwable arg2) {}

  @Override
  public void trace(Marker arg0, Object arg1) {}

  @Override
  public void trace(Marker arg0, Object arg1, Throwable arg2) {}

  @Override
  public void trace(Marker arg0, String arg1) {}

  @Override
  public void trace(Marker arg0, String arg1, Object... arg2) {}

  @Override
  public void trace(Marker arg0, String arg1, Throwable arg2) {}

  @Override
  public void trace(Message arg0) {}

  @Override
  public void trace(Message arg0, Throwable arg1) {}

  @Override
  public void trace(Object arg0) {}

  @Override
  public void trace(Object arg0, Throwable arg1) {}

  @Override
  public void trace(String arg0) {}

  @Override
  public void trace(String arg0, Object... arg1) {}

  @Override
  public void trace(String arg0, Throwable arg1) {}

  @Override
  public void warn(Marker arg0, Message arg1) {}

  @Override
  public void warn(Marker arg0, Message arg1, Throwable arg2) {}

  @Override
  public void warn(Marker arg0, Object arg1) {}

  @Override
  public void warn(Marker arg0, Object arg1, Throwable arg2) {}

  @Override
  public void warn(Marker arg0, String arg1) {}

  @Override
  public void warn(Marker arg0, String arg1, Object... arg2) {}

  @Override
  public void warn(Marker arg0, String arg1, Throwable arg2) {}

  @Override
  public void warn(Message arg0) {}

  @Override
  public void warn(Message arg0, Throwable arg1) {}

  @Override
  public void warn(Object arg0) {}

  @Override
  public void warn(Object arg0, Throwable arg1) {}

  @Override
  public void warn(String arg0) {}

  @Override
  public void warn(String arg0, Object... arg1) {}

  @Override
  public void warn(String arg0, Throwable arg1) {}
  
}
