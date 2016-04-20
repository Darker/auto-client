/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.PVP_net;

/**
 *
 * @author Jakub
 */
public class QueueConfig {
  public String championName;
  public GameMode mode;
  public GameMap map;
  public GameType type;
  public GameDifficulty difficulty;
  
  
  
  
  
  /**Because Java is retarded, I'll have to write the exact class methods for every enum bellow**/
  //The code would be much more readable if I could defer the code to abstract class
  public enum GameMode implements ImageEnum {
    CLASSIC, DOMINION, ARAM;
    
    public final Images image;
    public final PixelOffset pixel;
    GameMode(PixelOffset px) {pixel=px;image = null;}
    GameMode(Images im) {image = im;pixel=null;}
    GameMode(PixelOffset px, Images im) {image = im;pixel=px;}
    GameMode(Images im, PixelOffset px) {image = im;pixel=px;}
    GameMode() {image = null;pixel=null;}
    
    @Override
    public Images getImage() {return image;}
    @Override
    public PixelOffset getPixel() {return pixel;}
  }
  public enum GameMap implements ImageEnum {
    SUMMONERS_RIFT, TWISTED_TREELINE, CRYSTAL_STAR, HOWLING_ABYSS;
    
    public final Images image;
    public final PixelOffset pixel;
    GameMap(PixelOffset px) {pixel=px;image = null;}
    GameMap(Images im) {image = im;pixel=null;}
    GameMap(PixelOffset px, Images im) {image = im;pixel=px;}
    GameMap(Images im, PixelOffset px) {image = im;pixel=px;}
    GameMap() {image = null;pixel=null;}
    @Override
    public Images getImage() {return image;}
    @Override
    public PixelOffset getPixel() {return pixel;}
  }
  public enum GameType  implements ImageEnum {
    TEAM_BUILDER, BLIND_PICK, DRAFT_PICK, DRAFT_PICK_SOLO_DUO;
    
    public final Images image;
    public final PixelOffset pixel;
    GameType(PixelOffset px) {pixel=px;image = null;}
    GameType(Images im) {image = im;pixel=null;}
    GameType(PixelOffset px, Images im) {image = im;pixel=px;}
    GameType(Images im, PixelOffset px) {image = im;pixel=px;}
    GameType() {image = null;pixel=null;}
    @Override
    public Images getImage() {return image;}
    @Override
    public PixelOffset getPixel() {return pixel;}
  }
  public enum GameDifficulty  implements ImageEnum {
    INTRO, BEGINNER, INTERMEDIATE; 
    
    public final Images image;
    public final PixelOffset pixel;
    GameDifficulty(PixelOffset px) {pixel=px;image = null;}
    GameDifficulty(Images im) {image = im;pixel=null;}
    GameDifficulty(PixelOffset px, Images im) {image = im;pixel=px;}
    GameDifficulty(Images im, PixelOffset px) {image = im;pixel=px;}
    GameDifficulty() {image = null;pixel=null;}
    @Override
    public Images getImage() {return image;}
    @Override
    public PixelOffset getPixel() {return pixel;}
  }

}
