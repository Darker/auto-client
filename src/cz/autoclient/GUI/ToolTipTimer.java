/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ToolTipManager;

/**
 *
 * @author Jakub
 */
public class ToolTipTimer extends MouseAdapter  {
   
  public final static ToolTipTimer INSTANT_TOOLTIP = new ToolTipTimer(0);
  
  private int defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();
  public ToolTipTimer(int delay) {
    
  }
  @Override
  public void mouseEntered(MouseEvent e) {
    defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();
    ToolTipManager.sharedInstance().setInitialDelay(0);
  }

  @Override
  public void mouseExited(MouseEvent e) {
      ToolTipManager.sharedInstance().setInitialDelay(defaultTimeout);
  }
  /*@Override
  public void mouseEntered(MouseEvent e) {
      JComponent c = (JComponent) e.getComponent();
      Action action = c.getActionMap().get("postTip");
      //it is also possible to use own Timer to display 
      //ToolTip with custom delay, but here we just 
      //display it immediately
      if (action != null) {
          action.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "postTip"));
      }
  }*/

}
