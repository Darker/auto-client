/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows;

import cz.autoclient.autoclick.exceptions.APIException;
import cz.autoclient.autoclick.exceptions.WindowAccessDeniedException;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Jakub
 */
public class ClickProxy implements Runnable {
  private Window window;
  private JFrame frame;
  private Thread thread;
  
  public ClickProxy(Window window) {
    this.window = window;
  }

  public Window getWindow() {
    return window;
  }
  public void setWindow(Window window) {
    if(window==this.window)
      return;
    boolean running = thread!=null && thread.isAlive();
    if(running)
      thread.interrupt();
    this.window = window;
    if(running)
      start();
  }
  public boolean isRunning() {
    return thread!=null && thread.isAlive();
  }
  public void start() {
    if(thread!=null && thread.isAlive())
      throw new IllegalStateException("The mouse proxy UI is already running.");
    if(window==null)
      throw new IllegalStateException("Window is null, the mouse proxy cannot start.");
    
    thread = new Thread(this, "Mouse proxy UI");
    thread.start();
  }
  public void stop() {
    thread.interrupt();
  }
  @Override
  public void run() {
    if(thread==null || thread!=Thread.currentThread())
      throw new IllegalStateException("Running from wrong thread. Terminating now.");
    //The window
    frame = new JFrame();
    //Topmost component of the window
    Container main = frame.getContentPane();
    //Turns out this is probably the simplest way to render image on screen 
    //with guaranteed 1:1 aspect ratio
    final ScreenDisplay display = new ScreenDisplay(window, frame);
    MouseProxy mprox = new MouseProxy();
    display.addMouseListener(mprox);
    display.addMouseMotionListener(mprox);

    //Put the image drawer in the topmost window component
    main.add(display);
    //System.out.println(image.getWidth(null)+", "+image.getHeight(null));
    //frame.pack();

    //Set window size to the image size plus some padding dimensions
    frame.pack();
    frame.setVisible(true);

    frame.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent e) {}
      @Override
      public void windowClosing(WindowEvent e) {
        synchronized(thread) {thread.notify();}
        //System.out.println("Closing");
        frame.dispose();
      }
      @Override
      public void windowClosed(WindowEvent e) {
         //System.out.println("Closed");
         //synchronized(t) {
           //t.notify();
         //}
      }
      @Override
      public void windowIconified(WindowEvent e) {}
      @Override
      public void windowDeiconified(WindowEvent e) {}
      @Override
      public void windowActivated(WindowEvent e) {}
      @Override
      public void windowDeactivated(WindowEvent e) {}
    });
   
    ActionListener task = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //System.out.println("Redrawing");
        try {
          display.forceRedraw();
        }
        catch(APIException error) {
          //Terminate the program if window is lost
          frame.setVisible(false);
          frame.dispose();
          error.printStackTrace();
          synchronized(thread) {thread.notify();}
        }
      }
    };
    Timer timer = new Timer(100, task);
    timer.start();
    synchronized(thread) {
      try {
        thread.wait();
      }
      catch(InterruptedException e) {
        //Hide and destroy the window if it's still visible
        if(frame.isDisplayable()) {
          frame.setVisible(false);
          frame.dispose();
        }
      }
     //System.out.println("Wait over.");
    }
    timer.stop();
  }
  private class MouseProxy implements MouseListener, MouseMotionListener {
    @Override
    public void mouseClicked(MouseEvent e) {
      //System.out.println("Mouseclick: ["+e.getX()+", "+e.getY()+"]"+MouseButton.fromJavaAwtMouseEvent(e).name());
      //window.click(e.getX(), e.getY(), MouseButton.fromJavaAwtMouseEvent(e));
    }
    @Override
    public void mousePressed(MouseEvent e) {
      //System.out.println("Mousedown: ["+e.getX()+", "+e.getY()+"]"+MouseButton.fromJavaAwtMouseEvent(e).name());
      try {
        window.mouseDown(e.getX(), e.getY(), MouseButton.fromJavaAwtMouseEvent(e));
      }
      catch(WindowAccessDeniedException ex) {
        ClickProxy.this.stop();
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      //System.out.println("Mouseup: ["+e.getX()+", "+e.getY()+"] "+MouseButton.fromJavaAwtMouseEvent(e).name());
      try {
        window.mouseUp(e.getX(), e.getY(), MouseButton.fromJavaAwtMouseEvent(e));
      }
      catch(WindowAccessDeniedException ex) {
        ClickProxy.this.stop();
      }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      //window.mouseOver(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
      //No way to proxy this now
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      try {
        window.mouseOver(e.getX(), e.getY());
      }
      catch(WindowAccessDeniedException ex) {
        ClickProxy.this.stop();
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      try {
        window.mouseOver(e.getX(), e.getY());
      }
      catch(WindowAccessDeniedException ex) {
        ClickProxy.this.stop();
      }
    }
  }
  private static class ScreenDisplay extends JPanel {
    private BufferedImage image;
    private Window window;
    private JFrame parentFrame;
    public ScreenDisplay(Window w, JFrame fr) {
      this.window = w;
      parentFrame = fr;
    }
    public BufferedImage getImage() {
      return image!=null?image:(image=window.screenshot());
    }

    public void forceRedraw() {
      parentFrame.repaint();
      parentFrame.pack();
    }
    private long lastDraw = 0;
    private final int MAX_FPS = 30;
    private final int MIN_INTERVAL = 1000/MAX_FPS;
    
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      long time = System.currentTimeMillis();
      if(image!=null && MIN_INTERVAL>time-lastDraw) {
        g.drawImage(image, 0, 0, null);          
      }
      else {
        try {
          g.drawImage(image=window.screenshot(), 0, 0, null);
          lastDraw = time;
        }
        catch(APIException e) {
          if(image!=null) {
            g.drawImage(image, 0, 0, null);
          }
        }
      }
    }
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(getImage().getWidth(null), getImage().getHeight(null));
    }
  }
}
