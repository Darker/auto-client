/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.json;

import cz.autoclient.github.html.RepositoryHtml;
import cz.autoclient.github.interfaces.GitHub;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 */
public class GitHubJson implements GitHub {
  @Override
  public Repository getRepository(RepositoryId id) {
    return new RepositoryJson(id);
  } 
  public static JSONObject fromURL(URL url) {
    Scanner s;
    try {
      s = new Scanner(url.openStream(), "UTF-8");
    } catch (IOException ex) {
      throw new IllegalArgumentException("JSON file not found at "+url);
    }
    s.useDelimiter("\\A");
    if(s.hasNext()) {
      String out = s.next(); 
      try { 
        return new JSONObject(out);
      } catch (JSONException ex) {
        throw new IllegalArgumentException("Invalid JSON contents at "+url+" - \n         JSON error:" +ex);
        //Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    else
      throw new IllegalArgumentException("JSON file not found at "+url);
  }
}
