/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;

public class ProgressBarWindow extends JFrame {

    private JProgressBar progressBar;
    private JTextArea taskOutput;




    public ProgressBarWindow() {
        super();
        setTitle("Installing updates.");
        
        JPanel content = new JPanel(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 40);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Installing updates: "));
        panel.add(progressBar);

        //content.add(panel, BorderLayout.PAGE_START);
        content.add(panel, BorderLayout.NORTH);
        content.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.setOpaque(true); //content panes must be opaque        
        
        enableDrag(content);
        // Fill the contents and remove decorations
        setContentPane(content);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //Display the window.
        pack();
        setVisible(true);
        // Center the window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);


        addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            
          }
        });
    }

    private void enableDrag(JPanel main) {
      final JFrame frame = this;
      MouseAdapter adapter = new MouseAdapter() {
         // Position where the mouse was when drag started
         private Point dragStart = null;
         // position where window was when drag started
         private Point dragStartOffset = null;
         @Override
         public void mousePressed(MouseEvent e) {
            dragStart = e.getLocationOnScreen();
            dragStartOffset = frame.getLocationOnScreen();
         }
         @Override
         public void mouseReleased(MouseEvent e) {
            dragStart = null;
            dragStartOffset = null;
         }
         @Override
         public void mouseExited(MouseEvent e) {
            //dragStart = null;
            //dragStartOffset = null;
         }
         @Override
         public void mouseDragged(MouseEvent e) {
           //System.out.println("Move: "+e.getLocationOnScreen());
           if(dragStart!=null) {
             Point current = e.getLocationOnScreen();
             current.translate(-dragStart.x, -dragStart.y);
             frame.setLocation(dragStartOffset.x+current.x, dragStartOffset.y+current.y);
           }
         }
      };
      this.rootPane.addMouseListener(adapter);
      main.addMouseListener(adapter);
      progressBar.addMouseListener(adapter);
      
      this.rootPane.addMouseMotionListener(adapter);
      main.addMouseMotionListener(adapter);
      progressBar.addMouseMotionListener(adapter);
    }
    

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        new ProgressBarWindow();
    }
    public void setProgress(double current, double max) {
      progressBar.setValue((int)current);
      progressBar.setMaximum((int)max);
    }
    public void close() {
      setVisible(false);
      dispose();
    }
    public void status(String status) {
      taskOutput.append(status+"\n");
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}