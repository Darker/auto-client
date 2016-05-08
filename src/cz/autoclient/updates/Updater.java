/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.event.EventCallback;
import cz.autoclient.event.EventEmitter;
import cz.autoclient.github.constructs.BasicRepositoryId;
import cz.autoclient.github.html.GitHubHtml;
import cz.autoclient.github.interfaces.GitHub;
import cz.autoclient.github.interfaces.Release;
import cz.autoclient.github.interfaces.Releases;
import cz.autoclient.github.interfaces.Repository;
import cz.autoclient.github.interfaces.RepositoryId;
import cz.autoclient.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author Jakub
 */
public class Updater implements EventEmitter {
  private final RepositoryId repository;
  public final VersionId version;
  private UpdateCache updates;
  
  protected Settings settings;

  public void setSettings(Settings settings) {
    this.settings = settings;
  }
 
  private File cacheDir;
  private File cacheMainFile;
  //UpdateInfoListener updateListener = UpdateInfoListener.Empty.getInstance();
  // This is minimum interval, not interval how often does updater check
  public final int checkInterval = 60*60*1000/100;//24*60*60*1000;
  private ExecutorService executor;
  private final UpdaterThreadFactory threadFactory = new UpdaterThreadFactory();
  
  // remember that update files have just been copied
  private boolean justInstalled = false;

  private HashMap<String, List<EventCallback>> events = new HashMap();
  @Override
  public Map<String, List<EventCallback>> getListeners() {
    return events;
  }
  
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
      dbgmsg("Update action: "+currentAction+"->"+a.name());
      currentAction = a; 
    }
    //updateListener.actionChanged(a);
    dispatchEvent("action_changed", a);
  }
 
  public Updater(RepositoryId repository, VersionId version, File cacheDir) {
    this.repository = repository;
    this.version = version;
    this.cacheDir = cacheDir;

    cacheMainFile = new File(cacheDir, "cache.bin");
  } 
  public Updater(String repository, VersionId version, File cacheDir) {
    this(new BasicRepositoryId(repository), version, cacheDir);
  }
  public Updater(RepositoryId repository, String version, File cacheDir) {
    this(repository, new VersionId(version), cacheDir);
  }
  public Updater(String repository, String version, File cacheDir) {
    this(new BasicRepositoryId(repository), new VersionId(version), cacheDir);
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
            //dbgmsg("Updates loaded from file.");
            //new Exception("ble").printStackTrace();
          }
          catch(Exception e) {
            dbgmsg("UpdateCache deserialization failed:");
            e.printStackTrace();
            updates = new UpdateCache(); 
          }
        }
      } 
      else {
        dbgmsg("UpdateCache not in a file.");
        updates = new UpdateCache(); 
      }
    }
    return updates;
  }
  public static void dbgmsg(String msg) {
    System.out.println("[UPDATES] " + msg);
  }
  /**
   * Call when the filtering on used updates is changed
   */
  public void updateFiltersChanged() {
    checkForUpdates();
  }
  
  //public void setUpdateListener(UpdateInfoListener i) {
  //  updateListener = i;
  //}
  public synchronized void saveAll() {
    try {
      updates.saveToFile(cacheMainFile);
    } catch (IOException ex) {
      dbgmsg("Failed to save cache: "+ex.getMessage());
      ex.printStackTrace();
    }
  }
  public void checkForUpdates() {
    checkForUpdates(false);
  }
  /**
   * 
   * @param silent currently has no effect. In future surpresses notifications.
   */
  public void checkForUpdates(final boolean silent) {
    if(runInExecutorIfNeeded(()->checkForUpdates(silent)))
      return;
    setCurrentActionAndThenIdleDelayed(Action.CHECKING);
    updates = getUpdates();
    dbgmsg("Checking for updates...");
    
    boolean ignoreBetas = true;
    if(settings!=null)
      ignoreBetas = settings.getBoolean(
          Setnames.UPDATES_IGNORE_BETAS.name, 
          (Boolean)Setnames.UPDATES_IGNORE_BETAS.default_val);
    
    
    if(updates.shouldCheck(checkInterval))
    {
      dbgmsg("Connecting to GitHub.");
      GitHub git = new GitHubHtml();
      Repository repo = git.getRepository(repository);
      Releases releases = repo.releases();
      dbgmsg("Downloading releases from "+repo.getURL()+"...");
      if(releases.fetch()) {
        dbgmsg("Downloaded.");
        for(Release r: releases) {
          VersionId id = new VersionId(r.tag());
  
          if(updates.findVersion(id)==null) {
            dbgmsg("Adding release: "+r.tag());
            UpdateInfo info = new UpdateInfo(r, cacheDir);
            if(info.valid)
              updates.add(info);
            else
              dbgmsg("  Release "+id+" skipped because it doesn't have expected download file.");
          }
          else {
            dbgmsg("Release already exists: "+r.tag());
          }
        }
      }
      else {
        dbgmsg("Cannot fetch releases.");
      }
      dbgmsg("Connected...");
      updates.checkedRightNow();
    }
    else {
      dbgmsg("No need to download updates, everything is cached."); 
    }
    // Debug
    /*VersionId debug = new VersionId("v4.8-beta");
    if(updates.findVersion(debug)==null) {
       UpdateInfo info = new UpdateInfo(debug, cacheDir);
       updates.add(info);
    }*/
    
    dbgmsg("Available releases: "+updates.size());
    UpdateInfo newest = null;
    for(UpdateInfo info : updates) {
      dbgmsg("  "+info.basicInfo());
      boolean newer = info.version.compareTo(version)>0;

      dbgmsg("     - this is "+(newer?"newer":"older")+" compared to the current version.");
      if(newer && (newest==null || newest.version.compareTo(info.version)<0)) {
        if(ignoreBetas && (info.prerelease || info.version.isBeta )) {
          dbgmsg("     - Ignoring "+info+" because it's beta or prerelease."); 
        }
        else
          newest = info;
      }
    }
    
    if(newest!=null) {
      updates.installInProgress(newest);
      dbgmsg("Latest release that can be installed: "+newest.version);
      if(!newest.validateFile()) {
        dbgmsg("Update can be downloaded.");
  
        updates.installStep(InstallStep.CAN_DOWNLOAD);
        //if(!newest.seen) {
          //updateListener.updateAvailable(newest);
        dispatchEvent("update_available", newest);
        //}
      }
      else {
        dbgmsg("Latest update already downloaded and ready to install.");
        updates.installStep(InstallStep.CAN_UNPACK);
        dispatchEvent("download.finished");
        //updateListener.download.finished();
        /*newest.unzip();
        newest.install(new Progress.Empty() {
          public void status(String s) {
            dbgmsg("Progress status: "+s);
          }
        });*/
      }
    }
    else {
      //updateListener.upToDate(version);
      dispatchEvent("up_to_date", version);
      updates.installStep(InstallStep.NOT_INSTALLING);
    }
  }
  public void downloadUpdate() {
    if(runInExecutorIfNeeded(()->downloadUpdate()))
      return;
    setCurrentActionAndThenIdleDelayed(Action.DOWNLOADING);
    if(getUpdates().installStepIs(InstallStep.CAN_DOWNLOAD)) {
      if(!updates.installInProgress().downloadFile(this)) {
        updates.installInProgress().localFile.delete();
        return; 
      }
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
    if(getUpdates().installStepIs(InstallStep.CAN_UNPACK)) {
      try {
        updates.installInProgress().unzip();
        updates.installStep(InstallStep.CAN_COPY_FILES);
      } catch (ZipException ex) {
        updates.installInProgress().deleteFile();
        updates.installStep(InstallStep.CAN_DOWNLOAD);
        dispatchEvent("install.stopped", ex);
      }
    }
    if(updates.installStepIs(InstallStep.CAN_COPY_FILES)) {
      updates.installInProgress().install(this);
    }
  }
  public void terminateAfterAction() {
    getExecutor().submit(()->terminate());
  }
  private void terminate() {
    getExecutor().shutdown();
  }
  public void checkCurrentState(final Runnable whenDone) {
    if(runInExecutorIfNeeded(()->checkCurrentState(whenDone)))
      return;
    //dbgmsg("Latest release that can be installed: "+info.version);
    getUpdates();
    if(updates.installStep()!=InstallStep.NOT_INSTALLING) {
      UpdateInfo info = updates.installInProgress();
      if(!info.validateFile()) {
        dbgmsg("checkCurrentState: Update can be downloaded.");
        updates.installStep(InstallStep.CAN_DOWNLOAD);
      }
      else {
        dbgmsg("checkCurrentState: Latest update already downloaded.");
        if(!info.isUnzipped())
          updates.installStep(InstallStep.CAN_UNPACK);
        else
          updates.installStep(InstallStep.CAN_COPY_FILES);
      }
    }
    else {
      dbgmsg("checkCurrentState: Not installing, unknown state."); 
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
        dbgmsg("Exception during updates: "+e.getMessage());
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
      //dbgmsg("Deferred callback to event queue.");
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
