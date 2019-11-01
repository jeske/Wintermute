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
 * ItemGUIFrame.java
 *
 * Created on November 2, 2002, 8:10 AM
 */

package simpleimap;

import guicomp.toolbar.*;

import java.util.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 *
 * @author  David Jeske
 */
public class ItemGUIFrame extends ItemGUIBase implements ItemGUIInterface, IRelationChangeNotification {
    public static final String TypeID = "gui.ItemGUIFrame";
    javax.swing.JFrame my_frame;
    private boolean transientFlag = false;
    
    
    protected Toolbar my_menu_toolbar;
    protected ToolbarContainer my_toolbar_container;

    private ItemRelation menuRelation = new ItemRelation("menu","frame");
    
    /** Creates a new instance of ItemTemplate */
    public ItemGUIFrame() {
        super();
        my_icon = new ImageIcon(ClassLoader.getSystemResource("images/application.png"));
    }
    
    java.awt.Image my_image_icon = null;
        
    public void onActivate() {
        super.onActivate();
        
        this.item_storage.notifyOfRelationChange(this.menuRelation,this);
        
        // Instantiate the Frame
        my_frame = new javax.swing.JFrame(item_storage.name);

        // Listen for window close
        my_frame.addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
               exitForm(evt);
            }
        });

        
        // Setup the Window Icon
        java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
        my_image_icon = kit.getImage(ClassLoader.getSystemResource("images/application.gif"));
        my_frame.setIconImage(my_image_icon);
        
        
        // setup dimension
        String dim_str = this.get("Dimension");
        if (dim_str == null) {
            my_frame.setSize(new Dimension(640,480));
        } else {
            String[] parts = dim_str.split(",");
            if (parts.length == 2) {
                int w = Integer.parseInt(parts[0]);
                int h = Integer.parseInt(parts[1]);
                my_frame.setSize(new Dimension(w,h));
            } else {
                my_frame.setSize(new Dimension(640,480));
            }
        }
        
        // setup position
        String pos_str = this.get("Position");
        if (pos_str != null) {
            String parts[] = pos_str.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            my_frame.setLocation(x,y);
        }
        
        // watch for position and size changes
        my_frame.addComponentListener(new ComponentListener() {
            public void componentMoved(ComponentEvent e) {
                Rectangle r = my_frame.getBounds();
                put("Position", "" + r.x + "," + r.y);
            }
            public void componentResized(ComponentEvent e) {
                Rectangle r = my_frame.getBounds();
                put("Dimension", "" + r.width + "," + r.height);
            }
            public void componentShown(ComponentEvent e) {
            }
            public void componentHidden(ComponentEvent e) {
            }
            
        });
        
        // Make sure we are in border layout mode for the toolbar to work!
        JPanel container = new JPanel();
        BorderLayout layoutmgr = new BorderLayout();
        layoutmgr.setVgap(0);
        container.setLayout(layoutmgr);
        my_frame.setContentPane(container);
        
        // ---------------------------------------------------------------
        // Add: Toolbar Container
        
        ToolbarContainer tc = new ToolbarContainer(true);
        my_toolbar_container = tc;
        tc.addToFrame(my_frame);

        // add menu bar
        this.refreshMenus();
        
        // add static toolbars
        if (this.get("name").equals("Wintermute Mail")) {
            this.buildStaticToolbar();
        }
        
        // ---------------------------------------------------------------
        // Add: Subitems
        JPanel subcontainer = new JPanel();
        subcontainer.setLayout(new GridLayout());
        Iterator itr = my_subitems.iterator();
        
        while (itr.hasNext()) {
            Object obj = itr.next();
            try {
                ItemGUIInterface subitem = (ItemGUIInterface) obj;
                subcontainer.add(subitem.getComponent());
            } catch (ClassCastException e) {
            }
        }
        container.add(subcontainer,BorderLayout.CENTER);
        
        if (false) {
            show();
        } else {
            // make sure we show in the AWT mainthread!

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    show();
                }
            });
        }
        
    }
    
    private void refreshMenus() {
        Debug.debug("refresh menus");
        boolean has_menus = false;
        
        JMenuBar mb = new JMenuBar();
        // ---------------------------------------------------------------
        // Add: Menu
        List my_menus = this.getRelatedItems(this.menuRelation);        
        if (my_menus != null && my_menus.size() > 0) {
            Iterator mitr = my_menus.iterator();
            while (mitr.hasNext()) {
                ItemGUIMenu menu = (ItemGUIMenu) mitr.next();
                mb.add(menu.getMenu());
                has_menus = true;
                Debug.debug("add item to bar: " + menu.get_oid() + ":" + menu.get("name"));
            }
        }

        
        if (my_menu_toolbar == null) {
            if (!has_menus) return;
            my_menu_toolbar = new Toolbar("Menu","NAV_ID_MENU:" + this.get_oid());
            my_toolbar_container.add(my_menu_toolbar);
        } else {
            my_menu_toolbar.removeAll();
        }
        my_menu_toolbar.add(mb);
 
    }

    
    private void buildStaticToolbar() {
        ToolbarContainer tc = this.my_toolbar_container;
        
        // global Actions
        Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1:" + this.get_oid());
        tb_1.add(new JButton("New", new ImageIcon(ClassLoader.getSystemResource("images/compose-message.png"))));
        JButton sendrecvButton = new JButton("Send Receive", new ImageIcon(ClassLoader.getSystemResource("images/send-24-receive.png")));
        tb_1.add(sendrecvButton);
        sendrecvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Debug.debug("trigger server sync");
            }
        });
        tc.add(tb_1);
        
        // message Actions
        Toolbar tb_2 = new Toolbar("Message Actions","NAV_ID_MessageActions:" + this.get_oid());
        JButton replyButton = new JButton("Reply", new ImageIcon(ClassLoader.getSystemResource("images/reply.png")));
        tb_2.add(replyButton);
        JButton replyAllButton = new JButton("Reply All", new ImageIcon(ClassLoader.getSystemResource("images/reply-to-all.png")));
        tb_2.add(replyAllButton);
        JButton forwardButton = new JButton("Forward");
        tb_2.add(forwardButton);
        tc.add(tb_2);

        // search toolbar
        Toolbar tb_3 = new Toolbar("Search", "NAV_ID_Search:" + this.get_oid()); 
        tb_3.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/search.png"))));
        JTextField searchtextbox = new JTextField();
        tb_3.add(searchtextbox);     
        tc.add(tb_3);
        
        searchtextbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JTextField field = (JTextField)evt.getSource();
                String text = field.getText();
                DefaultItem results = WinterMute.indexmgr.searchFor(text);
                WinterMute.my_db.rootNode().relateTo(WinterMute.parentChildRelation,results);
            }
        });


        // Calendar
        Toolbar tb_4 = new Toolbar("Calendar", "NAV_ID_Calendar:" + this.get_oid());
        tb_4.add(new JButton("New Event", new ImageIcon(ClassLoader.getSystemResource("images/newappointment.png"))));
        tb_4.add(new JButton("Goto Today"));
        tb_4.add(new JButton("Day", new ImageIcon(ClassLoader.getSystemResource("images/caldayview.png"))));
        tb_4.add(new JButton("Work Week", new ImageIcon(ClassLoader.getSystemResource("images/calworkweek.png"))));
        tb_4.add(new JButton("Week", new ImageIcon(ClassLoader.getSystemResource("images/calweekview.png"))));
        tb_4.add(new JButton("Month", new ImageIcon(ClassLoader.getSystemResource("images/calmonthview.png"))));
        tc.add(tb_4);
    }
    
    
    public void setTransient(boolean frameType) {
        this.transientFlag = frameType;
    }
    public void show() {
        my_frame.show();
    }
    
    private void exitForm(java.awt.event.WindowEvent evt) {
        this.my_frame.hide();
        if(this.transientFlag == false) {
            WinterMute.exitApplication();
        }
    }
    
    
    public java.awt.Component getComponent() {
        return my_frame;
    }

    
    public void addChild(DefaultItem item) {
        super.addChild(item);
        ItemGUIInterface subitem = (ItemGUIInterface) item;
        Container panel = my_frame.getContentPane();
        panel.add(subitem.getComponent());
        panel.validate();
        
    }
    
    public void addMenu(ItemGUIMenu menu) {
        if(this.isRelated(WinterMute.menuFrameRelation, menu)) return;
        
        this.relateTo(WinterMute.menuFrameRelation, menu);
        // this.refreshMenus();
    }
    
    //////////////////////
    
     public void itemAddedAfter(ItemRelation relation, DefaultItem item, DefaultItem afterItem) {
         if (relation.equals(menuRelation)) {
             refreshMenus();
         }
     }
     
     public void itemRemoved(ItemRelation relation, DefaultItem item) {
         if (relation.equals(menuRelation)) {
             refreshMenus();
         }
     }
    
    
    //////////////////////
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIFrame();
            }
        });
    }
    
    
}
