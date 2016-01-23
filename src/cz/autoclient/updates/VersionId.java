/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;


/**
 *
 * @author Jakub
 */
public class VersionId implements java.io.Serializable {
  private static final long serialVersionUID = 666;
  final int[] numbers;
  final String affix;
  final boolean isBeta;
  public VersionId(String id) {
    id = id.replaceFirst("^[^0-9]+", "");
    affix = id.replaceFirst("^[0-9\\.]+[\\s-]*", "");
    isBeta = affix.toLowerCase().equals("beta");
    
    id = id.replaceFirst("[^0-9\\.]+$", "");
    String[] ids = id.split("\\.");
    numbers = new int[ids.length];
    for(int i=0,l=ids.length; i<l; i++) {
      numbers[i] = Integer.parseInt(ids[i]);
    }
  }


  public boolean equals(VersionId id) {
    if(id.numbers.length!=numbers.length)
      return false;
    if(!affix.equals(id.affix))
      return false;
    for(int i=0,l=numbers.length; i<l; i++) {
      if(numbers[i]!=id.numbers[i])
        return false;
    }
    return true;
  }
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof VersionId)
      return equals((VersionId)obj);
    else
      return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + this.numbers.hashCode();
    hash = 31 * hash + this.affix.hashCode();
    hash = 31 * hash + (this.isBeta ? 1 : 0);
    return hash;
  }
  
  public byte compareTo(VersionId id) {
    // Compare version numbers first
    for(int i=0,l=Math.min(numbers.length, id.numbers.length); i<l; i++) {
      if(numbers[i]!=id.numbers[i])
        return numbers[i]>id.numbers[i]?1:(byte)-1;
    }
    // Check if one of the versions has aditional number at the end of the array
    // the one that has additional number is higher
    if(numbers.length!=id.numbers.length)
      return numbers.length>id.numbers.length?1:(byte)-1;
    // Affixes cannot be compared except for "beta", beta version is smaller
    // than non beta version
    if(isBeta!=id.isBeta)
      return isBeta?-1:(byte)1;
    
    return 0;
  }
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    for(int i=0,l=numbers.length; i<l; i++) {
      if(i!=0)
        s.append('.');
      s.append(numbers[i]);
    }
    s.append("-");
    s.append(affix);
    return s.toString();
  }
  
  public static class Comparator implements java.util.Comparator<VersionId>, java.io.Serializable {
    protected Comparator(){}
    @Override
    public int compare(VersionId o1, VersionId o2) {
      if(o1==null || o2==null)
        return 0;
      return o1.compareTo(o2);
    }
    public Comparator inst = new Comparator();
  }
}
