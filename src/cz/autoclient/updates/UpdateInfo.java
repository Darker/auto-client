/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;


import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.ReleaseFile;
import static cz.autoclient.updates.Updater.dbgmsg;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author Jakub
 */
public class UpdateInfo implements java.io.Serializable {
  private static final long serialVersionUID = 666;
  
  public static String UPDATE_DIR = "updates";

  public UpdateInfo(VersionId version, File baseDir) {
    this.downloadLink = null;
    this.localFile = new File(baseDir, version.toString()+".zip");
    this.version = version;
    this.originalSize = 0;
    this.id = -1;
    this.prerelease = version.isBeta;
    this.valid = false;
  }
  
  
  
  public UpdateInfo(JsonObject json, File baseDir) {
    JsonArray assets = json.getJsonArray("assets");
    URL tmp = null;
    long sizetmp = -1;
    for (int i=0; i<assets.size(); i++) {
      JsonObject item = assets.getJsonObject(i);
      String contentType = item.getString("content_type");
      if("application/zip".equals(contentType) || "application/x-zip-compressed".equals(contentType)) {
        try {
          tmp = new URL(item.getString("browser_download_url"));
        } catch (MalformedURLException ex) {
          continue;
        }
        sizetmp = item.getInt("size");
        break;
      }
    }
    
    downloadLink = tmp;
    originalSize = tmp==null?-1:sizetmp;
    
    version = new VersionId(json.getString("tag_name"));
    id = json.getInt("id");
    localFile = new File(baseDir, version.toString()+".zip");

    isNew = true;
    prerelease = json.getBoolean("prerelease");
    valid = tmp!=null;
  }
  public UpdateInfo(Release gitReleaseObject, File baseDir) {
    ReleaseFile file = null;
    for(ReleaseFile f: gitReleaseObject.downloads()) {
      if(f.name().endsWith(".zip") && f.size()>1e1) {
        file = f;
        break;
      }
    }
    downloadLink = file==null?null:file.downloadUrl();
    originalSize = file==null?-1:file.size();
    
    version = file==null?null:new VersionId(file.parent().tag());
    id = -1;
    localFile = file==null?null:new File(baseDir, version.toString()+".zip");

    isNew = true;
    prerelease = file!=null && file.parent().isPrerelease();
    
    valid = file!=null && version!=null;
  }

  public boolean isUnzipped() {
    return new File(localFile.getParentFile(), version.toString()).isDirectory();
  }
  
  public final URL downloadLink;
  public final File localFile;
  public final VersionId version;
  public final long originalSize;
  public final int id;
  public final boolean prerelease;
  public final boolean valid;
  // Whether this release was just downloaded
  boolean isNew;
  // Whether this release was presented to the user
  // this will be set even if the code decides not to actually
  // present it (silent update).
  boolean seen;
  
  public boolean isDownloaded() {
    return localFile.exists(); 
  }
  public boolean validateFile() {
    if(!isDownloaded())
      return false;
    //if(localFile.length()!=originalSize) {
    //  localFile.delete();
    //  return false;
    //}
    return true;
  }
  boolean downloadFile(Updater updater) {
    if(downloadLink==null) {
      updater.dispatchEvent("download.stopped", 
          new NullPointerException("Download link is null for version "+this.version)
      );
      return false;
    }
      //throw new RuntimeException("Cannot download update, download link is null.");
    URLConnection connection = null;
    try {
      connection = downloadLink.openConnection();
    } catch (IOException ex) {
      updater.dispatchEvent("download.stopped",ex);
    }
    System.out.println("Downloading update: "+downloadLink);
    if(connection instanceof HttpURLConnection) {
      if(!download_http((HttpURLConnection) connection, updater))
        return false;
    }
    else if(connection instanceof URLConnection) {
      if(!download_http((URLConnection) connection, updater))
        return false;
    }
    else {
      updater.dispatchEvent("download.stopped",
          new IllegalStateException("Unknown class type for download conenction: "
              + connection.getClass().getName()));
      return false;
    }
 
    updater.dispatchEvent("download.finished");
    return true;
  }
  private boolean download_http(URLConnection httpConnection, Updater updater) {
    System.out.println("Downloading update: "+downloadLink);
 
    long completeFileSize = httpConnection.getContentLength();
    System.out.println("File size: "+completeFileSize);
    localFile.getParentFile().mkdirs();
    java.io.FileOutputStream fos;
    final int BUFFER_SIZE = 128;
    try {
      fos = new java.io.FileOutputStream(localFile);
    } catch (FileNotFoundException e) {
      updater.dispatchEvent("download.stopped", e);
      return false;
    }
    try (java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
        java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)
        ) { 
      updater.dispatchEvent("download.started");
      byte[] data = new byte[BUFFER_SIZE];
      long downloadedFileSize = 0;
      //process.process((double)downloadedFileSize, completeFileSize);
      updater.dispatchEvent("download.process", (double)downloadedFileSize, (double)completeFileSize);
      int x = 0;
      while ((x = in.read(data, 0, BUFFER_SIZE)) >= 0) {
        downloadedFileSize += x;
        //System.out.println("Downloaded bytes: "+downloadedFileSize);
        bout.write(data, 0, x);
        updater.dispatchEvent("download.process", (double)downloadedFileSize, (double)completeFileSize);
        //process.process(downloadedFileSize, completeFileSize);
      }
      bout.close();
      validateFile();
    }
    catch(Exception e) {
      dbgmsg(e.getMessage());
      e.printStackTrace(System.out);
      updater.dispatchEvent("download.stopped", e);
      return false;
    }
    finally {
      try { 
        fos.close();
        // nobody cares if close fails
      } catch (IOException ex) {}
    }
    return true;
  }
  public void unzip() throws ZipException {
    File destination = new File(localFile.getParentFile(), version.toString());
    destination.mkdirs();
   
    ZipFile zipFile = new ZipFile(localFile);
    zipFile.extractAll(destination.getAbsolutePath());
  }
  public void deleteFile() {
    if(localFile.isFile() && localFile.canWrite())
      localFile.delete();
  }
  /** Replaces all sourceFileiles, then schledules replacement osourceFile this jar sourceFileile. Also creates backup.
  Shut down all other threads besourceFileore calling this sourceFileunction.
   */
  public void install(Updater updater) {
    updater.dispatchEvent("install.started");
    
    File updates = localFile.getParentFile();
    File updateDirectory = new File(updates, version.toString());
    File backup = new File(updates, "backup");
    
    File myself;
    try {myself = new File(UpdateInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());}
    catch(Exception e) {
      System.out.println("Some error with getting current jar file path.");
      e.printStackTrace();
      updater.dispatchEvent("install.stopped", e);
      return;
    }
    File home = myself.getParentFile();
    /** Part 1: backup **/
    String[] ignoreList = new String[] {"updates","data","LOLResources"};
    // For now just use regex
    String ignore = "^(updates|data|LOLResources)(\\\\|/).*?$";
    
    /*ArrayList<File> originalFiles = listFileChildren(home, new FileFilter() {
      public boolean accept(File file) {
        if(file.isDirectory())
          return false;
      }
    });*/
    updater.dispatchEvent("install.status", "Getting list of files.");
    /** Part 2: copy **/
    ArrayList<File> updateFiles = listFileChildren(updateDirectory, new FileFilter() {
      @Override
      public boolean accept(File file) {
        return !file.isDirectory();
      }
    });
    
    
    int count = updateFiles.size();
    updater.dispatchEvent("install.status", "Copying "+count+" files.");
    int processed = 0;
    Process copyScript = null;
    for(File sourceFile: updateFiles) {
      updater.dispatchEvent("install.process", (double)processed++, (double)count);
      String relative = relativePath(updateDirectory, sourceFile);
      if(relative.matches(ignore))
        continue;
      
      File targetFile = new File(home,relative);
      if(targetFile.exists()) {
        File backupFile = new File(backup, relative);
        updater.dispatchEvent("install.status",
            "Backup "+targetFile.getAbsolutePath()+" to "+backup.getAbsolutePath());
        try {
          backup.mkdir();
          copyFile(targetFile, backupFile);
        } catch (IOException ex) {
          Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      if(targetFile.compareTo(myself)==0) {
        System.out.println("Cannot overwrite jar.");
        // Prepare jar file to be copied
        ScriptWithParameters script;
        try {
          script = BatchScript.fromResource("/update.bat");
          script.setParameter("AUTO_CLIENT_JAR", myself.getName());
          script.setParameter("UPDATE_NAME", this.version.toString());
          script.setParameter("HOME_DIR", home.getAbsolutePath());
          File batchFile = new File(updateDirectory, "copy.bat");
          script.saveToFile(batchFile);
          copyScript = new RunnableScript(batchFile).run();
        } catch (IOException ex) {
          System.out.println("Cannot create or run the batch file.");
        }
        continue;
      }
      try {
        updater.dispatchEvent("install.status", "Copy "+sourceFile.getAbsolutePath()+" to "+targetFile.getAbsolutePath());
        copyFile(sourceFile, targetFile);
      } catch (IOException ex) {
        updater.dispatchEvent("install.status", "ERROR: "+ex.getMessage()+" file: "+sourceFile.getAbsolutePath());
      }
    }
    if(copyScript!=null && copyScript.isAlive()) {
      updater.dispatchEvent("install.status", "Waiting for the copy script.");
      try {
        copyScript.waitFor();
      } catch (InterruptedException ex) {/*do not allow interupts*/}
    }
    updater.dispatchEvent("install.finished");
  }
  /**
   * Removes any sourceFileiles (except sourceFileor the zip sourceFileile) created by this update. This is 
 usesourceFileul isourceFile new version is discovered mid-update or the update is canceled.
   */
  public void removeGarbage() {
    
  }
  boolean isValid() {
    return downloadLink!=null; 
  }
  boolean isVersion(VersionId v) {
    return v.equals(version); 
  }
  boolean isVersion(String v) {
    return version.equals(new VersionId(v)); 
  }
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + this.version.hashCode();
    hash = 43 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof UpdateInfo)) {
      return false;
    }
    final UpdateInfo other = (UpdateInfo) obj;
    if (!Objects.equals(this.version, other.version)) {
      return false;
    }
    if (this.id != other.id) {
      return false;
    }
    return true;
  }
  String basicInfo() {
    StringBuilder s = new StringBuilder();
    s.append(version);
    if(downloadLink!=null) {
      s.append(" Link:");
      s.append(downloadLink.toString());
    }
    return s.toString();
  }
  public static class Comparator implements java.util.Comparator<UpdateInfo>, java.io.Serializable {
    protected Comparator(){}
    @Override
    public int compare(UpdateInfo o1, UpdateInfo o2) {
      int ret = o1.version!=null && o2.version!=null?o1.version.compareTo(o2.version):0;
      if(ret==0)
        return o1.id-o2.id;
      else
        return ret;
    }
    public static Comparator inst = new Comparator();
  }
  
  /** Adds all sourceFileiles in updateDirectory to given list, also returns the list.
   * @param parent parent updateDirectory
   * @param f sourceFileilter that can skip some sourceFileiles, can be null without errors
   * @param list list to add sourceFileiles into. The list must not be null.
   * @return  **/
  public static ArrayList<File> listFileChildren(File parent, FileFilter f, ArrayList<File> list) {
    File[] files = parent.listFiles();
    for(File file:files) {
      if(f!=null && !f.accept(file))
        continue;
      list.add(file);
      if(file.isDirectory())
        listFileChildren(file, f, list);
    }
    return list;
  }
  /** Returns list osourceFile all child sourceFileiles, recursively.
   * @param parent parent updateDirectory
   * @param f sourceFileilter that can skip some sourceFileiles, can be null without errors
   * @return  **/
  public static ArrayList<File> listFileChildren(File parent, FileFilter f) {
    return listFileChildren(parent, f, new ArrayList());
  }
  public static String relativePath(File parent, File child) {
    return parent.toURI().relativize(child.toURI()).getPath(); 
  }
  /** From: http://stackoversourceFilelow.com/a/115086/607407 **/
  public static void copyFile(File sourceFile, File destFile) throws IOException {
    System.out.println("copy "+sourceFile.getAbsolutePath()+" "+destFile.getAbsolutePath());
    //if(true)
    //  return;

    if(!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    }
    finally {
      if(source != null) {
          source.close();
      }
      if(destination != null) {
          destination.close();
      }
    }

  }
  public static interface FileFilter {
    boolean accept(File file); 
  }
}
