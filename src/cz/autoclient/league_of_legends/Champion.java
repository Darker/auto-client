/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.GUI.LazyLoadedRemoteImage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class Champion {
  private final JSONObject data;
  private final Champions pool;
  public final LazyLoadedRemoteImage img;
  public final String name;
  public Champion(Champions pool, JSONObject src) {
    this.data = src; 
    this.pool = pool;
    String name_tmp = "undefined";
    try {
      name_tmp = src.getString("name");
    } catch (JSONException ex) {}
    name = name_tmp;
    
    img = new LazyLoadedRemoteImage(getImgPath(), getImgUrl());
  }
  public String getImgPath() {
    if(img!=null) {
      return img.path; 
    }
    else {
      return pool.getRoot().getAbsolutePath()+"/champion_avatar/"+getImgName();
    }
  }
  public String getImgName() {
    if(img!=null) {
      return img.file.getName();
    }
    else {
      try {
        return data.getJSONObject("image").getString("full");
      } catch (JSONException ex) {
        return null;
      }
    }
  }
  public String getImgUrl() {
    if(img!=null) {
      return img.url.toString();
    }
    else { 
      return "http://ddragon.leagueoflegends.com/cdn/"+pool.getVersion()+"/img/champion/"+name+".png";
    }
  }
}
