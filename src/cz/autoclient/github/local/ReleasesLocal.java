/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.local;

import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.league_of_legends.DataLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jakub
 */
public class ReleasesLocal implements Releases {
  private ArrayList<Release> releases = null;



  boolean failed = false;
  private final Object releasesMutex = new Object();
  public final URL url;
  public final File file;
  public final RepositoryLocal parent; 
  public ReleasesLocal(RepositoryLocal parent) {
    file = new File(parent.file, "releases.json");
    try {
      url = new URL(parent.getURL(), "releases.json");
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("For some reason, this failed to parse as url: "+parent.getURL().toExternalForm()+"/releses");
    }
    this.parent = parent;
  }
  public Repository parent() {
    return parent;
  }
  
  public ArrayList<Release> getReleases() {
    if(releases==null) {
      synchronized(releasesMutex) {
        if(releases==null) {
          fetch();
        }
      }
    }
    return releases;
  }
  @Override
  public boolean fetch() {
    try {
      ArrayList<Release> rels = new ArrayList();
      System.out.println("Getting releases from: "+file.getAbsolutePath());
      JSONArray releases_json = DataLoader.arrayFromFile(file);
      
      /*Iterator<String> iter = releases_json.keys();
      while (iter.hasNext()) {
          String key = iter.next();
          JSONObject value = releases_json.getJSONObject(key);
          rels.add(new ReleaseLocal(this.parent, value));
      }*/
      for (int i=0,l=releases_json.length(); i<l; i++) {
        JSONObject value = releases_json.getJSONObject(i);
        rels.add(new ReleaseLocal(this.parent, value));
      }
      
      //if(false)
      //  throw new IOException("ddd");
      releases = rels;
      return true;
    } catch (JSONException ex) {
      failed = true;
      ex.printStackTrace(System.out);
    } catch (FileNotFoundException ex) {
      failed = true;
      ex.printStackTrace(System.out);
    }
    return false;
  }
  
  @Override
  public boolean containsAll(Collection<?> c) {
    return getReleases().containsAll(c);
  }

  @Override
  public int size() {
    return getReleases().size();
  }

  public boolean isEmpty() {
    return getReleases().isEmpty();
  }

  public boolean contains(Object o) {
    return getReleases().contains(o);
  }

  public int indexOf(Object o) {
    return getReleases().indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return getReleases().lastIndexOf(o);
  }

  public Object clone() {
    return getReleases().clone();
  }

  public Object[] toArray() {
    return getReleases().toArray();
  }

  public <T> T[] toArray(T[] a) {
    return getReleases().toArray(a);
  }

  public Release get(int index) {
    return getReleases().get(index);
  }

  public Release set(int index, Release element) {
    return getReleases().set(index, element);
  }

  public boolean add(Release e) {
    return getReleases().add(e);
  }

  public void add(int index, Release element) {
    getReleases().add(index, element);
  }

  public Release remove(int index) {
    return getReleases().remove(index);
  }

  public boolean remove(Object o) {
    return getReleases().remove(o);
  }

  public void clear() {
    getReleases().clear();
  }

  public boolean addAll(Collection<? extends Release> c) {
    return getReleases().addAll(c);
  }

  public boolean addAll(int index, Collection<? extends Release> c) {
    return getReleases().addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return getReleases().removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return getReleases().retainAll(c);
  }

  public ListIterator<Release> listIterator(int index) {
    return getReleases().listIterator(index);
  }

  public ListIterator<Release> listIterator() {
    return getReleases().listIterator();
  }

  public Iterator<Release> iterator() {
    return getReleases().iterator();
  }

  public List<Release> subList(int fromIndex, int toIndex) {
    return getReleases().subList(fromIndex, toIndex);
  }

  public void forEach(Consumer<? super Release> action) {
    getReleases().forEach(action);
  }

  public Spliterator<Release> spliterator() {
    return getReleases().spliterator();
  }

  public boolean removeIf(Predicate<? super Release> filter) {
    return getReleases().removeIf(filter);
  }

  public void replaceAll(UnaryOperator<Release> operator) {
    getReleases().replaceAll(operator);
  }

  public void sort(Comparator<? super Release> c) {
    getReleases().sort(c);
  }
}
