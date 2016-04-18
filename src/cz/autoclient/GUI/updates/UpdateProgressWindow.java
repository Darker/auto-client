/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.GUI.notifications.Notification;
import cz.autoclient.updates.Progress;
import cz.autoclient.updates.UpdateInfo;
import cz.autoclient.updates.UpdateInfoListener;
import cz.autoclient.updates.Updater;
import cz.autoclient.updates.VersionId;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jakub
 */
public class UpdateProgressWindow extends UpdateInfoListener {
  private final Updater updater;
  private ProgressBarWindow window;
  private final Runnable onEnd;

  public UpdateProgressWindow(final Updater updater, Runnable onEnd) {
    super(null, Progress.Empty.getInstance());
    this.onEnd = onEnd;
    if(updater == null)
      throw new IllegalArgumentException("Updater is null!");
    this.updater = updater;
    this.window = new ProgressBarWindow();
    download = Progress.Empty.getInstance();
    install = new Progress() {
      @Override
      public void process(double current, double max) {
        window.setProgress(current, max);
      }
      @Override
      public void status(String status) {
        window.status(status);
      }
      @Override
      public void started() {
        
      }
      @Override
      public void stopped(Throwable error) {
        System.out.println("Install failed.");
        error.printStackTrace();
        window.close();
        Dialogs.dialogErrorAsync(error.getMessage(), "Error during updates.");
        onEnd.run();
      }
      @Override
      public void finished() {
        System.out.println("Finished install. Closing window and calling callback.");
        window.status("Done. Restarting program.");
        window.close();
        onEnd.run();
      }
      @Override
      public void paused() {}
      @Override
      public void resumed() {}
    };
  }
  @Override
  public void updateAvailable(UpdateInfo info) {}

  protected void displayCurrentStatus() {}
  @Override
  public void upToDate(UpdateInfo info) {}
  @Override
  public void upToDate(VersionId version) {}
  @Override
  public void actionChanged(Updater.Action a) {}
  protected UpdateInfo inProgress() {
    return updater.getUpdates().installInProgress();
  }
  protected UpdateInfo current() {
    return updater.getUpdates().findVersion(updater.version);
  }
}
