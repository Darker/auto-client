/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.league_of_legends.maps.SummonerSpells;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class LoLVersion extends DataLoader {
  public static enum Realm {NA, OCE, EUNE, EUW,  RU;};
  public static enum Language {en_US, cs_CZ;};
  
  private String version;
  private String language;
  private Realm realm;
  private String realm_str;
  
  private Champions_old champion;
  private SummonerSpells spells;
  
  public LoLVersion(Realm r, File p) {
    this(r, p, false); 
  }
  public LoLVersion(Realm r, File p, boolean b) {
    super(null, p, b); 
    setRealm(r);
    setBaseVersion(this);
  }
  
  public final void setRealm(Realm realm) {
    this.realm = realm;
    realm_str = realm.name().toLowerCase();
  }
  
  @Override
  public String getVersion() {
    if(version==null) {
      JSONObject data = getJSONData();
      try {
        version=data.getString("v");
      }
      catch(JSONException e) {
        System.out.println(e);
        return "error"; 
      }
      if(version!=null) {
        tryClearData(); 
      }
    }

    return version;
  }
  public String getLanguage() {
    if(language==null) {
      JSONObject data = getJSONData();
      try {
        language=data.getString("l");
      }
      catch(JSONException e) {
        //System.out.println(e);
        return "en_US"; 
      }
    }
    if(language!=null) {
      tryClearData(); 
    }
    return language;
  }
  
  private void tryClearData() {
    if(language!=null && version !=null)
      unloadData();
  }
  
  public Champions_old getChampions() {
    if(champion==null) {
      champion = new Champions_old(this, auto_download); 
    }
    return champion; 
  }
  public SummonerSpells getSummonerSpells() {
    if(spells==null) {
      spells = new SummonerSpells(this, auto_download); 
    }
    return spells;  
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
  
  private static String latestVersion = null;
  public static boolean isLatestVersion(String version, Realm realm) {
    if(latestVersion==null) {
      try {
        JSONObject data = DataLoader.fromURL(new URL("http://ddragon.leagueoflegends.com/realms/"+realm+".json"));
        latestVersion = data.getString("v");
      }
      catch(Exception e) {
        //If fail, something is broken so any updates are not possible anyway
        return true; 
      }
    }
    return version.equals(latestVersion);
  }
  public static boolean isLatestVersion(String version) {
    return isLatestVersion(version, Realm.EUNE);
  }
}
