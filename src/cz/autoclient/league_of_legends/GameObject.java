/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import cz.autoclient.league_of_legends.maps.GameObjectMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public abstract class GameObject implements java.io.Serializable {
  protected final GameObjectMap parent;
  protected transient JSONObject jsonData;
  public final String jsonKey;
  
  private String name;

  public GameObject(GameObjectMap parent, String key, JSONObject jsonData) {
    this.parent = parent;
    this.jsonData = jsonData;
    jsonKey = key;
  }
  /** Returns visible (in-game) localised name of this spell.
   * wtf. Spell? I don't understand my own javadoc!
   * 
   * @return Name as string. Can contain diacritic characters.
   * @throws JSONException when JSON is broken
   */
  public String getName() throws JSONException {
    if(name==null) {
      name = jsonData.getString("name");
    }
    return name;
  }
  public JSONObject getJSONData() throws JSONException {
    if(jsonData==null && parent!=null) {
      jsonData = parent.getJSONObject(jsonKey);
    }
    return jsonData; 
  }
  public void unloadData() {
    jsonData = null; 
  }
  public LoLVersion getBaseVersion() {
    return parent.getBaseVersion();
  }
}
