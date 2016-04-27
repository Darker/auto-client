/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

/**
 *
 * @author Jakub
 */
public class Dialogs {
    public static void dialogErrorAsync(final String message, final String title, final Component parentComponent) {
        new Thread("AsyncErrorDialog") {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(
                parentComponent,
                makeTextPane(message),
                title,
                JOptionPane.ERROR_MESSAGE
            );
          }
        }.start();
    }
    public static void dialogErrorAsync(final String message) {
      dialogErrorAsync(message, "Error", null);
    }
    public static void dialogErrorAsync(final String message, final Component parentComponent) {
      dialogErrorAsync(message, "Error", parentComponent);
    }
    public static void dialogErrorAsync(final String message, final String title) {
      dialogErrorAsync(message, title, null);
    }
    public static void dialogInfoAsync(final String message, final String title, final Component parentComponent) {
        new Thread("AsyncErrorDialog") {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(
                parentComponent,
                makeTextPane(message),
                title,
                JOptionPane.INFORMATION_MESSAGE
            );
          }
        }.start();
    }
    public static void dialogInfoAsync(final String message) {
      dialogInfoAsync(message, "Error", null);
    }
    public static void dialogInfoAsync(final String message, final Component parentComponent) {
      dialogInfoAsync(message, "Error", parentComponent);
    }
    public static void dialogInfoAsync(final String message, final String title) {
      dialogInfoAsync(message, title, null);
    }
    public static JTextPane makeTextPane(String text) {
      JTextPane f = new JTextPane();
      f.setContentType("text/html"); // let the text pane know this is what you want
      f.setText("<html>"+text+"</html>"); // showing off
      f.setEditable(false); // as before
      f.setBackground(null); // this is the same as a JLabel
      f.setBorder(null); // remove the border
      return f;
    }
    
    
}
