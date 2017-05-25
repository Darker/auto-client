/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.html;

import cz.autoclient.github.exceptions.DataException;
import cz.autoclient.github.exceptions.DataParseException;
import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.ReleaseFile;
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
  //protected final Element rootElm;

  public ReleaseHtml(RepositoryHtml parent, Element elm) {
    this.parent = parent;
    this.tag = getElementTextValue("div.release-meta span.css-truncate-target", elm);
    String desc = "";
    try {
      desc = getElementTextValue("div.release-body div.markdown-body", elm, (element)->{return element.html();});
    }
    catch(DataException e) {}
    this.description = desc;

    this.url = url_or_null_java_is_retarded(parent.getURL(), "releases/tag/"+tag);
    //https://github.com/twbs/bootstrap/releases/tag/v1.4.0

    
    loadDownloads(elm);
    // The release label
    Element label = null;
    try {label = elm.select("span.release-label").get(0);} catch(Exception e){}
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
  public String getElementTextValue(String selector, Element topElm) {
    return getElementTextValue(selector, topElm, null, selector);
  }
  public String getElementTextValue(String selector, Element topElm, GetStringFromNode handler) {
    return getElementTextValue(selector, topElm, handler, selector);
  }
  public String getElementTextValue(String selector, Element topElm, GetStringFromNode handler, String nameForErrors) {
    Element elm = null;
    try {
      elm = topElm.select(selector).get(0);
      if(elm==null)
        throw new NullPointerException("No elm");
    }
    catch(IndexOutOfBoundsException | NullPointerException e) {
      throw new DataParseException("Cannot get "+nameForErrors+"!", parent.url, "");
    }
    try {
      return handler!=null?handler.get(elm):elm.text();
    }
    catch(IndexOutOfBoundsException | NullPointerException e) {
      throw new DataParseException("Cannot get text value for "+nameForErrors+"!", parent.url, "");
    }
  }
  // Lambda to get the strings from node
  public static interface GetStringFromNode {
    public String get(Element elm);
  }
  public static class GetStringFromNodeText implements GetStringFromNode {
    @Override
    public String get(Element elm) {
      return elm.text();
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
  
  protected void loadDownloads(Element elm) {
    ArrayList<ReleaseFileHtml> releases = new ArrayList();
    Elements elms = elm.select("ul.release-downloads li");
    //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Download elements: "+elms.size());
    for (Element div : elms) {
      ReleaseFileHtml tmp = null;
      try {
        tmp = new ReleaseFileHtml(this, div);
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
