/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;

import cz.autoclient.GUI.ImageResources;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 *
 * @author Jakub
 */
@RunWith(value = Parameterized.class)
public class TestResources {

    //DO NOT MODIFY
    ImageResources resource;

    //DO NOT MODIFY
    public TestResources(ImageResources resource) {
        this.resource = resource;
    }

    //DO NOT MODIFY
    @Parameterized.Parameters(name="{index}")
    public static Iterable<Object[]> data() {
      ArrayList<Object[]> params = new ArrayList<>();
      for(ImageResources ir : ImageResources.values()) {
        params.add(new Object[] {ir});
      }
      return params;
    }
    
    @Test
    public void testImage() {
      Image im = resource.getImage();
      assertNotNull("Image is null for "+resource.name()+"!", im);
    }
    @Test
    public void testIcon() {
      ImageIcon im = resource.getIcon();
      assertNotNull("Icon is null for "+resource.name()+"!", im);
    }
}
