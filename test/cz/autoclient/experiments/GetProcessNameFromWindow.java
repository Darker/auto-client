/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.WindowValidator;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public class GetProcessNameFromWindow {
  public static void main(String[] args) {
    long time = System.currentTimeMillis();
    
    // Try to find window by process name
    Window win2 = MSWindow.findWindow(new WindowValidator.CompositeValidatorAND(new WindowValidator[] {
      //new WindowValidator.TitlePatternWindowValidator(Pattern.compile("League of")),
      new WindowValidator.ProcessNameValidator(ConstData.game_process_name)
      
      //new WindowValidator.ExactTitleValidator("League of Legends"),
      //new WindowValidator.ProcessNameValidator(ConstData.process_name)
    })   
    );
    System.out.println("Lookup took "+(System.currentTimeMillis()-time)/1000.0 + " seconds.");
    if(win2!=null) {
        System.out.println(win2.getProcessName());
        //win2.close();
    }
    else {
        System.out.println("No window by process bitch!");  
    }
  }
}
