/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class Version extends DataLoader {
  private String version;
  private String language;
  
  private Champions champion;
  
  public Version(DataLoader.Realm r, File p) {
    super(r, p); 
  }
  public Version(DataLoader.Realm r, File p, boolean b) {
    super(r, p, b); 
  }
  
  @Override
  public String getVersion() {
    if(version==null) {
      JSONObject data = getData();
      try {
        version=data.getString("v");
      }
      catch(JSONException e) {
        System.out.println(e);
        return "error"; 
      }
    }
    return version;
  }
  public String getLanguage() {
    if(language==null) {
      JSONObject data = getData();
      try {
        language=data.getString("l");
      }
      catch(JSONException e) {
        //System.out.println(e);
        return "en_US"; 
      }
    }
    return language;
  }
  
  public Champions getChampions() {
    if(champion==null) {
      champion = new Champions(this, auto_download); 
    }
    return champion; 
  }
  @Override
  public String getFilename() {
    return realm_str;
  }
  @Override
  public URL getURL() {
    try {
      return new URL("http://ddragon.leagueoflegends.com/realms/"+realm+".json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }

  @Override
  public boolean upToDate() {
    data_tmp = fromURL(getURL());
    try {
      return getVersion().equals(data_tmp.getString("v"));
    }
    catch(JSONException e) {
      //Can't update so it doesn't matter
      return true;
    }
  }
}
