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
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author Jakub
 */
public abstract class DataLoader {
  public enum Realm {NA, OCE, EUNE, EUW,  RU;};
  
  protected String realm_str;
  protected Realm realm;
  protected final File base_path; 
  protected final File file_path; 
  protected JSONObject data;
  protected final boolean auto_download;
  /**
   * When checking for updates, this will be filled with the downloaded file. 
   * It can be later reused to perform update.
   */
  protected JSONObject data_tmp;
  
  public DataLoader(Realm realm_, File path) {
    this(realm_, path, false);
  }
  public DataLoader(Realm realm_, File path, boolean download_if_missing) {
    realm_str = realm_.name().toLowerCase();
    realm = realm_;
    auto_download = download_if_missing;

    String filename = getFilename();
    if(path.exists()&&path.isDirectory()) {
      file_path = new File(path.getAbsolutePath()+"/"+filename+".json");
      base_path = path;
    }
    else {
      throw new IllegalArgumentException("Path is not an existing directory."); 
    }
  }
  
  
  public void forceUpdate() {
    if(data_tmp!=null)
      data=data_tmp;
    else
      data = fromURL(getURL());
    toFile(file_path);
  }
  public boolean update() {
    if(!this.upToDate()) {
      forceUpdate();
      return true;
    }
    return false;
  }
  
  public JSONObject getData() {
    if(data==null) {
      if(!file_path.exists() || !file_path.isFile()) {
        if(auto_download)
          forceUpdate();
        else
          throw new IllegalArgumentException(file_path.getName()+".json not fond in "+file_path.getParent()); 
      }
      else {
        try {
          data = fromFile(file_path);
        }
        catch(FileNotFoundException e) {
          throw new IllegalArgumentException(file_path.getName()+" in "+file_path.getParent()+" caused error:\n         "+e); 
        }
      }
    }
    return data;
  }
  
  public String getRealm() {
    return realm_str;
  }

  public void setRealm(Realm realm) {
    data = null;
    this.realm = realm;
    realm_str = realm.name().toLowerCase();
  }


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
   * Checks whether this is up to date version of file.
   * @return true if the online version matches the local one
   */
  public abstract boolean upToDate();
}
