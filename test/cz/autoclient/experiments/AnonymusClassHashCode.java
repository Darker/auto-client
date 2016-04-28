/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

/**
 *
 * @author Jakub
 */
public class AnonymusClassHashCode {
  public static interface DoSomething {
    void doIt(); 
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    DoSomething class1 = getImpl();
    System.out.println("Test 1: "+class1.getClass());
    System.out.println("    Hash: "+class1.getClass().hashCode());
    
    DoSomething class2 = getImpl();
    System.out.println("Test 2: "+class2.getClass());
    System.out.println("    Hash: "+class2.getClass().hashCode());
    
    DoSomething class3 = getImpl2();
    System.out.println("Test 3: "+class3.getClass());
    System.out.println("    Hash: "+class3.getClass().hashCode());
    
    
  }
  
  public static DoSomething getImpl() {
    return new DoSomething() {
      @Override
      public void doIt() {
        return; 
      }
    };
  }
  public static DoSomething getImpl2() {
    return new DoSomething() {
      @Override
      public void doIt() {
        return; 
      }
    };
  }
}
