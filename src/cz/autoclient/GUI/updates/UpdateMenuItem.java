/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.updates;

import cz.autoclient.updates.VersionId;
import java.awt.Color;
import java.awt.Container;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jakub
 */
public class UpdateMenuItem extends JMenuItem {
  double downloadProgress = 0;

  public double getProgress() {
    return downloadProgress;
  }
  public UpdateMenuItem() {
    setUI(new UpdateMenuItemUI());
  }

  public UpdateMenuItem(Icon icon) {
    super(icon);
    setUI(new UpdateMenuItemUI());
  }

  public UpdateMenuItem(String text) {
    super(text);
    setUI(new UpdateMenuItemUI());
  }

  public UpdateMenuItem(Action a) {
    super(a);
    setUI(new UpdateMenuItemUI());
  }

  public UpdateMenuItem(String text, Icon icon) {
    super(text, icon);
    setUI(new UpdateMenuItemUI());
  }

  public UpdateMenuItem(String text, int mnemonic) {
    super(text, mnemonic);
    setUI(new UpdateMenuItemUI());
  }
  public void setUnknown(VersionId version) {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setUnknown(version));
      return;
    }
    setText("Running "+version+" - click to check");
    setBackground(new Color(188, 188, 188));
    downloadProgress = 0;
  }
  public void setUpToDate(VersionId version) {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setUpToDate(version));
      return;
    }
    setText("Up to date: "+version);
    setBackground(new Color(173, 206, 182));
    downloadProgress = 0;
  }
  public void setDownloadAvailable(final VersionId version) {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setDownloadAvailable(version));
      return;
    }
    setText("Click to download: "+version);
    setBackground(Color.ORANGE);
    this.revalidate();
    downloadProgress = 0;
  }
  public void setDownloadProgress(VersionId version, double progress) {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setDownloadProgress(version, progress));
      return;
    }
    if(progress<100)
      setText("Downloading "+version+" "+Math.round(progress*100)+"%");
    else {
      setText("Downloaded "+version);
      setBackground(Color.GREEN);
    }
    downloadProgress = progress;
    //setBackground(Color.ORANGE);
  }
  public void setDownloaded(VersionId version) {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setDownloaded(version));
      return;
    }
    setText("Downloaded "+version+" - click to restart and install");
    setBackground(Color.GREEN);
    //setBackground(Color.ORANGE);
  }

  public void setChecking() {
    if(!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(()->setChecking());
      return;
    }
    setText("Checking for updates...");
    setBackground(new Color(200, 200, 200));
  }
  
  @Override
  public void setText(String text) {
    super.setText(text);
    Container c = SwingUtilities.getUnwrappedParent(this);
    if(c instanceof JPopupMenu) {
      JPopupMenu cm = (JPopupMenu)c;
      cm.pack();
      //System.out.println("Resized menu.");
    }
    else {
      if(c!=null)
        System.out.println("Error: Unexpected update menu item parent class: "+c.getClass().getName());
      //else
        //System.out.println("Error: Update parent class is fucking NULL!");
    }
  }
}
