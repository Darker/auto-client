/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.github.exceptions;

import java.net.URL;

/**
 * This exception describes error in obtaining od parsing dat from the remote server.
 * @author Jakub
 */
public class DataException extends RuntimeException {

  public DataException(URL sourceURL, String message) {
    super(message);
    this.sourceURL = sourceURL;
  }
  // Url of the data source
  public final URL sourceURL;
  
  @Override
  public String toString() {
    return "Error fetching data from "+sourceURL;
  }
}
