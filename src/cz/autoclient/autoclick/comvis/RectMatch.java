/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.comvis;

import cz.autoclient.autoclick.Rect;
import com.sun.jna.platform.win32.WinDef;

/**
 *
 * @author Jakub
 */
public class RectMatch extends Rect {
  public final double diff;
  public RectMatch(WinDef.RECT rect) {
    super(rect);
    this.diff = 0;
  }
  public RectMatch(int x, int y) {
    super(x, y);
    this.diff = 0;
  }
  public RectMatch(int t, int r, int b, int l) {
    super(t, r, b, l);
    this.diff = 0;
  }
  public RectMatch(WinDef.RECT rect, double diff) {
    super(rect);
    this.diff = diff;
  }
  public RectMatch(int x, int y, double diff) {
    super(x, y);
    this.diff = diff;
  }
  public RectMatch(int t, int r, int b, int l, double diff) {
    super(t, r, b, l);
    this.diff = diff;
  }
  public static RectMatch byWidthHeight(int t, int l, int w, int h, double diff) {
    return new RectMatch(t, l+w, t+h, l, diff);
  }
}
