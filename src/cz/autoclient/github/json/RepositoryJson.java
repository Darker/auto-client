/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.json;

import cz.autoclient.github.html.ReleasesHtml;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Jakub
 */
class RepositoryJson implements Repository {
  public final URL url;
  public RepositoryJson(RepositoryId id) {
    if(id==null)
      throw new IllegalArgumentException("Given RepositoryId was null.");
    try { 
      url = new URL("https://api.github.com/repos/"+id+"/");
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Invalid RepositoryId "+id+" created invalid URL.");
    }
  }
  @Override
  public Releases releases() {
    return new ReleasesJson(this);
  }

  @Override
  public URL getURL() {
    return url;
  }
}
