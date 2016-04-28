/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.experiments;

import java.beans.Expression;
import java.lang.reflect.Constructor;

/**
 * Test how Class.getDeclaredConstructor seeks for constructors.
 * @author Jakub
 */
public class Constructors {
  public static class A {
    private final B b;
    public A(B b) {
      this.b = b;
    }
  }
  public static class B {}
  public static class C extends B {}
  public static class D {}
  public static void main(String[] args) //throws Exception
  {
    //System.out.println(System.getProperty("os.name"));
    /** TRY USING REFLECTION **/
    //Make A from B
    tryAfromParam(new B());
    //Make A from C that is cast to B
    tryAfromParam((B)new C());
    //Make A from C without casting
    tryAfromParam(new C());
    /** TRY USING NORMAL new **/
    //Make A from B
    tryAfromParamNew(new B());
    //Make A from C that is cast to B
    tryAfromParamNew((B)new C());
    //Make A from C without casting
    tryAfromParamNew(new C());
    /** TRY USING java.beans.Expression **/
    //Make A from B
    tryAfromParamExpression(new B());
    //Make A from C that is cast to B
    tryAfromParamExpression((B)new C());
    //Make A from C without casting
    tryAfromParamExpression(new C());
    //Make A from D (should be error)
    tryAfromParamExpression(new D());
  }
  public static A tryAfromParam(Object param) {
    System.out.println("Try to make A from "+param.getClass()+" using A.class.getConstructor(...)");
    try {
      A a = AfromParam(param);
      System.out.println("    Sucess :)");
      return a;
    } catch (Exception ex) {
      System.out.println("    CONSTRUCTOR FAILED: "+ex);
    }
    return null;
  }
  public static A tryAfromParamNew(B param) {
    System.out.println("Try to make A from "+param.getClass()+" using new A(...)");
    try {
      A a = new A((param.getClass().cast(param)));
      System.out.println("    Sucess :)");
      return a;
    } catch (Throwable ex) {
      System.out.println("    CONSTRUCTOR FAILED: "+ex);
    }
    return null;
  }
  public static A tryAfromParamExpression(Object param) {
    System.out.println("Try to make A from "+param.getClass()+" using new A(...)");
    try {
      A a = AfromExpression(param);
      System.out.println("    Sucess :)");
      return a;
    } catch (Throwable ex) {
      System.out.println("    CONSTRUCTOR FAILED: "+ex);
    }
    return null;
  }
  public static A AfromParam(Object param) throws Exception {
    //Fetch the A's class instance
    Class cls = A.class;
    //Define constructor parameters
    Class[] arguments = new Class[] {
      param.getClass()
    };
    //Try to get the constructor
    Constructor<A> c = cls.getConstructor(arguments);
    //Try to instantiate A
    A a = c.newInstance(param);
    //Return result
    return a;
  }
  public static A AfromExpression(Object param) throws Exception {
    A a = (A)new Expression(A.class, "new", new Object[]{param}).getValue();
    return a;
  }
}
