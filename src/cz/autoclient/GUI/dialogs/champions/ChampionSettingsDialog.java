/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.dialogs.champions;

import cz.autoclient.GUI.champion.ConfigurationManager;
import cz.autoclient.settings.Settings;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * This is a dialog for editing and viewing champion settings.
 * @author Jakub
 */
public class ChampionSettingsDialog extends JFrame {
    private Settings main_settings;
    private ConfigurationManager manager;
    public ChampionSettingsDialog(ConfigurationManager manager) {
       super("Champion configuration.");
       this.main_settings = manager.getSettings();
       this.manager = manager;
//       this.addWindowListener(new WindowAdapter()
//       {
//          @Override
//          public void windowOpened(WindowEvent event)
//          {
//            System.out.println("Window opened.");
//          }
//          @Override
//          public void windowClosing(WindowEvent event)
//          {
//            System.out.println("Window is closing!");
//          }
//          @Override
//          public void windowClosed(WindowEvent event)
//          {
//            //System.out.println("This function is never called.");
//          }
//          @Override
//          public void windowIconified(WindowEvent event) {
//
//          }
//       });
       this.initGui();
       pack();
       setLocationRelativeTo(null);
       setVisible(true);
    }
    
    private void initGui() {
        this.add(new ChampionList(manager));
//        GridBagConstraints c = new GridBagConstraints();
//
//        removeAll();
//        revalidate();
//
//        setLayout(new GridBagLayout());       
//
//        // --->
//        c.weightx = 1.5;
//        c.weighty = 1;
//        //c.fill = GridBagConstraints.BOTH;      
//        this.add(new ChampionList(manager), c);
//        // <---
//
//        // --->
//        Panel secondPanel = new Panel(); 
//        secondPanel.setBackground(Color.green);
//
//        c.weightx = 0.25;
//        c.weighty = 1;
//        c.fill = GridBagConstraints.BOTH;
//
//        this.add(secondPanel, c);
//        this.requestFocusInWindow();    
    }
}
