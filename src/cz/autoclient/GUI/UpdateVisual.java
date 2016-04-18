/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.GUI.updates.UpdateMenuItem;
import cz.autoclient.updates.Progress;
import cz.autoclient.updates.UpdateInfo;
import cz.autoclient.updates.UpdateInfoListener;
import cz.autoclient.updates.Updater;
import cz.autoclient.updates.VersionId;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jakub
 */
public class UpdateVisual extends UpdateInfoListener {
  private final Gui gui;
  private final UpdateMenuItem item;
  private final Updater updater;
  public UpdateVisual(final Gui gui, final UpdateMenuItem m, final Updater updater) {
    super(null, Progress.Empty.getInstance());
    if(updater == null)
      throw new IllegalArgumentException("Updater is null!");
    this.updater = updater;
    this.gui = gui;
    if(m == null)
      throw new IllegalArgumentException("The update menu item is null!");
    this.item = m;
    download = new Progress() {
               @Override
               public void process(double current, double max) {
                 SwingUtilities.invokeLater(()->m.setDownloadProgress(inProgress().version, (current/max)*100));
               }
               @Override
               public void status(String status) {}
               @Override
               public void started() {item.setDownloadProgress(inProgress().version, 0);}
               @Override
               public void stopped(Throwable error) {}
               @Override
               public void finished() {
                 item.setDownloaded(inProgress().version);
               }
               @Override
               public void paused() {}
               @Override
               public void resumed() {}
     };
    install = new Progress() {
      @Override
      public void process(double current, double max) {
        System.out.println("Installing: "+(current/max)*100+"%");
      }
      @Override
      public void status(String status) {
        System.out.println(status);
      }
      @Override
      public void started() {
        System.out.println("Install started.");
      }
      @Override
      public void stopped(Throwable error) {
        System.out.println("Install failed.");
        error.printStackTrace();
      }
      @Override
      public void finished() {
        System.out.println("Install succesful asking for restart.");
        gui.restart(false);
      }
      @Override
      public void paused() {}
      @Override
      public void resumed() {}
    };
    updater.checkCurrentState(()->{displayCurrentStatus();});
  }
  @Override
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
          System.out.println("Error: invalid install step: "+updater.getUpdates().installStep().name());
      }
    }
  }
  @Override
  public void upToDate(UpdateInfo info) {
    upToDate(info.version);
  }
  @Override
  public void upToDate(VersionId version) {
    upToDate = true;
    SwingUtilities.invokeLater(()->item.setUpToDate(version));
  }
  @Override
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
}
