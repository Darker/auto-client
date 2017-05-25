/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.updates.Progress;
import cz.autoclient.updates.Updater;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author Jakub
 */
public class UpdateProgressWindow implements Progress {
  private final Updater updater;
  private ProgressBarWindow window;
  private final Runnable onEnd;

  public UpdateProgressWindow(final Updater updater, Runnable onEnd) {
    this.onEnd = onEnd;
    if(updater == null)
      throw new IllegalArgumentException("Updater is null!");
    this.updater = updater;
    this.window = new ProgressBarWindow();
    
    updater.addEventListener("install.started", this::started);
    updater.addEventListener("install.status", this::status);
    updater.addEventListener("install.process", this::process);
    updater.addEventListener("install.stopped", this::stopped);
    updater.addEventListener("install.finished", this::finished);
  
  }

  
  // Progress
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
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Install failed.");
    error.printStackTrace();
    window.close();

    String mainText = error.toString();
    if(error instanceof ZipException) {
      mainText = "Error when unziping package: "+error.getMessage();
    }
    
    Dialogs.dialogErrorAsync("<b>An error occured when updating:</b><br />"
        + mainText
        ,"Error during install.");
        
    onEnd.run();
  }
  @Override
  public void finished() {
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Finished install. Closing window and calling callback.");
    window.status("Done. Restarting program.");
    window.close();
    onEnd.run();
  }
  @Override
  public void paused() {}
  @Override
  public void resumed() {}
}
