/*
 * ToolbarLayout.java
 *
 * Author: David Jeske
 *
 * This layout helps position items on a Toolbar. The rules are simple:
 *   - stack all items horizontally with their minimum horizontal sizes
 *   - the maximum vertical size is the size of the panel, and items
 *     which are not as tall are centered.
 *   - if the containing panel is larger, distribute the extra space
 *     proportionally between the items which have flexible width.
 *   - if the containing panel is smaller, just punt
 * Created on February 14, 2003, 4:17 PM
 */

package guicomp.toolbar;

import javax.swing.*;
import java.awt.*;
// import simpleimap.Debug;

public class ToolbarLayout implements java.awt.LayoutManager, java.io.Serializable {
    int hgap = 0;
    public ToolbarLayout() {
    }

    public void setHgap(int gap) {
        this.hgap = gap;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    private boolean shouldExpand(Component comp) {
        if ((comp instanceof JTextField)) {
            return true;
        }
        return false;
    }
    
    public void layoutContainer(Container target) {
        Insets insets = target.getInsets();

        Dimension cursize = target.getSize();
        int numcomp = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;
        int maxheight = 0;
        int usedwidth = x;
        int i;
        int num_to_expand = 0;

        // find the max value for minimum height.
        // find the consumed width
        for (i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {
                Dimension dp = m.getPreferredSize();
                if (dp.height > maxheight) {
                    maxheight = dp.height;     
                }
                usedwidth += hgap;
                usedwidth += dp.width;
                if (shouldExpand(m)) {
                    num_to_expand++;
                }
            }
        }
        usedwidth += insets.right;
        if (maxheight > cursize.height) {
            maxheight = cursize.height;
        }
        int extrawidth = cursize.width - usedwidth;
        
        // size and layout components
        for (i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {

                Dimension dp = m.getPreferredSize();
                Dimension dmax = m.getMaximumSize();
                
                int newwidth = dp.width;
                int newheight = dp.height;
                // calculate expand width
                if (shouldExpand(m)) {
                    newwidth += (extrawidth / num_to_expand);
                }
                
                if (newheight > maxheight) {
                    newheight = maxheight;
                }
                
                Dimension newsize = new Dimension(newwidth, maxheight);
                m.setSize(newsize);
                
                if (newsize.height < maxheight) {
                    // center the component vertically
                    int adjust = (maxheight - dp.height) / 2;
                    m.setLocation(x, y + adjust);
                } else {
                    m.setLocation(x, y);
                }

                // calculate the next position
                x += newsize.width + hgap; 
            }
        }
        // finish the calculations
        y += insets.bottom;
        x += insets.right;
        
        // Debug.debug("x,y = " + x + "," + y);

    }

    /**
     * Calculates the minimum size dimensions for the specified
     * container, given the components it contains.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     *
     */
    public Dimension minimumLayoutSize(Container target) {
        Insets insets = target.getInsets();

        int numcomp = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;
        int maxheight = 0;

        for (int i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {
                Dimension d = m.getMinimumSize();
                if (d.height > maxheight) {
                    maxheight = d.height;
                }
                x += d.width;

            }
        }

        y += maxheight;
        
        x += insets.right;
        y += insets.bottom;

        return new Dimension(x,y);
    }

    /** Calculates the preferred size dimensions for the specified
     * container, given the components it contains.
     * @param parent the container to be laid out
     *
     * @see #minimumLayoutSize
     *
     */
    public Dimension preferredLayoutSize(Container target) {
        Insets insets = target.getInsets();

        int numcomp = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;
        int maxheight = 0;

        for (int i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();
                if (d.height > maxheight) {
                    maxheight = d.height;
                }
                x += d.width;

            }
        }

        y += maxheight;
        
        x += insets.right;
        y += insets.bottom;

        return new Dimension(x,y);
    }

    public void removeLayoutComponent(Component comp) {
    }


}
