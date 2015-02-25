import java.awt.*;
 import java.awt.Container;
 import java.awt.Dialog;
 import java.awt.Dimension;
 import java.awt.Insets;
 import java.awt.Rectangle;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.beans.PropertyChangeEvent;
 import java.beans.PropertyChangeListener;
import javax.swing.*;
 import javax.swing.GroupLayout;
 import javax.swing.JButton;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JProgressBar;
 import javax.swing.JScrollPane;
 import javax.swing.JSpinner;
 import javax.swing.JTextField;
 import javax.swing.JToggleButton;
 import javax.swing.JTree;
import javax.swing.LayoutStyle;
 import javax.swing.SpinnerNumberModel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeModel;
 
 public class GUINew
   extends JFrame
 {
   private Main ac;
   private String[] selected;


   
   public String[] getSelectedMode()
   {
     return (String[]) (selected != null ? selected : "");
   }
   
   public GUINew(Main acmain)
   {
     this.ac = acmain;
     initComponents();
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
   
   private void toggleButton1StateChanged(ChangeEvent e) {
   
   
   
   
   }
   
   private void ToolAction(ActionEvent e)
   {
     this.toggleButton1.setText(this.toggleButton1.isSelected() ? "Stop" : "Start");
     if (this.toggleButton1.isSelected()) {
       ac.StartTool();
     } else {
       ac.StopTool();
     }
   }
   public void setToggleButton1State(boolean state) {
     toggleButton1.setSelected(state);
     toggleButton1.setText(state ? "Stop" : "Start");
   }
   
   public JToggleButton getToggleButton1()
   {
     return this.toggleButton1;
   }
   
   public JTextField getChampField()
   {
     return this.champField;
   }
   
   public JTextField chatTextField()
   {
     return this.textField2;
   }
   
   public JTextField getTextField2()
   {
     return this.textField2;
   }
   
   public JProgressBar getProgressBar1()
   {
     return this.progressBar1;
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

        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        progressBar1 = new JProgressBar();
        scrollPane1 = new JScrollPane();
        tree1 = new JTree();
        champField = new JTextField();
        textField2 = new JTextField();
        toggleButton1 = new JToggleButton();
        chatDialog = new Dialog(this);
        label1 = new JLabel();
        spinner1 = new JSpinner();
        button1 = new JButton();

        //======== this ========
        setTitle("Auto call");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
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
        }
        setJMenuBar(menuBar1);

        //---- progressBar1 ----
        progressBar1.setOrientation(SwingConstants.VERTICAL);
        contentPane.add(progressBar1);
        progressBar1.setBounds(0, 0, 18, 151);

        //======== scrollPane1 ========
        {

            //---- tree1 ----
            tree1.setFont(tree1.getFont().deriveFont(tree1.getFont().getSize() - 1f));
            tree1.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("Game Mode") {
                    {
                        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("PvP");
                            DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Classic");
                                DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("5v5");
                                    node3.add(new DefaultMutableTreeNode("Blind"));
                                    node3.add(new DefaultMutableTreeNode("Draft"));
                                node2.add(node3);
                                node3 = new DefaultMutableTreeNode("3v3");
                                    node3.add(new DefaultMutableTreeNode("Blind"));
                                node2.add(node3);
                            node1.add(node2);
                            node2 = new DefaultMutableTreeNode("Dominion");
                                node2.add(new DefaultMutableTreeNode("Blind"));
                                node2.add(new DefaultMutableTreeNode("Draft"));
                            node1.add(node2);
                            node1.add(new DefaultMutableTreeNode("ARAM"));
                        add(node1);
                        node1 = new DefaultMutableTreeNode("Co-op vs. AI");
                            node2 = new DefaultMutableTreeNode("Classic");
                                node3 = new DefaultMutableTreeNode("5v5");
                                    node3.add(new DefaultMutableTreeNode("Beginner"));
                                    node3.add(new DefaultMutableTreeNode("Intermediate"));
                                node2.add(node3);
                                node3 = new DefaultMutableTreeNode("3v3");
                                    node3.add(new DefaultMutableTreeNode("Beginner"));
                                    node3.add(new DefaultMutableTreeNode("Intermediate"));
                                node2.add(node3);
                            node1.add(node2);
                            node2 = new DefaultMutableTreeNode("Dominion");
                                node2.add(new DefaultMutableTreeNode("Beginner"));
                                node2.add(new DefaultMutableTreeNode("Intermediate"));
                            node1.add(node2);
                        add(node1);
                    }
                }));
            tree1.setRootVisible(false);
            tree1.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    ModeSelected(e);
                }
            });
            scrollPane1.setViewportView(tree1);
        }
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(18, 0, 194, 151);

        //---- champField ----
        champField.setToolTipText("Enter Champion Name");
        contentPane.add(champField);
        champField.setBounds(1, 151, 68, champField.getPreferredSize().height);

        //---- textField2 ----
        textField2.setToolTipText("Enter AutoCall Text");
        contentPane.add(textField2);
        textField2.setBounds(71, 151, 69, 20);

        //---- toggleButton1 ----
        toggleButton1.setText("Start");
        toggleButton1.setToolTipText("Start/Stop tool");
        toggleButton1.setFocusable(false);
        toggleButton1.setFocusPainted(false);
        toggleButton1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                toggleButton1StateChanged(e);
            }
        });
        toggleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolAction(e);
            }
        });
        contentPane.add(toggleButton1);
        toggleButton1.setBounds(142, 151, 69, 19);

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
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Jakub Mareda
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JProgressBar progressBar1;
    private JScrollPane scrollPane1;
    private JTree tree1;
    private JTextField champField;
    private JTextField textField2;
    private JToggleButton toggleButton1;
    private Dialog chatDialog;
    private JLabel label1;
    private JSpinner spinner1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
 }
