/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autoclick.windows;

/**
 *
 * @author Jakub
 */
public enum ShowWindow {
  FORCEMINIMIZE(11),	
  HIDE(0),
  MAXIMIZE(3),	
  MINIMIZE(6),	
  RESTORE(9),	
  SHOW(5),	
  SHOWDEFAULT(10),	
  SHOWMAXIMIZED(3),	
  SHOWMINIMIZED(2),	
  SHOWMINNOACTIVE(7),	
  SHOWNA(8),	
  SHOWNOACTIVATE(4),	
  SHOWNORMAL(1);
  
  public final int code;
  ShowWindow(int code) {
    this.code = code; 
  }
  public int toInt() {
    return this.code; 
  }
  public static ShowWindow byCode(int code) {
    for(ShowWindow v : values()){
      if(v.code==code) {
        return v;
      }
    }
    return null;
  }
}
