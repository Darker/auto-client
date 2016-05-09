/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.local;

import cz.autoclient.github.html.*;
import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.ReleaseFile;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jakub
 */
public class ReleaseLocal implements Release {
  public final String tag;
  public final URL url;
  public final String description;
  public final boolean isLatest;
  public final boolean isPre;
  
  protected ArrayList<ReleaseFileLocal> downloads;
  public final RepositoryLocal parent;
  //protected final Element rootElm;
  public ReleaseLocal(RepositoryLocal parent, JSONObject elm) {
    String tag = "";
    URL url = null;
    String description = null;
    boolean isLatest = false;
    boolean isPre = false;
    try {
      tag = elm.getString("tag_name");
      description = elm.getString("body");
      //https://github.com/twbs/bootstrap/releases/tag/v1.4.0
      url = new URL(parent.getURL(), "releases/"+URLEncoder.encode(tag)+"/");
      System.out.println("Release "+tag+" at "+url.toString());
      //loadDownloads(elm);
      // The release label
      isPre = elm.getBoolean("prerelease");
      isLatest = false;
    } catch (JSONException ex) {
      Logger.getLogger(ReleaseLocal.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Invalid path  created invalid URL: "+parent.getURL()+"/releases/tag/"+tag);
    }
    this.tag = tag;
    this.description = description;
    //https://github.com/twbs/bootstrap/releases/tag/v1.4.0
    this.parent = parent;
    this.url = url;

    this.isPre = isPre;
    this.isLatest = isLatest;
    try {
      loadDownloads(elm.getJSONArray("assets"));
    } catch (JSONException ex) {
      System.out.println("Failed to parse released files.");
      ex.printStackTrace();
    }
  }
  private static URL url_or_null_java_is_retarded(URL baseUrl, String relativeUrl) {
    try {
      return new URL(baseUrl, relativeUrl); 
    }
    catch(MalformedURLException e) {
      return null;
    }
  }
  @Override
  public Repository parent() {
    return parent; 
  }

  /*public final URL url;
  public final ArrayList<ReleaseFile> files;
  public final String tag;*/
  @Override
  public String tag() {
    return this.tag;
  }

  @Override
  public URL url() {
    return this.url;
  }

  @Override
  public List<? extends ReleaseFile> downloads() {
    return downloads;
  }
  
  protected void loadDownloads(JSONArray elm) {
    ArrayList<ReleaseFileLocal> releases = new ArrayList();
    
    //System.out.println("Download elements: "+elms.size());
    for (int i=0,l=elm.length(); i<l; i++) {
      ReleaseFileLocal tmp = null;
      try {
        tmp = new ReleaseFileLocal(this, elm.getJSONObject(i));
      }
      catch(Exception e) {
        continue;
      }
      releases.add(tmp);
    }
    downloads = releases;
  }

  @Override
  public boolean isLatest() {
    return isLatest;
  }

  @Override
  public boolean isPrerelease() {
    return isPre;
  }
}
