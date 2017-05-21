/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.Gui;
import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.event.EventCallbackLog;
import cz.autoclient.updates.Progress;
import cz.autoclient.updates.UpdateInfo;
import cz.autoclient.updates.Updater;
import cz.autoclient.updates.VersionId;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipError;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jakub
 */
public class UpdateVisual {
  private final Gui gui;
  private final UpdateMenuItem item;
  private final Updater updater;
  
  private final Progress download;
  private final EventCallbackLog events;
  //private final Progress install;
  
  public UpdateVisual(final Gui gui, final UpdateMenuItem m, final Updater updater) {
    //super(null, Progress.Empty.getInstance());
    if(updater == null)
      throw new IllegalArgumentException("Updater is null!");
    this.updater = updater;
    this.gui = gui;
    if(m == null)
      throw new IllegalArgumentException("The update menu item is null!");
    this.item = m;
    events = new EventCallbackLog(updater);
    
    events.addEventListener("action_changed", this::actionChanged);
    events.addEventListener("update_available", this::updateAvailable);
    events.addEventListener("up_to_date", this::upToDate);
    //updater.addEventListener("up_to_date", this::actionChanged);
    
    download = new Download();
    events.addEventListener("download.started", download::started);
    events.addEventListener("download.status", download::status);
    events.addEventListener("download.process", download::process);
    events.addEventListener("download.stopped", download::stopped);
    events.addEventListener("download.finished", download::finished);
    
    events.addEventListener("install.started", ()->{throw new RuntimeException("Install callback in GUI!");});
    //install = new Install();
    
    updater.checkCurrentState(()->{displayCurrentStatus();});
  }
  
  public void removeListeners() {
    events.removeAll();
  }

  public void updateAvailable(UpdateInfo info) {
    upToDate = false;
    SwingUtilities.invokeLater(()->item.setDownloadAvailable(info.version));
    gui.notification(Notification.Def.UPDATE_AVAILABLE);
    /*if(gui.settings.getBoolean(Setnames.UPDATES_AUTODOWNLOAD.name, (Boolean)Setnames.UPDATES_AUTODOWNLOAD.default_val)) {
      updater.downloadUpdate();
    }*/
  }
  boolean upToDate = false;
  protected void displayCurrentStatus() {
    Updater.Action a = updater.currentAction();
    if(a != Updater.Action.IDLE) {
      actionChanged(a);
    }
    else {
      switch(updater.getUpdates().installStep()) {
        case NOT_INSTALLING: 
          if(upToDate)
            upToDate(updater.version);
          else
            SwingUtilities.invokeLater(()->item.setUnknown(updater.version));
        break;
        case CAN_DOWNLOAD:
          SwingUtilities.invokeLater(()->item.setDownloadAvailable(inProgress().version));
        break;
        case CAN_UNPACK:
        case CAN_COPY_FILES: 
          SwingUtilities.invokeLater(()->item.setDownloaded(inProgress().version));
        break;
        default:
          Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error: invalid install step: "+updater.getUpdates().installStep().name());
      }
    }
  }

  //public void upToDate(UpdateInfo info) {
  //  upToDate(info.version);
  //}

  public void upToDate(VersionId version) {
    upToDate = true;
    SwingUtilities.invokeLater(()->item.setUpToDate(version));
  }

  public void actionChanged(Updater.Action a) {
    switch(a) {
      case DOWNLOADING: 
        item.setDownloadProgress(inProgress().version, 0);
        break;
      case CHECKING:
        item.setChecking();
        break;
    }
  }
  protected UpdateInfo inProgress() {
    return updater.getUpdates().installInProgress();
  }
  protected UpdateInfo current() {
    return updater.getUpdates().findVersion(updater.version);
  }
  
  protected class Download implements Progress {
    @Override
    public void process(double current, double max) {
      SwingUtilities.invokeLater(()->item.setDownloadProgress(inProgress().version, (current/max)));
    }
    @Override
    public void status(String status) {}

    @Override
    public void started() {item.setDownloadProgress(inProgress().version, 0);}
 
    @Override
    public void stopped(Throwable error) {
      String mainText = error.toString();
      if(error instanceof UnknownHostException) {
        mainText = "Cannot connect to server "+((UnknownHostException)error).getMessage();
      }
     

      Dialogs.dialogErrorAsync("<b>An error occured when downloading:</b><br /><tt>    </tt>"
          + mainText +"<br />" +
          "You can try to delete the <tt>/updates</tt> folder or download new version manually.<br />"+
          "Do not forget to <a href=\"https://github.com/Darker/auto-client/issues/new\">report this problem</a>."
          ,"Error during download."); 
      displayCurrentStatus();
    }

    @Override
    public void finished() {
      item.setDownloaded(inProgress().version);
      gui.notification(Notification.Def.UPDATE_DOWNLOADED);
    }

    public void paused() {}
    public void resumed() {}
  }
  protected class Install implements Progress {
      @Override
      public void process(double current, double max) {}
      @Override
      public void status(String status) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, status);
      }
      @Override
      public void started() {}
      @Override
      public void stopped(Throwable error) {
        String mainText = error.toString();
        if(error instanceof ZipError) {
          mainText = "Error when unziping package: "+error.getMessage();
        }
        Dialogs.dialogErrorAsync("<b>An error occured when updating:</b><br />"
            + mainText
            ,"Error during install.");
      }
      @Override
      public void finished() {}
      @Override
      public void paused() {}
      @Override
      public void resumed() {}
  }
}
