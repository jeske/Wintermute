/*
 * Wintermute - Personal Data Organizer
 * 
 * Copyright (C) 2002, by David Jeske
 *
 * Written by David Jeske <jeske@neotonic.com>. 
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * ItemGUISplitPane.java
 *
 * Created on November 2, 2002, 8:25 AM
 */

package simpleimap;

import javax.swing.*;
import java.util.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.event.*;


/**
 *
 * @author  David Jeske
 */
public class ItemGUISplitPane extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUISplitPane";
    JSplitPane my_split_pane;
    JPanel my_panel;
    
    
    /** Creates a new instance of ItemTemplate */
    public ItemGUISplitPane() {
        super();
        my_panel = new JPanel();
        my_panel.setLayout(new java.awt.BorderLayout());
        my_panel.setBorder(BorderFactory.createEmptyBorder());
    }
    protected void onActivate() {
        super.onActivate();
        initContainedItems();
    }
    public void addChild(DefaultItem item) {
        super.addChild(item);
        initContainedItems();
    }
    

    
    private void initContainedItems() {
        Iterator itr = my_subitems.iterator();
        int gui_count = 0;
        int splitter_orientation = JSplitPane.HORIZONTAL_SPLIT;
        
        String vsplit = this.get("vsplit");

        
        
        if (vsplit != null && vsplit.equals("yes")) {
            splitter_orientation = JSplitPane.VERTICAL_SPLIT;
        }
        
        Object[] subitems = my_subitems.toArray();
        
        // first, count the GUI components
        for (int i=0;i<subitems.length;i++) {
            if (subitems[i] instanceof ItemGUIInterface) {
                gui_count++;
            }
        }
        
        // second, build the splitter tree...
        
        if (gui_count == 1) {
            my_panel.removeAll();
            
            for (int i=0;i<subitems.length;i++) {
                if (subitems[i] instanceof ItemGUIInterface) {
                    ItemGUIInterface gui_item = (ItemGUIInterface) subitems[i];
                    my_panel.add(gui_item.getComponent());
                    break;
                }
            }
        } else {
            JSplitPane cur = new JSplitPane();
            cur.setOrientation(splitter_orientation);
            cur.setDividerSize(5);
            cur.setBorder(BorderFactory.createEmptyBorder());

            
            
            my_panel.removeAll();
            my_panel.add(cur);

            for (int i=0;i<subitems.length;i++) {
                if (subitems[i] instanceof ItemGUIInterface) {
                    ItemGUIInterface gui_item = (ItemGUIInterface) subitems[i];
                    if (gui_count == 1) {
                        // last one, put it on the right...
                        cur.setRightComponent(gui_item.getComponent());
                        break; // we are done!
                    } else if (gui_count == 2) {
                        // only two more, put this in the left....
                        cur.setLeftComponent(gui_item.getComponent());
                    } else {
                        // lots more, make another splitter...
                        cur.setLeftComponent(gui_item.getComponent());
                        JSplitPane sp = new JSplitPane();
                        sp.setOrientation(splitter_orientation);
                        sp.setDividerSize(5);

                        cur.setRightComponent(sp);
                        cur = sp;
                        
                    }
                    gui_count--;
                }
            }
            
            if(true) {
                String resizeWeight_str = this.get("resizeWeight");
                if (resizeWeight_str != null) {
                    float resizeWeight = Float.parseFloat(resizeWeight_str);
                    if (resizeWeight < 0 || resizeWeight > 1) {
                        Debug.debug("invalid resizeWeight: " + resizeWeight_str);
                        this.put("resizeWeight", "0.5");
                        resizeWeight = 0.5f;
                    } 
                    cur.setResizeWeight(resizeWeight);
                    
                    int m = cur.getMaximumDividerLocation();
                    Debug.debug("--------------------------->>> m=" + m);
                }
            }

            int dividerLocation = this.getInt("dividerLocation", 0);
            if (dividerLocation > 0) {
                cur.setDividerLocation(dividerLocation);
            }            
            
            String dividerSize_str = this.get("dividerSize");
            if (dividerSize_str != null) {
                int size = Integer.parseInt(dividerSize_str);
                cur.setDividerSize(size);
            } else {
               cur.setDividerSize(5);
            }
            
            cur.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new ResizeListener(this));
 
            cur.setContinuousLayout(true);
            
        }
        my_panel.validate();
                
    }
    
    public class ResizeListener implements PropertyChangeListener {
        DefaultItem item;
        public ResizeListener(DefaultItem item) {
            this.item = item;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            JSplitPane p = (JSplitPane) evt.getSource();
            int m = p.getMaximumDividerLocation();
            Integer val = (Integer) evt.getNewValue();
            
            double resizeWeight = 1.0 * val.intValue() / (double) m;
            Debug.debug("divider location=" + val + "/" + m + "=" + resizeWeight);
            
            if(this.item.get("resizeWeight") != null) {
                this.item.put("resizeWeight", resizeWeight);
            }
            this.item.put("dividerLocation", val.toString());
        }
    }
    
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUISplitPane();
            }
        });
    }
    
    public java.awt.Component getComponent() {
        return my_panel;
    }
    
}
