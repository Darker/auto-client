/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.local;

import cz.autoclient.github.html.*;
import cz.autoclient.github.interfaces.GitHub;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import java.io.File;

/**
 *
 * @author Jakub
 */
public class GitHubLocal implements GitHub {
  public final File root;
  public GitHubLocal(File root) {
    this.root = root;
  }
  
  @Override
  public Repository getRepository(RepositoryId id) {
    return new RepositoryLocal(id, this);
  } 
}
