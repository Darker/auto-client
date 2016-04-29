/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.notifications;

import cz.autoclient.settings.Settings;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Jakub
 */
public class NotificationSound extends Notification {
  
  public NotificationSound(Notification.Def def, Settings sets) {
    super(def, sets);
    if(def.audioFile==null) {
      throw new CantCreateNotification("Audio file is null");
    }
  }
  @Override
  public void notification() {
    new Thread(()->playSound()).start();
  }
  protected void playSound() {
   if(this.definition.audioFile!=null) {
      AudioListener listener = new AudioListener();
      InputStream file = NotificationSound.class.getResourceAsStream(this.definition.audioFile);

      try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(listener);
        clip.open(audioInputStream);
        try {
          clip.start();
          try {
            listener.waitUntilDone();
          } catch (InterruptedException ex) {
            clip.stop();
            clip.close();
            return;
          }
        } finally {
          clip.close();
        }
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public enum Def {
    ;
   /* public final 
    Def(String soundPath) {
      
    }*/
    
  }
  public static class AudioListener implements LineListener {
    private boolean done = false;
    @Override
    public synchronized void update(LineEvent event) {
      LineEvent.Type eventType = event.getType();
      if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
        done = true;
        notifyAll();
      }
    }
    public synchronized void waitUntilDone() throws InterruptedException {
      while (!done) { wait(); }
    }
  }
}
