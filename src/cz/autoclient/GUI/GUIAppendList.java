/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * This class allows you to prepare a list of swing elements that should be
 * joined together. After you create the list, use .create() method which will
 * then add all the items within the Swing main thread. This is done using
 * invokeLater.
 * @author Jakub
 */
public class GUIAppendList extends ArrayList<GUIAppendList.AddChild> {
  /**
   * Represents two nodes, container and component.
   */
  public static class AddChild {
    public AddChild(Container parent, Component child) {
      this.parent = parent;
      this.child = child;
    }
    final Container parent;
    final Component child;
    
    // Joins the two elements together
    // THIS MUST BE CALLED IN THE SWING THREAD!!!
    void join() {
      parent.add(child); 
    };
  }
  /**
   * This allows you to add component before other existing component.
   */
  public static class AddChildBefore extends AddChild {
    final Component before;
    public AddChildBefore(Component child, Component before) {
      super(before.getParent(), child);
      this.before = before;
    }
    public AddChildBefore(Container parent, Component child, Component before) {
      super(parent, child);
      this.before = before;
    }
    @Override
    void join() {
      parent.add(child, getComponentIndex(before));
    }
    // Thanks to: http://www.java2s.com/Code/Java/Swing-JFC/GetComponentIndex.htm
    public static final int getComponentIndex(Component component) {
      if (component != null && component.getParent() != null) {
        Container c = component.getParent();
        for (int i = 0; i < c.getComponentCount(); i++) {
          if (c.getComponent(i) == component)
            return i;
        }
      }
      return -1;
    }
  }
  /** 
   * Adds component at specific offset, instead of adding at the end;
   **/
  public static class AddChildAt extends AddChild {
    public final int index;
    public AddChildAt(Container parent, Component child, int index) {
      super(parent, child);
      this.index = index;
    }
    @Override
    void join() {
      parent.add(child, index); 
    }
  }
  // Prevents from calling the add in the list multiple times
  private Boolean done = false;
  // List of other actions to be executed after creating the GUI
  protected ArrayList<Runnable> after = new ArrayList();
  
  synchronized public void add(Container parent, Component child) {
    this.add(new AddChild(parent, child));
  }
  /** Command to add component before another component which is already
   * within the container.
   * @param parent
   * @param child 
   */
  synchronized public void addBefore(Container parent, Component child, Component before) {
    this.add(new AddChildBefore(parent, child, before));
  }
  synchronized public void addBefore(Component child, Component before) {
    this.add(new AddChildBefore(child, before));
  }
  synchronized public void addAt(Container parent, Component child, int index) {
    this.add(new AddChildAt(parent, child, index));
  }
  synchronized public void after(Runnable action) {
    after.add(action);
  }
  /**
   * Will ask Swing to run our runnable object which will append all the items.
   */
  public void create() {
    synchronized(done) {
      if(done) 
        return;
      done = true;
    }
    SwingUtilities.invokeLater(new Runner(this));
  }
  
  protected static class Runner implements Runnable {
    public final GUIAppendList list;
    public Runner(GUIAppendList list) {
      this.list = list;
    }
    @Override
    public void run() {
      // Append all nodes to each other
      for(AddChild c : list) {
        c.join();
      }
      // Run after actions
      if(!list.after.isEmpty()) {
        for(Runnable c : list.after) {
          c.run();
        }
      }
    }
  }
}
