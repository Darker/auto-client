/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.dllinjection;

/**
 *
 * @author Jakub
 */
public class ProcessNotFoundException extends Exception {
  public final String name;
  /**
   * Creates a new instance of <code>ProcessNotFoundException</code> without
   * detail message.
   */
  public ProcessNotFoundException() {
    name = null;
  }

  /**
   * Constructs an instance of <code>ProcessNotFoundException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public ProcessNotFoundException(String msg) {
    super(msg);
    name = null;
  }
  /**
   * Constructs an instance of <code>ProcessNotFoundException</code> with the
   * specified detail message and the name that has been searched.
   *
   * @param msg the detail message.
   * @param name name of the process we were looking for
   */
  public ProcessNotFoundException(String msg, String name) {
    super(msg);
    this.name = name;
  }
  
  @Override
  public String toString() {
    if(name!=null) {
      return this.getMessage()+" ["+name+"]";
    }
    else
      return 
        super.toString();
  }
}
