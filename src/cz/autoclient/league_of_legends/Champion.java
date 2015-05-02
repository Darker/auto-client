/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.GUI.LazyLoadedRemoteImage;
import cz.autoclient.league_of_legends.maps.Champions;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class Champion extends GameObjectWithImage {

  private Champions_old pool;
  
  public final LazyLoadedRemoteImage img;
  
  public final String name;
  public Champion(Champions_old pool, JSONObject src) {
    super(null, null, src);
    this.pool = pool;
    String name_tmp = "undefined";
    try {
      name_tmp = src.getString("name");
    } catch (JSONException ex) {}
    name = name_tmp;
    
    img = new LazyLoadedRemoteImage(getImgPath(), getImgUrl());
  }
  
  public Champion(Champions pool, String key, JSONObject src) {
    super(pool, key, src);

    String name_tmp = "undefined";
    try {
      name_tmp = src.getString("name");
    } catch (JSONException ex) {
      System.out.println("JSON data: "+src.toString());
      throw new IllegalArgumentException("Invalid json data. Got error when fetching name: "+ex);
    }
    name = name_tmp;
    
    img = new LazyLoadedRemoteImage(getImgPath(), getImgUrl());
  }
  
  
  @Override
  public String createImgPath() {
    if(pool!=null)
      return pool.getRoot().getAbsolutePath()+"/champion_avatar/"+getImgName();
    else {
      return parent.getRoot().getAbsolutePath()+"/champion_avatar/"+getImgName();
    }
  }

  /**
   * Generates name from ["image"]["full"] or ["name"] if the first is not available.
   * @return
   */
  @Override
  public String createImgName() {
    try {
      return getJSONData().getJSONObject("image").getString("full");
    }
    catch(JSONException e) {
      return  name.replaceAll("[^a-zA-Z]", "")+".png"; 
    }
  }
  @Override
  public String createImgUrl() {
    return "http://ddragon.leagueoflegends.com/cdn/"+(pool!=null?pool.getVersion():parent.getVersion())+"/img/champion/"+getImgName();
  }
}
