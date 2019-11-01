/*
 * ItemGUITextView.java
 *
 * Created on January 20, 2003, 4:45 PM
 */

package simpleimap;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.Dimension;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.regex.*;



import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;

/**
 *
 * @author  hassan
 */
public class ItemGUITextView extends ItemGUIBase implements ItemGUIInterface  {
    public static final String TypeID = "gui.ItemGUITextView";
    private JComponent topLevel;
    private JPanel aPanel;
    private JScrollPane my_scroller;
    //private JEditorPane emailContentView;
    private JTextPane emailContentView;
    /** Creates a new instance of ItemGUITextView */
    public ItemGUITextView() {
        super();
        
        aPanel = new JPanel();
        this.topLevel = aPanel;
        aPanel.setLayout(new java.awt.BorderLayout());
        
        this.emailContentView = new JTextPane();
        this.emailContentView.setEditable(false);
        this.emailContentView.setContentType("text/plain");
        
        my_scroller = new JScrollPane();
        my_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        my_scroller.setViewportView(this.emailContentView);
        
        aPanel.add(my_scroller, java.awt.BorderLayout.CENTER);
        
    } 
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUITextView();
            }
        });
    }    
    public java.awt.Component getComponent() {
        return this.topLevel;
    }
    
        
    public void clearViewedItem() {
        super.clearViewedItem();
        emailContentView.setText("");
    }
    
    
    public void setViewedItem(DefaultItem item) {
        super.setViewedItem(item);
        byte[] msg = null;
        if (item instanceof DefaultItem) {
            msg = item.getData();
        } else {
            Debug.debug("setViewedItem: cannot display item: " + item.toString());
            return;
        }
        
        if(msg == null) return;
        
        String text = new String(msg);
        
        emailContentView.setContentType("text/plain");
        emailContentView.setText(text);        
    }
}