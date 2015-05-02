/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.GUI.LazyLoadedRemoteImage;
import cz.autoclient.league_of_legends.maps.GameObjectMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public abstract class GameObjectWithImage extends GameObject {
  public final LazyLoadedRemoteImage img;
  private String imgName;

  public GameObjectWithImage(GameObjectMap parent, String key, JSONObject jsonData) {
    super(parent, key, jsonData);
    img = new LazyLoadedRemoteImage(getImgPath(), getImgUrl());
  }
  public String getImgPath() {
    if(img!=null) {
      return img.path; 
    }
    else {
      return createImgPath();
    }
  }
  public abstract String createImgName();
  public abstract String createImgUrl();
  public abstract String createImgPath();
  
  public final String getImgName() {
    if(img!=null) {
      return img.file.getName();
    }
    else if(imgName!=null) {
      return imgName; 
    }
    else {
      imgName = createImgName();
    }
    return imgName;
  }
  
  public final String getImgUrl() {
    if(img!=null) {
      return img.url.toString();
    }
    else {
      return createImgUrl();//"http://ddragon.leagueoflegends.com/cdn/"+(parent.getVersion())+"/img/champion/"+getImgName();
    }
  }
}
