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
public class Champions extends DataLoader {
  private String version;
  private Version ver;
  private JSONObject champ_data;
  private HashMap<String, Champion> champs = new HashMap<>();
  private String[] champion_names;
  private ArrayList<String> champion_names_list;
  public Champions(Version v) {
    this(v, false); 
  }
  public Champions(Version v, boolean b) {
    super(v.realm, v.base_path, b); 
    ver = v;
    try {
      champ_data = getData().getJSONObject("data");
    } catch (JSONException ex) {
      champ_data = null;
    }
  }
  public Champion getChampion(String name) {
    if(champ_data!=null) {
      if(champs.containsKey(name)) {
        return (Champion)champs.get(name);
      }
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
      JSONObject data = getData();
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
    if(champ_data!=null) {
      if(champion_names!=null) {
        return champion_names; 
      }
      if(champion_names_list==null) 
        allNamesList();
      return champion_names = champion_names_list.toArray(new String[champion_names_list.size()]);
    }
    return new String[0];
  }
  public ArrayList<String> allNamesList() {
    if(champ_data!=null) {
      if(champion_names_list!=null) {
        return champion_names_list; 
      }
      ArrayList<String> names = new ArrayList<>();
      String[] ids = JSONObject.getNames(champ_data);
      System.out.println("Feteching "+ids.length+" champion names.");
      for(String id: ids) {
        try {
          names.add(champ_data.getJSONObject(id).getString("name"));
        } catch (JSONException ex) {
          System.err.println("Error fetching champion "+id+" name: "+ex);
          //Ignore and continue
        }
      }  
      return champion_names_list = names;
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
      System.out.print("Version: ");
      System.out.println(ver.getVersion());
      
      return new URL("http://ddragon.leagueoflegends.com/cdn/"+ver.getVersion()+"/data/"+ver.getLanguage()+"/champion.json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }

  @Override
  public boolean upToDate() {
    return getVersion().equals(ver.getVersion());
  }


  
}
