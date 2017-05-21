/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 *
 * @author Jakub
 */
public class PassiveMenuItemUI extends BasicMenuItemUI{
  @Override
  protected void doClick(MenuSelectionManager msm) {
    //System.exit(0);
    menuItem.doClick(0);
  }
  
  @Override
  protected MouseInputListener createMouseInputListener(JComponent c) {
      return new MouseInput();
  }
  @Override
  protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
      return null;
  }
  @Override
  protected MenuKeyListener createMenuKeyListener(JComponent c) {
      return null;
  }
  
  protected class MouseInput implements MouseInputListener {
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {
      if (!menuItem.isEnabled()) {
          return;
      }
      MenuSelectionManager manager =
          MenuSelectionManager.defaultManager();
      Point p = e.getPoint();
      if(p.x >= 0 && p.x < menuItem.getWidth() &&
         p.y >= 0 && p.y < menuItem.getHeight()) {
          doClick(manager);
      } else {
          manager.processMouseEvent(e);
      }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
  }
}
