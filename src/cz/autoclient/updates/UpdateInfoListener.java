/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

/**
 *
 * @author Jakub
 */
public abstract class UpdateInfoListener {
  public UpdateInfoListener(Progress download, Progress install) {
    this.download = download;
    this.install = install;
  }
  public UpdateInfoListener() {
    this(Progress.Empty.getInstance(), Progress.Empty.getInstance());
  }
  abstract public void updateAvailable(UpdateInfo info);
  abstract public void upToDate(UpdateInfo info);
  abstract public void upToDate(VersionId id);
  abstract public void actionChanged(Updater.Action a);
  public Progress download;
  public Progress install;
  /*public void downloadProgress(UpdateInfo info, double percent);
  public void downloadSuccess(UpdateInfo info);
  public void downloadError(UpdateInfo info, Throwable error);*/
  /*public void installProgress(UpdateInfo info, double percent);
  public void installSuccess(UpdateInfo info);
  public void installError(UpdateInfo info, Throwable error);*/
  
  public static class Empty extends UpdateInfoListener {
    public Empty() {
      super(Progress.Empty.getInstance(), Progress.Empty.getInstance());
    }
    @Override
    public void updateAvailable(UpdateInfo info){};
    @Override
    public void upToDate(UpdateInfo info){
      upToDate(info.version);
    };
    @Override
    public void upToDate(VersionId id) {}
    
    private static Empty instance;
    public static synchronized Empty getInstance() {
      if(instance==null)
        instance=new Empty();
      return instance;
    }

    @Override
    public void actionChanged(Updater.Action a) {}




  }
}
