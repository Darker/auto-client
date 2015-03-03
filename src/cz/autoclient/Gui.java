package cz.autoclient;

import cz.autoclient.GUI.*;
import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.automat_settings.Settings;
import java.awt.Color;
 import java.awt.Container;
 import java.awt.Dialog;
 import java.awt.Dimension;
import java.awt.GridLayout;
 import java.awt.Insets;
 import java.awt.Rectangle;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
 import java.beans.PropertyChangeEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
 import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
 import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
import javax.swing.JPanel;
 import javax.swing.JSpinner;
 import javax.swing.JTextField;
 import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
 import javax.swing.SpinnerNumberModel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 public class Gui
   extends JFrame
 {
   private Main ac;
   private Settings settings;
   private String[] selected;
   
   public String[] getSelectedMode()
   {
     return new String[0];//(String[]) (selected != null ? selected : "");
   }
   
   public Gui(Main acmain, final Settings settings)
   {
     this.ac = acmain;
     this.settings = settings;
     
     initComponents();

     this.addWindowListener(new WindowAdapter()
     {
        @Override
        public void windowClosing(WindowEvent event)
        {
          if(settings==null) {
            System.out.println("Settings is null!");
            return;
          }
          try {
            settings.loadSettingsFromBoundFields();
            settings.saveToFile(Main.SETTINGS_FILE, false);
          }
          catch(IOException e) {
             
          }
        }
     });
     setSize(500, 300);
   }
   
   private void ModeSelected(PropertyChangeEvent e)
   {
     if (e.getOldValue() != null) {
       try
       {
         this.selected = e.getNewValue().toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
         setTitle("AC: " + e.getNewValue().toString().split(",")[(e.getNewValue().toString().split(",").length - 1)].replace("]", "").replace(" ", ""));
       }
       catch (NullPointerException e2) {}
     } else {
       setTitle("AutoCall");
     }
   }
   
   public void displayToolAction(boolean state) {
     toggleButton1.setText(state ? "Stop" : "Start");
     toggleButton1.setSelected(state);
   }
   public void displayToolAction() {
     displayToolAction(ac.ToolRunning());
   }
   
   private boolean ToolAction()
   {
     boolean toolState = ac.ToolRunning();
     displayToolAction(!toolState);
     
     if (!toolState) {
       ac.StartTool();
     } else {
       ac.StopTool();
     }
     return !toolState;
   }

   private boolean ToolAction(ActionEvent e) {
     return ToolAction();
   }
 
   
   public JToggleButton getToggleButton1()
   {
     return this.toggleButton1;
   }
   

   
 
  
   private void DelaySelected(ActionEvent e)
   {
     this.chatDialog.setVisible(true);
   }
   
   private void ChatOKClicked(ActionEvent e)
   {
     this.chatDialog.setVisible(false);
   }
   
   public int getDelay()
   {
     return (int)(((Double)this.spinner1.getValue()).doubleValue() * 1000.0D);
   }
   
   public JSpinner getSpinner1()
   {
     return this.spinner1;
   }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Jakub Mareda
        menuBar1 = new JMenuBar();

        toggleButton1 = new JToggleButton();
        chatDialog = new Dialog(this);
        label1 = new JLabel();
        spinner1 = new JSpinner();
        button1 = new JButton();

        //======== this ========
        setTitle("Application - stopped");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        createTabs(contentPane);

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1 = new JMenu();
                menuItem1 = new JMenuItem();
                menu1.setText("Settings");

                //---- menuItem1 ----
                menuItem1.setText("Set Chat Delay");
                menuItem1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DelaySelected(e);
                    }
                });
                menu1.add(menuItem1);
                

            }
            menuBar1.add(menu1);
            
            /*// Start button
            JMenuItem startbut = new JMenuItem();
            startbut.setText("Start");
            startbut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ToolAction();
                }
            });
            menuBar1.add(startbut);*/
            
        }
        setJMenuBar(menuBar1);

        //---- champField ----
        /*champField.setToolTipText("Enter Champion Name");
        contentPane.add(champField);
        settings.bindToInput("champ_name", champField, true);
        champField.setBounds(1, 151, 68, champField.getPreferredSize().height);

        //---- textField2 ----
        textField2.setToolTipText("Enter AutoCall Text");
        contentPane.add(textField2);
        settings.bindToInput("call_text", textField2, true);
        textField2.setBounds(71, 151, 69, 20);*/

        //---- toggleButton1 ----
        toggleButton1.setText("Start");
        toggleButton1.setToolTipText("Start/Stop tool");
        toggleButton1.setFocusable(true);
        toggleButton1.setFocusPainted(false);
        toggleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolAction(e);
            }
        });
        menuBar1.add(toggleButton1);
        //contentPane.add(toggleButton1);
        //toggleButton1.setBounds(142, 151, 69, 19);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());

        //======== chatDialog ========
        {
            chatDialog.setTitle("Chat Delay");
            chatDialog.setResizable(false);

            //---- label1 ----
            label1.setText("Enter a delay:");
            label1.setToolTipText("Sets an additional delay before tool types your message. (Seconds)");

            //---- spinner1 ----
            spinner1.setModel(new SpinnerNumberModel(0.0, 0.0, 5.0, 0.1));

            //---- button1 ----
            button1.setText("OK");
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ChatOKClicked(e);
                }
            });

            GroupLayout chatDialogLayout = new GroupLayout(chatDialog);
            chatDialog.setLayout(chatDialogLayout);
            chatDialogLayout.setHorizontalGroup(
                chatDialogLayout.createParallelGroup()
                    .addGroup(chatDialogLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(button1)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(chatDialogLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(label1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
            chatDialogLayout.setVerticalGroup(
                chatDialogLayout.createParallelGroup()
                    .addGroup(chatDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(chatDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label1)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button1)
                        .addContainerGap())
            );
            chatDialog.pack();
            chatDialog.setLocationRelativeTo(chatDialog.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        
        //Display settings:
        settings.displaySettingsOnBoundFields();
    }
    //http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
    public void createTabs(Container pane) {
        pane.setLayout(new GridLayout(1, 1));
        System.out.println("Creating tabbed window.");
        TabbedWindow win = new TabbedWindow();
        
        win.newTab("Blind pick lobby", "Lobby where lane is called and champion is picked");
        
        
        FieldDef field = new FieldDef("Champion:", "Enter champion name", "champ_name");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        System.out.println(" Adding first line.");
        win.addLine(field);
        
        
        field = new FieldDef("Call text:", "Enter text to say after entering lobby.", "call_text");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        System.out.println(" Adding second line.");
        win.addLine(field);
        
        win.newTab("Team builder", "All teambuilder automation");
        
        field = new FieldDef("Enabled:", "Enable or disable this function.", Setnames.TEAMBUILDER_ENABLED.name);
        field.addField(new JCheckBox());
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Greet new player:", "As captain, you'll automatically call this to newcomers", "tb_cap_greet");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        win.addLine(field);
        
        field = new FieldDef("Lock call:", "Sentence you call when everybody should lock in.", "tb_cap_lock");
        field.addField(new JTextField());
        field.attachToSettings(settings);
        win.addLine(field);
        
        
        win.newTab("Invite friends", "Start automatically when everybody accepts.");
        
        field = new FieldDef("Auto start:", "Enable or disable this function.", Setnames.INVITE_ENABLED.name);
        field.addField(new JCheckBox());
        field.attachToSettings(settings);
        win.addLine(field);

        pane.add(win.container);
        win.close();
        System.out.println("Done.");
        /*
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = null;
        JPanel panel1 = new JPanel(false);
        
        GroupLayout gLayout = new GroupLayout(panel1);
        panel1.setLayout(gLayout);
        ParallelGroup hGroup = gLayout.createParallelGroup();
        gLayout.setHorizontalGroup(hGroup);
        SequentialGroup vGroup = gLayout.createSequentialGroup();
        gLayout.setVerticalGroup(vGroup);

        JPanel line = newLine();
        line.add(makeTextPanel());
        JTextField field = new JTextField();
        field.setToolTipText("Enter champion name");
        line.add(field);
        
        endLine(hGroup, vGroup, line);
        
        line = newLine();
        line.add(makeTextPanel("Call text:"));
        field = new JTextField();
        field.setToolTipText("Enter text to say after entering lobby.");
        line.add(field);
        
        endLine(hGroup, vGroup, line);
        
        //field.setBounds(100, 151, 68, field.getPreferredSize().height);
        
        tabbedPane.addTab("Blind pick lobby", icon, (JComponent)panel1,
                "Lobby where lane is called and champion is picked");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_B);

        
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        //Add the tabbed pane to this panel.
        pane.add(tabbedPane);
        */
        //The following line enables to use scrolling tabs.
        
   }
   protected static JPanel newLine() {
     JPanel line = new JPanel();
     line.setLayout(new GridLayout(1, 1));
     line.setBorder(BorderFactory.createLineBorder(Color.red));
     return line;
   }
   protected JPanel textLine(String label, String title, String setting) {
     JPanel line = newLine();
     line.add(makeTextPanel("Champion:"));
     JTextField field = new JTextField();
     field.setToolTipText("Enter champion name");
     if(setting!=null) {
       settings.bindToInput(setting, field);
     }
     line.add(field);
     return line;
   }
   protected static void endLine(ParallelGroup h, SequentialGroup v, JComponent line) {
     h.addComponent(line);
     v.addComponent(line, GroupLayout.PREFERRED_SIZE,
            GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
     v.addGap(10);
   }
    protected static JComponent makeTextPanel(String text) {
        //JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        return filler;
        //panel.setLayout(new GridLayout(1, 1));
        //panel.add(filler);
        //return panel;
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Gui.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Jakub Mareda
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;



    //private JTextField champField;
    //private JTextField textField2;
    private JToggleButton toggleButton1;
    private Dialog chatDialog;
    private JLabel label1;
    private JSpinner spinner1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
 }

