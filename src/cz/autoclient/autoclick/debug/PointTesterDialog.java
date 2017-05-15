/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.debug;

import cz.autoclient.GUI.Dialogs;
import cz.autoclient.PVP_net.ConstData;
import cz.autoclient.PVP_net.PixelOffset;
import cz.autoclient.autoclick.*;
import cz.autoclient.autoclick.comvis.DebugDrawing;
import cz.autoclient.autoclick.windows.Window;
import cz.autoclient.autoclick.windows.ms_windows.MSWindow;
import cz.autoclient.main_automation.WindowTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Jakub
 */
public class PointTesterDialog extends JFrame {
  protected Enum<? extends GraphicPredicate>[] entries;

  JList list;
  public PointTesterDialog(Enum<? extends GraphicPredicate>[] entries) {
    super();
    this.entries = entries;
    
    setTitle("Debug");

    JPanel content = new JPanel(new BorderLayout());

    list = new JList(entries);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    list.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                System.out.println("Doubleclick: "+list.getSelectedValue().toString());
                PointTesterDialog.this.test();
            }
        }
    });
    //content.add(list);

    JScrollPane listScroller = new JScrollPane(list);
    listScroller.setPreferredSize(new Dimension(250, 120));
    listScroller.setAlignmentX(LEFT_ALIGNMENT);
    
    // Fill the contents and remove decorations

    content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
    JLabel label = new JLabel("Hovno");
    label.setLabelFor(list);
    content.add(label);
    content.add(Box.createRigidArea(new Dimension(0,5)));
    content.add(listScroller);
    content.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    
    setContentPane(content);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //setUndecorated(true);
    getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
    //Display the window.
    pack();
    // Center the window
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);


    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {}
    });
  }
  
  protected Window getWindowForTesting() {
    // Try all know window names
    final String[] names = {
      ConstData.window_title_part,
      ConstData.patcher_window_title
    };
    Window window = null;
    for(String name: names) {
      window = MSWindow.windowFromName(name, false);
      if(window!=null)
        return window;
    }
    // No window found so show error and fail
    Dialogs.dialogErrorAsync("Cannot access main window!", "Point testing", this);
    return null;
   
  }
  protected void test() {
    Window window = getWindowForTesting();
    if( window!=null ) {
      Object v = list.getSelectedValue();
      if( v!=null ) {
        BufferedImage im = window.screenshot();
        im = doTestOnScreenshot(im, v);
        try {
          DebugDrawing.displayImage(im, "Point test", false);
        } catch (InterruptedException ex) {
          return;
        }
      }
    }
  }
  protected BufferedImage doTestOnScreenshot(BufferedImage im, Object selectedValue) {
    if(selectedValue instanceof ComparablePixel) {
      ComparablePixel pixel = (ComparablePixel)selectedValue;
      boolean result = WindowTools.checkPoint(im, pixel);
      DebugDrawing.drawPoint(im, pixel, result?Color.green:Color.red);
    }
    else if(selectedValue instanceof PixelGroupWithPixels) {
      PixelGroupWithPixels group = (PixelGroupWithPixels)selectedValue;
      ComparablePixel[] pxs = group.getPixels();
      WindowTools.drawCheckPoint(im, pxs);
    }
    return im;
  }

  public static void main(String[] args) {
    PointTesterDialog dialog = new PointTesterDialog(PixelOffset.values());
    dialog.setVisible(true);
  }
  
}
