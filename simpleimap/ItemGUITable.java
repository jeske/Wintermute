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
 * ItemGUITable.java
 *
 * Created on November 2, 2002, 10:08 AM
 */

package simpleimap;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;

/**
 *
 * @author  David Jeske
 */
  class MyJTable extends JTable {
        ItemGUITable owner = null;
        MyJTable(ItemGUITable o) {
            this.owner = o;
        }
        
        public void tableChanged(TableModelEvent evt) {
            if (owner != null) {
                owner.beforeTableChanged(evt);
            }
            super.tableChanged(evt);
            if (owner != null) {
                owner.afterTableChanged(evt); // tell our owner!
            } else {
                Debug.debug("MyJTable: null owner");
            }
        }
    }

public class ItemGUITable extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUITable";
    JScrollPane my_scroller;
    MyJTable my_table;
    JPopupMenu column_config_popup;
    
    ItemTableModel curColumnModel;

    
    int current_selected_oid = -1;
 
    
    
    class PopupListener extends MouseAdapter {
        ItemGUITable table;
        PopupListener(ItemGUITable table) {
            this.table = table;
        }
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private class SelectColConfigAction implements ActionListener {
            private int obj_oid;
            SelectColConfigAction(int oid) {
                obj_oid = oid;
            }
            
            /** Invoked when an action occurs.
             *
             */
            public void actionPerformed(ActionEvent e) {
                selectColConfig(obj_oid);
            }
            
        };
        
        private void maybeShowPopup(MouseEvent e) {
            //if (e.isPopupTrigger()) {
            // Column config popup
            column_config_popup = new JPopupMenu();
        
            // if we have saved column configs...
            //    add them
            //    add separator
            //column_config_popup.addSeparator();

            // add existing columnconfigs
            
            List configs = getRelatedItems(WinterMute.tableColumnsetRelation);
            Iterator config_loop = configs.iterator();
            boolean need_sep = false;
            while (config_loop.hasNext()) {
                need_sep = true;
                
                DefaultItem colconfig = (DefaultItem)config_loop.next();
                String name = colconfig.get("name");
                boolean check_state = false;
                
                if (colconfig == getCurrentColumnConfiguration()) {
                    check_state = true;
                }
                
                JCheckBoxMenuItem mi = new JCheckBoxMenuItem(name,check_state);
                mi.addActionListener(new SelectColConfigAction(colconfig.get_oid()));
                
                column_config_popup.add(mi);
            }
            if (need_sep) {
                column_config_popup.addSeparator();
                need_sep = false;
            }
            
            
            // customize table columns...
            JMenuItem menuItem = new JMenuItem("Customize...");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showMyColumnConfigUI(e);
                }
            });
            column_config_popup.add(menuItem);
            
            
            // create new column config
            menuItem = new JMenuItem("New Column Config");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    makeEmptyColumnConfig();
                }
            });
            column_config_popup.add(menuItem);
            
            
            String relation_to_view = table.get("relation_to_view");
            if (relation_to_view != null && "SELF".equals(relation_to_view)) {
                JMenuItem addPropMenuItem = new JMenuItem("Add Property...");
                addPropMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        addNewProp();
                    }
                });
                column_config_popup.add(addPropMenuItem);
            }


            
            column_config_popup.pack();
            column_config_popup.setLocation(e.getX() - column_config_popup.getWidth(),e.getY());
            column_config_popup.show(e.getComponent(),
                       e.getX() - column_config_popup.getWidth(), e.getY());
            //}
        }
    }
    
    public TreeModel getTreeModelForProperties() {
        TableModel tm = my_table.getModel();
        if (tm instanceof ItemTableModel) {
            ItemTableModel a_model = (ItemTableModel)my_table.getModel();
            if (a_model != null && a_model.getRowCount() > 0) {
                int selectedRow = my_table.getSelectedRow();
                if(selectedRow == -1) selectedRow = 0;
                DefaultItem an_item = (DefaultItem)a_model.getItem(selectedRow);
                if(an_item == null) {
                    Debug.debug("an_item is null");
                    DefaultTreeModel tm2 = new DefaultTreeModel(new DefaultMutableTreeNode());
                    return tm2;
                } else {
                    return an_item.getTreeModelForProperties();
                }
            }
        } else {
            Debug.debug("TableModel is wrong class: " + tm.getClass());
        }
        
        // return empty model...
        return new DefaultTreeModel(new DefaultMutableTreeNode());
    }
    
    private int columnHeaderWidth(TableColumn column) {
        //Get the Renderer foe the Table Header
        TableCellRenderer renderer = my_table.getTableHeader().getDefaultRenderer();

        //Get the Renderer Component
        Component component = renderer.getTableCellRendererComponent( my_table, column.getHeaderValue(), false, false, 0, 0);
        //returns the Preferred width of the Header
        return component.getPreferredSize().width;
    }

    
    /** Creates a new instance of ItemTemplate */
    public ItemGUITable() {
        super();
        

        
        
        my_table = new MyJTable(this);
        my_table.setBorder(BorderFactory.createEtchedBorder());
        
        my_scroller = new JScrollPane(my_table);
        my_scroller.setColumnHeader(new JViewport());  // this is to workaround an init bug - jeske
        my_scroller.setName("ItemGUITable.Scroller");
        my_scroller.getViewport().setName("ItemGUITable.Scroller.Viewport");
        my_scroller.setBorder(BorderFactory.createEmptyBorder());
        
        
        
        JTableHeader hdr = my_table.getTableHeader();
        hdr.setResizingAllowed(true);
        hdr.setReorderingAllowed(true);

        
        my_scroller.setViewportView(my_table);
        my_scroller.getViewport().setBackground(java.awt.Color.white);
        my_scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JViewport vp = my_scroller.getViewport();
        
        vp.addChangeListener(new ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JViewport vp = my_scroller.getViewport();
                java.awt.Point p = vp.getViewPosition();
                
                //Debug.debug("point", p);
                
                TableModel tm = my_table.getModel();
                if(tm instanceof TableSorter) {
                    TableSorter ts = (TableSorter) tm;
                    tm = ts.getModel();
                }
                if(tm instanceof SimpleItemRelationTableModel ) {
                    SimpleItemRelationTableModel itm_new_model2 = (SimpleItemRelationTableModel) tm;
                    String key = "scroll_position." + itm_new_model2.parentRelation.getSpec();
                    if(itm_new_model2.parentItem.get(key) != null || p.y > 0) {
                        itm_new_model2.parentItem.put(key, "" + p.x + "," + p.y);
                    }
                }
            }
        });        
        
        // my_table.setIntercellSpacing(new Dimension(0, 0));
        // my_table.setGridColor(java.awt.Color.red);
        //my_table.setRowMargin(0);
        my_table.setRowSelectionAllowed(true);
        my_table.setShowGrid(false);
        
        my_table.setIntercellSpacing(new Dimension(1,1));
        //my_table.getTableHeader().getColumnModel().setColumnMargin(0);

        //my_table.setRowMargin(0);
        my_table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));

        //my_table.setCellSelectionEnabled(false);
        //my_table.setSelectionBackground(java.awt.Color.blue);
        //my_table.setSelectionForeground(java.awt.Color.white);
        my_table.addMouseListener(new PopupListenerRow());

        // Column config button...
        JButton foo = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/colconfig.gif")));
        foo.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Debug.debug("button clicked");
                //column_config_popup.show(e.getComponent(),
//                           e.getX(), e.getY());
            }
        });

        MouseListener popupListener = new PopupListener(this);
        foo.addMouseListener(popupListener);
        
        
        
        my_scroller.setCorner(JScrollPane.UPPER_RIGHT_CORNER,foo);
        
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
        
        
        ListSelectionModel rowSM = my_table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent  evt) {
                selectionChanged(evt);
            }
        });
        my_scroller.setPreferredSize(new Dimension(150,150));
        
        
        Debug.debug("ItemGUITable: constructed");
    }
    
    public DefaultItem getCurrentColumnConfiguration() {
        List items = this.getRelatedItems(new ItemRelation("table","columnset"));
        if(items == null || items.size() == 0) return null;
        String curcoloid = this.get("CurrentColumnConfigOID");
        if (curcoloid != null) {
            int oid = Integer.parseInt(curcoloid);
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                DefaultItem columnset = (DefaultItem)itr.next();
                if (columnset.get_oid() == oid) {
                    return columnset;
                }
            }
        }

        DefaultItem columnset = (DefaultItem)items.get(0);       
        return columnset;
    }

    private void addNewProp() {
        // quick and dirty property creation

        if (my_viewed_items != null && my_viewed_items.size()==1) {
            DefaultItem item = (DefaultItem) my_viewed_items.get(0);
            int n = 0;
            String propname = "newprop";
            while (item.get(propname) != null) {
              n++;
              propname = "newprop" + n;
            }
            item.put(propname,"");
        }
        
    }
    
    public void selectColConfig(int oid) {
        this.put("CurrentColumnConfigOID","" + oid);
        initViewedItem();
    }
    
    public void makeEmptyColumnConfig() {
            DefaultItem newcolumnset = WinterMute.my_db.newItem(null, ItemDefault.TypeID, null);
            if (false) {
                ItemDefault newcolconfig = (ItemDefault) WinterMute.my_db.newItem(null,  ItemDefault.TypeID, null);
                newcolconfig.relateTo(new ItemRelation("colconfig","field"),WinterMute.my_db.getCommonField("SUMMARY"));
                newcolconfig.put("Title", "Summary");
                newcolconfig.put("width", "50");
                newcolconfig.relateTo(new ItemRelation("colconfig","columnset"),newcolumnset);
            }

            this.relateTo(new ItemRelation("table","columnset"), newcolumnset);
            //this.relateTo(WinterMute.parentChildRelation, newcolumnset);

    }
    
    GUITableColumnConfig colconfig_gui;
    private void showMyColumnConfigUI(ActionEvent e) {
        // make sure we have at least one column config
        if (getCurrentColumnConfiguration() == null) {
            // make a new column configuration...
            makeEmptyColumnConfig();
            // force us to use the new column config
            initViewedItem();
        }
        
        if (colconfig_gui == null) {
            // find parent
            java.awt.Container p = (java.awt.Container) this.my_table.getParent();
            while (p != null && !(p instanceof JFrame))  {
                p = (java.awt.Container) p.getParent();
            }
            
            colconfig_gui = new GUITableColumnConfig((JFrame)p,this);
            
        }
        
        colconfig_gui.setLocationRelativeTo((Component)e.getSource());
        colconfig_gui.show();
        Debug.debug("show config UI");
    }
    
    private void selectionChanged(ListSelectionEvent e) {
        DefaultItem selectedItem = null;
        Debug.debug("ItemGUITable: select row");
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;
        
        ListSelectionModel lsm =
            (ListSelectionModel)my_table.getSelectionModel();
       
        if (!lsm.isSelectionEmpty()) {
            int selectedRow = lsm.getMinSelectionIndex();
            
            // figure out which item is selected in the table
            TableModel tm = my_table.getModel();
            if (tm instanceof ItemTableModel) {
                ItemTableModel itm = (ItemTableModel)tm;
                selectedItem = itm.getItem(selectedRow);
                if (selectedItem != null) {
                    this.current_selected_oid = selectedItem.get_oid();

                    
                } else {
                    this.current_selected_oid = -1;
                }
                
                if (itm.getSourceModel() instanceof ItemTableModel) {
                    String current_relation = itm.getSourceRelation().getSpec();
                    DefaultItem saveTo = itm.getSourceItem();

                    if (saveTo != null) {
                        saveTo.putNoNotify("selected_row_oid " + current_relation,
                                       "" + current_selected_oid);
                    }    
                }

            }
            
        } else {
            this.current_selected_oid = -1;
        }
        
        
        triggerListeners(selectedItem);
    }
    
    protected void initColumns() {
        DefaultItem columnset = this.getCurrentColumnConfiguration();
        if(columnset == null) {
            this.curColumnModel = null;
        } else {
            ItemTableModel tm = columnset.makeTableModelForRelation(new ItemRelation("columnset","colconfig"));
            if (tm != this.curColumnModel) {
                this.curColumnModel = tm;
               
            }
        }
    }
    
    protected void onActivate() {
        super.onActivate();
        my_table.setName(this.get("name"));
        my_scroller.setName(this.get("name"));

        initViewedItem();
        
        String dim_str = get("Dimension");
        // my_scroller.setPreferredSize(new Dimension(150,150));   
        if (dim_str != null) {
            String[] parts = dim_str.split(",");
            int w = Integer.parseInt(parts[0]);
            int h = Integer.parseInt(parts[1]);
            my_scroller.setPreferredSize(new Dimension(w,h));    
            
            // Debug.debug("preferred size: " + w + "," + h);
        }
        
        
    }
    protected void onViewedItemChange() {
        Debug.debug("ItemGUITable(" + this.get("name") + ").onViewedItemChange");
        initViewedItem();
    }
    
    private void initViewedItem() {
        TableModel new_model = null;
        DefaultItem subitem = null;
        if (my_viewed_items.size() > 1) {
            throw new RuntimeException("Need compound Table Model to view more than one item!!!");
        }
        
        Iterator itr = my_viewed_items.iterator();
        while (itr.hasNext()) {
            subitem = (DefaultItem) itr.next();
            Debug.debug("Setting Table Model from item: " + subitem);
            String relation_to_view = this.get("relation_to_view");
            if (relation_to_view == null) { 
                // contains:container default model
                new_model  = subitem.makeTableModel();
                break;
            } else if (relation_to_view.equals("SELF")) {
                // properties model....
                new_model = subitem.makeTableModelForItemData();
                break;
            } else {
                // specified relation...
                new_model = subitem.makeTableModelForRelation(new ItemRelation(relation_to_view));
                break;
            }
        }
        if (new_model != null) {
            //((MyJTable)my_table).stopWatching();
            
            this.initColumns();
            TableSorter sorter = new TableSorter(new_model); //ADDED THIS
            // if I have a columnset
            if (this.curColumnModel != null) {
                sorter.setColumnModel(this.getCurrentColumnConfiguration(),this.curColumnModel);
            }
            if (new_model instanceof ItemTableModel) {
                my_table.setAutoCreateColumnsFromModel(false);
                my_table.setColumnModel(sorter.getTableColumnModel());
                my_table.setModel(sorter);
            } else {
                my_table.setAutoCreateColumnsFromModel(true);
                my_table.setModel(sorter);
            }
            sorter.addMouseListenerToHeaderInTable(my_table); //ADDED THIS
            Debug.debug("initViewedItem", new_model.getClass().getName());
            
            if(new_model instanceof SimpleItemRelationTableModel ) {
                Debug.debug("initViewedItem", "figuring selected_row_oid");
                SimpleItemRelationTableModel itm_new_model2 = (SimpleItemRelationTableModel) new_model;
                
                Debug.debug("itm_new_model2", itm_new_model2.parentItem, itm_new_model2.parentRelation);
                
                if(true) {
                    String pointStr = itm_new_model2.parentItem.get("scroll_position." + itm_new_model2.parentRelation.getSpec());
                    if(pointStr != null) {
                        String[] parts = pointStr.split(",");
                        if(parts.length == 2) {
                            java.awt.Point p = new java.awt.Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            JViewport vp = this.my_scroller.getViewport();
                            vp.setViewPosition(p);
                        }
                    }
                }

                int selectedRowOID = itm_new_model2.parentItem.getInt("selected_row_oid " + itm_new_model2.parentRelation.getSpec(), -1);
                if(selectedRowOID != -1) {
                    DefaultItem viewed_item = WinterMute.my_db.getItem(selectedRowOID);
                    this.current_selected_oid = selectedRowOID;
                    int selection_index = itm_new_model2.getItemRowIndex(viewed_item);
                    ListSelectionModel tblSM = my_table.getSelectionModel();
                    Debug.debug("row=" + selection_index, "oid=" + selectedRowOID);
                    tblSM.addSelectionInterval(selection_index, selection_index);
                    triggerListeners(viewed_item);
                }
            }
            
            //((MyJTable)my_table).loadColumns();

            if (false) {
                // fix selection if necessary
                List listeners = this.getRelatedItems(WinterMute.setViewedItemSenderRelation);
                if (listeners.size() > 0 && new_model instanceof ItemTableModel) {
                    ItemGUIBase first_listener = (ItemGUIBase)listeners.get(0);

                    DefaultItem viewed_item = first_listener.getViewedItem();
                    ItemTableModel itm_new_model = (ItemTableModel) new_model;
                    if (viewed_item != null)  {
                        int selection_index = itm_new_model.getItemRowIndex(viewed_item);

                        ListSelectionModel tblSM = my_table.getSelectionModel();
                        if (selection_index >= 0) {
                            //tblSM.clearSelection();
                            tblSM.addSelectionInterval(selection_index,selection_index);
                        } else {
                            // if the table is supposed to select
                            String should_auto_select = this.get("should_auto_select");
                            if (should_auto_select != null && Integer.parseInt(should_auto_select) == 1) {
                                tblSM.addSelectionInterval(0,0);
                            }
                        }
                    }
                }
            }

        }
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUITable();
            }
        });
    }
    
    public java.awt.Component getComponent() {
        return my_scroller;
    }
    
    int old_selected_oid = -1;
    public void beforeTableChanged(TableModelEvent e) {
        old_selected_oid = current_selected_oid;
    }
    
    public void afterTableChanged(TableModelEvent e) {
        if (true) return;
        
        // Debug.debug("ItemGUITable: handle tableChanged() event. old oid:" + current_selected_oid);
        if (old_selected_oid != -1) {
            
        
            
            ListSelectionModel lsm = my_table.getSelectionModel();
            if (true || lsm.isSelectionEmpty()) {
                TableModel tm = my_table.getModel();
                if (tm instanceof ItemTableModel) {
                    ItemTableModel itm = (ItemTableModel) tm;
                    
                    DefaultItem item = WinterMute.my_db.getItem(old_selected_oid);
                    int new_selected_row = itm.getItemRowIndex(item);
                    if (new_selected_row != -1) {
                        lsm.setSelectionInterval(new_selected_row,new_selected_row);
                        Debug.debug("resetting selection to: " + new_selected_row);
                    }
                }
            }
        }
        
        
    }
    
        JPopupMenu table_context_popup;

    class PopupListenerRow extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
           // maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                DefaultItem item;
                
                int selectedRow = my_table.getSelectedRow();
                TableModel tm = my_table.getModel();
                
                if (tm instanceof ItemTableModel) {
                    ItemTableModel itm = (ItemTableModel)tm;
                    item = (DefaultItem)itm.getItem(selectedRow);
 
                    if (item != null) {
                        List actions = item.getContainedActions();
                        if (actions != null) {
                            table_context_popup = new JPopupMenu();
                            Iterator itr = actions.iterator();
                            while (itr.hasNext()) {
                                table_context_popup.add((Action)itr.next());
                            }
                            
                            table_context_popup.pack();
                            table_context_popup.setLocation(e.getX(),e.getY());
                            table_context_popup.show(e.getComponent(),
                            e.getX(), e.getY());
                        }
                    }
                }
            }
        }
    }
}
