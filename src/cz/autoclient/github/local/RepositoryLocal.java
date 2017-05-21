/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.local;

import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Jakub
 */
public class RepositoryLocal implements Repository {
  public final URL url;
  public final GitHubLocal parent;
  public final File file;
  
  public RepositoryLocal(RepositoryId id, GitHubLocal parent) {
    if(id==null)
      throw new IllegalArgumentException("Given RepositoryId was null.");
    file = new File(parent.root, id.getUsername()+"/"+id.getName()+"/");
    try { 
      url = file.toURI().toURL();
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Invalid RepositoryId "+id+" created invalid URL.");
    }
    
    this.parent = parent;
  }
  @Override
  public Releases releases() {
    return new ReleasesLocal(this);
  }

  @Override
  public URL getURL() {
    return url;
  }
}
