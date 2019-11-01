
package guicomp;

import simpleimap.Debug;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.table.*;

/* TODO
 *
 *
 *  - make sure an open event scrolls down to expose the view
 *
 *
 *
 */

class TestFrame extends JFrame {
    java.awt.Color tbColor = null;

    /** Creates new form MVTest */
    private TestFrame() {

        // http://java.sun.com/j2se/1.4/docs/guide/swing/1.4/w2k_props.html
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        tbColor = (java.awt.Color) tk.getDesktopProperty("win.frame.activeCaptionGradientColor");

        
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
        CollapsePanel cp1 = new CollapsePanel();
        cp1.setSubview(new JButton("foobar"));
        mv.add(cp1);
        
        // second panel
        for(int i=0;i<3;i++) {
            CollapsePanel cp2 = new CollapsePanel();
            mv.add(cp2);

            JTextPane t = new JTextPane();
            t.setContentType("text/html");
            t.setEditable(false);
            t.setText("This is a test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping test of how this whole word wrapping size thing works This is a test of how this whole word wrapping size thing works This is a test of how this whole word wrapping size thing works");
            cp2.setSubview(t);
        }
        
        
        // fourth panel with a JTable
        CollapsePanel cp3 = new CollapsePanel();
        mv.add(cp3);
        
        JTable tbl = new JTable();
        Object[] columnNames = { "Test", "Test2" };
        DefaultTableModel tm = new DefaultTableModel(columnNames, 0);
        tbl.setModel(tm);
        JScrollPane sp2 = new JScrollPane();
        sp2.setViewportView(tbl);
        
        cp3.setSubview(sp2);
        
        Object[] testRow = { "a", "b" };
        tm.addRow(testRow);
        
        
        
        // the scrollpane for MultiView to fit in...
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(mv);
        if (tbColor == null) { tbColor = java.awt.Color.WHITE; }
        sp.getViewport().setBackground(tbColor);
        this.setSize(400,400);
        
        sp.validate();
        
        this.getContentPane().add(sp,BorderLayout.CENTER);
    }
    
    
    public static void main(String args[]) {
        simpleimap.Debug.start();
        
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        String propnames[] = (String[])tk.getDesktopProperty("win.propNames");
        Debug.debug("Supported windows property names:");
        for(int i = 0; i < propnames.length; i++) {
            Debug.debug(propnames[i]);
        }
        
        
        new TestFrame().show();

        
        
    }
    
}