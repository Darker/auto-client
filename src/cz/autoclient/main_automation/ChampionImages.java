/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.main_automation;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.autoclick.comvis.ScreenWatcher;
import cz.autoclient.league_of_legends.Champion;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jakub
 */
public class ChampionImages implements
    java.io.Serializable,
    Iterable<Entry<String, ChampionImages.ColorInfo>> {

  private HashMap<String, ColorInfo> colors;
  private final Object color_mutex = new Object();


  private GenerateColors generatorThread = null;
  private final File homeDir;

  public ChampionImages(File homeDir) {
    if(homeDir==null || !homeDir.isDirectory())
      throw new IllegalArgumentException("homeDir must be a valid directory.");
    this.homeDir = homeDir;
  }
  /*@Override
  public Iterator<Color> iterator() {
    throw new UnsupportedOperationException("Not supported yet.");
  }*/

  /**
   * Calling this is NOT RECOMMENDED if interrupt is expected or possible.
   * If the thread gets interrupt, this will not throw and instead return null. That's BAD!
   * @return 
   */
  @Override
  public Iterator<Entry<String, ColorInfo>> iterator() {
    try {
      return getColors().entrySet().iterator();
    } catch (InterruptedException ex) {
      return null;
    }
  }

  public Iterator<Entry<String, ColorInfo>> iteratorWait() throws InterruptedException {
    return getColors().entrySet().iterator();
  }
  public String find(final BufferedImage b) {
    return find(getColorInfo(b));
  }
  public String find(ColorInfo avgColor) {
    String best = null;
    long bestDiff = Long.MAX_VALUE;
    for(Entry<String, ColorInfo> e: this) {
      ColorInfo c = e.getValue();
      long diff = avgColor.compare(c);
      if(bestDiff>diff) {
        bestDiff = diff;
        best = e.getKey();
      }
    }
    return best;
  }
  
  public HashMap<String, ColorInfo> getColors() throws InterruptedException {
    if(generatorThread==null) {
      synchronized(color_mutex) {
        if(generatorThread==null) {
          generatorThread = new GenerateColors(); 
          generatorThread.start();
          generatorThread.join();
        }
      }
    }
    else if(generatorThread.isAlive()) {
      generatorThread.join();
    }
    return colors;
  }
  public void getColorsAsync() throws InterruptedException {
    if(generatorThread==null) {
      synchronized(color_mutex) {
        if(generatorThread==null) {
          generatorThread = new GenerateColors(); 
          generatorThread.start();
        }
      }
    }
  }
  private class GenerateColors extends Thread {
    public GenerateColors() {
      super("Generating champion average colors.");
      colors = new HashMap();
    }
    @Override
    public void run() {
      try {
        load();
      } catch (InterruptedException ex) {
        return;
      }
    }
    private void load() throws InterruptedException {
      File cache = new File(homeDir, "average_champion_colors.ser");
      
      if(cache.isFile()) {
        if(loadFromFile(cache))
          return;
      }
      // Load all images and calculate average values
      Iterator<Entry<String, Champion>> it = ConstData.lolData.getChampions().entrySet().iterator();
      // No entries? Game ovar. Yes, "ovar" that's not a typo, that's internal joke
      if(!it.hasNext())
        return;
      Champion current = it.next().getValue();
      ChampionImageDownload download = new ChampionImageDownload(current);
      download.start();
      while(!interrupted() && download!=null) {
        if(download.isAlive()) {
          //System.out.println("[ChampionImages] Waiting for image "+current.name);
          download.join();
        }
        BufferedImage img = download.image;
        Champion img_champ = current;
        // Start downloading next image, in paralel
        if(it.hasNext()) {
          current = it.next().getValue();
          download = new ChampionImageDownload(current);
          download.start();
        }
        else 
          download = null;
        // While downloading the next image, we calculate average color
        // of the current image
        if(img != null) {
          //System.out.println("[ChampionImages] Started generating color for  "+img_champ.name);
          //Color average = ScreenWatcher.averageColor(
          //    img, 0, 0, img.getWidth(), img.getHeight());
          colors.put(img_champ.name, getColorInfo(img));
          //System.out.println("[ChampionImages] Done generating color for  "+img_champ.name);
        }
      }
      // Ensure the child thread is also killed
      if(interrupted()) {
        download.interrupt();
        throw new InterruptedException("Interrupted while downloading images.");
      }
      // Save into cache file
      saveToFile(cache);
    }
  }
  private static class ChampionImageDownload extends Thread {
    public final Champion ch;
    private BufferedImage image = null;

    public ChampionImageDownload(Champion ch) {
      super("Download champ image.");
      this.ch=ch;
    }
    @Override
    public void run() {
      //System.out.println("[ChampionImageDownload] Started downloading "+ch.name);
      BufferedImage im = ch.img.getBufferedImage();
      image = im.getSubimage(2, 2, im.getWidth()-4, im.getHeight()-4);
      //System.out.println("[ChampionImageDownload] Finished downloading "+ch.name);
    }
    public BufferedImage getImage() {
      return image;
    }
  }
  /** 
   * Contains info about champion image color(s). This class will changed 
   * untill it contains enough info to identify every champion in LoL.
   */
  public static class ColorInfo implements java.io.Serializable {
    public final Color top_color;
    public final Color bottom_color;
    public ColorInfo(Color top_color, Color bottom_color) {
      this.top_color = top_color;
      this.bottom_color = bottom_color;
    }
    public long compare(ColorInfo i) {
      return ScreenWatcher.compareColorsSq(top_color, i.top_color)+
             ScreenWatcher.compareColorsSq(bottom_color, i.bottom_color);
    }
    @Override
    public String toString() {
      return top_color+" "+bottom_color; 
    }
  }
  public static ColorInfo getColorInfo(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    int mid = height/2;
    
    Color top = ScreenWatcher.averageColor(image, 0, 0, width, mid);
    Color bottom = ScreenWatcher.averageColor(image, 0, mid, width, height-mid);
    return new ColorInfo(top, bottom);
  }
  /*public static class ColorIterator implements Iterator<Color> {
    private final Iterator<Entry<Name, Color>>
    protected ColorIterator(
    @Override
    public boolean hasNext() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color next() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    
  }*/
  public boolean loadFromFile(File path) {    
    InputStream file;
    try {
      file = new FileInputStream(path);
    } catch (FileNotFoundException ex) {
      return false;
    }
    //Get the properties out of the object
    try(InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream (buffer);
    ) {
      Map set = (Map)input.readObject();
      System.out.println(set.size()+" champions loaded from file.");
      if(set.size()>0)
        this.colors.putAll(set);
    }
    catch(ClassNotFoundException | IOException e) {
      System.out.println("Some class was not found when loading champion colors: "+e.getMessage());
      e.printStackTrace();
      return false;
    }
    finally {
      try {
        file.close();
      } catch (IOException ex) {
        return false;
      }
    };  
    return true;
  }
  public boolean saveToFile(File path) {
    try {
      //File f = new File(path);
      if(!path.getParentFile().exists()&&!path.getParentFile().mkdirs()) {
        return false;
      }
      path.createNewFile();

      OutputStream file = new FileOutputStream(path);
      ObjectOutput output;
      try (OutputStream buffer = new BufferedOutputStream(file)) {
        output = new ObjectOutputStream(buffer);
        System.out.println("Saving champ color HashMap - "+colors.size()+" fields.");
        output.writeObject(colors);
      }
      output.close();
    }
    catch(IOException e) {
      return false; 
    }
    return true;
  }
}
