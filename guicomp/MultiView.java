package guicomp;

import javax.swing.*;  
import java.awt.*;
import simpleimap.Debug;

public class MultiView extends JPanel implements Scrollable {
    final int LVL = 3;
    
    VerticalStackedLayout lm;
    public MultiView() {
        lm = new VerticalStackedLayout();
        setLayout(lm);
        // setBackground(Color.LIGHT_GRAY);
        this.setOpaque(false);
    }
    public void setVgap(int gap) {
        lm.setVgap(gap);
    }

    //////// SCROLLABLE ///////////////////////////////////////


    /** Returns the preferred size of the viewport for a view component.
     * For example the preferredSize of a JList component is the size
     * required to accommodate all of the cells in its list however the
     * value of preferredScrollableViewportSize is the size required for
     * JList.getVisibleRowCount() rows.   A component without any properties
     * that would effect the viewport size should just return
     * getPreferredSize() here.
     *
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     * @see JViewport#getPreferredSize
     *
     */
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        Debug.debug(LVL,"trackwidth");
        return true;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }


}