/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import cz.autoclient.GUI.urls.AcURL;
import cz.autoclient.GUI.urls.BasicURL;
import cz.autoclient.GUI.urls.LazyURL;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 *
 * @author Jakub
 */
public class URLMenuItem extends JMenuItem {
  public final AcURL url;
  public URLMenuItem(String text, String title, AcURL url, ImageResources icon) {
    super(text);
    this.url = url;
    this.setToolTipText(title);
    addActionListener(new URLOpener(this.url));
    if(icon != null) {
      icon.setIconAsync(this);
    }
  }

  public URLMenuItem(String text, String title, String url, ImageResources icon) {
    this(text, title,  new BasicURL(url), icon);
  }
  public URLMenuItem(String text, String title, String url) {
    this(text, title, new BasicURL(url), null);
  }
  public URLMenuItem(String text, String title, LazyURL url) {
    this(text, title, url, null);
  }

  
  public static class URLOpener implements ActionListener {
    public final AcURL link;
    public URLOpener(AcURL link) {
      this.link = link;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      displayPage(link);
    }
  };
  
  
  
  private static Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
  public static void displayPage(AcURL page) {
    if(page==null) {
      Gui.inst.dialogErrorAsync("This menu item is broken and tries to display invalid (null) URL. Contact the developer.");
    }
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(page.toURI());
      } catch (Exception e) {
        Gui.inst.dialogErrorAsync("Due to an error, the page can't"
            + " be opened in your browser. The error was\n    "+e+"\n"
            + "You can manually type the following url of the help page: "+page.toString());
      }
    }
    else {
        Gui.inst.dialogErrorAsync("Your system doesn't support opening URL in browser. Try to open it manually:\n"
            +page.toString());
    }
  }
}
