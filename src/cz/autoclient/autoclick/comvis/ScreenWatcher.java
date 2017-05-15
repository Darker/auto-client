/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.comvis;

import cz.autoclient.autoclick.Rect;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
/**All static. Set of methods to allow you finding locations on screen **/

public class ScreenWatcher {
  /**
   * 
   * @param image
   * @param bigImage
   * @param tolerance
   * @param return_nearest
   * @return 
   */
  public static RectMatch findByAvgGrayscale(BufferedImage image, BufferedImage bigImage, float tolerance, boolean return_nearest) {
    //These variables will be used to return the best (nearest) match
    //If return_nearest is false, these will not be used
    double difference = Double.MAX_VALUE;    
    int difference_pos[] = new int[2];
    //Multiply tolerance to make it equal to other values
    // 1 unit represents 3 colors in width/height image
    tolerance = tolerance*3*255*image.getHeight()*image.getWidth();
    //Create the integral image
    double integral_image[][] = integralImageGrayscale(bigImage);
    double sum_small = grayscaleSum(image);
    
    //System.out.println("Small image sum: "+sum_small);
    
    //Loop and find the least different image
    int wb = bigImage.getWidth();
    int hb = bigImage.getHeight();
    
    int ws = image.getWidth();
    int hs = image.getHeight();
    
    //Loop from 0 to big image width/height MINUS the small image width/height
    //The MINUS there is, because once you are at the end, the small image overlaps to undefined area
    for(int rect_x=0, mrx=wb-ws; rect_x<mrx; rect_x++) {
      for(int rect_y=0, mry=hb-hs; rect_y<mry; rect_y++) {
        //Calculate sum of colors on that region (please read this article: http://en.wikipedia.org/wiki/Summed_area_table
        double sum = + integral_image[rect_y][rect_x]  
                     + integral_image[rect_y+hs][rect_x+ws] 
                     - integral_image[rect_y+hs][rect_x]  
                     - integral_image[rect_y][rect_x+ws];
        //The small image image sum is already calculated so we just compare these two
        double diff = Math.abs(sum-sum_small);
        
        //Find nearest match - ALWAYS LOOPS THROUGH WHOLE IMAGE
        if(return_nearest) {
          //If the numbers are closest of all we were through we remember the position
          if(diff<difference) {
            //System.out.println("New difference: "+diff+" at ["+difference_pos[0]+", "+difference_pos[1]+"]");
            //System.out.println(" Big image sum: "+sum);
            difference = diff;
            difference_pos[0] = rect_x;
            difference_pos[1] = rect_y;            
          }
        }
        //Find first match below tolerance - ONLY USE WHEN YOUR SEARCHED OBJECT IS VERY UNIQUE
        else {
          if(diff<=tolerance) {
            return RectMatch.byWidthHeight(rect_y, rect_x, ws, hs, diff);
          }
        }
      }
    }
    if(return_nearest) {
      //System.out.println("Lowest difference: "+difference+". Wolerance: "+tolerance);
      if(difference<=tolerance) {
        return RectMatch.byWidthHeight(difference_pos[1], difference_pos[0], ws, hs, difference);
      }      
    }
    //Nothing found - return null
    return null;
  }
  public static Rect findByAvgGrayscale(BufferedImage image, BufferedImage bigImage) {
    return findByAvgGrayscale(image, bigImage, 0.5f, true); 
  }
  /**
   * Searches integral image and returns either first (by treshold) or best match as Rect object. 
   * @param sum_small sum of color components (R, G, B) of the small image we're searching for. This must be array with 3 elements
   * @param integral_image 
   * @param ws width of the small image
   * @param hs height of the small image
   * @param wb width of the big image
   * @param hb height of the big image
   * @param tolerance threshold
   * @param return_nearest true if you want to remember all matches and return the best.
   * @param matches if this is not null, it will be filled with all matches that are below threshold
   * @return Rect object or null if nothing was found below threshold.
   */
  public static RectMatch findByAvgColor(double sum_small[], double integral_image[][][], final int ws, final int hs, final int wb, final int hb, float tolerance, boolean return_nearest, ArrayList<RectMatch> matches) {
    //These variables will be used to return the best (nearest) match
    //If return_nearest is false, these will not be used
    double difference =  Double.MAX_VALUE;
    //difference[0] = difference[1] = difference[2] = Double.MAX_VALUE;    
    int difference_pos[] = new int[2];
    //Multiply tolerance to make it equal to other values
    // 1 unit represents a color (0-255) in width/height image
    tolerance = tolerance*(float)Math.pow(255, 2)*3;
    //System.out.println("Tolerance: "+tolerance);
    //Number of pixels
    int no_pixels = ws*hs;
    
    //System.out.println("Sum: ["+sum_small[0]+", "+sum_small[1]+", "+sum_small[2]+"]");
    //Divide the color sum by number of pixels to get average value
    for(byte i = 0; i<3; i++) {
      sum_small[i] = sum_small[i]/no_pixels; 
    }
    
    //System.out.println("Pixels: "+ws+"*"+hs+" = "+no_pixels);
    //System.out.println("Average: ["+sum_small[0]+", "+sum_small[1]+", "+sum_small[2]+"]");
    
    //Loop and find the least different image
    //VVariable names:
    //  ws, hs - width/height of the small image
    //  wb, hb - width/height of the big image
    //  rect_x, rect_y - left/top coordinate of the current rectangle
    
    //Loop from 0 to big image width/height MINUS the small image width/height
    //The MINUS there is, because once you are at the end, the small image overlaps to undefined area
    for(int rect_x=0, mrx=wb-ws; rect_x<mrx; rect_x++) {
      for(int rect_y=0, mry=hb-hs; rect_y<mry; rect_y++) {
        //Squared sum of color differences 
        double diff = 0;
        for(byte i=0; i<3; i++) {
          //Calculate sum of colors on that region (please read this article: http://en.wikipedia.org/wiki/Summed_area_table
          diff+= Math.pow(
                       sum_small[i]-
                       (+ integral_image[rect_y][rect_x][i]  
                        + integral_image[rect_y+hs][rect_x+ws][i] 
                        - integral_image[rect_y+hs][rect_x][i]  
                        - integral_image[rect_y][rect_x+ws][i])/no_pixels, 2);

        }
        //Find nearest match - ALWAYS LOOPS THROUGH WHOLE IMAGE
        if(return_nearest) {
          //If the numbers are closest of all we were through we remember the position
          if(diff<difference) {
            //System.out.println("New difference: "+diff+" at ["+difference_pos[0]+", "+difference_pos[1]+"]");
            //System.out.println(" Big image sum: "+sum);
            difference = diff;
            difference_pos[0] = rect_x;
            difference_pos[1] = rect_y;          
          }
          if(matches!=null && diff<=tolerance) {
            matches.add(RectMatch.byWidthHeight(rect_y-1, rect_x-1, ws, hs, difference, tolerance));
            //System.out.println("Diff "+diff+" <= tolerance. Adding ["+(rect_x-1)+", "+(rect_y-1)+"]");
          }
        }
        //Find first match below tolerance - ONLY USE WHEN YOUR SEARCHED OBJECT IS VERY UNIQUE
        else {
          if(diff<=tolerance) {
            return RectMatch.byWidthHeight(rect_y-1, rect_x-1, ws, hs, diff, tolerance);
          }
        }
      }
    }
    if(return_nearest) {
      //System.out.println("Lowest difference: "+difference+". Tolerance: "+tolerance);
      if(difference<=tolerance) {
        return RectMatch.byWidthHeight(difference_pos[1]-1, difference_pos[0]-1, ws, hs, difference, tolerance);
      }      
    }
    //Nothing found - return null
    return null;
  }
  public static RectMatch findByAvgColor(BufferedImage image, BufferedImage bigImage) {
    return findByAvgColor(image, bigImage, 0.5f, true, null); 
  }/* */
  public static RectMatch findByAvgColor(BufferedImage image, BufferedImage bigImage, float tolerance, boolean return_nearest) {
    return findByAvgColor(image, bigImage, tolerance, return_nearest, null); 
  } 
  public static RectMatch findByAvgColor(BufferedImage image, double[][][] bigIntegralImage, float tolerance, boolean return_nearest) {
    return findByAvgColor(image, bigIntegralImage, tolerance, return_nearest, null); 
  } 
  public static RectMatch findByAvgColor(BufferedImage image, BufferedImage bigImage, float tolerance, boolean return_nearest, ArrayList<RectMatch> matches) {
    double integral_image[][][] = integralImage(bigImage);
    double sum_small[] = colorSum(image);
    return findByAvgColor(sum_small, integral_image, image.getWidth(), image.getHeight(), bigImage.getWidth(), bigImage.getHeight(), tolerance, return_nearest, matches);
  }
  public static RectMatch findByAvgColor(BufferedImage image, double[][][] integral_image, float tolerance, boolean return_nearest, ArrayList<RectMatch> matches) {
    if(integral_image.length==0)
      throw new IllegalArgumentException("Empty integral image can't be accepted.");
    double sum_small[] = colorSum(image);
    return findByAvgColor(sum_small, integral_image, image.getWidth(), image.getHeight(), integral_image[0].length, integral_image.length, tolerance, return_nearest, matches);
  }
  public static ArrayList<RectMatch> findByAvgColor_isolated_matches(BufferedImage image, double[][][] integral_image, float tolerance) {
    int sh = image.getHeight();
    int sw = image.getWidth();
    return findByAvgColor_isolated_matches(colorSum(image), integral_image, sw, sh, tolerance);
  }
  public static ArrayList<RectMatch> findByAvgColor_isolated_matches(double[] sum_small, double[][][] integral_image, final int ws, final int hs, float tolerance) {
    ArrayList<RectMatch> matches = new ArrayList<>();
    int bh = integral_image.length;
    int bw = integral_image[0].length;
    findByAvgColor(sum_small, integral_image, ws, hs, bw, bh, tolerance, true, matches);
    System.out.println("findByAvgColor_isolated_matches obtained "+matches.size()+" matches.");
    return bestMatches(matches);
  }
  public static ArrayList<RectMatch> bestMatches(ArrayList<RectMatch> matches) {
    System.out.println("Grouping "+matches.size()+" rectangles.");
    ArrayList<ArrayList<Rect>> grouped = Rect.groupOverlapingRects(matches.toArray(new RectMatch[0]));
    System.out.println(grouped.size()+" groups.");
    ArrayList<RectMatch> best_matches = new ArrayList<>();

    ArrayList<Rect> current;
    RectMatch best;
    RectMatch currentMatch;
    double best_ratio;
    for(int i=0, l=grouped.size(); i<l; i++) {
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
    return best_matches;
  }
  
  
  public static Rect findByExactMatch(BufferedImage image, BufferedImage bigImage, float tolerance) {
    image    = (BufferedImage)image.getScaledInstance((int)(tolerance*image.getWidth()), (int)(tolerance*image.getHeight()), Image.SCALE_SMOOTH);
    bigImage = (BufferedImage)bigImage.getScaledInstance((int)(tolerance*bigImage.getWidth()), (int)(tolerance*bigImage.getHeight()), Image.SCALE_SMOOTH);
    return findByExactMatch(image, bigImage);
  }
  /** Finds image in bigImage by exact pixel match (all pixels must be exactly the same color).
   * 
   * @param image the smaller image you want to find
   * @param bigImage the big image you're searching in
   * @return Rect object describing the location where the small image was found. Returns null if nothing was found.
   */
  public static Rect findByExactMatch(BufferedImage image, BufferedImage bigImage) {
    //I marked these final so that I don't accidentally change them later
    final int iw = image.getWidth();
    final int ih = image.getHeight();
    final int bw = bigImage.getWidth();
    final int bh = bigImage.getHeight();
    
    //Loop from 0 to big image width/height MINUS the small image width/height
    //The MINUS there is, because once you are at the end, the small image overlaps to undefined area
    for(int rect_x=0, mrx=bw-iw; rect_x<mrx; rect_x++) {
      for(int rect_y=0, mry=bh-ih; rect_y<mry; rect_y++) {
          //This is where pixel looping begins
          int x = 0;
          int y;
          for (; x < iw; x++) {
              for (y=0; y < ih; y++) {
                //Get RGB returns 0x00rrggbb
                if(image.getRGB(x, y)!=
                    bigImage.getRGB(x+rect_x, y+rect_y)) {
                  //If the color does not match, break back to the rectangular search
                  //WITHOUT -1 THE VALUE OVERFLOWS ON NEXT ITERATION (damnit, debuged this like an idiot!!!)
                  x = Integer.MAX_VALUE-1;
                  break;
                }
              }
          }
          //This statement asks if the loop ended normally
          // - otherwise, the x and y are MAX_INT and greater than iw
          if(x==iw) {
            return Rect.byWidthHeight(rect_y, rect_x, iw, ih); 
          }
      }
    }
    //Nothing found - return null
    return null;
  }
  

  
  /** Utility methods **/
  
  /** Resample image smoothly by given ratio.
   * 
   * @param original image to resample
   * @param xscale X axis resample rate. If greater than 1, result is larger
   * @param yscale Y axis resample rate. If greater than 1, result is larger
   * @return 
   */
  public static BufferedImage resampleImage(BufferedImage original, double xscale, double yscale) {
    double w = original.getWidth();
    double h = original.getHeight();
    BufferedImage after = new BufferedImage((int)Math.round(w*xscale), (int)Math.round(yscale*h), BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(xscale, yscale);
    //System.out.println("["+xscale+", "+yscale+"]");
    AffineTransformOp scaleOp = 
       new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    after = scaleOp.filter(original, after);
    return after;
  }
  public static BufferedImage resampleImageTo(BufferedImage original, int width, int height) {
    int w = original.getWidth();
    int h = original.getHeight();
    return resampleImage(original, (double)width/(double)w, (double)height/(double)h);
  }
  public static Color averageColor(BufferedImage bi) {
    return averageColor(bi, 0, 0, bi.getWidth(), bi.getHeight());
  }
  /*
 * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
 * are your width and height respectively
 */
  public static Color averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
    int x1 = x0 + w;
    int y1 = y0 + h;
    long sumr = 0, sumg = 0, sumb = 0;
    for (int x = x0; x < x1; x++) {
        for (int y = y0; y < y1; y++) {
            //Color color = new Color(bi.getRGB(x, y));
            //sumr += color.getRed();
            //sumg += color.getGreen();
            //sumb += color.getBlue();
            
            int pixel = bi.getRGB(x, y);
            //Prepare all the colors in advance
            sumr += ((pixel&0x00FF0000)>>16);
            sumg += ((pixel&0x0000FF00)>>8);
            sumb +=  (pixel&0x000000FF);
        }
    }
    double num = w * h;
    return new Color(
        (int)Math.round(sumr / num),
        (int)Math.round(sumg / num),
        (int)Math.round(sumb / num)
    );
  }
  /**
   * Returns squared distance between two colors. Ignores alpha.
   * @param a
   * @param b
   * @return 
   */
  public static int compareColorsSq(Color a, Color b) {
    int argb = a.getRGB();
    int ar = ((argb&0x00FF0000)>>16);
    int ag = ((argb&0x0000FF00)>>8);
    int ab =  (argb&0x000000FF);
    
    int brgb = b.getRGB();
    int br = ((brgb&0x00FF0000)>>16);
    int bg = ((brgb&0x0000FF00)>>8);
    int bb =  (brgb&0x000000FF);
    
    return (ar-br)*(ar-br)+(ag-bg)*(ag-bg)+(ab-bb)*(ab-bb);
  }
  /** Generate an integral image. Every pixel on such image contains sum of colors or all the
   *  pixels before and itself.
      @return array in format double[collumn index, y][row index, x][color index, 0:r, 1:g, 2:b]
  **/
  public static double[][][] integralImage(BufferedImage image) {
    /* */
    //Cache width and height in variables
    int w = image.getWidth();
    int h = image.getHeight();
    //Create the 2D array as large as the image is
    //Notice that I use [Y, X] coordinates to comply with the formula
    double integral_image[][][] = new double[h][w][3];
    
    int pixel = 0;
    int[] color = new int[3];
    //Well... the loop
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        //Get pixel. It's actually 0xAARRGGBB, so the function should be getARGB
        pixel = image.getRGB(x, y);
        //Prepare all the colors in advance
        color[0] = ((pixel&0x00FF0000)>>16);
        color[1] = ((pixel&0x0000FF00)>>8);
        color[2] =  (pixel&0x000000FF);
        for(int i=0; i<3; i++) {
          //Calculate the integral image field
          double A = (x > 0 && y > 0) ? integral_image[y-1][x-1][i] : 0;
          double B = (x > 0) ? integral_image[y][x-1][i] : 0;
          double C = (y > 0) ? integral_image[y-1][x][i] : 0;
          integral_image[y][x][i] = - A + B + C + color[i];
        }
      }
    }
    //Return the array
    return integral_image;
    /* * /
    //Cache width and height in variables
    int w = image.getWidth();
    int h = image.getHeight();
    //Create the 2D array as large as the image is
    //Notice that I use [Y, X] coordinates to comply with the formula
    double integral_image[][][] = new double[h][w][3];
    
    //Variables for the image pixel array looping
    final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    //final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    //If the image has alpha, there will be 4 elements per pixel
    final boolean hasAlpha = image.getAlphaRaster() != null;
    final int pixel_size = hasAlpha?4:3;
    //If there's alpha it's the first of 4 values, so we skip it
    final int pixel_offset = hasAlpha?1:0;
    //Coordinates, will be iterated too
    //It's faster than calculating them using % and multiplication
    int x=0;
    int y=0;
    
    //Tmp storage for color
    int[] color = new int[3];
    
    int same = 0;
    
    //Loop through pixel array
    for(int i=0, l=pixels.length; i<l; i+=pixel_size) {
      
      //Prepare all the colors in advance
      //color[2] = ((int) pixels[i + pixel_offset] & 0xff); // blue;
      //color[1] = ((int) pixels[i + pixel_offset + 1] & 0xff); // green;
      //color[0] = ((int) pixels[i + pixel_offset + 2] & 0xff); // red;
      color[0] = (int)(pixels[i + pixel_offset + 2] & 0xFFFFFFFFL); // red;
      color[1] = (int)(pixels[i + pixel_offset + 1] & 0xFFFFFFFFL); // green;
      color[2] = (int)(pixels[i + pixel_offset    ] & 0xFFFFFFFFL); // blue;

      //For every color, calculate the integrals
      for(int j=0; j<3; j++) {
        //Calculate the integral image field
        double A = (x > 0 && y > 0) ? integral_image[y-1][x-1][j] : 0;
        double B = (x > 0) ? integral_image[y][x-1][j] : 0;
        double C = (y > 0) ? integral_image[y-1][x][j] : 0;
        integral_image[y][x][j] = - A + B + C + color[j];
      }
      //Iterate coordinates
      x++;
      if(x>=w) {
        x=0;
        y++;        
      }
    }
    //Return the array
    return integral_image;/* */
  }
  
  public static double[] colorSum(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    
    double the_sum[] = new double[3];
    
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int pixel = image.getRGB(x, y);
        the_sum[0]+= (pixel & 0x00FF0000)>>16;
        the_sum[1]+= (pixel & 0x0000FF00)>>8;
        the_sum[2]+=  pixel & 0x000000FF;
      }
    }
    return the_sum;
  }
  
  public static double[][] integralImageGrayscale(BufferedImage image) {
    //Cache width and height in variables
    int w = image.getWidth();
    int h = image.getHeight();
    //Create the 2D array as large as the image is
    //Notice that I use [Y, X] coordinates to comply with the formula
    double integral_image[][] = new double[h][w];
    //Well... the loop
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        //Get pixel. It's actually 0xAARRGGBB, so the function should be getARGB
        int pixel = image.getRGB(x, y);
        //Extrapolate color values from the integer 
        double A = (x > 0 && y > 0) ? integral_image[y-1][x-1] : 0;
        double B = (x > 0) ? integral_image[y][x-1] : 0;
        double C = (y > 0) ? integral_image[y-1][x] : 0;
        integral_image[y][x] = - A + B + C + ((pixel&0x00FF0000)>>16)+((pixel&0x0000FF00)>>8)+(pixel&0x000000FF);
      }
    }
    //Return the array
    return integral_image;
  }
  public static double grayscaleSum(BufferedImage image) {
    //Cache width and height in variables
    int w = image.getWidth();
    int h = image.getHeight();
    //The sum
    double the_sum = 0;
    
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int pixel = image.getRGB(x, y);
        the_sum+= ((pixel&0x00FF0000)>>16)+((pixel&0x0000FF00)>>8)+(pixel&0x000000FF);
      }
    }
    return the_sum;
  }
  
  /**DEBUG AND DRAW FUNCTIONS **/
  
  /**Draws integral image made from the normal image
   * @param source normal image
   * @return rendered integral image **/ 
  public static BufferedImage drawIntegralImage(BufferedImage source) {
    double iimg[][] = integralImageGrayscale(source);
    double max = 0;
    try {
      max = iimg[source.getHeight()-1][source.getWidth()-1];
    }
    catch(ArrayIndexOutOfBoundsException e) {
      System.err.println("The image and integral image sizes are inconsistent!");
      System.err.println("  Image: "+source.getWidth()+" x "+source.getHeight());
      if(iimg.length>0) {
        System.err.println("  Map: "+iimg[0].length+" x "+iimg.length);
      }
      else {
        System.err.println("  Map: 0 x 0");
      }
    }
    
    BufferedImage result = new BufferedImage(source.getWidth(),source.getHeight(),BufferedImage.TYPE_INT_RGB);
    
    for(int y=0,ly=iimg.length; y<ly; y++) {
      for(int x=0,lx=iimg[x].length; x<lx; x++) {
        int value = ((int)Math.round((((double)iimg[y][x])/max)*255))%256;
        //Basically duplicate the value 3 times over the size of int
        result.setRGB(x, y, value|(value<<8)|(value<<16));
      }
    }
    return result;
  }
  /**Draws colored integral image made from the normal image
   * @param image
   * @param max
   * @return rendered integral image **/ 
  public static BufferedImage drawIntegralImage(double[][][] image, double[] max) {

    int height = image.length;
    int width = image[0].length;
    
    BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    System.out.println("Sum: ["+max[0]+", "+max[1]+", "+max[2]+"]");
    
    for(int y=0,ly=image.length; y<ly; y++) {
      for(int x=0,lx=image[x].length; x<lx; x++) {
        int[] color = {0,0,0};
        color[0]=(int)(Math.round(255*image[y][x][0]/max[0]));
        color[1]=(int)(Math.round(255*image[y][x][1]/max[1]));
        color[2]=(int)(Math.round(255*image[y][x][2]/max[2]));
        //int value = ((int)Math.round((((double)iimg[y][x])/max)*255))%256;
        //Basically duplicate the value 3 times over the size of int
        result.setRGB(x, y, color[0]|(color[1]<<8)|(color[2]<<16));
      }
    }
    return result;
  }
  public static BufferedImage drawIntegralImage(double[][][] image) {
    if(image.length==0)
      throw new IllegalArgumentException("Image height cannot be 0!");
    if(image[0].length==0)
      throw new IllegalArgumentException("Image width cannot be 0!");
    return drawIntegralImage(image, image[image.length-1][image[0].length-1]);
  }
  /**
   * 
   * @param image
   * @param S change in saturation
   * @param B change in brihtness
   */
  public static void changeHSB(BufferedImage image, float S, float B) {
    final int height = image.getHeight();
    final int width = image.getWidth();
    float[] hsb = new float[] {0,0,0};
    for(int y = 0; y < height; y++) {
        for(int x = 0; x < width; x++) {
            int pixel = image.getRGB(x, y);
            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = (pixel) & 0xFF;

            //Adjust saturation:
            Color.RGBtoHSB(red, green, blue, hsb);
            //System.out.println("dd"+hsb[1]);
            
            hsb[1] += S;
            hsb[2] += B;
            
            if(hsb[1]>1.0F)
              hsb[1] = 1.0F;
            if(hsb[1]<0F)
              hsb[1] = 0F;
            
            if(hsb[2]>1.0F)
              hsb[2] = 1.0F;
            if(hsb[2]<0F)
              hsb[2] = 0F;
            
            int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);            
            image.setRGB(x, y, rgb);
        }
    }
  }
  
  public static String arrayToStr(int[] ar) {
    return "["+ar[0]+", "+ar[1]+", "+ar[2]+"]";
  }
  public static String arrayToStr(double[] ar) {
    return "["+ar[0]+", "+ar[1]+", "+ar[2]+"]";
  }
}
