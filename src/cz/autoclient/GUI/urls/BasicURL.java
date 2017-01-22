/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.urls;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Jakub
 */
public class BasicURL implements AcURL {
  public final URL url;

  public BasicURL(URL url) {
    this.url = url;
  }
  public BasicURL(String url) {
    this.url = urlOrNull(url);
  }

  public static URL urlOrNull(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException ex) {
      return null;
    }
  }
  
  @Override
  public URL getURL() {
    return this.url;
  }

}
