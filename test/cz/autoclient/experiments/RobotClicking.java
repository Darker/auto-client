/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.autoclick.Rect;
import cz.autoclient.autoclick.windows.MouseButton;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowRobot;
import cz.autoclient.autoclick.windows.WindowValidator;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public class RobotClicking {

  /**
   * @param args the command line arguments
   * @throws java.lang.InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    Window w = MSWindow.findWindow(new WindowValidator.CompositeValidatorAND(new WindowValidator[] {
      new WindowValidator.TitlePatternWindowValidator(Pattern.compile("Click measuring - JSFiddle - Mozilla Firefox", 0))
    })   
    );
    if(w!=null) {
      w = new WindowRobot(w);
      
      System.out.println(w.getTitle());
      Rect r = w.getRect();
      System.out.println(r);
      Rect pos = new Rect((int)Math.round(r.width*0.7304772877070304D), (int)Math.round(r.height*0.6154028531972243D));
      w.click(pos);
      Thread.sleep(3000);
      w.click(pos.left, pos.top, MouseButton.Right);
    }
  }
  
}
