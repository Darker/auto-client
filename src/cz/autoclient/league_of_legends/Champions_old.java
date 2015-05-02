/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class Champions_old extends DataLoader {
  private String version;
  private JSONObject champ_data;
  private HashMap<String, Champion> champs = new HashMap<>();
  private String[] champion_names;
  private ArrayList<String> champion_names_list;
  public Champions_old(LoLVersion v) {
    this(v, false); 
  }
  public Champions_old(LoLVersion v, boolean b) {
    super(v, v.base_path, b); 
  }
  public JSONObject getChampData() {
    if(champ_data==null) {
      try {
        champ_data = getJSONData().getJSONObject("data");
      } catch (JSONException ex) {
        champ_data = null;
      }
    }
    return champ_data;
  }
  
  public Champion getChampion(String name) {
    if(champs.containsKey(name)) {
      return (Champion)champs.get(name);
    }
    if(getChampData()!=null) {
      try {
        JSONObject node = champ_data.getJSONObject(name);
        Champion champ = new Champion(this, node);
        champs.put(name, champ);
        return champ;
      } catch (JSONException ex) {
        return null;
      }
    }
    return null;
  }
  
  
  @Override
  public String getVersion() {
    if(version==null) {
      JSONObject data = getJSONData();
      try {
        version=data.getString("version");
      }
      catch(JSONException e) {
        System.out.println(e);
        return "error"; 
      }
    }
    return version;
  }
  
  public String[] allNames() {
    /*if(champion_names!=null) {
      return champion_names; 
    }*/
    
    if(champion_names_list==null)
      allNamesList();
    return champion_names = champion_names_list.toArray(new String[champion_names_list.size()]);
   
    //return new String[0];
  }
  
  @Override
  public void unloadData() {
    champ_data = null;
    super.unloadData();
  }
  
  public ArrayList<String> allNamesList() {
    if(champion_names_list!=null) {
      return champion_names_list; 
    }
    if(getChampData()!=null) {
      return champion_names_list = DataLoader.listJSONNames(champ_data);
    }
    return new ArrayList<String>();
  }
  @Override
  public String getFilename() {
    return "champion";
  }

  @Override
  public URL getURL() {
    try {
      //System.out.print("LoLVersion: ");
      //System.out.println(ver.getVersion());
      
      return new URL("http://ddragon.leagueoflegends.com/cdn/"+getBaseVersion().getVersion()+"/data/"+getBaseVersion().getLanguage()+"/champion.json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }

  @Override
  public boolean upToDate() {
    return getVersion().equals(getBaseVersion().getVersion());
  }


  
}
