package cz.autoclient.GUI.dialogs.champions;

import cz.autoclient.GUI.champion.ConfigurationManager;
import cz.autoclient.settings.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;


/**
 * This class represents list of champion names to be configured.
 * @author Jakub
 */
public class ChampionList extends JPanel {
    private JPanel mainList;
    // name of the setting where champ settings are saved
    private final ConfigurationManager manager;
    
    public ChampionList(ConfigurationManager manager) {
        setLayout(new BorderLayout());
        this.manager = manager;
        mainList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        mainList.add(new JPanel(), gbc);
        add(new JScrollPane(mainList));
        addAllChampions();
//        JButton add = new JButton("Add");
//        add.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JPanel panel = new JPanel();
//                panel.add(new JLabel("Hello"));
//                panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
//                GridBagConstraints gbc = new GridBagConstraints();
//                gbc.gridwidth = GridBagConstraints.REMAINDER;
//                gbc.weightx = 1;
//                gbc.fill = GridBagConstraints.HORIZONTAL;
//                mainList.add(panel, gbc, 0);
//
//                validate();
//                repaint();
//            }
//        });
        //add(add, BorderLayout.SOUTH);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }
    protected class ChampionInfo {
        public final String name;
        public final boolean hasSettings;
        public ChampionInfo(String name, boolean hasSettings) {
          this.name = name;
          this.hasSettings = hasSettings;
        }
    }
    protected void addChampion(String name) {
        addChampions(new ArrayList<ChampionInfo>(Arrays.asList(new ChampionInfo[]{new ChampionInfo(name, false)})));
    }
    protected void addChampions(List<ChampionInfo> names) {
        for(int i=0,l=names.size(); i<l; ++i) {
            JPanel panel = new JPanel();
            panel.add(new JLabel(names.get(i).name));
            panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            mainList.add(panel, gbc, 0);
        }
        validate();
        repaint();
    }
    protected void addAllChampions() {
        HashMap<String, Settings> settings = (HashMap)manager.getSettings().getSetting(manager.setting_name);
        ArrayList<ChampionInfo> list = new ArrayList();
        for(Map.Entry<String, Settings> entry : settings.entrySet()) {
            list.add(new ChampionInfo(
                entry.getKey(), false
            ));
        }
        addChampions(list);
    }
}
