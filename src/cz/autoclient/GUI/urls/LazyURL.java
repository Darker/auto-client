/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.urls;

import java.net.URL;

/**
 *
 * @author Jakub
 */
public abstract class LazyURL implements AcURL {
  @Override
  public abstract URL getURL();


}
