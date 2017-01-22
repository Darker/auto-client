/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.interfaces;

import java.net.URL;
import java.util.List;

/**
 *
 * @author Jakub
 */
public interface Release {
  String tag();
  List<? extends ReleaseFile> downloads();
  URL url();
  Repository parent();
  boolean isLatest();
  boolean isPrerelease();
}
