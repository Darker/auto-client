/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.dialogs;

import cz.autoclient.settings.Settings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jakub
 */

public class DoNotAskAgainPanel extends JPanel {

    private final JCheckBox dontAskMeAgain;
    //protected final Settings settings;
    //protected final String settingName;

    public DoNotAskAgainPanel(Object message) {
        // Construct GUI
        setLayout(new BorderLayout());
        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            add(new JLabel(message.toString()));
        }
        dontAskMeAgain = new JCheckBox("Don't show this dialog again");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(dontAskMeAgain);
        add(panel, BorderLayout.SOUTH);
        
        // Assign finals
          // nope
    }

    public boolean dontAskMeAgain() {
        return dontAskMeAgain.isSelected();
    }
    public void setDontAskMeAgain(boolean val) {
        dontAskMeAgain.setSelected(val);
    }

}