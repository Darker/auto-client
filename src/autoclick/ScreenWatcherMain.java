/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autoclick;


import PVP_net.Images;
import autoclick.comvis.ScreenWatcher;
import autoclick.comvis.RectMatch;
import autoclick.exceptions.APIError;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



/**
 *
 * @author Jakub
 */
public class ScreenWatcherMain {
   public static void main(String[] args) throws Exception
   {
     /*TestRectangles();
     if(true)
       return;/* */
     //The small image to search for
     BufferedImage thing = loadFromPath(/*"CVTestSearchObject.png"*/"launcher_screens/search_objects/pending.png");
     BufferedImage thing2 = loadFromPath(/*"CVTestSearchObject.png"*/"launcher_screens/search_objects/accepted.png");
     //The big image to search in
     BufferedImage screenshot = loadFromPath(/*"screenshot.png"*/"launcher_screens/INVITE-PENDING.png");
     
     if(thing!=null && screenshot!=null && thing2!=null) {
       //TestSearchColorAll(thing, screenshot);
       //TestSearch(thing, screenshot);
       TestBestWhile(thing, thing2, screenshot);
     }
   }
   public static void TestRectangles() {
     ArrayList<Rect> test = new ArrayList<>();
     /*test.add(new Rect(10,50,50,10));
     test.add(new Rect(30,80,80,40));
     test.add(new Rect(60,100,100,60));
     test.add(new Rect(80,90,130,40));
     //test.add(new Rect(8,30,120,70));
     test.add(new Rect(130,150,190,100));*/
     //Three overlaping
     /*for(int i=0; i<20*7; i+=7) {
       test.add(new Rect(10+i,150+i,150+i,10+i));
     }
     test.add(new Rect(290,299,299,290));
     */
     //Actual rectangles from CV output
     test.add(new Rect(188, 354+50, 211+50, 330));
     test.add(new Rect(188, 356+50, 211+50, 332));
     test.add(new Rect(189, 356+50, 212+50, 332));
     test.add(new Rect(190, 357+50, 213+50, 333));
     
     /*for(int i=0; i<test.size(); i++) {
       for(int j=0; j<test.size(); j++) {
         //if(j==i)
         //  continue;
         boolean a,b;
         a = Rect.intersection(test.get(i), test.get(j));
         b = Rect.intersection(test.get(j), test.get(i));
         if(a!=b) {
           System.out.println("Intersections are ambigious!");
           return;
         }
       }
     }*/
     //if(Rect.intersection(new Rect(0, 5, 5, 0), new Rect(0, 6, 5, 1)))
       //return;
     //All consuming rect
     //test.add(new Rect(2,198,198,2));
     
     Color[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.BLACK, Color.WHITE};
     BufferedImage debug = new BufferedImage(450,450, BufferedImage.TYPE_INT_RGB);
     
     drawResult(debug, test, Color.WHITE);
     
     ArrayList<ArrayList<Rect>> grouped = Rect.groupOverlapingRects(test.toArray(new Rect[0]));
     
     System.out.println("Groups: "+grouped.size());
     ArrayList<Rect> current;
     Rect currentMatch;
     for(int i=0, l=grouped.size(); i<l&&i<colors.length; i++) {
       current = grouped.get(i);
       for(int j=0, k=current.size(); j<k; j++) {
         currentMatch = current.get(j);
         drawResult(
             debug,
             new Rect(currentMatch.top-i-1, currentMatch.right-i-1, currentMatch.bottom-i-1, currentMatch.left-i-1),
             colors[i]);
       }
     }
       
     saveToPath("rectangles.png", debug);
   }
   public static void TestBestMatches(BufferedImage thing1, BufferedImage thing2, BufferedImage screenshot) {
     double[][][] ii = ScreenWatcher.integralImage(screenshot);
     double sum1[] = ScreenWatcher.colorSum(thing1);
     double sum2[] = ScreenWatcher.colorSum(thing2);
     
     ArrayList<RectMatch> matches1 = ScreenWatcher.findByAvgColor_isolated_matches(sum1, ii, thing1.getWidth(), thing1.getHeight(), 0.00009f);
     ArrayList<RectMatch> matches2 = ScreenWatcher.findByAvgColor_isolated_matches(sum2, ii, thing2.getWidth(), thing2.getHeight(), 0.00009f);
     System.out.println("Players accepted: "+matches2.size());
     System.out.println("Players pending: "+matches1.size());
     
     drawResult(screenshot, (ArrayList<Rect>)(Object)matches1, Color.RED);
     drawResult(screenshot, (ArrayList<Rect>)(Object)matches2, Color.GREEN);
     saveToPath("ScreenWatcher.BestMatches.png", screenshot);
     saveToPath("ScreenWatcher.Test1.png", thing1);
   }
   public static void TestBestWhile(BufferedImage thing1, BufferedImage thing2, BufferedImage screenshot) throws APIError, InterruptedException {
     System.out.println("Inviting players now. ");
     double[][][] integral_image;
     double[] accepted, pending;
     try {
       accepted = Images.INVITE_ACCEPTED.getColorSum();
       pending = Images.INVITE_PENDING.getColorSum();
     }
     catch(IOException e) {
       System.err.println("Can't find required image! Invite lobby can't be automated!"); 
       return;
     }
     //Declare the two arrays of matches
     ArrayList<RectMatch> accepted_all, pending_all;
     while(true) {
       System.out.println("Taking screenshot from window.");
       integral_image = ScreenWatcher.integralImage(screenshot);
       //displayImage(screenshot);
       //displayImage(ScreenWatcher.drawIntegralImage(integral_image, ScreenWatcher.colorSum(screenshot)));
       System.out.println("Analysing the screenshot.");
       //System.out.println(Images.INVITE_PENDING);
       System.out.println("  Invited players: ");
       

       pending_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    pending.clone(),
                    integral_image,
                    Images.INVITE_PENDING.getWidth(),
                    Images.INVITE_PENDING.getHeight(),
                    0.00009f);
       System.out.println("    Pending: "+pending_all.size());
       //System.out.println(Images.INVITE_ACCEPTED);
       accepted_all = ScreenWatcher.findByAvgColor_isolated_matches(
                    accepted.clone(),
                    integral_image,
                    Images.INVITE_ACCEPTED.getWidth(),
                    Images.INVITE_ACCEPTED.getHeight(),
                    0.00009f);
       System.out.println("    Accepted: "+accepted_all.size());
       
       //Only start if all players accepted or declined and at least one accepted
       if(accepted_all.size()>0 && pending_all.isEmpty()) {
         System.out.println("All players have been invited and are in lobby. Time to start!");
         return;
       }
       System.out.println("Next test in 1.2 seconds.");
       sleep(1200L);
       System.out.println("Timeout over, next test?");
       if(false)
         break;
     }
     System.out.println("Lobby has exit spontaneously.");
   }
   public static void TestSearchColorAll(BufferedImage thing, BufferedImage screenshot) {
     double start = System.nanoTime();
     ArrayList<RectMatch> matches = new ArrayList<>();

     Rect pos1 = autoclick.comvis.ScreenWatcher.findByAvgColor(thing, screenshot, 0.00008f, true, matches);
     double pos1t = System.nanoTime();


     System.out.println("Search by average color: "+(pos1t-start));


     if(pos1!=null) {
        System.out.println("Found object: "+pos1);
        BufferedImage groups = cloneImage(screenshot);
        System.out.println("In total, there's "+matches.size()+" matches.");
        for(int i=0, l=matches.size(); i<l; i++) {
          drawResult(screenshot, matches.get(i), Color.RED);
          //System.out.println("    "+matches.get(i).toCode());
        }
        Color[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.BLACK, Color.WHITE};
        
        ArrayList<ArrayList<Rect>> grouped = Rect.groupOverlapingRects(matches.toArray(new RectMatch[0]));
        ArrayList<RectMatch> best_matches = new ArrayList<>();
        
        System.out.println("There's "+grouped.size()+" groups of rectangles now.");
        
        ArrayList<Rect> current;
        RectMatch best;
        RectMatch currentMatch;
        double best_ratio;
        for(int i=0, l=grouped.size(); i<l&&i<colors.length; i++) {
          current = grouped.get(i);
          best_ratio = Double.MAX_VALUE;
          best = null;
          for(int j=0, k=current.size(); j<k; j++) {
            currentMatch = (RectMatch)current.get(j);
            if(currentMatch.diff<best_ratio) {
              best_ratio = currentMatch.diff;
              best = currentMatch;
            }
          }
          best_matches.add(best);
        }
        
        for(int i=0,l=best_matches.size(); i<l; i++) {
          drawResult(groups, best_matches.get(i), colors[i]);
        }
        //Draw all groups
        BufferedImage outputx;
        for(int i=0, l=grouped.size(); i<l&&i<colors.length; i++) {
          current = grouped.get(i);
          outputx = cloneImage(groups);
          for(int j=0, k=current.size(); j<k; j++) {
            drawResult(outputx, current.get(j), colors[i]);
          }      
          saveToPath("garbage/ScreenWatcherMain.output.group_"+(1+i)+".png", outputx);
        }
        
        //drawResult(screenshot, pos2, Color.BLUE);

        //Save the file for review
        saveToPath("ScreenWatcherMain.output.png", screenshot);
        
        saveToPath("ScreenWatcherMain.output.groups.png", groups);
        
        
     }
     else {
       System.err.println("No object found!"); 
     }
   }
   public static void TestSearchColor(BufferedImage thing, BufferedImage screenshot) {
     double start = System.nanoTime();

     Rect pos1 = autoclick.comvis.ScreenWatcher.findByAvgColor(thing, screenshot, 1, true);
     double pos1t = System.nanoTime();

     Rect pos2 = autoclick.comvis.ScreenWatcher.findByExactMatch(thing, screenshot);
     double pos2t = System.nanoTime();

     System.out.println("Search by average color: "+(pos1t-start));
     System.out.println("Search by exact pixel match color: "+(pos2t-pos1t));

     if(pos1!=null || pos2 !=null) {
        System.out.println("Found object: "+pos1);
        drawResult(screenshot, pos1, Color.RED);
        //drawResult(screenshot, pos2, Color.BLUE);

        //Save the file for review
        saveToPath("ScreenWatcherMain.output.png", screenshot);
     }
     else {
       System.err.println("No object found!"); 
     }
   }
   public static void TestSearch(BufferedImage thing, BufferedImage screenshot) {
     double start = System.nanoTime();

     Rect pos1 = autoclick.comvis.ScreenWatcher.findByAvgGrayscale(thing, screenshot, 1, true);
     double pos1t = System.nanoTime();

     Rect pos2 = autoclick.comvis.ScreenWatcher.findByExactMatch(thing, screenshot);
     double pos2t = System.nanoTime();

     System.out.println("Search by average color grayscale: "+(pos1t-start));
     System.out.println("Search by exact pixel match color: "+(pos2t-pos1t));

     if(pos1!=null || pos2 !=null) {
        System.out.println("Found object: "+pos1);
        //drawResult(screenshot, pos1, Color.RED);
        drawResult(screenshot, pos2, Color.RED);

        //Save the file for review
        saveToPath("ScreenWatcherMain.output.png", screenshot);
     }
     else {
       System.err.println("No object found!"); 
     }
   }
   public static void testGrayscaleSums(BufferedImage thing, BufferedImage screenshot) {
    //Sum comparing:

     //Summed area table (thing is BufferedImage)
     double is[][] = ScreenWatcher.integralImageGrayscale(thing);
     //Sum generated by a normal for loop
     double ss = ScreenWatcher.grayscaleSum(thing);
     //Height of the resulting array
     int ish = is.length;
     //Width of resulting array. Also throws nasty error if something goes wrong
     int isw = is[is.length-1].length;
     //Testing whether different methods give same results
     System.out.println(
         ss +" =? " + 
       //Last "pixel" in integral image must contain the sum of the image
         is[ish-1][isw-1]+" =? "+
       //The "sum over rectangle" with a rectangle that contains whole image
       //     A            B            C              D
         (+is[0][0]  -is[0][isw-1] -is[ish-1][0] +is[ish-1][isw-1])
     );
     //See the integral image
     saveToPath("screenshot.png_integralImage.png", ScreenWatcher.drawIntegralImage(thing));

     double ib[][] = ScreenWatcher.integralImageGrayscale(screenshot);


     double sb = ScreenWatcher.grayscaleSum(screenshot);
   }
   public static void testColorSums(BufferedImage thing, BufferedImage screenshot) {
    //Sum comparing:

     //Summed area table (thing is BufferedImage)
     double is[][][] = ScreenWatcher.integralImage(thing);
     //Sum generated by a normal for loop
     double ss[] = ScreenWatcher.colorSum(thing);
     //Height of the resulting array
     int ish = is.length;
     //Width of resulting array. Also throws nasty error if something goes wrong
     int isw = is[is.length-1].length;
     //Calculate rectangle sum
     double rect_sum[] = new double[3];
     for(int i=0; i<3; i++) {
       //The "sum over rectangle" with a rectangle that contains whole image
       //                  A               B                C                  D
       rect_sum[i] = (+is[0][0][i]  -is[0][isw-1][i] -is[ish-1][0][i] +is[ish-1][isw-1][i]);
     }
     //Testing whether different methods give same results
     System.out.println(
         ScreenWatcher.arrayToStr(ss) +" =? " + 
       //Last "pixel" in integral image must contain the sum of the image
         ScreenWatcher.arrayToStr(is[ish-1][isw-1])+" =? "+
       //Rectangle sum
         ScreenWatcher.arrayToStr(rect_sum)
     );
     //See the integral image
     //saveToPath("screenshot.png_integralImage.png", ScreenWatcher.drawIntegralImage(thing));

     //double ib[][] = ScreenWatcher.integralImageGrayscale(screenshot);


     //double sb = ScreenWatcher.grayscaleSum(screenshot);
   }
   public static void drawResult(BufferedImage target, Rect rect, Color color) {
     //silent fail for invalid result
     if(rect==null)
       return;
     //Draw rectangle on discovered position
     Graphics2D graph = target.createGraphics();
     graph.setColor(color);
     graph.drawRect((int)rect.top, (int)rect.left, (int)rect.width, (int)rect.height);
     graph.dispose();
   }
   public static void drawResult(BufferedImage target, ArrayList<Rect> rect, Color color) {
     for(int i=0,l=rect.size(); i<l; i++) {
       drawResult(target, rect.get(i), color); 
     }
     
   }
   public static BufferedImage loadFromPath(String path) {
     File img = new File(path);
     BufferedImage thing = null;
     try {
       thing = ImageIO.read(img);
     }
     catch(IOException e) {
       System.err.println("Can't read '"+path+"': "+e);
     }
     return thing;
   }
   public static boolean saveToPath(String path, BufferedImage image) {
     File img = new File(path);
     try {
       ImageIO.write(image, "png", new File(path));
     } catch (IOException ex) {
       System.err.println("Failed to save image as '"+path+"'. Error:"+ex);
       return false;
     }
     return true;
   }
   public static void displayImage(Image image) {
     JLabel label = new JLabel(new ImageIcon(image));

     JPanel panel = new JPanel();
     panel.add(label);

     JScrollPane scrollPane = new JScrollPane(panel);
     JOptionPane.showMessageDialog(null, scrollPane);
   }
   static BufferedImage cloneImage(BufferedImage bi) {
     ColorModel cm = bi.getColorModel();
     boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
     WritableRaster raster = bi.copyData(null);
     return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
   }
}
