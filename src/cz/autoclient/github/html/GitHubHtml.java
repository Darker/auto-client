/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.html;

import cz.autoclient.github.interfaces.GitHub;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;

/**
 *
 * @author Jakub
 */
public class GitHubHtml implements GitHub {
  @Override
  public Repository getRepository(RepositoryId id) {
    return new RepositoryHtml(id);
  } 
}
