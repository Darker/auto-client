/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends.maps;

import cz.autoclient.league_of_legends.LoLVersion;
import cz.autoclient.league_of_legends.SummonerSpell;
import java.net.MalformedURLException;
import java.net.URL;



/**
 *
 * @author Jakub
 */
public class SummonerSpells extends GameObjectMap<SummonerSpell> {
  public SummonerSpells(LoLVersion v, boolean download_if_missing) {
    super(SummonerSpell.class, v, download_if_missing);
  }
  
  private boolean allImagesDownloaded = false;
  /** Downloads all spell images.
   * A blocking function that only returns after images have been
   * downloaded.
   */
  /*public void loadAllImages() {
    List<Thread> threads = new ArrayList<>();
    for(Object s : this.enumValues(null)) {
      Thread t = (new Thread("Downloading summoner spell"+((SummonerSpell)s).getName()+".") {
        @Override
        public void run() {
          ((SummonerSpell)s).img.getImage();
        }
      });
      t.start();
      threads.add(t);
    }
    for(Thread t:threads) {
      try {
        t.join();
      } catch (InterruptedException ex) {
        continue;
      }
    }
  }*/

  @Override
  public String getFilename() {
    return "summoner";
  }

  @Override
  public URL getURL() {
    try {
      return new URL("http://ddragon.leagueoflegends.com/cdn/"+getBaseVersion().getVersion()+"/data/"+getBaseVersion().getLanguage()+"/summoner.json");
    } catch (MalformedURLException ex) {
      return null;
    }
  }
}
