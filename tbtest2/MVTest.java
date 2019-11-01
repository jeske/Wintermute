/*
 * MVTest.java
 *
 * Created on January 30, 2003, 4:02 PM
 */



///////////////////////////////////////////////////////////////





// You really shouldn't be here... edit this stuff in
// "guicomp"






//////////////////////////////////////////////////////////////


package tbtest2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.*;

import simpleimap.Debug;

/**
 *
 * @author  David W Jeske
 */
public class MVTest extends javax.swing.JFrame {
    
    class MultiView extends JPanel implements Scrollable {
        VerticalStackedLayout lm;
        MultiView() {
            lm = new VerticalStackedLayout();
            setLayout(lm);
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
            Debug.debug("trackwidth");
            return true;
        }
        
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 1;
        }
        
    }
    
    class VerticalStackedLayout implements java.awt.LayoutManager, java.io.Serializable {
        int vgap = 0;
        VerticalStackedLayout() {
        }
        
        VerticalStackedLayout(int vgap) {
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
            
            Debug.debug("layout : ", target, "size: ", cursize);

            
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
            
            Debug.debug("x,y = " + x + "," + y);
            
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
    
    class CollapsablePanel extends JPanel {
        JButton trigger;
        JViewport viewport;
        JComponent subview;
        boolean isCollapsed = false;

        public void invalidate() {
            super.invalidate();
            sizeViewport(); // later we'll trigger the animation here!
        }
        
        CollapsablePanel() {
            this.setBorder(BorderFactory.createLineBorder(java.awt.Color.RED,2));
            this.setLayout(new VerticalStackedLayout());
            
            trigger = new JButton("title");
            trigger.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   toggleState();
               }
            });
            
            viewport = new JViewport();
            viewport.setPreferredSize(new Dimension(0,0));
            viewport.setMaximumSize(new Dimension(0,0));
            
            JPanel vppanel = new JPanel();
            vppanel.add(viewport);
            vppanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.GREEN,2));
            
            this.add(trigger);
            this.add(viewport);
        }
        
        public void sizeViewport() {
            Dimension wdim = this.getSize();
            if (isCollapsed) {
                //viewport.setSize(wdim.width,0);
                viewport.setPreferredSize(new Dimension(wdim.width,0));
                viewport.revalidate();
            } else {
                Dimension dim;
                if (this.subview != null) {
                    dim = this.subview.getPreferredSize();
                } else {
                    dim = new Dimension(0,0);
                }
                //viewport.setMaximumSize(new Dimension(wdim.width,dim.height));
                viewport.setPreferredSize(new Dimension(wdim.width,dim.height));
                //viewport.setSize(wdim.width,dim.height);
                viewport.revalidate();
            }
            //this.validate();
            //this.revalidate();
            //this.doLayout();
            //this.invalidate();

        }
        
        public void toggleState() {
            isCollapsed = !isCollapsed;
            
            sizeViewport();
        }
        
        public void setSubview(JComponent subview) {
            this.subview = subview;
            viewport.removeAll();
            viewport.add(subview);
            
            Dimension viewportsize = viewport.getSize();
            
            //subview.setSize(viewportsize.width, viewportsize.height);
            Dimension requestedsize = subview.getPreferredSize();
            //viewport.setSize(viewportsize.width, requestedsize.height);
            viewport.setPreferredSize(new Dimension(viewportsize.width, requestedsize.height));
            //viewport.doLayout();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    
    /** Creates new form MVTest */
    public MVTest() {

        // This is the panel which will contain
        // our items (i.e. the multiview)
        JPanel mv;
        
        if (true) {
            MultiView mv1 = new MultiView();
            mv1.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            mv1.setVgap(10);
            mv = mv1;
        } else {
            mv = new JPanel();
            mv.setLayout(new VerticalStackedLayout(10));
        }
        
        // first panel
        CollapsablePanel cp1 = new CollapsablePanel();
        cp1.setSubview(new JButton("foobar"));
        mv.add(cp1);
        
        // second panel
        CollapsablePanel cp2 = new CollapsablePanel();
        mv.add(cp2);
        
        JTextPane t = new JTextPane();
        t.setContentType("text/html");
        t.setEditable(false);
        t.setText("This is a test of how this whole word wrapping size thing works");
        cp2.setSubview(t);
        
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(mv);
        this.setSize(400,400);
        
        sp.validate();
        
        this.getContentPane().add(sp,BorderLayout.CENTER);
        
        
        initComponents();
       
        
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        pack();
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        simpleimap.Debug.start();
        new MVTest().show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
