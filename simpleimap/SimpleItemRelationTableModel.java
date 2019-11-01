package simpleimap;

import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;


import java.util.*;

// This class now stores an entire COPY of the relation itself so that it can present a
// "single-threaded" view of the contents to the AWT thread. - jeske

public class SimpleItemRelationTableModel implements TableModel, ItemTableModel,IPropChangeNotification,IRelationChangeNotification {
        DefaultItem parentItem;
        ItemRelation parentRelation;
        
        
        private List relation_item_oids = null;  // COPY OF relation item oids
        
        ItemField[] columns = {};
        String[] column_names = {};
        DefaultItem[] column_config = {};
        ItemFormatter[] column_formatter = {};
        ItemRelation[][] followRelations = {};
        
        boolean has_column_names = false;
        
        // colconfig
        DefaultItem columnset_item = null;
        ItemTableModel columnModel = null;
        SimpleItemTableColumnModel myTableColumnModel = null;
        
        List row_oids = null;
        
        Vector tableModelListeners = null;
        
        private List pendingChanges;
        
        private class ProcessPendingTrigger implements ActionListener {
            SimpleItemRelationTableModel tm;
            ProcessPendingTrigger(SimpleItemRelationTableModel tm) {
                this.tm = tm;
            }
            public void actionPerformed(ActionEvent evt) {
                tm.processChanges();
            }
        }
        
        javax.swing.Timer my_trigger = null;
        
        public String toString() {
            return "SimpleItemRelationTableModel(" + parentItem.get_oid() + " -- " + parentRelation.getSpec();
        }
        
        protected SimpleItemRelationTableModel(DefaultItem parentItem, ItemRelation parentRelation) {
            this.parentRelation = parentRelation;
            this.parentItem = parentItem;
            this.pendingChanges = new LinkedList();
            
            synchronized (parentItem.item_storage) {
                this.relation_item_oids = (LinkedList)parentItem.getRelatedItemOIDs(parentRelation).clone();
                parentItem.item_storage.notifyOfRelationChange(parentRelation,this);
            }
            
            this.myTableColumnModel = new SimpleItemTableColumnModel();
            this.tableModelListeners = new Vector();
            
            
            
            // setup our oneshot timer
            this.my_trigger = new javax.swing.Timer(0,new ProcessPendingTrigger(this));
            this.my_trigger.setCoalesce(true);
            this.my_trigger.setRepeats(false);
            

            // init
            checkColumns();
        }

        
        ///////////////////////////////////////////////////////////////////////////////////
        //
        // Tricky thread synchronization stuff....
        //
        
        public int getRowCount() { return relation_item_oids.size(); }
        
        public DefaultItem getItem(int row) {
            int oid = ((Integer) relation_item_oids.get(row)).intValue();
            try {
                DefaultItem subitem = WinterMute.my_db.getItem(oid);
                subitem.item_storage.noticeTableModel(this);
                return subitem;
            } catch (eNoSuchItem e) {
                throw new RuntimeException("MyListTableModel: can't get Item for row: " + row + " oid:" + oid);
                // return null;
            }
            
        }
        
        public int getItemRowIndex(DefaultItem item) {
            if (item == null) { return -1; }
            int find_oid = item.get_oid();
            for(int x=0;x<relation_item_oids.size();x++) {
                if (((Integer)relation_item_oids.get(x)).intValue() == find_oid) {
                    return x;
                }
            }
            return -1;
        }

        

        //
        //
        ////////////////////////////////////////////////////////////////////////////////////////
        //
        // these notifications can be fired by anyone and need to be serialized into
        // the AWT thread.

        private final String ADD    = "add";
        private final String REMOVE = "remove";
        private final String UPDATE = "update";
        private final String HEADER = "header";
        
        public void itemAddedAfter(ItemRelation relation, DefaultItem item, DefaultItem afterItem) {
            synchronized (pendingChanges) {
                trigger();
                Object[] change = { ADD , item , afterItem };
                pendingChanges.add(change);
            }
        }
        public void itemRemoved(ItemRelation relation, DefaultItem item) {
            synchronized (pendingChanges) {
                trigger();
                Object[] change = { REMOVE, item };
                pendingChanges.add(change);
            }
        }
        
        public void itemChanged(DefaultItem item) {              
            synchronized (pendingChanges) {
                trigger();
                Object[] change = { UPDATE, item };
                pendingChanges.add(change);
            }
        }
        
        //////////////////////////////
        // tableStructureChanged()
        //
        // the header row has changed...
        
        public void tableStructureChanged() {
            synchronized (pendingChanges) {
                trigger();
                Object[] change = { HEADER , null };
                pendingChanges.add(change);
            }
        }
        
        //
        //
        /////////////////////////////////////////////////////////////////////////////
        //
        //  The trigger methods should only be called by the above methods
        //
        
        
        private void trigger() {
            this.my_trigger.start();
        }
        
        //
        //
        ////////////////////////////////////////////////////////////////////////////////////
        //
        // AWT thread event sending code...
        //
        
        protected void processChanges() {
            synchronized (pendingChanges) {
                for (Iterator itr = pendingChanges.iterator();itr.hasNext();) {
                    Object[] change  = (Object[]) itr.next();
                    String type      = (String) change[0];
                    DefaultItem item = (DefaultItem) change[1];

                    if (type.equals(UPDATE)) {
                        this.apply_TableRowUpdated(item);
                    } else if (type.equals(ADD)) {
                        DefaultItem afterItem = (DefaultItem) change[2];
                        this.apply_TableRowInserted(item,afterItem);
                    } else if (type.equals(REMOVE)) {
                        this.apply_TableRowDeleted(item);
                    } else if (type.equals(HEADER)) {
                        this.apply_TableStructureChanged();
                    }
                }
                
                // empty the pending changes
                pendingChanges.clear();
            }
        }
        
        ///////////////////////
        //
        // apply and send each type of event
        
        private void apply_TableStructureChanged() {
            // get a new copy of all data
            // this.relation_item_oids = (LinkedList)parentItem.getRelatedItemOIDs(parentRelation).clone();
            
            // send notification
            for(Iterator itr = this.tableModelListeners.iterator();itr.hasNext();) {
                TableModelListener l = (TableModelListener) itr.next();
                TableModelEvent evt = new TableModelEvent(this, TableModelEvent.HEADER_ROW); // header changed
                l.tableChanged(evt);
            }
        }
        
        private void apply_TableRowInserted(DefaultItem item, DefaultItem afterItem) {
            // insert the new oid (at the end for now)
            int rowIndex = this.relation_item_oids.size();
            this.relation_item_oids.add(new Integer(item.get_oid()));
            
            // send the notification
            for(Iterator itr = this.tableModelListeners.iterator();itr.hasNext();) {
                TableModelListener l = (TableModelListener) itr.next();
                TableModelEvent evt = new TableModelEvent(this,rowIndex,rowIndex,
                                                    TableModelEvent.ALL_COLUMNS,
                                                    TableModelEvent.INSERT);
                l.tableChanged(evt);
            }
        }
        private void apply_TableRowDeleted(DefaultItem item) {
            int rowIndex = this.getItemRowIndex(item);
            
            // find and delete the row in question
            this.relation_item_oids.remove(new Integer(item.get_oid()));
            
            for(Iterator itr = this.tableModelListeners.iterator();itr.hasNext();) {
                TableModelListener l = (TableModelListener) itr.next();
                TableModelEvent evt = new TableModelEvent(this,rowIndex,rowIndex,
                                                    TableModelEvent.ALL_COLUMNS,
                                                    TableModelEvent.DELETE);
                l.tableChanged(evt);
            }
        }
        
        private void apply_TableRowUpdated(DefaultItem item) {
            int rowIndex = this.getItemRowIndex(item);
            if (rowIndex < 0) {
                Debug.debug("apply_TableRowUpdated(" + item.toString() + ") -> " + rowIndex);
                return;
            }
            
            for(Iterator itr = this.tableModelListeners.iterator();itr.hasNext();) {
                TableModelListener l = (TableModelListener) itr.next();
                TableModelEvent evt = new TableModelEvent(this,rowIndex);  // row changed
                l.tableChanged(evt);
            }
            
        }
      
        //
        //
        ///////////////////////////////////////////////////////////////////////////////////
        //
        // Normaly (mostly) free-threading friendly code
        //
        
        public Class getColumnClass(int col) {
            Object o = getValueAt(0, col);
            if (o == null) {
                return new Object().getClass();
            } else {   
                return o.getClass();
            }
        }
        public TableModel getSourceModel() {
            return this;
        }
        public DefaultItem getSourceItem() {
            return parentItem;
        }
        
        public ItemRelation getSourceRelation() {
            return parentRelation;
        }
        
        public TableColumnModel getTableColumnModel() {
            return myTableColumnModel;
        }
        
        
        public void setColumnModel(DefaultItem colconfig,ItemTableModel cm) {
            columnModel = cm;
            columnset_item = colconfig;
            
            has_column_names = true;
            
            // become a listener
            cm.addTableModelListener( new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    refreshColumnModel();
                }
            });

            // we would also like to know about formatter changes..
            
            try {
                refreshColumnModel();
            } catch (java.lang.IndexOutOfBoundsException e) {
                Debug.debug(e);
            }
        }
            
        
        // completely rebuild the column information from scracth!
        private void refreshColumnModel() {
            ItemTableModel cm = columnModel;
            
            myTableColumnModel.stopWatching();
            
            // clear myTableColumnModel
            myTableColumnModel.clearColumns();

            
            int count = cm.getRowCount();
            ItemRelation fieldrelation = new ItemRelation("colconfig","field");
            ItemRelation formatterrelation = new ItemRelation("colconfig","formatter");

            String[] field_names = null;
            ItemRelation[][] afollowRelations = null;

            ItemField[] fields = null;
            ItemFormatter[] field_formatter = null;
            DefaultItem[] field_config = null;
            
            if (count == 0) {
                // make sure we have at least one column!          
                field_names = new String[1];
                field_names[0] = " ";
                //fields = new ItemField[1];
                //field_formatter = new ItemFormatter[1];
                TableColumn c = new TableColumn(0);
                c.setHeaderValue(" ");
                myTableColumnModel.addColumn(c);
            } else {
                field_names = new String[count];
                fields = new ItemField[count];
                field_formatter = new ItemFormatter[count];
                field_config    = new DefaultItem[count];
                afollowRelations = new ItemRelation[count][];
                

                for (int n=0;n < count;n++) {
                    DefaultItem colconfig = cm.getItem(n);
                    field_config[n] = colconfig;
                    
                    field_names[n] = colconfig.get("Title");
                    fields[n] = (ItemField)colconfig.getRelatedItems(fieldrelation).get(0);
                    
                    if(true) {
                        String arelation = colconfig.get("followRelation");
                        if(arelation != null && arelation.length() > 0) {
                            String[] parts = arelation.split("\\.");
                            //Debug.debug(">>>>>>>>>>>>>>>>>>>>>>      parts[] =" + parts.length);

                            if(parts.length == 0) {
                                parts = new String[1];
                                parts[0] = arelation;
                            }
                            
                            ItemRelation[] myrelations = new ItemRelation[parts.length];
                            for(int i=0; i<parts.length; i++) {
                                myrelations[i] = new ItemRelation(parts[i]);
                            }
                            afollowRelations[n] = myrelations;
                        } else {
                            afollowRelations[n] = null;
                        }
                    }
                       
                    List formatter_chain = colconfig.getRelatedItems(formatterrelation);
                    
                    
                    if (formatter_chain.size() > 0) {
                        field_formatter[n] = (ItemFormatter)formatter_chain.get(0);
                        
                    } else {
                        field_formatter[n] = null;
                    }

                    TableColumn c = new TableColumn(n);
                    
                    int preferredWidth = colconfig.getInt("width", 0);
                    if(preferredWidth > 0) c.setPreferredWidth(preferredWidth);

                    c.setHeaderRenderer(new CustomHeaderRenderer(colconfig));
                    String sizeMethod = colconfig.get("SizeMethod");
                    if (sizeMethod == null) { sizeMethod = "auto"; }
                    if (sizeMethod.equals("fixed")) {
                        c.setMaxWidth(preferredWidth);
                        c.setMinWidth(preferredWidth);
                        c.setResizable(false);
                    } else if (sizeMethod.equals("min")) {
                        // handle minimum size...
                    }
                    
                    // custom icon hack
                    if (field_names[n] == "Status") {
                        c.setHeaderValue(new ImageIcon(ClassLoader.getSystemResource("images/colhead_status.png")));
                        c.setMaxWidth(preferredWidth);
                        c.setMinWidth(preferredWidth);
                        c.setResizable(false);
                    } else {
                        c.setHeaderValue(field_names[n]);
                        
                    }
                    
                    myTableColumnModel.addColumn(c);
                }
            }
            
            
            
            this.columns = fields;
            this.column_names = field_names;
            this.column_formatter = field_formatter;
            this.column_config    = field_config;
            this.followRelations = afollowRelations;
            
            // load the saved column settings...
            myTableColumnModel.loadColumns(columnset_item);
            myTableColumnModel.startWatching();
            
            tableStructureChanged();
        }
        
        
        private void checkColumns() {
            if (!has_column_names) {
                // we really need to do something different here!
                if (getRowCount() > 0) {
                    DefaultItem test_item = (DefaultItem)getItem(0);
                    // col_names HACK
                    // the view should really be telling us what columns it wants from the
                    // column config... probably when it asks for the table model to be made...
                    
                    myTableColumnModel.clearColumns();
                    
                    columns = test_item.getCachedFields();
                    column_names = new String[columns.length];
                    //fields = new ItemField[columns.length];
                    this.followRelations = new ItemRelation[columns.length][];
                    
                    for (int n=0;n<columns.length;n++) {
                        column_names[n] = columns[n].toString();
                                                  
                        TableColumn c = new TableColumn(n);
                        c.setHeaderValue(column_names[n]);
                        myTableColumnModel.addColumn(c);
                    }
                    
                    has_column_names = true;
                    tableStructureChanged();
                }
            }
        }
        
        public String getColumnName(int col) {
            checkColumns();
            return column_names[col];
        }
        public int getColumnCount() { 
            checkColumns();
            return columns.length; 
        }

        
        private void getFollowedRelationValue(StringBuffer buf, ItemRelation[] followRelation, int n, DefaultItem item, ItemField a_col) {
            if(n == followRelation.length-1) {
                List myrelations = item.getRelatedItems(followRelation[n]);
                
                for(Iterator iter = myrelations.iterator(); iter.hasNext();) {
                    DefaultItem item2 = (DefaultItem) iter.next();
                    if(buf.length() > 0) buf.append(", ");
                    buf.append(item2.get(a_col));
                }
            } else {
                List myrelations = item.getRelatedItems(followRelation[n]);
                
                for(Iterator iter = myrelations.iterator(); iter.hasNext();) {
                    DefaultItem item2 = (DefaultItem) iter.next();
                    this.getFollowedRelationValue(buf, followRelation, n+1, item2, a_col);
                }
            }
            return;
        }

        
        public Object getValueAt(int row,int col) {
            checkColumns();
            if (columns == null) { return ""; } // if there is no real fieldset....
            ItemField a_col = columns[col];
            
            
            DefaultItem item = this.getItem(row);
            
            ItemRelation[] followRelation = null;
            if(this.followRelations != null) followRelation = this.followRelations[col];
            
            Object value;
            
            if(true && followRelation != null && followRelation.length>0) {
                StringBuffer buf = new StringBuffer();
                this.getFollowedRelationValue(buf, followRelation, 0, item, a_col);
                value = buf.toString();
            } else {
                value = item.get(a_col);
            }
            
            if (this.column_formatter.length > col) {
                DefaultItem colconfig = (DefaultItem)this.column_config[col];
                ItemFormatter colformatter = (ItemFormatter)this.column_formatter[col];
                
                if (colformatter != null) {
                    value = colformatter.formatData(item, a_col, colconfig, value);
                    
                }
            }
            return value;
        }

        
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void addTableModelListener(TableModelListener l) {
            this.tableModelListeners.addElement(l);
        }
        public void removeTableModelListener(TableModelListener l) {
            this.tableModelListeners.removeElement(l);
        }
        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // FIX
            throw new RuntimeException("not implemented");
        }
        
        
        ////////////////////////////////////
        //
        // customHeaderRenderer
        //
        // http://javaalmanac.com/egs/javax.swing.table/HeadHeight.html
        //
        
        private class CustomHeaderRenderer extends DefaultTableCellRenderer {
            DefaultItem colconfig;
            CustomHeaderRenderer(DefaultItem colconfig) {
                this.colconfig = colconfig;
            }
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                     boolean isSelected, boolean hasFocus, int row, int column) {
            // Inherit the colors and font from the header component
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }
    
            if (value instanceof Icon) {
                // Value is an Icon
                setIcon((Icon)value);
                setText("");
            } else {
                // Value is text
                setText((value == null) ? "" : value.toString());
                setIcon(null);
            }
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            
            String alignment = colconfig.get("Alignment");
            if (alignment == null) { alignment = "Left"; }
            if (alignment.equals("Right")) {
                setHorizontalAlignment(JLabel.RIGHT);
            } else if (alignment.equals("Center")) {
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setHorizontalAlignment(JLabel.LEFT);
            }
            
            
            return this;
        }
    };

        
        
        
        
    };