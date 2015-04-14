/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.champion;

import cz.autoclient.GUI.ImageResources;
import cz.autoclient.GUI.summoner_spells.ButtonSummonerSpell;
import cz.autoclient.PVP_net.Constants;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.settings.Settings;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class ConfigurationManager {
  public final JComboBox input;
  public final JButton save;
  public final JButton delete;
  public static final List<String> names = java.util.Arrays.asList(new String[] {
    Setnames.BLIND_MASTERY.name,
    Setnames.BLIND_RUNE.name,
    Setnames.BLIND_SUMMONER1.name,
    Setnames.BLIND_SUMMONER2.name,
    Setnames.BLIND_CALL_TEXT.name
  });
  /**
   * Map of settings per champion name.
   */
  private HashMap<String, Settings> settings;
  
  private Settings main_settings;
  
  private static int instID = 0;
  
  public final String setting_name;
  protected String currentSetup = null;
  protected String currentChampion = null;
  
  private static List<String> champion_names;
  
  public ConfigurationManager(JComboBox input, Settings main) {
    setting_name = "ConfigurationManager_"+(instID++);
    //Initialise settings
    main_settings = main;
    
    if(main.exists(setting_name, HashMap.class)) {
      settings = (HashMap)main.getSetting(setting_name); 
    }
    else {
      settings = new HashMap<String, Settings>();
      //Add settings to main settings object
      main.setSetting(setting_name, settings);
    }
    
    this.input = input;
    this.save = new JButton();
    this.delete = new JButton();
    createGUI();
  }
  private void createGUI() {
    save.setIcon(ImageResources.SAVE.getIcon());
    save.setBorder(ButtonSummonerSpell.emptyBorder);
    save.setContentAreaFilled(false);
    save.setEnabled(false);
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConfigurationManager.this.saveSettings();
      }
    });

    delete.setIcon(ImageResources.DELETE.getIcon());
    delete.setBorder(ButtonSummonerSpell.emptyBorder);
    delete.setContentAreaFilled(false);
    delete.setEnabled(false);
    delete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConfigurationManager.this.deleteSettings();
      }
    });

    //Add event listener
    //input.addKeyListener();
    Component[] comps = input.getComponents();
    //input.remove(comps[0]);
    //comps[0].setVisible(false);
    //Third is the textfieldcomponent
    /*comps[2].addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        ConfigurationManager.this.updateButtons();
      }
      @Override
      public void keyPressed(KeyEvent e) {}
      @Override
      public void keyReleased(KeyEvent e) {
        ConfigurationManager.this.updateButtons();
      }
    });*/
    input.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        ConfigurationManager.this.updateButtons();
      }
    });
    
    input.addActionListener (new ActionListener () {
        @Override
        public void actionPerformed(ActionEvent e) {
          ConfigurationManager.this.updateButtons();
        }
    });
    
    
    //input.setInputVerifier(new ButtonUpdater());
    //System.out.println("Combo box value: ");
    //Gui.debugInspectElement(input);
  }
  /**
   * Call this bebore saving main settings.
   */
  public void cleanBeforeSave() {
    //Remove the null entry. Last entry shall be remembered anyway, so 
    //null entry would be overwrited once the user starts typping in the field
    //Nobody cares about the null entry.
    settings.remove(null); 
  }
  protected void updateButtons() {
    //Component[] comps = input.getComponents();
    String item = (String)input.getEditor().getItem();//(String)input.getSelectedItem();
    
    if(champion_names==null)
      champion_names = Constants.lolData.getChampions().allNamesList();
    if(item!=null) {
      //System.out.println("JComboBox.getEditor().getItem() = "+input.getEditor().getItem()+"");
      //System.out.println("JComboBox.getSelectedItem()     = "+input.getSelectedItem()+"");

      if(champion_names.contains(item)) {
        //Avoid repetitive loading...
        if(currentChampion!=null && currentChampion.equals(item))
          return;
        save.setEnabled(true);
        //save old setting - only if already exists or is null
        if(currentSetup==null&&currentChampion==null || currentSetup!=null&&!currentSetup.equals(item)&&settings.containsKey(currentSetup)) {
          saveSettings();
        }

        currentChampion = item;
        loadSettings();
        if(settings.containsKey(item)) {
          delete.setEnabled(true); 
        }
        else {
          delete.setEnabled(false);  
        }
      }
      else {
        if(currentSetup!=null&&settings.containsKey(currentSetup)) {
          saveSettings();
        }
        currentChampion = null;
        loadSettings();
        save.setEnabled(false);
        delete.setEnabled(false); 
      }
    }
  }
  protected void saveSettings() {
    Settings set;
    if(!settings.containsKey(currentChampion))
      settings.put(currentChampion, set=new Settings());
    else
      set = settings.get(currentChampion);
    main_settings.copyTo(set, names);
    //Now, the current setup has a name
    currentSetup = currentChampion;
    //Enable delete for non-null setups
    if(currentSetup!=null)
      delete.setEnabled(true);
    
    System.out.println("Saved settings for "+currentChampion+". "+settings.size()+" settings total.");
  }
  protected void loadSettings(String name) {
    Settings set = settings.get(name);
    if(set!=null) {
      set.copyTo(main_settings, names);
      main_settings.displaySettingsOnBoundFields(names);
      currentSetup = name;
    }
    else {
      currentSetup = null;
    }
    System.out.println("Loaded settings for "+name);
    if(currentSetup==null && name!=null)
      System.out.println("  Acutally loaded null, settings were unavalable.");
  }
  protected void loadSettings() {
    loadSettings(currentChampion);
  }
  protected void deleteSettings() {
    settings.remove(currentChampion);
    loadSettings(null);    
  }
  
  protected class ButtonUpdater extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
      System.out.println("Combo box value: "+(String)ConfigurationManager.this.input.getSelectedItem());
      return false;
    }
  }
}
