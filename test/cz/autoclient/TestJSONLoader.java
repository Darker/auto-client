/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.league_of_legends.DataLoader;
import cz.autoclient.league_of_legends.Champions;
import cz.autoclient.league_of_legends.Champion;
import cz.autoclient.league_of_legends.Version;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import java.io.File;

/**
 *
 * @author Jakub
 */
public class TestJSONLoader {
   public static void main(String[] args) throws Exception
   {
     Version ver = new Version(DataLoader.Realm.EUNE, new File("LOLResources"), true);
     System.out.println("Version:" +ver.getVersion());
     
     Champions champs = ver.getChampions();
     
     Champion vladimir = champs.getChampion("Vladimir");
     if(vladimir!=null) {
       DebugDrawing.displayImage(vladimir.img.getImage());
     }
     
     /*JSONObject data = ver.getData();
     String[] names = JSONObject.getNames(data);

     for(String name: JSONObject.getNames(data)) {
       System.out.println("data["+name+"] = "+data.get(name));
     }*/
   }
}
