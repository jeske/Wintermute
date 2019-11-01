package guicomp;

import javax.swing.*;
import java.awt.*;
import simpleimap.Debug;

public class VerticalStackedLayout implements java.awt.LayoutManager, java.io.Serializable {
    private final int LVL = 3;
    int vgap = 0;
    public VerticalStackedLayout() {
    }

    public VerticalStackedLayout(int vgap) {
        this();
        setVgap(vgap);

    }

    public void setVgap(int gap) {
        this.vgap = gap;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Lays out the specified container.
     * @param parent the container to be laid out
     *
     */
    public void layoutContainer(Container target) {
        Insets insets = target.getInsets();

        // the width is fixed
        Dimension cursize = target.getSize();
        int width = cursize.width;
        int subwidth = cursize.width - (insets.left + insets.right);
        int numcomp = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;
        boolean isfirst = true;

        Debug.debug(LVL,"layout : " + target + "size: " + cursize);


        for (int i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {

                Dimension d = m.getPreferredSize();
                Dimension chosensize = new Dimension(subwidth, d.height);

                if (m instanceof Scrollable) {
                    // give the item a chance to change it's preferred height
                    // once we tell it the preferred width
                    if (d.width != width) {
                        m.setSize(chosensize);  
                    }
                    Dimension newd = m.getPreferredSize();
                    if (newd.height != d.height) {
                        chosensize = new Dimension(subwidth, newd.height);
                    }
                }

                Dimension cs = m.getSize();
                if (!(cs.width == chosensize.width && cs.height == chosensize.height)) {
                    m.setSize(chosensize);
                }                    


                // put the object in the right spot
                 if (isfirst) {
                    isfirst = false;
                } else {
                    if (chosensize.height != 0) {
                        y += vgap;
                    }
                }

                Point loc = m.getLocation();
                if (!(loc.x == x && loc.y == y)) {
                    m.setLocation(x,y);
                }

                // calculate the next position
                y += chosensize.height; 

            }
        }
        y += insets.bottom;
        x += subwidth + insets.right;

        if (cursize.height != y) {
            // Debug.debug("height " + cursize.height + " -> " + y);

            // this causes a recursive loop!
            // target.setSize(width,y);
        }
        //target.invalidate();

        Debug.debug(LVL,"x,y = " + x + "," + y);

    }

    /**
     * Calculates the minimum size dimensions for the specified
     * container, given the components it contains.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     *
     */
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(parent.getSize().width,0);
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

        // the width is fixed
        int width = target.getSize().width;
        int numcomp = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;

        for (int i=0;i<numcomp;i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();

                // calculate the next position
                y += d.height + vgap; 

            }
        }

        x += target.getSize().width + insets.right;
        y += insets.bottom;

        return new Dimension(x,y);
    }

    public void removeLayoutComponent(Component comp) {
    }


}