/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class UpdateCache extends TreeSet<UpdateInfo> implements java.io.Serializable {
  private static final long serialVersionUID = 666;
  
  public UpdateCache() {
    super(UpdateInfo.Comparator.inst); 
  }
  // Version that is being installed
  private UpdateInfo installInProgress = null;
  // Current installation step
  private Updater.InstallStep installStep = Updater.InstallStep.NOT_INSTALLING;

  public boolean isInstalling() {
    return installInProgress!=null; 
  }
  public boolean canInstall(UpdateInfo info) {
    return installInProgress==null || info==installInProgress; 
  }
  public UpdateInfo installInProgress() {
    return installInProgress;
  }
  public boolean installStepIs(Updater.InstallStep step) {
    return step==installStep;
  }
  public boolean installStepIs(Updater.InstallStep... steps) {
    for(Updater.InstallStep step:steps) {
      if(installStepIs(step))
        return true;
    }
    return false;
  }
  public void installInProgress(UpdateInfo installInProgress) {
    this.installInProgress = installInProgress;
  }
  public Updater.InstallStep installStep() {
    return installStep!=null?installStep:Updater.InstallStep.NOT_INSTALLING;
  }
  public void installStep(Updater.InstallStep installStep) {
    this.installStep = installStep;
  }
  public boolean hasChanged() {
    return changed;
  }
  public void changed(boolean changed) {
    this.changed = changed;
  }
  public void changed() {
    this.changed = true;
  }
  // Last datetime when a check for new version was performed
  private long lastCheck = 0;
  // Allows to determine whether file should be overwritten
  private transient boolean changed = false;
  // does tne necessary comparison to calculate date difference for last check
  long lastCheckDelay() {
    return new Date().getTime() - lastCheck;
  }
  // does tne necessary comparison to calculate date difference for last check
  boolean shouldCheck(long maxDiff) {
    //Logger.getLogger(this.getClass().getName()).log(Level.INFO, maxDiff+" > "+lastCheckDelay()+" => "+(maxDiff>lastCheckDelay()));
    return maxDiff<lastCheckDelay();
  }
  
  void checkedRightNow() {
    lastCheck = new Date().getTime(); 
  }
  
  
  public void saveToFile(File path) throws IOException {
    //File f = new File(path);
    if(!path.getParentFile().exists()&&!path.getParentFile().mkdirs()) {
      throw new IOException("Can't create folders in "+path.getAbsolutePath());
    }
    path.createNewFile();
    
    OutputStream file = new FileOutputStream(path);
    ObjectOutput output;
    try (OutputStream buffer = new BufferedOutputStream(file)) {
      output = new ObjectOutputStream(buffer);
      output.writeObject(this);
    }
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cache saved in "+path.getAbsolutePath());
    output.close();
  }
  public static UpdateCache loadFromFile(File path) throws IOException {    
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    
    //Get the properties out of the object
    try(ObjectInput input = new ObjectInputStream (buffer)) {
      return (UpdateCache)input.readObject();
    }
    catch(ClassNotFoundException e) {}
    finally {
      buffer.close();
      file.close();
    }
    return null;
  }
  public UpdateInfo findId(int id) {
    for(UpdateInfo i:this)
      if(i.id==id)
        return i;
    return null;
  }
  public UpdateInfo findVersion(VersionId id) {
    for(UpdateInfo i:this)
      if(i.version.equals(id))
        return i;
    return null;
  }
  /*public boolean add(UpdateInfo info) {
    for(UpdateInfo i:this) {
      if(i.id==info.id)
        return false;
    }
    super.add(info);
    return true;
  }*/
  
}
