/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.interfaces;

import java.net.URL;

/**
 *
 * @author Jakub
 */
public interface ReleaseFile {
  URL downloadUrl();
  String name();
  long size();
  // Returns release that contains this file
  Release parent();
}
