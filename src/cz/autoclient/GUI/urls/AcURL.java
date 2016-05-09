/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.urls;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Jakub
 */
public interface AcURL {
  /** Returns URL if available, otherwise null
   * @return  URL object if available.**/
  public URL getURL();
  /** Returns URI if available, otherwise null
   * @return URI
   * @throws java.net.URISyntaxException when there's no valid URI available **/
  public default URI toURI() throws URISyntaxException {
    URL u = getURL();
    if(u!=null)
      return u.toURI();
    else
      throw new URISyntaxException("", "Cannot convert null to URI.", 0);    
  }
}
