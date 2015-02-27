/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

/**
 *
 * @author Jakub
 */
public class FieldDef {
  public final String setting_name;
  public final String title;
  public final String label;
  public FieldDef(String sn, String tt, String lab) {
    setting_name = sn;
    title = tt;
    label = lab;
  }
}
