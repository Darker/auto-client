 /*
* Copyright (c) 2002-2007 TeamDev Ltd. All rights reserved.
*
* Use is subject to license terms.
*
* The complete licence text can be found at
* http://www.teamdev.com/winpack/license.jsf
*/
package cz.autoclient.autoclick;

import java.awt.Color;

/**
* This class represents COLORREF structure.
*
* @author Serge Piletsky
*/
public class ColorRef //extends UInt32
{
  static final int CLR_INVALID = 0xFFFFFFFF;

public static int toBGR(int rgb)
{
int r = (rgb & 0xFF0000) >> 16;
int g = rgb & 0xFF00;
int b = rgb & 0xFF;
int result = r | g | b << 16;
return result;
}
public static int toRGB(int bgr)
{
int b = (bgr & 0xFF0000) >> 16;
int g = bgr & 0xFF00;
int r = bgr & 0xFF;
int result = b | g | r << 16;
return result;
}
/**
* Converts a Java color to a native color presentation.
*
* @param color is Java color.
* @return native color presentation.
*/
public static int toNativeColor(Color color)
{
return toBGR(color.getRGB());
}
/**
* Converts a native color to Java color presentation.
*
* @param color is a native color presentation.
* @return Java color.
*/
public static Color fromNativeColor(int color)
{
return new Color(toRGB(color));
}/*
public boolean isInvalid()
{
long value = getValue();
return (value == CLR_INVALID);
}*/
}