/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.sound;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Jakub
 */
public class LazyLoadedSound {
  public final String path;
  public final File file;
  private Media media = null;
  private MediaPlayer player = null;
  private boolean error = false;
  public LazyLoadedSound(String path) {
    this.path = path;
    file = new File(path);
  }
  public boolean isValid() {
    getPlayer();
    return !error;
  }
  public Media getMedia() {
    if(media==null) {
      if(!file.exists()) {
        error = true;
        return null;
      }
      media = new Media(file.toURI().toString());
      //media.getError()
    }
    return media;
  }
  public MediaPlayer getPlayer() {
    if(player==null) {
      getMedia();
      if(media==null)
        return null;
      player = new MediaPlayer(media);
    }
    return player; 
  }
  public void play() {
    if(error)
      return;
    getPlayer().play();
  }
}
