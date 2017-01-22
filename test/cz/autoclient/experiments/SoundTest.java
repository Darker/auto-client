/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

/**
 *
 * @author Jakub
 */
public class SoundTest {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Throwable {
    class AudioListener implements LineListener {
      private boolean done = false;
      @Override
      public synchronized void update(LineEvent event) {
        Type eventType = event.getType();
        if (eventType == Type.STOP || eventType == Type.CLOSE) {
          done = true;
          notifyAll();
        }
      }
      public synchronized void waitUntilDone() throws InterruptedException {
        while (!done) { wait(); }
      }
    }
    AudioListener listener = new AudioListener();
    InputStream file = SoundTest.class.getResourceAsStream("/notify.wav");
    
    for(byte i=0; i<3; i++) {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
      try {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(listener);
        clip.open(audioInputStream);
        try {
          clip.start();
          listener.waitUntilDone();
        } finally {
          clip.close();
        }
      } finally {
        audioInputStream.close();
      }
    }
  }
  
}
