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
 * GUITableColumnConfig.java
 *
 * Created on November 5, 2002, 7:57 AM
 */

package simpleimap;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.tree.TreePath;
/**
 *
 * @author  David W Jeske
 */
public class GUITableColumnConfig extends javax.swing.JDialog {
    ItemGUITable my_table;
    
    /** Creates new form GUITableColumnConfig */
    public GUITableColumnConfig(JFrame owner, ItemGUITable my_table) {
        super(owner);
        this.my_table = my_table;
        initComponents();
        this.setTitle("Configure Table [" + my_table.get("name") + "]");
    }
    public void show() {
        propTree.setModel(my_table.getTreeModelForProperties());
        
        DefaultItem columnset = my_table.getCurrentColumnConfiguration();
        columnTbl.setModel(columnset.makeTableModelForRelation(WinterMute.columnsetColconfigRelation));
        
        super.show();
    }
    private void addColumn(ActionEvent e) {
        // find the selected item in the tree
        Object selectedObject = propTree.getLastSelectedPathComponent();
        if (!(selectedObject instanceof SimpleItemStorage.PropertySlot)) {
            return;
        }
        
        TreePath treepath = propTree.getSelectionPath();
        Object[] objs = treepath.getPath();
        
        
        if(objs.length <= 1) return;
        
        
        ItemDefault columnconfig = (ItemDefault) WinterMute.my_db.newItem(null,  ItemDefault.TypeID, null);
        
        LinkedList relations = new LinkedList();
        StringBuffer sb = new StringBuffer();
        
        for(int i=0; i<objs.length; i++) {
            Debug.debug("" + i + ". ", objs[i], objs[i].getClass().getName());
            if(objs[i] instanceof SimpleItemStorage.RelationSlot) {
                SimpleItemStorage.RelationSlot rs = (SimpleItemStorage.RelationSlot) objs[i];
                String relation = rs.relation.getSpec();
                relations.add(relation);
            } else if (objs[i] instanceof SimpleItemStorage.PropertySlot) {
                SimpleItemStorage.PropertySlot ps = (SimpleItemStorage.PropertySlot) objs[i];
                ItemField field =  ((SimpleItemStorage.PropertySlot)selectedObject).getField();
                
                if (field == null) { return; }
                
                for(Iterator iter = relations.iterator(); iter.hasNext(); ) {
                    String relation = (String) iter.next();
                    if(sb.length() > 0) sb.append(".");
                    sb.append(relation);
                }
                
                if(sb.length() > 0) {
                    columnconfig.put("followRelation", sb.toString());
                }

                // create a new colconfig
                columnconfig.relateTo(WinterMute.colConfigFieldRelation, field);
                
                String title = field.getFieldName();
                if(sb.length() > 0) {
                    title = title + " [" + sb.toString() + "]";
                }
                columnconfig.put("Title", title);
                columnconfig.put("width", "50");
                
                

            } else {
            }
        }


        // add it to the relation!
        DefaultItem columnset = my_table.getCurrentColumnConfiguration();
        columnset.relateTo(WinterMute.columnsetColconfigRelation,columnconfig);
        
    }
    private void removeColumn(ActionEvent e) {
        // find the selected column
        
        int selectedIndex = columnTbl.getSelectedRow();
        if (selectedIndex == -1) { return; }
        
        List items = my_table.getRelatedItems(new ItemRelation("table","columnset"));
        DefaultItem columnset = (DefaultItem)items.get(0);
        
        List cols = columnset.getRelatedItems(new ItemRelation("columnset","colconfig"));
        DefaultItem col = (DefaultItem)cols.get(selectedIndex);
        
        col.unrelateFrom(new ItemRelation("colconfig","columnset"), columnset);
        
        
    }
    
    private void configureColumnOptions(ActionEvent e) {
        // find the selected column

        int selectedIndex = columnTbl.getSelectedRow();
        if (selectedIndex == -1) { return; }
        
        List items = my_table.getRelatedItems(new ItemRelation("table","columnset"));
        DefaultItem columnset = (DefaultItem)items.get(0);
        
        List cols = columnset.getRelatedItems(new ItemRelation("columnset","colconfig"));
        DefaultItem col = (DefaultItem)cols.get(selectedIndex);
        
        
        
        
        JDialog configframe = new GUITableColumnOptions(col,this);
        configframe.setSize(400,400);
        Object src_obj = e.getSource();
        if (src_obj instanceof Component) {
            Component source = (Component) src_obj;
            configframe.setLocationRelativeTo(source);
        
            // java.awt.Point sourceloc = source.getLocation();
            // setLocation(sourceloc.getX()-configframe.getWidth(),sourceloc.getY());
        }
        
        configframe.show();
        
    }
   
    JTabbedPane tabpane;
    
    private void initComponents() {
        columnTbl = new javax.swing.JTable();
        


        java.awt.Container mypane = this.getContentPane();
        tabpane = new JTabbedPane();
        mypane.add(tabpane);
        
        //////////////////////////////////
        //
        // columns config panel
        
        
        
        
        // left side
        JPanel propspanel = new JPanel();
        propspanel.setLayout(new BorderLayout());
        propspanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        
        propspanel.add(new JLabel("Available Properties"),
                BorderLayout.NORTH);

        propTree = new javax.swing.JTree();
        propTree.setBorder(new javax.swing.border.EtchedBorder());
        propspanel.add(new JScrollPane(propTree),BorderLayout.CENTER);

        JPanel leftBottomButtonPanel = new JPanel();
        leftBottomButtonPanel.setLayout(new java.awt.FlowLayout());
        leftBottomButtonPanel.setAlignmentX(java.awt.FlowLayout.TRAILING);
        propspanel.add(leftBottomButtonPanel,BorderLayout.SOUTH);
        
        addButton = new javax.swing.JButton();
        addButton.setText("Add Column");
        leftBottomButtonPanel.add(addButton);
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addColumn(e);
            }
        });
        
        // right side
        JPanel colspanel = new JPanel();
        colspanel.setLayout(new BorderLayout());
        colspanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        
        colspanel.add(new JLabel("Columns"),BorderLayout.NORTH);
        
        columnTbl.setBorder(new javax.swing.border.EtchedBorder());
        columnTbl.setPreferredSize(new java.awt.Dimension(10, 10));
        colspanel.add(columnTbl, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new java.awt.FlowLayout());
        colspanel.add(bottomButtonPanel,BorderLayout.SOUTH);
        
        removeButton = new javax.swing.JButton();
        removeButton.setText("<-- Remove");
        removeButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeColumn(e);
            }
        });
        bottomButtonPanel.add(removeButton);
        
        JButton configure = new JButton("Options");
        configure.addActionListener( new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               configureColumnOptions(e);
           }
        });
        bottomButtonPanel.add(configure);
        
        

        JSplitPane colconfigsplitter = 
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        propspanel, colspanel);
                
        colconfigsplitter.setResizeWeight(0.5);
        colconfigsplitter.setBorder(BorderFactory.createEmptyBorder());
        tabpane.addTab("Columns", colconfigsplitter);
        tabpane.setSelectedIndex(0);
        
        //
        //////////////////////////////////////
        //
        // Filters
        //
        JPanel filterpanel = new JPanel();
        
        tabpane.addTab("Filters",filterpanel);
        
        //
        /////////////////////////////////////
        //
        // Table Appearance
        //

        JPanel appearancepanel = new JPanel();
        appearancepanel.setLayout(new guicomp.VerticalStackedLayout());

        JPanel rbPanel = new JPanel();
        rbPanel.setBorder(BorderFactory.createTitledBorder("Resize Behavior"));
        rbPanel.setLayout(new java.awt.GridLayout(2,2));
        appearancepanel.add(rbPanel);
        
        ButtonGroup bg = new ButtonGroup();
        JRadioButton abutton;
        
        abutton = new JRadioButton("Resize Table");
        rbPanel.add(abutton);
        bg.add(abutton);
        
        abutton = new JRadioButton("Resize Next Column");
        rbPanel.add(abutton);
        bg.add(abutton);
        
        abutton = new JRadioButton("Resize Subsequent Columns");
        rbPanel.add(abutton);
        bg.add(abutton);
        
        abutton = new JRadioButton("Resize All Columns");
        rbPanel.add(abutton);
        bg.add(abutton);
        
        
        tabpane.addTab("Appearance",appearancepanel);
        
        //
        //////////////////////////////////
        
        
        pack();
        this.setSize(400,400);
    }
    
        
    private javax.swing.JButton addButton, removeButton;
    private javax.swing.JTable columnTbl;
    private javax.swing.JTree propTree;
   
    
}
