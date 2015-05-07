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
public class math {
  public static void main(String[] args) throws Exception
  {
    System.out.println("Byte to int: "+((int)((byte)-20)&0xff));
    
    Vector a = new Vector(5,10);
    Vector b = new Vector(1,0);
    long result = a.longValue()+b.longValue();
  }
  public static class Vector extends Number {
    int x;
    int y;
    Vector(int x, int y) {
      this.x = x;
      this.y = y;
    }
    @Override
    public int intValue() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public long longValue() {
      System.out.format("%064X", (long)x+(((long)y)<<16));
      return (long)x+(((long)y)<<16);
    }
    @Override
    public float floatValue() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double doubleValue() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
