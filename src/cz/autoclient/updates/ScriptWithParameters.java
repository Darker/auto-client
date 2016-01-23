/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Jakub
 */
public interface ScriptWithParameters {
  void setParameter(String name, String value);
  String getParameter(String name);
  String getRawData();
  boolean saveToFile(File file) throws IOException;
  
}
