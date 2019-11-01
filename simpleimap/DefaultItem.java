/*
 * Wintermute - Personal Data Organizer
 * 
 * Copyright (C) 2002, by David Jeske
 *
 * Written by David Jeske <jeske@neotonic.com>. 
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbid
 den.
 */

/*
 * DefaultItem.java
 *
 * This is the abstract class from which all System Items derive.
 * It has lots of helpful functionality for the use of others...
 * 
 * It should not be confused with "ItemDefault" which is the default type of instantiated item.
 *
 * Created on November 1, 2002, 7:47 AM
 */

package simpleimap;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.text.*;
import javax.swing.AbstractAction;



/**
 *
 * @author  David Jeske
 */
abstract public class DefaultItem implements PropMapType {
    // public abstract void register(SimpleDB db);
    protected SimpleItemStorage item_storage;
    Icon my_icon;
    String icon_path;
    
    /** Creates a new instance of DefaultItem */
    public DefaultItem() {
        java.net.URL name = ClassLoader.getSystemResource("images/folder.png");
        if (name == null) {
            throw new RuntimeException("can't load default icon images/folder.png, check paths");
        }
            
        my_icon = new ImageIcon(name);
        icon_path = "images/folder.png";
    }

    private void loadIcon() {
        String custom_icon_path = this.get("icon_path");
        if (custom_icon_path != null && !custom_icon_path.equals(icon_path)) {
            java.net.URL o = ClassLoader.getSystemResource(custom_icon_path);
            if (o != null) {
                Icon new_icon = new ImageIcon(o);
                if (new_icon != null) {
                    my_icon = new_icon;
                }
            }
        }
    }
    
    ////////////////////////// start: ItemTreeNodeInterface ///////////////////////
    
    public Icon getIcon() {
        return my_icon;
    }
    public String getTreeNodeName() {
        return get("name");
    }
    
    ////////////////////////// end: ItemTreeNodeInterface ////////////////////////
    
    
    public String toString() {
        if (item_storage != null) {
            String name = (String) get("name");
            //return "Item (" + item_storage.typename + "," + item_storage.name + ")";
            return name + "(" + item_storage.typename + ")";

        } else {
            return "UninitializedItem";
        }
    }
    
    public final void setStorage(SimpleItemStorage is) {
        // SimpleDB needs a reference to us before we can use our item_storage...
        item_storage = is;
        is.item_object = this;
    }
    // give SimpleDB access to our item storage.
    final public SimpleItemStorage getItemStorage() {
        return item_storage;
    }

    /////////////////////////////////////////////////
    //
    // for offline data handling...
    
    public boolean isDataOffline() {
        return false;
    }
    public void requestDataFetch() {
    }
    
    public void addChangeListener(IPropChangeNotification listener) {
        this.item_storage.noticeTableModel(listener);
    }
    
    public void removeChangeListener(IPropChangeNotification listener) {
    }
    //
    //////////////////////////////////////////////////
    //
    // Data Fetch
    
    public byte[] getData() {
        return item_storage.getData();
    }
    public void setData(byte[] data) {
        item_storage.setData(data);
        
        // HACK - FIX - jeske
        // WinterMute.indexmgr.indexItem(this);
        
    }
    public int get_oid() {
        return item_storage.get_oid();
    }
    
    //
    //////////////////////////////////////////////////

    public void debug_displayRelations(DefaultItem item) {
        List relations = item.getAvailableRelations();
        Iterator iter = relations.iterator();
        while(iter.hasNext()) {
            ItemRelation relation = (ItemRelation) iter.next();
            Debug.debug("relation " + relation.toString() + " " + item.getRelatedItemOIDs(relation).size());
        }
    }
    
    public void relateToOnce(ItemRelation relation,DefaultItem item) {
        if(this.isRelated(relation, item)) return;
        this.relateTo(relation, item);
    }
    
    public void relateTo(ItemRelation relation,DefaultItem item) {
        item_storage.relateTo(relation,item.getItemStorage());
    }
    public void unrelateFrom(ItemRelation relation, DefaultItem item) {
        item_storage.unrelateFrom(relation,item.getItemStorage());
    }
    
    public void unrelateFromAll(ItemRelation relation) {
        List items = this.getRelatedItems(relation);
        for(Iterator iter = items.iterator(); iter.hasNext();) {
            DefaultItem aitem = (DefaultItem) iter.next();  
            this.unrelateFrom(relation,  aitem);
        }
    }

    public java.util.LinkedList keyList() {
        java.util.LinkedList ret = new java.util.LinkedList();
        Enumeration e = this.getItemStorage().keys();
        while(e.hasMoreElements()) {
            String key = (String) e.nextElement();
            ItemField f = WinterMute.my_db.getItemFieldFromOID(key);
            ret.add(f.getFieldName());
        }
        return ret;
    }
    
    // string field name convinence methods...
    public String get(String key) {
        return get(item_storage.db.getCommonField(key));
    }
    
    public long getLong(String key, long defaultValue) {
        String str = get(item_storage.db.getCommonField(key));
        if(str == null) return defaultValue;
        long n = Long.parseLong(str);
        return n;
    }
    
    public int getInt(String key, int defaultValue) {
        String str = get(item_storage.db.getCommonField(key));
        if(str == null) return defaultValue;
        int n = Integer.parseInt(str);
        return n;
    }
    public float getFloat(String key, float defaultValue) {
        String str = get(item_storage.db.getCommonField(key));
        if(str == null) return defaultValue;
        float n = Float.parseFloat(str);
        return n;
    }
    
    public void put(String key, String value) {
        put(item_storage.db.getCommonField(key),value);
    }

    public void put(String key, int value) {
        put(item_storage.db.getCommonField(key), Integer.toString(value));
    }
    
    public void put(String key, long value) {
        put(item_storage.db.getCommonField(key), Long.toString(value));
    }
    
    public void put(String key, float value) {
        put(item_storage.db.getCommonField(key), Float.toString(value));
    }

    public void put(String key, double value) {
        put(item_storage.db.getCommonField(key), Double.toString(value));
    }    
    
    public void put(String key, Date date) {
        String datestr="no date";
        String datenum = "";

        if(date != null) {
            // Format the current time.
            SimpleDateFormat date_formatter = new SimpleDateFormat ("yyyy.MM.dd HH:mm:ss");
            datestr = date_formatter.format(date);
            
            datenum = Long.toString(date.getTime());
        } 
        put(item_storage.db.getCommonField(key), datestr);
        put(item_storage.db.getCommonField("#Date." + key), datenum);

    }
    
    public void putNoNotify(String key, String value) {
        putNoNotify(item_storage.db.getCommonField(key),value);
    }
    
    public boolean contains(String key) {
        return contains(item_storage.db.getCommonField(key));
    }
    
    // real ItemField methods
    public String get(ItemField key) {
        return (String) item_storage.get("" + key.get_oid());
    }
    
    public void put(ItemField key, String value) {
        String old_value = (String) item_storage.put("" + key.get_oid(),value);
        this.onPropChange(key,old_value,value);
    }
    
    public void putNoNotify(ItemField key, String value) {
        item_storage.putNoNotify("" + key.get_oid(),value);
    }
        
    public boolean contains(ItemField key) {
        return item_storage.contains("" + key.get_oid());
    }
    
    // other hashtable like methods...
    
    
    public Set cachedKeys() {
        return item_storage.keySet();
    }
    public List getAvailableRelations() {
        return item_storage.getAvailableRelations();
    }
    
    public List getRelatedItems(ItemRelation relation) {
        return item_storage.getRelatedItems(relation);
    }
    
    public DefaultItem getItem(ItemRelation relation, String name) {
        List items = this.getRelatedItems(relation);
        Iterator iter = items.iterator();
        while(iter.hasNext()) {
            DefaultItem item = (DefaultItem) iter.next();
            
            if(item.get("name").equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    public boolean isRelated(ItemRelation relation, DefaultItem item) {
        List items = this.getRelatedItems(relation);
        for(Iterator iter = items.iterator(); iter.hasNext();) {
            DefaultItem aitem = (DefaultItem) iter.next();   
            if(item == aitem) {
                return true;
            }
        }
        return false;
    }
    
    public LinkedList getRelatedItemOIDs(ItemRelation relation) {
        return item_storage.getRelatedItemOIDs(relation);
    }
    
    public static void sortStrings(Collator collator, 
                               String[] words) {
        String tmp;
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) { 
                if (collator.compare(words[i], words[j]) > 0) {
                    tmp = words[i];
                    words[i] = words[j];
                    words[j] = tmp;
                }
            }
        }
    }

    public ItemField[] getCachedFields() {
        Enumeration e = item_storage.keys();
        Object[] objs = item_storage.keySet().toArray();
        String[] names = new String[objs.length];
        for(int i=0;i<objs.length;i++) { 
            names[i] = (String)objs[i]; 
        }
        sortStrings(Collator.getInstance(),names);
        
        ItemField[] fields = new ItemField[objs.length];
        for(int i=0;i<objs.length;i++) { 
            fields[i] = (ItemField) item_storage.db.getItem(Integer.parseInt(names[i]));
        }
        
        return fields;
        
    }
    
    public String[] DEPRECEITATED_getCachedPropertyNames() {
        Enumeration e = item_storage.keys();
        Object[] objs = item_storage.keySet().toArray();
        String[] names = new String[objs.length];
        for(int i=0;i<objs.length;i++) { names[i] = (String)objs[i]; }
        sortStrings(Collator.getInstance(),names);
        return names;
    }
    
    // Default List Relation "contains:container"
    
    public int itemCount() {
        List oid_list = getRelatedItemOIDs(WinterMute.containerContainsRelation);
        return oid_list.size();
    }
    public void addItem(DefaultItem item) {
        relateTo(WinterMute.containerContainsRelation,item);
    }
    
    
    
    public void removeItem(DefaultItem item) {
        unrelateFrom(WinterMute.containerContainsRelation,item);
    }
    public List containedItemOIDs() {
        return getRelatedItemOIDs(WinterMute.containerContainsRelation);
    }

    public DefaultItem getItemAt(int index) {
        List oid_list = getRelatedItemOIDs(WinterMute.containerContainsRelation);
        int oid = ((Integer)oid_list.get(index)).intValue();
        try {
            return item_storage.db.getItem(oid);
        } catch (eNoSuchItem e) {
            return null;
        }
        
    }
    
    // Default Tree Relation "parent:child"
    
    public void addChild(DefaultItem item) {
        relateTo(WinterMute.parentChildRelation,item);
    }
    
    //////////////////////////////// ACTIONS ///////////////////////////////////
    
     class DeleteItemAction extends AbstractAction {
        DefaultItem item;
        DeleteItemAction(DefaultItem item) {
            super("Delete Item");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // delete item and its children - swh
            Debug.debug("DeleteItemAction.actionPerformed(): start");
            debug_displayRelations(item);
            
            List parents = item.getRelatedItems(WinterMute.childParentRelation);

            Iterator iter = parents.iterator();
            while(iter.hasNext()) {
                DefaultItem parent = (DefaultItem) iter.next();
                item.unrelateFrom(WinterMute.childParentRelation, parent);
                //parent.unrelateFrom(new ItemRelation("parent","child"), item);
                
            }      
            Debug.debug("DeleteItemAction.actionPerformed(): unrelate completed");
            debug_displayRelations(item);
            Debug.debug("DeleteItemAction.actionPerformed(): end");

        }
    }
     
    class AddItemAction extends AbstractAction {
        DefaultItem item;
        AddItemAction(DefaultItem item) {
            super("Add Item");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // add item - swh
            DefaultItem newitem = WinterMute.my_db.newItem(null, ItemDefault.TypeID, null);
            item.addChild(newitem);
        }
    }
    class RDF_ImportAction extends AbstractAction {
        DefaultItem item;
        RDF_ImportAction(DefaultItem item) {
            super("RDF_ImportAction");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
           new RDF_ImportWizard(null, false, this.item).show();
        }
    }
    
    public void buildInspectionFrame() {
        ItemGUIFrame frm = (ItemGUIFrame) WinterMute.my_db.newItem(null, ItemGUIFrame.TypeID, "Item Inspector");
        frm.setTransient(true);
        
        DefaultItem split = (DefaultItem) WinterMute.my_db.newItem(null, ItemGUISplitPane.TypeID, "split");
        frm.addChild(split);
        split.put("vsplit","yes");
        //itemvtop.put("resizeWeight","0.30");
        split.put("dividerLocation", 100);
        
        ItemGUITable itemview1 = (ItemGUITable) WinterMute.my_db.newItem(null, ItemGUITable.TypeID, "item_view");
        itemview1.put("relation_to_view","SELF");
        split.addChild(itemview1);
        
        //ItemGUITable itemview2 = (ItemGUITable) WinterMute.my_db.newItem(null, ItemGUITable.TypeID, "item_view");
        //itemview2.put("relation_to_view",WinterMute.containerContainsRelation.getSpec());
        //split.addChild(itemview2);
        
        ItemGUITextView itemview3 = (ItemGUITextView) WinterMute.my_db.newItem(null, ItemGUITextView.TypeID, "textview");
        itemview1.put("relation_to_view","SELF");
        split.addChild(itemview3);
        
        itemview1.setViewedItem(this);
        //itemview2.setViewedItem(this.item);
        itemview3.setViewedItem(this);
    }
    
    class InspectItemAction extends AbstractAction {
        DefaultItem item;
        InspectItemAction(DefaultItem item) {
            super("Inspect Item");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            this.item.buildInspectionFrame();
        }
    }    

    
    public List getActions() {
        List actions = new LinkedList();

        actions.add(new DeleteItemAction(this));
        actions.add(new AddItemAction(this));
        actions.add(new InspectItemAction(this));
        
        return actions;
    }
    
    public List getContainedActions() {
        List actions = new LinkedList();

        actions.add(new InspectItemAction(this));
        
        return actions;
    }
    
    
    
    ///////////////////////////// end: ACTIONS ///////////////////////////////
    
   
    
    ////////////////////////////// MODEL FACTORIES /////////////////////////

        
    private MyItemTableModel my_data_table_model;
    
    public TableModel makeTableModelForItemData() {
        if (my_data_table_model == null) {
            my_data_table_model = new MyItemTableModel(this);
        }
        return my_data_table_model;
    }

        // PROPERTY SUPPORT
    
    private class MyItemTableModel extends AbstractTableModel implements IPropChangeNotification {
        private DefaultItem myitem;
        private String[] col_names = {"Name","Value" };;
        private MyItemTableModel(DefaultItem item) {
            myitem = item;
            item.item_storage.noticeTableModel(this);
        }
        public String getColumnName(int col) {
            return col_names[col];
        }
        public int getColumnCount() { return col_names.length; }
        public int getRowCount() { return myitem.getCachedFields().length; }
        public Object getValueAt(int row,int col) {
            ItemField[] fields = myitem.getCachedFields();
            if (col == 0) {
                return fields[row];
            } else {
                return myitem.get(fields[row]);
            }
        }
        public boolean isCellEditable(int row, int col) {
            return col == 1;
        }
        public void setValueAt(Object value, int row,int col) {
            String str_value = (String) value;
            Debug.debug("value: " + str_value);
            ItemField[] fields = myitem.getCachedFields();
            myitem.put(fields[row],(String)value);
        }
        
        public void itemChanged(DefaultItem item) {
            this.fireTableDataChanged();
        }
        
    };

    
    // LIST SUPPORT (this really needs to support a named relation)
    public ItemTableModel makeTableModel() {
        return item_storage.makeTableModelForRelation(WinterMute.containerContainsRelation);
    }
    public ItemTableModel makeTableModelForRelation(ItemRelation relation) {
        return item_storage.makeTableModelForRelation(relation);
    }
        
    public TreeModel makeTreeModel() {
        return item_storage.makeTreeModelForRelation(WinterMute.parentChildRelation);
    }
    
    public TreeModel getTreeModelForProperties() {
        return item_storage.getTreeModelForProperties();
    }
    
    /////////////////////////////// end: MODEL FACTORIES //////////////////
    
    /////////////////////////////// EVENTS ////////////////////////////////
    
    // happens when this Item is instantiated for the first time...
    protected void onCreate() {
    }
    
    // happens with this Item is brought in from the database...
    protected void onActivate() {
        loadIcon();
    }
    
    protected void onPropChange(ItemField field,String old_value, String new_value) {
        if (field.getFieldName().equals("icon_path")) {
            loadIcon();
        }
    }
    
    ///////////////////////////// end: EVENTS //////////////////////////////
    
   
}
