/*
 * ItemGUIDocument.java
 *
 * Created on December 15, 2002, 1:02 PM
 */

package simpleimap;

import java.awt.BorderLayout;


import javax.swing.*;

/**
 *
 * @author  jeske
 */
public class ItemGUIDocument extends DefaultItem implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUIDocument";
    javax.swing.JPanel mypanel;
    
    JPanel top_panel;
    JPanel title_panel;
    JLabel title_label;
    JLabel title_icon;
    
    /** Creates a new instance of ItemGUIDocument */
    public ItemGUIDocument() {
        mypanel = new JPanel(new BorderLayout());
        
        // top level panel
        top_panel = new JPanel();
        top_panel.setLayout(new BorderLayout());
        top_panel.setBorder(BorderFactory.createEmptyBorder());
        // top_panel.add(my_scroller,BorderLayout.CENTER);
        
        // title panel
        title_panel = new JPanel();
        title_panel.setBackground(java.awt.Color.GRAY);
        title_panel.setLayout(new BorderLayout());
        title_panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        title_label = new JLabel();
        title_label.setForeground(java.awt.Color.WHITE);
        title_label.setOpaque(false);
        title_panel.add(title_label,BorderLayout.CENTER);
        
        title_icon = new JLabel();
        title_panel.add(title_icon, BorderLayout.WEST);
        
        top_panel.add(title_panel,BorderLayout.NORTH);
        top_panel.add(mypanel,BorderLayout.CENTER);
        
        
        // should be in onActivate
        // top_panel.setName(this.get("name"));
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIDocument();
            }
        });
    }
    
    public void clearViewedItem() {
        mypanel.removeAll();
        mypanel.revalidate(); // revalidate the layout internals
        mypanel.repaint();    // request a repaint
    }
    
    public java.awt.Component getComponent() {
        return top_panel;
    }
    
    public void setViewedItem(DefaultItem item) {
        // pick the right GUI for this item type
        ItemGUIInterface gui = this.guiForItem(item);
        mypanel.removeAll();
        if (gui != null) {
            gui.clearViewedItem();
            java.awt.Component subcomponent = gui.getComponent();
            mypanel.add(subcomponent);
            subcomponent.setSize(mypanel.getSize());
            
            //subcomponent.invalidate();
            
            gui.setViewedItem(item);
        } else {
            mypanel.add(new JLabel("no GUI for item: " + item.get_oid() + " type: " + item.toString()));
            
        }
     
        // set title from subitem
        if (item != null) {
            title_label.setText(item.get("name"));
            title_icon.setIcon(item.getIcon());
        } else {
            title_label.setText("");
            title_icon.setIcon(null);
        }
        
        mypanel.revalidate();
        mypanel.repaint();    // request a repaint
        
        // for more info on PAINTING see this:
        // http://java.sun.com/docs/books/tutorial/uiswing/overview/draw.html
    }
    
    private ItemGUIInterface guiForItem(DefaultItem item) {
        String viewer = item.get("default_viewer");
        if (viewer != null) {
            DefaultItem guiitem = WinterMute.my_db.getItemFromName(viewer);
            if (guiitem instanceof ItemGUIInterface) {
                return (ItemGUIInterface) guiitem;
            }
        }
        
        if (item instanceof ItemIMAPFolder || item instanceof ItemUnixMailboxFolder || item instanceof ItemUnixMailbox) {
            return (ItemGUIInterface) WinterMute.my_db.getItemFromName("folder_viewer");
        } else if (item instanceof ItemICal || item.get("name").equals("Calendar")) {
            return (ItemGUIInterface) WinterMute.my_db.getItemFromName("calendar_viewer");
        } else if (item instanceof ItemICal || item.get("name").equals("Contacts")) {
            return (ItemGUIInterface) WinterMute.my_db.getItemFromName("contact_viewer");
        } else {
            return (ItemGUIInterface) WinterMute.my_db.getItemFromName("item_viewer");
        }
        
        //return null;
    }
    
}
