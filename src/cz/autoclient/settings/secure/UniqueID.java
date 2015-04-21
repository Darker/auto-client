/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.settings.secure;

import cz.autoclient.dllinjection.NativeProcess;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

/**
 *
 * @author Jakub
 */
public enum UniqueID implements PasswordInitialiser {
  MAC_ADDRESS {
    @Override
    public String getPassword() {
       return getMacAddress();
    }
  },
  WINDOWS_USER_SID {
    @Override
    public String getPassword() {
      return getSingleMatch("(S\\-[0-9]\\-[0-9]\\-[0-9\\-]+)", getCmdline("whoami /user"));
    }
  };
  
  @Override
  public abstract String getPassword();
  
  @Override
  public boolean equals(PasswordInitialiser p) {
    return p!=null && (p==this || p.getPassword().equals(this.getPassword()));
  }
  /*@Override
  public int hashCode() {
    return getPassword().hashCode();
  }*/
    
  public static String getCmdline(String command) {
    String ret;
    try {
      ret = NativeProcess.readWholeOutput(command, 2000);
    }
    catch(IOException e) {
      return ""; 
    }
    if(ret!=null)
      return ret;
    else 
      return "";
    
  }
  public static String getSingleMatch(Pattern expr, String search) {
    Matcher m = expr.matcher(search);
    if (m.find()) {
      return m.group(1);
    }
    return "";
  }
  
  public static String getSingleMatch(String expr, String search) {
    return getSingleMatch(Pattern.compile(expr), search);
  }
  public static String getMacAddress() {
    byte[] mac = null;
    try {
      Enumeration<NetworkInterface> infs = NetworkInterface.getNetworkInterfaces();
      //mac = .nextElement().getHardwareAddress();
      for (;infs.hasMoreElements();) {
        NetworkInterface d = infs.nextElement();
        /*System.out.println("Network inf: ");
        System.out.println("             Name: "+d.getDisplayName());
        System.out.println("             Virtual: "+d.isVirtual());
        System.out.println("             MAC: "+MAC2String(d.getHardwareAddress()));*/
        byte[] addr = d.getHardwareAddress();
        if(addr!=null) {
          mac = addr;
          break;
        }
      }
    } catch (SocketException ex) {
      System.out.println(ex);
      return "";
      //Logger.getLogger(SecureSettings.class.getName()).log(Level.SEVERE, null, ex);
    } catch(NullPointerException e) {
      System.out.println(e);
      e.printStackTrace();
      return ""; 
    }
    
    if(mac==null) {
      System.out.println("MAC is null.");
      return "";
    }
    return MAC2String(mac);
  }
  public static String MAC2String(byte[] mac) {
    if(mac==null)
      return "";
    StringBuilder b = new StringBuilder();
    
    for (int i = 0; i < mac.length; i++) {
      if(i>0)
        b.append('-');
      b.append(String.format("%02X", (int)(mac[i]&0xFF)));
    }
    return b.toString();
  }
}
