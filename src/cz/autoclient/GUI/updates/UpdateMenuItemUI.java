/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;

import cz.autoclient.GUI.PassiveMenuItemUI;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Jakub
 */
public class UpdateMenuItemUI extends PassiveMenuItemUI {
  
  @Override
  protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
    ButtonModel model = menuItem.getModel();
    Color oldColor = g.getColor();
    int menuWidth = menuItem.getWidth();
    int menuHeight = menuItem.getHeight();
    
    //g.fillRect(0,0, menuWidth/4, menuHeight);
    if(menuItem.isOpaque()) {
        if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
            g.setColor(bgColor);
            g.fillRect(0,0, menuWidth, menuHeight);
        } else {
            g.setColor(menuItem.getBackground());
            g.fillRect(0,0, menuWidth, menuHeight);
            if(menuItem instanceof UpdateMenuItem) {
              UpdateMenuItem u = (UpdateMenuItem)menuItem;
              if(u.getProgress()>0) {
                g.setColor(Color.GREEN);
                g.fillRect(0,0, (int)(menuWidth*u.getProgress()), menuHeight);
              }
            }
        }
        
    }
    else if (model.isArmed() || (menuItem instanceof JMenu &&
                                 model.isSelected())) {
        g.setColor(bgColor);
        g.fillRect(0,0, menuWidth, menuHeight);
    }
    g.setColor(oldColor);
  }
}
