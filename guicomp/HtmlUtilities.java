/*   
     Name: mate/util/DisplayUtilities.java
     Copyright (C): 2000 Natural Interactive Systems Laboratory, Odense University

     This library is free software; you can redistribute it and/or
     modify it under the terms of the GNU Lesser General Public
     License as published by the Free Software Foundation; either
     version 2.1 of the License, or (at your option) any later version.

     This library is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
     Lesser General Public License for more details.

     You should have received a copy of the GNU Lesser General Public
     License along with this library; if not, write to the Free Software
     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
     USA.
*/

package guicomp;

import java.awt.Color;
import java.util.Vector;
import java.util.StringTokenizer;

/** Utility functions for display, mostly to do with colour.

 This should be a static class, as it should never be instantiated. */

public class HtmlUtilities {

  /** Convert a colour name or HTML type # string into a Color */

  public static Color getColor(String colour) {
    if (colour.equals("Blue"))      return Color.blue;
    if (colour.equals("Cyan"))      return Color.cyan;
    if (colour.equals("DarkGray"))  return Color.darkGray;
    if (colour.equals("Gray") || colour.equals("Grey")) return Color.gray;
    if (colour.equals("Green"))     return Color.green;
    if (colour.equals("LightGray")) return Color.lightGray;
    if (colour.equals("Magenta"))   return Color.magenta;
    if (colour.equals("Orange"))    return Color.orange;
    if (colour.equals("Pink"))      return Color.pink;
    if (colour.equals("Red"))       return Color.red;
    if (colour.equals("White"))     return Color.white;
    if (colour.equals("Yellow"))    return Color.yellow;
    if (colour.equals("Black"))    return Color.black;
    if (colour.equals("Purple"))    return new Color(127,0,127);
    if (colour.startsWith("#") && colour.length()==7) {  // html format
      try {
	return new Color(Integer.parseInt(colour.substring(1,2),16),
			 Integer.parseInt(colour.substring(3,4),16),
			 Integer.parseInt(colour.substring(5,6),16));
      } catch (NumberFormatException e) {}
    }
    return null;
  }

    public static String getHtmlColorCode(String s) {
        Color c = getColor(s);
        return "#"+hex(c.getRed())+hex(c.getGreen())+hex(c.getBlue());
    }

    public static String getHtmlColorCode(Color c) {
        return "#"+hex(c.getRed())+hex(c.getGreen())+hex(c.getBlue());
    }
    
  /** Convert an integer into a hex string */

  static String hex(int i) { // two digits, upper case
    String s = "00"+Integer.toHexString(i).toUpperCase();
    return s.substring(s.length()-2);
  }
    
  /** Split a string into a vector at the given delimiter */

  public static Vector vectorize(String s,String delim){
    StringTokenizer st = new StringTokenizer(s,delim);
    Vector v = new Vector();
    while (st.hasMoreElements()){ v.add(st.nextElement()); }
    return v;
  }



}
