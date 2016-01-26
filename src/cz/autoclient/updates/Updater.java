/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;

/**
 *
 * @author Jakub
 */
public class Updater {
  private final Coordinates.Simple repository;
  public final VersionId version;
  private UpdateCache updates;
 
  private File cacheDir;
  private File cacheMainFile;
  UpdateInfoListener updateListener = UpdateInfoListener.Empty.getInstance();
  public int checkInterval = 24*60*60*1000;
  private ExecutorService executor;
  private final UpdaterThreadFactory threadFactory = new UpdaterThreadFactory();
  
  public static enum InstallStep {
    NOT_INSTALLING,
    CANCELED,
    CAN_DOWNLOAD,
    CAN_UNPACK,
    CAN_COPY_FILES,
    FINAL_STEP
  }
  public static enum Action {
    IDLE,
    CHECKING,
    DOWNLOADING,
    UNPACKING,
    COPYING
  }
  private Action currentAction = Action.IDLE;
  public Action currentAction() {
    synchronized(currentAction) {
      return currentAction;
    }
  }
  private void setCurrentAction(Action a) {
    if(Thread.currentThread() != threadFactory.updateThread) 
      throw new RuntimeException("Calling setCurrentAction from wrong thread!");
    synchronized(currentAction) {
      System.out.println("Update action: "+currentAction+"->"+a.name());
      currentAction = a; 
    }
    updateListener.actionChanged(a);
  }
  
  public Updater(Coordinates.Simple repository, VersionId version, File cacheDir) {
    this.repository = repository;
    this.version = version;
    this.cacheDir = cacheDir;

    cacheMainFile = new File(cacheDir, "cache.bin");
  } 
  public Updater(String repository, VersionId version, File cacheDir) {
    this(new Coordinates.Simple(repository), version, cacheDir);
  }
  public Updater(Coordinates.Simple repository, String version, File cacheDir) {
    this(repository, new VersionId(version), cacheDir);
  }
  public Updater(String repository, String version, File cacheDir) {
    this(new Coordinates.Simple(repository), new VersionId(version), cacheDir);
  }
  synchronized ExecutorService getExecutor() {
    if(executor == null) {
      executor = Executors.newSingleThreadExecutor(threadFactory);
    }
    return executor; 
  }
  public UpdateCache getUpdates() {
    if(updates==null) {
      if(cacheMainFile.isFile()) {
        synchronized(cacheMainFile) {
          try {
            updates = UpdateCache.loadFromFile(cacheMainFile);
            //System.out.println("Updates loaded from file.");
            //new Exception("ble").printStackTrace();
          }
          catch(Exception e) {
            System.out.println("UpdateCache deserialization failed:");
            e.printStackTrace();
            updates = new UpdateCache(); 
          }
        }
      } 
      else {
        System.out.println("UpdateCache not in a file.");
        updates = new UpdateCache(); 
      }
    }
    return updates;
  }
  public void setUpdateListener(UpdateInfoListener i) {
    updateListener = i;
  }
  public synchronized void saveAll() {
    try {
      updates.saveToFile(cacheMainFile);
    } catch (IOException ex) {
      System.out.println("Failed to save cache: "+ex.getMessage());
      ex.printStackTrace();
    }
  }

  public void checkForUpdates() {
    if(runInExecutorIfNeeded(()->checkForUpdates()))
      return;
    setCurrentActionAndThenIdleDelayed(Action.CHECKING);
    updates = getUpdates();
    System.out.println("Checking for updates...");
    if(updates.shouldCheck(checkInterval))
    {
      System.out.println("Connecting to GitHub.");
      Repo repo;
      Releases releases;
      try {
        repo = new RtGithub().repos().get(repository);
        releases = repo.releases();
      }
      catch(Throwable bullshit) {
        System.out.println("Something failed!");
        bullshit.printStackTrace();
        throw bullshit;
      }
      System.out.println("Connected...");
      for(Release r: releases.iterate()) {
        try {
          JsonObject data = r.json();
          UpdateInfo info;
          if((info=updates.findId(data.getInt("id")))!=null) {
            System.out.println("Update "+info.version+" already exists.");
          }
          else {
            updates.add(info = new UpdateInfo(data, cacheDir));
            System.out.println("New update: "+info.version);
          }
        } catch (IOException ex) {
          Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      updates.checkedRightNow();
    }
    else {
      System.out.println("No need to check for updates, everything is cached."); 
    }
    System.out.println("Available releases: "+updates.size());
    UpdateInfo newest = null;
    for(UpdateInfo info : updates) {
      System.out.println("  "+info.basicInfo());
      System.out.println("     - this is "+(info.version.compareTo(version)>0?"newer":"older")+" compared to the current version.");
      if(info.version.compareTo(version)>0 && (newest==null ||newest.version.compareTo(info.version)<0)) {
        newest = info;
      }
    }
    if(newest!=null) {
      updates.installInProgress(newest);
      System.out.println("Latest release that can be installed: "+newest.version);
      if(!newest.validateFile()) {
        System.out.println("Update can be downloaded.");
        updates.installStep(InstallStep.CAN_DOWNLOAD);
        if(!newest.seen) {
           updateListener.updateAvailable(newest);
        }
      }
      else {
        System.out.println("Latest update already downloaded and ready to install.");
        updates.installStep(InstallStep.CAN_UNPACK);
        updateListener.download.finished();
        /*newest.unzip();
        newest.install(new Progress.Empty() {
          public void status(String s) {
            System.out.println("Progress status: "+s);
          }
        });*/
      }
    }
  }
  public void downloadUpdate() {
    if(runInExecutorIfNeeded(()->downloadUpdate()))
      return;
    setCurrentActionAndThenIdleDelayed(Action.DOWNLOADING);
    if(getUpdates().installStepIs(InstallStep.CAN_DOWNLOAD)) {
      updates.installInProgress().downloadFile(new UpdaterProgress(this, updateListener.download));
    }
    File result = updates.installInProgress().localFile;
    if(result!=null && result.isFile() && result.length()>0) {
      updates.installStep(InstallStep.CAN_UNPACK);
    }
  }
  public void installUpdate() {
    if(runInExecutorIfNeeded(()->installUpdate()))
      return;
    setCurrentActionAndThenIdleDelayed(Action.COPYING);
    if(updates.installStepIs(InstallStep.CAN_UNPACK)) {
      updates.installInProgress().unzip();
      updates.installStep(InstallStep.CAN_COPY_FILES);
    }
    if(updates.installStepIs(InstallStep.CAN_COPY_FILES)) {
      updates.installInProgress().install(updateListener.install);
    }
  }
  public void checkCurrentState(final Runnable whenDone) {
    if(runInExecutorIfNeeded(()->checkCurrentState(whenDone)))
      return;
    //System.out.println("Latest release that can be installed: "+info.version);
    getUpdates();
    if(updates.installStep()!=InstallStep.NOT_INSTALLING) {
      UpdateInfo info = updates.installInProgress();
      if(!info.validateFile()) {
        System.out.println("checkCurrentState: Update can be downloaded.");
        updates.installStep(InstallStep.CAN_DOWNLOAD);
      }
      else {
        System.out.println("checkCurrentState: Latest update already downloaded.");
        if(!info.isUnzipped())
          updates.installStep(InstallStep.CAN_UNPACK);
        else
          updates.installStep(InstallStep.CAN_COPY_FILES);
      }
    }
    else {
      System.out.println("checkCurrentState: Not installing, unknown state."); 
    }
    if(whenDone!=null)
      whenDone.run();
  }
  
  public void waitFor() throws InterruptedException {
    threadFactory.updateThread.join();
  }
  /**
   * Do the next action required to get an update. That is:
   *  - check for update
   *  - download update
   *  - unpack update
   *  - install update
   * @return The step that is currently being executed.
   */
  public Action takeNextAction() {
    if(currentAction()!=Action.IDLE)
      return currentAction();
    switch(getUpdates().installStep()) {
      case CAN_DOWNLOAD: {
        downloadUpdate();
        return Action.DOWNLOADING; 
      }
      case NOT_INSTALLING: {
        checkForUpdates();
        return Action.CHECKING;
      }
    }
    return Action.IDLE;
  }
  /**
   * Wraps other runnable and catches any errors 
   */
  private class CatchRunnableErrors implements Runnable {
    private final Runnable runnable;
    public CatchRunnableErrors(Runnable r) {
      runnable = r;
      if(r==null)
        throw new NullPointerException("Inner runnable is null!");
    }
    @Override
    public void run() {
      try {
        runnable.run();
      }
      catch(Exception e) {
        System.out.println("Exception during updates: "+e.getMessage());
        e.printStackTrace();
      }
    }
    
  }
  /**
   * Provides compatibility between calls from executor thread pool and other threads. 
   * No matter the thread from which you call this method, the callback will be executed
   * within the loop. Proper events will be raised in the loop thread as well, so
   * mind to use SwingUtilities.invokeLater if event involves GUI operations.
   * @param callback
   * @return true if the callback was queued, false if it's already in that thread 
   */
  protected boolean runInExecutorIfNeeded(Runnable callback) {
    if(Thread.currentThread() != threadFactory.updateThread) {
      getExecutor().submit(new CatchRunnableErrors(callback));
      System.out.println("Deferred callback to event queue.");
      return true;
    }
    return false;
  }
  /**
   * Will set next action to selected value asynchronously using event thread.
   * This is good to ensure action will be updated even if one of them throws error.
   */
  protected void setCurrentActionDelayed(final Action a) {
    getExecutor().submit(()->{setCurrentAction(a);});
  }
  /**
   * Will set next action to idle asynchronously using event thread.
   * This is good to ensure action will idle when previous action throws error.
   */
  protected void setCurrentActionIdleDelayed() {
    getExecutor().submit(()->{setCurrentAction(Action.IDLE);});
  }
  /**
   * Sets current action (that must run in event loop) to selected val.
   * The very next thing in event loop will set that action back to idle.
   */
  protected void setCurrentActionAndThenIdleDelayed(final Action a) {
    setCurrentAction(a);
    getExecutor().submit(()->{setCurrentAction(Action.IDLE);});
  }
  private static class UpdaterThreadFactory implements ThreadFactory {
    public Thread updateThread;
    @Override
    synchronized public Thread newThread(Runnable r) {
      if(updateThread==null) {
        updateThread = new Thread(r, "Updater"); 
        updateThread.setDaemon(true);
        return updateThread;
      }
      else {
        throw new IllegalStateException("One thread has already been created for the updater, that's just enough."); 
      }
    }
  }
  public static class UpdaterProgress extends Progress.Empty {
    public UpdaterProgress(Updater updater, Progress userCallback) {
      this.updater = updater;
      this.userCallback = userCallback!=null?userCallback:Progress.Empty.getInstance();
      info = updater.getUpdates().installInProgress();
    }
    public final Updater updater;
    public final UpdateInfo info;
    public final Progress userCallback;
    
    @Override
    public void process(double current, double max) {
      userCallback.process(current, max);
    };
    @Override
    public void started(){
      userCallback.started();
    };
    // Stopped with error
    @Override
    public void stopped(Throwable error){
      userCallback.stopped(error);
    };
    // Stopped with success
    @Override
    public void finished(){
      //updater.updates.installStep(InstallStep.CAN_UNPACK);
      userCallback.finished();
    };
  }
  public static class UpdaterProgressDownload extends UpdaterProgress {

    public UpdaterProgressDownload(Updater updater, Progress userCallback) {
      super(updater, userCallback);
    }
    // Stopped with success
    @Override
    public void finished(){
      updater.updates.installStep(InstallStep.CAN_UNPACK);
      super.finished();
    };
  }
  public static class UpdaterProgressInstall extends UpdaterProgress {
    public UpdaterProgressInstall(Updater updater, Progress userCallback) {
      super(updater, userCallback);
    }
    // Stopped with success
    @Override
    public void finished(){
      updater.updates.installStep(InstallStep.FINAL_STEP);
      super.finished();
      
    };
  }
}
