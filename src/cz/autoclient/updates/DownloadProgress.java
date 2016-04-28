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
public abstract class DownloadProgress implements Progress {
  public void process(double current, double max){
    process((current/max)*100.0);
  };
  public void process(double percent){};
  public void started(){};
  // Stopped with error
  public void stopped(Throwable error){};
  // Stopped with success
  public void finished(){};
  public void paused(){};
  public void resumed(){};

  public void status(String status) {}
}
