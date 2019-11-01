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
 * ItemGUITree.java
 *
 * Created on November 2, 2002, 8:36 AM
 */

package simpleimap;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.event.*;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;

/**
 *
 * @author  David Jeske
 */
public class ItemGUITree extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUITree";
    JScrollPane my_scroller;
    private JTree my_tree;

    private class ExpansionState  {
        HashSet hash;
        ItemGUITree saveto;
        
        
        ////////////////// EXPANSION STATE /////////////////////////////////
        
        
        ExpansionState(ItemGUITree saveto) {
            this.saveto = saveto;
            hash = new HashSet();
        }
        public void unpack() {
            String expanded_tree_nodes = saveto.get("expanded_tree_nodes");
            if (expanded_tree_nodes != null ) {
                String [] parts = expanded_tree_nodes.split(",");
                for (int i = 0; i < parts.length; i++) {
                    if (!parts[i].equals("")) {
                        try {
                            int oid = Integer.parseInt(parts[i]);
                            hash.add("" + oid);
                        } catch (java.lang.NumberFormatException e) {
                            Debug.debug("Trouble decoding expand state part: " + parts[i]);
                        }
                    }
                }
                // now recursively force the nodes open!
            }
        }
        private void pack() {
            boolean first_element = true;
            String packed_str = "";
            
            for (Iterator itr = hash.iterator();
                 itr.hasNext();) {
                 if (first_element) {
                     first_element = false;
                 } else {
                     packed_str = packed_str + ",";
                 }
                 String oid = (String)itr.next();
                 packed_str = packed_str + oid;
            }
            saveto.put("expanded_tree_nodes",packed_str);
        }
        void expandNode(int oid) {
            hash.add("" + oid);
            pack();
        }
        void collapseNode(int oid) {
            hash.remove("" + oid);
            pack();
        }
        boolean isNodeExpanded(int oid) {
            return hash.contains("" + oid);
        }
        

    }; 

    private class MyTreeExpansionListener implements javax.swing.event.TreeExpansionListener {
        ItemGUITree mytree;
        MyTreeExpansionListener(ItemGUITree mytree) {
            this.mytree = mytree;
        }
        /** Called whenever an item in the tree has been collapsed.
         *
         */
        public void treeCollapsed(TreeExpansionEvent event) {
            mytree.expanded_nodes.collapseNode(((ItemTreeNodeInterface)event.getPath().getLastPathComponent()).getTreeItem().get_oid());
        }
        
        /** Called whenever an item in the tree has been expanded.
         *
         */
        public void treeExpanded(TreeExpansionEvent event) {
            mytree.expanded_nodes.expandNode(((ItemTreeNodeInterface)event.getPath().getLastPathComponent()).getTreeItem().get_oid());
        }
        
    }

    
    private ExpansionState expanded_nodes;
    
    ////////////////////////// ICON SUPPORT ////////////////////////////
    
    class MyRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            if (value instanceof ItemTreeNodeInterface) {
                ItemTreeNodeInterface node = (ItemTreeNodeInterface) value;
                setIcon(node.getIcon());
                setText(node.getTreeNodeName());
            }
            return this;
        }
    }

    
    
    ///////////////////////// POPUP //////////////////////////////////
    
    JPopupMenu tree_context_popup;
    
    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
           // maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
              DefaultItem item = (((ItemTreeNodeInterface)my_tree.getLastSelectedPathComponent()).getTreeItem());
              if (item != null) {
                List actions = item.getActions();
                if (actions != null) {
                    tree_context_popup = new JPopupMenu();
                    Iterator itr = actions.iterator();
                    while (itr.hasNext()) {
                        tree_context_popup.add((Action)itr.next());
                    }

                    tree_context_popup.pack();
                    tree_context_popup.setLocation(e.getX(),e.getY());
                    tree_context_popup.show(e.getComponent(),
                               e.getX(), e.getY());
                }
              }
            }
        }
    }
    
    
    /** Creates a new instance of ItemTemplate */
    public ItemGUITree() {
        super();
        my_scroller = new JScrollPane();
        my_tree = new JTree();
        my_tree.setCellRenderer(new MyRenderer());
        my_scroller.setViewportView(my_tree);
        
        
        expanded_nodes = new ExpansionState(this);
        
        my_tree.addMouseListener(new PopupListener());
        my_tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeValueChanged(evt);
            }
        });
        
        my_tree.addTreeExpansionListener( new MyTreeExpansionListener(this));
                
        my_scroller.addComponentListener(new ComponentListener() {
            public void componentMoved(ComponentEvent e) {
            }
            public void componentResized(ComponentEvent e) {
                Rectangle r = my_scroller.getBounds();
                put("Dimension", "" + r.width + "," + r.height);
                Debug.debug("resize for '" + item_storage.name + "' : (" + r.width + "," + r.height + ")");
            }
            public void componentShown(ComponentEvent e) {
            }
            public void componentHidden(ComponentEvent e) {
            }
            
        });
        
    }
    
    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {
       // first, find out what item was selected in the tree...
        
       
       //DefaultMutableTreeNode node = (DefaultMutableTreeNode)my_tree.getLastSelectedPathComponent();
       //DefaultItem item;
       //if (node == null) {
       //    item = null;
       //} else {
       //    item = (DefaultItem)node.getUserObject();
       //}
        TreeModel model = this.my_tree.getModel();
        
        ItemTreeNodeInterface node = (ItemTreeNodeInterface)(my_tree.getLastSelectedPathComponent()); 
        DefaultItem item = null;
        if (node != null) {
         item = node.getTreeItem();
        }
        
        if (model instanceof SimpleItemRelationTreeModel) {
            SimpleItemRelationTreeModel mymodel = (SimpleItemRelationTreeModel) model;

            DefaultItem rootNode = mymodel.rootItem;

            if(item != null) {
                int current_selected_oid = item.get_oid();
                rootNode.put("selected_row_oid/" + mymodel.relation.getSpec(), current_selected_oid);
            } else {
                rootNode.put("selected_row_oid/" + mymodel.relation.getSpec(), -1);
            }
        }

        if (item != null) {
            Debug.debug(0, "Sending tree selection changed, new OID: " + item.get_oid());
        } else {
            Debug.debug(0, "Sending tree unselected");
        }
        
        triggerListeners(item);
       
    }
    
    private void expand_nodes_recurse(TreeModel model, TreePath path, DefaultItem node) {
        int selected_row_oid = -1;       

        if(!(model instanceof SimpleItemRelationTreeModel)) return;
        SimpleItemRelationTreeModel mymodel = (SimpleItemRelationTreeModel) model;
        
        if (model == null) { return; }
        
        ItemTreeNodeInterface rootNode = (ItemTreeNodeInterface) model.getRoot();
        DefaultItem root = rootNode.getTreeItem();

        if (true) {
            return; // FIX - jeske
        }

        // extract selected row
        String selected_row_oid_str = root.get("selected_row_oid/" + mymodel.relation.getSpec());
        if (selected_row_oid_str != null) {
          selected_row_oid = Integer.parseInt(selected_row_oid_str);   
          Debug.debug("selected_row_oid read as: " + selected_row_oid);
        }
        if (node == null) { 
           node = (DefaultItem)root;
        }
        
        if (path == null) { path = new TreePath(node); }
        
        for (Iterator itr = node.getRelatedItems(mymodel.relation).iterator();
             itr.hasNext();) {
             DefaultItem item = (DefaultItem)itr.next();
             TreePath subpath = path.pathByAddingChild(item);
         
             if (item.get_oid() == selected_row_oid) {
                 Debug.debug("---- select node : " + item.get_oid() + ": " + item.get("name") + " ---");
                 Debug.debug("--- " + subpath.toString());

                 //my_tree.setSelectionPath(subpath);
                 my_tree.setSelectionPath(subpath);
                 my_tree.invalidate();
             }
             
             if (expanded_nodes.isNodeExpanded(item.get_oid())) {
                 // force node open..
                 my_tree.expandPath(subpath);
                 expand_nodes_recurse(model,subpath, item);
             }
             
         
        }
        
    }
    
    public void onActivate() {
        super.onActivate();
        initViewedItem();
        
        // load dimension
        String dim_str = get("Dimension");
        // my_scroller.setPreferredSize(new Dimension(150,150));   
        if (dim_str != null) {
            String[] parts = dim_str.split(",");
            int w = Integer.parseInt(parts[0]);
            int h = Integer.parseInt(parts[1]);
            my_scroller.setPreferredSize(new Dimension(w,h));    
            
            // Debug.debug("preferred size: " + w + "," + h);
        }
        
        // load tree state
        expanded_nodes.unpack();
        TreeModel model = this.my_tree.getModel();
        expand_nodes_recurse(model,null,null);

        
        if(model instanceof SimpleItemRelationTreeModel) {
            SimpleItemRelationTreeModel mymodel = (SimpleItemRelationTreeModel) model;
            
            int selectedRowOID = mymodel.rootItem.getInt("selected_row_oid/" + mymodel.relation.getSpec(), -1);
            if(selectedRowOID != -1) {
                DefaultItem viewed_item = WinterMute.my_db.getItem(selectedRowOID);
                
                int selection_index = mymodel.getIndexOfChild(mymodel.getRoot(), viewed_item);
                
                //ListSelectionModel tblSM = my_tree.getSelectionMo();
                
                // The selection row calls don't do anything unless
                // you make a row model - jeske
                //my_tree.setSelectionRow(selection_index);
                
                triggerListeners(viewed_item);
            }
        }
    }
    
    private void initViewedItem() {
        
        if (my_viewed_items.size() > 1) {
            throw new RuntimeException("Need compound Tree Model to view more than one tree!!!");
        }
        
        Iterator itr = my_viewed_items.iterator();
        while (itr.hasNext()) {
            DefaultItem subitem = (DefaultItem) itr.next();
            my_tree.setModel(subitem.makeTreeModel());
        }
    }
    
    
    protected void onViewedItemChange() {
        initViewedItem();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUITree();
            }
        });
    }
    
    public java.awt.Component getComponent() {
        return my_scroller;
    }
    
}