/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author Jakub
 */
public abstract class DataLoader implements java.io.Serializable {

  

  protected File base_path = null; 
  protected File file_path = null; 
  protected File cache_path = null;
  
  private transient JSONObject data;
  protected final boolean auto_download;
  
  public LoLVersion baseVersion = null;
  /**
   * When checking for updates, this will be filled with the downloaded file. 
   * It can be later reused to perform update.
   */
  protected JSONObject data_tmp;
  
  public DataLoader(LoLVersion v, File path) {
    this(v, path, false);
  }
  public DataLoader(LoLVersion v, File path, boolean download_if_missing) {
    baseVersion = v;
    
    auto_download = download_if_missing;
    if(path==null)
      throw new IllegalArgumentException("Cannot operate without cache path!");

    path.mkdirs();
    if(path.exists() && path.isDirectory()) {
      base_path = path;
    }
    else {
      throw new IllegalArgumentException("Path is not an existing directory."); 
    }
  }
  public void setCachePath(File path) {
    cache_path = path; 
  }

  public File getCache_path() {
    return cache_path;
  }
  /*public static <T extends DataLoader> T loadFromCache(File location, Class<T> className, Object... arguments) {
    if(location.isFile()) {
      
      
      
      
    }
    
  }*/
  
  
  public void forceUpdate() {
    if(data_tmp!=null) {
      data=data_tmp;
      data_tmp = null;
    }
    else
      data = fromURL(getURL());
    toFile(getFile());
  }
  public boolean update() {
    if(!this.upToDate()) {
      forceUpdate();
      return true;
    }
    return false;
  }
  
  public JSONObject getJSONData() {
    //Initialise the object path
    getFile();
    if(data==null) {
      if(!file_path.exists() || !file_path.isFile()) {
        if(auto_download)
          forceUpdate();
        else
          throw new IllegalArgumentException(file_path.getName()+" not fond in "+file_path.getParent()); 
      }
      else {
        try {
          data = fromFile(file_path);
          System.out.println("Loading "+this.getClass().getName()+" from "+file_path);
        }
        catch(FileNotFoundException e) {
          throw new IllegalArgumentException(file_path.getName()+" in "+file_path.getParent()+" caused error:\n         "+e); 
        }
      }
    }
    return data;
  }
  /** If everything has been extrated from the JSON data, unset the object as it's 
   *  consuming real bunch of memory.
   * 
   */
  public void unloadData() {
    data = null;   
  }




  /**
   * Returns the top working directory of the LoL data API.
   * @return 
   */
  public File getRoot() {
    return base_path; 
  }
  
  public static JSONObject fromFile(File file) throws FileNotFoundException {
    char[] buff = new char[(int)file.length()];
    try {
    (new FileReader(file)).read(buff);
    }
    catch(IOException e) {
      throw new FileNotFoundException("Invalid file.");
    }
    
    String result = new StringBuilder().append(buff).toString();
    //System.out.println("JSON from file:\n"+result);
    try { 
      return new JSONObject(result);
    } catch (JSONException ex) {
      throw new FileNotFoundException("Invalid file contents - \n         JSON error:" +ex);
      //Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  public static JSONArray arrayFromFile(File file) throws FileNotFoundException {
    String data = stringFromFile(file);
    //System.out.println("JSON from file:\n"+result);
    try { 
      return new JSONArray(data);
    } catch (JSONException ex) {
      throw new FileNotFoundException("Invalid file contents - \n         JSON error:" +ex);
      //Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static String stringFromFile(File file) throws FileNotFoundException {
    char[] buff = new char[(int)file.length()];
    try {
    (new FileReader(file)).read(buff);
    }
    catch(IOException e) {
      throw new FileNotFoundException("Invalid file.");
    }
    
    return new StringBuilder().append(buff).toString();
  }
  public void toFile(File file) {
    try {
      Writer w = new FileWriter(file);
      data.write(w);
      w.close();
    } catch (JSONException|IOException ex) {
      throw new Error("Couldn't write to file: "+ex);
    }
  }
  public static JSONObject fromURL(URL url) {
    Scanner s;
    try {
      s = new Scanner(url.openStream(), "UTF-8");
    } catch (IOException ex) {
      throw new IllegalArgumentException("JSON file not found at "+url);
    }
    s.useDelimiter("\\A");
    if(s.hasNext()) {
      String out = s.next(); 
      try { 
        return new JSONObject(out);
      } catch (JSONException ex) {
        throw new IllegalArgumentException("Invalid JSON contents at "+url+" - \n         JSON error:" +ex);
        //Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    else
      throw new IllegalArgumentException("JSON file not found at "+url);
  }
  public File getFile() {
    if(file_path == null) {
      String filename = getFilename();
      file_path = new File(base_path.getAbsolutePath()+"/"+filename+".json");
    }
    return file_path;
  }
  /**
   * Get name of the JSON file associated wth this class without the extension (allways .json)
   * @return String filename without extension.
   */
  public abstract String getFilename();
  /**
   * Get the league of legends URL of the JSON file.
   * @return URL
   */
  public abstract URL getURL();
  /**
   * Get the version string of this JSON file.
   * @return X.X.X version format
   */
  public abstract String getVersion();
  
  /**
   *  Get the expected version of this file.
   * @return 
   */
  public LoLVersion getBaseVersion() {
    return baseVersion;
  }

  public void setBaseVersion(LoLVersion baseVersion) {
    this.baseVersion = baseVersion;
  }
  


  public boolean upToDate() {
    return LoLVersion.isLatestVersion(getVersion());
  }
  
  /**
   * Loops through given object and returns name properties for every entry.
   * By name properties I mean {x:{name:something}, y: ...}, not the json keys
   * @param o 
   * @return array of names using for this entries in game
   */
  public static ArrayList<String> listJSONNames(JSONObject o) {
    ArrayList<String> names = new ArrayList<>();
    String[] ids = JSONObject.getNames(o);
    //System.out.println("Fetching "+ids.length+" champion names.");
    for(String id: ids) {
      try {
        names.add(o.getJSONObject(id).getString("name"));
      } catch (JSONException ex) {
        //System.err.println("Error fetching champion "+id+" name: "+ex);
        //Ignore and continue
      }
    }  
    return names;
  }
}
