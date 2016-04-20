/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends.maps;

import cz.autoclient.league_of_legends.Champion;
import cz.autoclient.league_of_legends.GameObject;
import cz.autoclient.league_of_legends.LoLVersion;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;

/**
 *
 * @author Jakub
 */
public class Champions extends GameObjectMap<Champion> {
  public Champions(LoLVersion v, boolean download_if_missing) {
    super(Champion.class, v, download_if_missing);
  }
  public static final ValueGetter<Champion, String> getName = new ValueGetter<Champion, String>() {
    @Override
    public String getValue(Champion source) {
      try {
        return source.getName();
      }
      catch(JSONException e) {
        throw new RuntimeException("Failed to fetch the champion name."); 
      }
    }
  };
  @Override
  public String getFilename() {
    return "champions";
  }

  @Override
  public URL getURL() {
    try {
      return new URL("http://ddragon.leagueoflegends.com/cdn/"+getBaseVersion().getVersion()+"/data/"+getBaseVersion().getLanguage()+"/champion.json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }
}
