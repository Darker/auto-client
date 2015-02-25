/*  1:   */ package sirius.core;
/*  2:   */ 
/*  3:   */ import com.sun.jna.Native;
/*  4:   */ import com.sun.jna.Structure;
/*  5:   */ import com.sun.jna.win32.StdCallLibrary;
/*  6:   */ import com.sun.jna.win32.W32APIOptions;
/*  7:   */ import java.util.Arrays;
/*  8:   */ import java.util.List;
/*  9:   */ 
/* 10:   */ public abstract interface CommCtl
/* 11:   */   extends StdCallLibrary
/* 12:   */ {
/* 13:   */   public abstract boolean InitCommonControlsEx(INITCOMMONCONTROLSEX paramINITCOMMONCONTROLSEX);
/* 14:   */   
/* 15:   */   public static class INITCOMMONCONTROLSEX
/* 16:   */     extends Structure
/* 17:   */   {
/* 18:20 */     public int dwSize = size();
/* 19:   */     public int dwICC;
/* 20:   */     
/* 21:   */     protected List getFieldOrder()
/* 22:   */     {
/* 23:30 */       return Arrays.asList(new String[] { "dwSize", "dwICC" });
/* 24:   */     }
/* 25:   */   }
/* 26:   */   
/* 27:35 */   public static final CommCtl INSTANCE = (CommCtl)Native.loadLibrary("Comctl32.dll", CommCtl.class, W32APIOptions.DEFAULT_OPTIONS);
/* 28:   */ }


/* Location:           C:\MYSELF\programing\java\AutoCall\decompiled\JAutoCall.jar
 * Qualified Name:     sirius.core.CommCtl
 * JD-Core Version:    0.7.0.1
 */