/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class SummonerSpells_old extends DataLoader {
  private String version_str;
  private JSONObject data;
  
  private HashMap<String, SummonerSpell> spells = new HashMap<>();
  
  public SummonerSpells_old(LoLVersion v) {
    this(v, false); 
  }
  public SummonerSpells_old(LoLVersion v, boolean b) {
    super(v, v.base_path, b); 
  }
  @Override
  public String getFilename() {
    return "summoner.json";
  }
  
  @Override
  public URL getURL() {
    try {
      return new URL("http://ddragon.leagueoflegends.com/cdn/"+getBaseVersion().getVersion()+"/data/"+getBaseVersion().getLanguage()+"/summoner.json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }
  @Override
  public void unloadData() {
    data = null;
    super.unloadData();
  }
  public JSONObject getSpellData() {
    if(data==null) {
      try {
        data = getJSONData().getJSONObject("data");
      } catch (JSONException ex) {
        data = null;
      }
    }
    return data;
  }
  
  public SummonerSpell getSpell(String name) throws JSONException {
    if(spells.containsKey(name))
      return spells.get(name);
    Iterator<String> keys = getSpellData().keys();
    String key;
    JSONObject json_spell;
    while( keys.hasNext() && (key = keys.next())!=null ) {
      if ((json_spell = data.getJSONObject(key)) != null) {
        if(json_spell.getString("name").equals(name)) {
          SummonerSpell spell = new SummonerSpell(null, null, json_spell);
          spells.put(name, spell);
          return spell;
        }
      }
    }
    return null;
  }

  @Override
  public String getVersion() {
    if(version_str==null) {
      JSONObject data = getJSONData();
      try {
        version_str=data.getString("version");
      }
      catch(JSONException e) {
        System.out.println(e);
        return "error"; 
      }
    }
    return version_str;
  }

  @Override
  public boolean upToDate() {
    return getVersion().equals(getBaseVersion().getVersion());
  }
  
}
