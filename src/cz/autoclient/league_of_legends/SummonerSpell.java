/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.GUI.LazyLoadedRemoteImage;
import cz.autoclient.league_of_legends.maps.GameObjectMap;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class SummonerSpell extends GameObjectWithImage {

  public final String name;
  private JSONObject data;

  public SummonerSpell(GameObjectMap parent, String key, JSONObject jsonData) {
    super(parent, key, jsonData);
    try {
      name = getJSONData().getString("name");
    }
    catch(JSONException e) {
      throw new IllegalArgumentException("Invalid JSON Data: "+jsonData.toString());
    }
  }



  @Override
  public String createImgPath() {
    return parent.getRoot().getAbsolutePath()+"/summoner_spell/"+getImgName();
  }
  @Override
  public String createImgName() {
    try {
      // data["image"]["full"]
      return getJSONData().getJSONObject("image").getString("full");
    }
    catch(JSONException e) {
      try {
        // data["id"] + ".png"
        return  getJSONData().getJSONObject("id")+".png"; 
      }
      catch(JSONException e2) {
        return null;
      }
    }
  }
  @Override
  public String createImgUrl() {
    return "http://ddragon.leagueoflegends.com/cdn/"+(parent.getVersion())+"/img/spell/"+getImgName();
  }
  
  
  /** Getters **/
  public static final GameObjectMap.ValueGetter<SummonerSpell, String> GET_NAME = new GameObjectMap.ValueGetter<SummonerSpell, String>() {
    @Override
    public String getValue(SummonerSpell source) {
      return source.name;
    }
  };
}
