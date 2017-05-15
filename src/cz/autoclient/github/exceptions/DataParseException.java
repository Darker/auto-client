package cz.autoclient.github.exceptions;

import java.net.URL;

/**
 * This class describes error in parsing raw data from remote server.
 * This exception is only applicable to string data. Message of this
 * exception should describe what's wrong with the data.
 * @author Jakub
 */
public class DataParseException extends DataException {

  public DataParseException(String message, URL sourceURL, String originalData) {
    super(sourceURL, message);
    this.originalData = originalData;
  }
  public final String originalData;
  @Override
  public String toString() {
    return "Data from "+sourceURL+" caused error: "+this.getMessage();
  }
}
