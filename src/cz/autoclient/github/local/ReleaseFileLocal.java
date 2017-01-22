/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.local;

import cz.autoclient.github.html.*;
import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.ReleaseFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jakub
 */
public class ReleaseFileLocal implements ReleaseFile {
  public final ReleaseLocal parent;
  public final URL downloadUrl;
  public final String name;
  public final long size;
  
  public ReleaseFileLocal(ReleaseLocal parent, JSONObject elm) {
    this.parent = parent;
    
    URL downloadUrl = null;
    String name = null;
    long size = 0;
    try {
      name = elm.getString("name");
      size = elm.getInt("size");
      downloadUrl = new URL(parent.url, URLEncoder.encode(name));
    }
    catch(JSONException e) {}
    catch (MalformedURLException ex) {
      System.out.println("Error creating url: \""+parent.url+"\" + \""+URLEncoder.encode(name)+"\"");
      ex.printStackTrace();
    };
   
    this.downloadUrl = downloadUrl;
    this.name = name;
    this.size = size;
  }


  public Release parent() {
    return parent; 
  }
  public static long sizeToNumber(String size) {
    //System.out.println("Parsing size!");
    // Space divides the size and the multipier (k, M, G...)
    int firstSpace = size.indexOf(' ');
    double multiplier = 1;
    // replace bytes to leae the multiplier alone
    size = size.replaceFirst("\\s*(B|b|Bytes)\\s*$", "");
    //System.out.println("Size: "+size);
    // If the space wasn't replaced there's stull some stuff
    if(firstSpace<size.length()) {
      //get the stuff after the space (if any)
      String unit = size.substring(firstSpace+1);
      switch(unit) {
        case "T" : 
          multiplier = 1e12;
        break;
        case "G" : 
          multiplier = 1e9;
        break;
        case "M" : 
          multiplier = 1e6;
        break;
        case "k":
          multiplier = 1e3;
        break;
      }
    }
    return (long)(Double.parseDouble(size.substring(0, firstSpace))*multiplier);
  }
  private static URL urlorexc(String url) {
    try { 
      return new URL(url);
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Invalid url \""+url+"\" passed to ReleaseFileHtml.");
    }
  }
  @Override
  public URL downloadUrl() {
    return downloadUrl;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public long size() {
    return size;
  }
  
}
