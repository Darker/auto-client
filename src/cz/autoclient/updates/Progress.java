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
public interface Progress {
  public void process(double current, double max);
  public void status(String status);
  public void started();
  // Stopped with error
  public void stopped(Throwable error);
  // Stopped with success
  public void finished();
  public void paused();
  public void resumed();
  
  public static class Empty implements Progress {
    private static Progress.Empty instance;
    public static synchronized Progress getInstance() {
      if(instance==null)
        instance = new Progress.Empty();
      return instance;
    }
    @Override
    public void process(double current, double max) {}
    @Override
    public void status(String status) {}
    @Override
    public void started() {}
    @Override
    public void stopped(Throwable error) {}
    @Override
    public void finished() {}
    @Override
    public void paused() {}
    @Override
    public void resumed() {}
  }
}
