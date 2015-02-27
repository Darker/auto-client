/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Jakub
 */
public class TabbedWindow {
  private GroupLayout layout;
  private JPanel currentPanel;
  
  
  private GroupLayout.ParallelGroup hGroup;
  private GroupLayout.SequentialGroup vGroup;
  public final JTabbedPane container;
  //After closing, no more tabs can be added
  private boolean closed = false;
  public TabbedWindow() {
    container = new JTabbedPane();
    
    
  }
  
  public JPanel newTab(String text, String tooltip, ImageIcon icon) {
    if(closed) {
      throw new IllegalStateException("UI has been locked. No more tabs can be added."); 
    }
    currentPanel = new JPanel();
    
    GroupLayout gLayout = new GroupLayout(currentPanel);
    currentPanel.setLayout(gLayout);
    hGroup = gLayout.createParallelGroup();
    gLayout.setHorizontalGroup(hGroup);
    vGroup = gLayout.createSequentialGroup();
    gLayout.setVerticalGroup(vGroup);
    
    container.addTab(text, icon, (JComponent)currentPanel,
                tooltip);
    
    return currentPanel;
  }
  public JPanel newTab(String text, String tooltip) {
    return newTab(text, tooltip, null);
  }
  public JPanel newTab(String text) {
    return newTab(text, null, null);
  }
  public void close() {
    container.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    closed = true;
  }
  
  public void addLine(FieldDef line) {
    if(currentPanel==null) {
      throw new IllegalStateException("First open tab using newTab."); 
    }
    hGroup.addComponent(line.container);
    vGroup.addComponent(line.container, GroupLayout.PREFERRED_SIZE,
            GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
    vGroup.addGap(5);
    //currentPanel.add(line.container);
  } 
}
