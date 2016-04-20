/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient;
import org.junit.Test;
import static org.junit.Assert.*;
import cz.autoclient.updates.VersionId;
/**
 *
 * @author Jakub
 */
public class TestVersionId {
  public static final String A = "v1.2.3.4";
  public static final String A2 = " v 1.2.3.4\n ";
  
  public static final String B = "v1.2.5";
  
  public static final String Abeta = "v1.2.3.4beta";
  public static final String Abeta2 = " v 1.2.3.4  beta\n";
  public static final String Abeta3 = "v1.2.3.4-beta";


  public static final VersionId A_ = new VersionId(A);
  public static final VersionId A2_ = new VersionId(A2);
  public static final VersionId B_ = new VersionId(B);
  public static final VersionId Abeta_ = new VersionId(Abeta);
  public static final VersionId Abeta2_ = new VersionId(Abeta2);
  public static final VersionId Abeta3_ = new VersionId(Abeta3);
  
  @Test
  public void TestEquals() {
    assertEquals(new VersionId(A), new VersionId(A2));
    assertEquals(new VersionId(Abeta), new VersionId(Abeta2));
    assertEquals(new VersionId(Abeta), new VersionId(Abeta3));
    assertNotEquals(A_, Abeta_);
    assertNotEquals(A2_, Abeta2_);
    assertNotEquals(A2_, Abeta3_);
  }
  @Test
  public void TestCompare() {
    assertEquals(A_.compareTo(A2_), 0);
    assertEquals(A2_.compareTo(A_), 0);
    
    assertEquals(A_.compareTo(Abeta_), 1);
    assertEquals(A2_.compareTo(Abeta_), 1);
    
    assertEquals(A_.compareTo(Abeta2_), 1);
    assertEquals(A2_.compareTo(Abeta2_), 1);
    
    assertEquals(A_.compareTo(Abeta3_), 1);
    assertEquals(A2_.compareTo(Abeta3_), 1);
    
    assertEquals(Abeta_.compareTo(A_), -1);
    assertEquals(Abeta2_.compareTo(A_), -1);
    
    assertEquals(Abeta_.compareTo(A2_), -1);
    assertEquals(Abeta2_.compareTo(A2_), -1);
    assertEquals(Abeta3_.compareTo(A2_), -1);
  }
}
