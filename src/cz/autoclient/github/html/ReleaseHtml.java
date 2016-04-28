/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.html;

import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.ReleaseFile;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jakub
 */
public class ReleaseHtml implements Release {
  public final String tag;
  public final URL url;
  public final String description;
  public final boolean isLatest;
  public final boolean isPre;
  
  protected ArrayList<ReleaseFileHtml> downloads;
  public final RepositoryHtml parent;
  protected final Element rootElm;
  public ReleaseHtml(RepositoryHtml parent, Element elm) {
    this.tag = elm.select("div.release-meta span.css-truncate-target").get(0).text();
    this.description = elm.select("div.release-body div.markdown-body").get(0).html();
    //https://github.com/twbs/bootstrap/releases/tag/v1.4.0
    this.parent = parent;
    this.url = url_or_null_java_is_retarded(parent.getURL(), "releases/tag/v1.4.0"); 
    this.rootElm = elm;
    // The release label
    Element label = null;
    try {label = elm.select("span.release-label").get(0);} catch(Throwable e){}
    this.isPre = label!=null && label.hasClass("prerelease");
    this.isLatest = label!=null && label.hasClass("latest");
    
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
    if(downloads==null) {
      ArrayList<ReleaseFileHtml> releases = new ArrayList();
      Elements elms = rootElm.select("ul.release-downloads li");
      //System.out.println("Download elements: "+elms.size());
      for (Element div : elms) {
        releases.add(new ReleaseFileHtml(this, div));
      }
      downloads = releases;
    }
    return downloads;
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
