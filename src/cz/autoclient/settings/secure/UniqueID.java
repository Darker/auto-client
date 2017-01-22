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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public enum UniqueID implements PasswordInitialiser {
  MAC_ADDRESS {
    @Override
    public String getPassword() throws PasswordFailedException {
       return getMacAddress();
    }
  },
  WINDOWS_USER_SID {
    @Override
    public String getPassword() throws PasswordFailedException {
      try {
        return getSingleMatch("(S\\-[0-9]\\-[0-9]\\-[0-9\\-]+)", getCmdline("whoami /user", 2000));
      } catch (IOException ex) {
        throw new PasswordFailedException("IO exception occured while trying to fetch UID.", ex);
        //Logger.getLogger(UniqueID.class.getName()).log(Level.SEVERE, null, ex);
      } catch (TimeoutException ex) {
        //Logger.getLogger(UniqueID.class.getName()).log(Level.SEVERE, null, ex);
        throw new PasswordFailedException("The system process responsible for UID generation timed out.", ex);
      }
    }
  };
  
  @Override
  public abstract String getPassword() throws PasswordFailedException;
  /*@Override
  public int hashCode() {
    return getPassword().hashCode();
  }*/

  
  public static String getCmdline(String command, int timeout) throws IOException, TimeoutException {
    String ret;

    ret = NativeProcess.readWholeOutput(command, timeout);

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
