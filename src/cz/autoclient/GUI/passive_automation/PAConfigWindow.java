/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.passive_automation;

import cz.autoclient.GUI.tabs.FieldDef;
import cz.autoclient.settings.Settings;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jakub
 */
public class PAConfigWindow extends JDialog {
  public final PAMenu menu;
  public final JPanel mainContainer;
  
  
  private GroupLayout.ParallelGroup hGroup;
  private GroupLayout.SequentialGroup vGroup;
  
  public boolean useSettingNamespace = false;
  public final JMenuItem actuator;
  
  private Settings settings;
  public PAConfigWindow(PAMenu menu, Settings settings) {
    this(menu, settings, SwingUtilities.getWindowAncestor(menu.root));
  }
  public PAConfigWindow(PAMenu menu, Settings settings, Window parent) {
    super(parent);
    this.menu = menu;
    //
    this.settings = settings;
    
    setTitle(menu.name+" settings");
    //Init GUI
    mainContainer = new JPanel();
    GroupLayout gLayout = new GroupLayout(mainContainer);
    mainContainer.setLayout(gLayout);
    hGroup = gLayout.createParallelGroup();
    gLayout.setHorizontalGroup(hGroup);
    vGroup = gLayout.createSequentialGroup();
    gLayout.setVerticalGroup(vGroup);
    
    //getRootPane().add(mainContainer);
    
    getContentPane().add(mainContainer);
    
    //Init menu item
    actuator = new JMenuItem("Settings");
    actuator.addActionListener(new OpenMenuListener());
    
    setSize(new Dimension(230, 100));
    
    setLocationRelativeTo(parent);
    //this.setLocation(parent.getLocation());
  }
  
  public void addLine(FieldDef line) {
    hGroup.addComponent(line.container);
    vGroup.addComponent(line.container, GroupLayout.PREFERRED_SIZE,
            GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
    vGroup.addGap(5);
    if(useSettingNamespace)
      line.setSetting_name(settingName(line.getSetting_name()));
    line.attachToSettings(settings);
    //currentPanel.add(line.container);
  }
  public String settingName(String name) {
    if(name==null)
      return null;
    else
      return menu.settingName+"_"+name.toUpperCase(); 
  }
  public class OpenMenuListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      PAConfigWindow.this.setVisible(true);
    }
  }
}
