/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.html;

import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class RepositoryHtml implements Repository {
  public final URL url;
  public RepositoryHtml(RepositoryId id) {
    if(id==null)
      throw new IllegalArgumentException("Given RepositoryId was null.");
    try { 
      url = new URL("https://github.com/"+id+"/");
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Invalid RepositoryId "+id+" created invalid URL.");
    }
  }
  @Override
  public Releases releases() {
    return new ReleasesHtml(this);
  }

  @Override
  public URL getURL() {
    return url;
  }
}
