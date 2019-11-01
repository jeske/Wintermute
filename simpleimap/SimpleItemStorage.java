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
 * SimpleItem.java
 *
 * Created on October 29, 2002, 5:09 PM
 */

package simpleimap;

import java.util.*;
import java.io.*;

import java.lang.ref.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;


/**
 *
 * @author  David Jeske
 */

// this object is itself persisted, so be careful about instance data you put in it!

public class SimpleItemStorage extends Hashtable implements Serializable {
    // These are filled by SimpleDb
    public transient SimpleDB db;
    public transient byte[] raw_data;
    public transient Hashtable relations; // Hashtable<ItemRelation> = List<int>
    public transient Hashtable relations_additions; // Hashtable<ItemRelation> = List<int>

    public transient Hashtable visible_in_table_models; // Hashtable<oid,Relation> = WeakReference<ItemTableModel>
    public transient Hashtable relation_change_listeners; // Hashtable<ItemRelation> = List<WeakReference<IRelationChangeNotification>>

    public transient SimpleTxContext tx_context;
    public transient DefaultItem item_object;
    
    // These are handled by us....
    public String name;
    public String typename;
    private int oid;

    private boolean bRelationAdditions = false;
    
    /** Creates a new instance of SimpleItem */
    public SimpleItemStorage() {
        init();
    }
    
    public int hashCode() {
        return this.get_oid();
    }
    
    public SimpleItemStorage(SimpleDB a_db, int oid, String typename, String name) {
        this();
        this.db = a_db;
        this.oid = oid;
        this.name = name;
        this.typename = typename;
    }
    
    //////////////////////////////////////
    //
    // markDirty()
    //
    // key/value/data changes don't need to be
    // synchronized in a complex way, so we just
    // mark these objects dirty for save later.
    //
    
    private void markDirty() {
        markDirty(true);
    }
    
    private void markDirty(boolean notifyOfChange) {
        if (db != null) {
            db.queueItemForWrite(this);
        }
        
        if (notifyOfChange) {
            // make sure our watchers get told about the change!
            // this should probably use a timer instead to avoid
            // repeat event delivery - jeske
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dataChangedNotice();
                }
            });
        }

    }
    
    //////////////////////////////////////
    //b
    // addToTransactionContext()
    //
    // making a bidirectional relation requires
    // transactions to maintain integrity. This
    // makes sure the objects end up in a TX
    // context.
    
    private void addToTransactionContext() {
        // assure that we have a TX Context
        if (db != null && tx_context == null) {
            db.needsTxContext(this);
        }
        
    }
    
    public Object put(Object key, Object value) {
        // addToTransactionContext();
        Object retval =  super.put(key,value);
        markDirty();
        return retval;
    }
    
    public Object putNoNotify(Object key, Object value) {
        // addToTransactionContext();
        Object retval = super.put(key,value);
        markDirty(false);
        return retval;
    }
    
    
    private void init() {
        relations = new Hashtable();
        relations_additions = new Hashtable();
        relation_change_listeners = new Hashtable();
        visible_in_table_models = new Hashtable();
    }
    
    public void loadCheck() {
        if (relations == null) {
            Debug.debug(3, "Empty relations field!!! oid = " + oid);
            init();
        }
    }

    
    public int get_oid() {
        return oid;
    }
    
    public List getAvailableRelations() {
        Hashtable all_relations = new Hashtable();
        
        // scan the relation additions
        Enumeration en = relations_additions.keys();
        while (en.hasMoreElements()) {
            String relation_spec = (String)en.nextElement();
            all_relations.put(relation_spec,"1");
        }
        
        // scan the relations
        Enumeration en3 = relations.keys();
        while (en3.hasMoreElements()) {
            String relation_spec = (String)en3.nextElement();
            all_relations.put(relation_spec,"1");
        }
        
        if (this.bRelationAdditions) {
            // scan the saved relations
            List relation_names = db.getAvailableRelationNamesForOID(this.get_oid());
            Iterator itr = relation_names.iterator();
            while (itr.hasNext()) { 
                all_relations.put(itr.next(),"1");
            }
        }

        
        // FINALLY, assemble the final relation list...
        List l = new LinkedList();                // List<ItemRelation>
        Enumeration en2 = all_relations.keys();
        
        while (en2.hasMoreElements()) {
            String relation_spec = (String)en2.nextElement();
            ItemRelation item_r = new ItemRelation(relation_spec);
            l.add(item_r);
        }

        
        
        return l;
        
    }
        
    public List OLD_getAvailableRelations() {
        // List<ItemRelation>
        List l = new LinkedList();
        Enumeration en = relations.keys();
        
        while (en.hasMoreElements()) {
            String relation_spec = (String)en.nextElement();
            
            ItemRelation item_r = new ItemRelation(relation_spec);
            l.add(item_r);
        }
        return l;
    }
    
    public List getRelatedItems(ItemRelation relation) {
        List rel = getRelatedItemOIDs(relation);
 
        List a_list = new LinkedList();
        
        if (rel != null) {
            Iterator itr = rel.iterator();
            while (itr.hasNext()) {
                int oid = ((Integer)itr.next()).intValue();
                try {
                    a_list.add(db.getItem(oid));
                } catch (eNoSuchItem e) {
                    e.printStackTrace();
                    // pass
                }
            }
        }
        return a_list;
    }
    
    public LinkedList getRelatedItemOIDs(ItemRelation relation) {
        String spec = relation.getSpec();
        LinkedList cur_related_oids = (LinkedList)relations.get(spec);

        if (cur_related_oids == null) {
            LinkedList new_related_oids = new LinkedList();
            int[] related_oid_list = db.relatedItemsForOID(oid,relation);
            if (related_oid_list != null) {
                for (int i=0;i<related_oid_list.length;i++) {
                    new_related_oids.add(new Integer(related_oid_list[i]));
                }
            }
            // if there are relations_additions, we need to add them here!
            synchronized (this) {
                LinkedList additions = (LinkedList) relations_additions.get(relation.getSpec());
                if (additions != null) {
                    new_related_oids.addAll(additions);
                    relations_additions.remove(relation.getSpec()); // clear additions
                }
            
                // save this relation...
                relations.put(relation.getSpec(),new_related_oids);
            }
            cur_related_oids = new_related_oids;
        } else {
            LinkedList additions = (LinkedList) relations_additions.get(relation.getSpec());
            if (additions != null) {
                throw new RuntimeException("relation additions not empty");
            }
        }
        
        
        
        LinkedList related_oids_copy = (LinkedList)cur_related_oids.clone();

        return related_oids_copy;
    }
    
    public void applyRelationChanges() {
        Enumeration elem_enum = relations_additions.keys();
        while (elem_enum.hasMoreElements()) {
            String relation_spec = (String) elem_enum.nextElement();
            ItemRelation relation = new ItemRelation(relation_spec);
            getRelatedItemOIDs(relation); // load this relation
        }
    }
    
    ////////////////////////////////////
    //
    // I_addRelatedItem(ItemRelation relation, SimpleItemStorage b)
    //
    // THREADS: any
    // 
    // This should only be called by SimpleDB.
    
    protected int I_addRelatedItem(ItemRelation relation, SimpleItemStorage b) throws eDuplicateRelation {
        boolean relation_is_loaded;
        List related_oids;
        String relation_spec = relation.getSpec();
        Integer b_oid_obj = new Integer(b.oid);
        int insert_index;
        
        addToTransactionContext();
        // tx_context.addItem(b);
        synchronized (this) {
            related_oids = (List) relations.get(relation_spec);
        }
        if (related_oids != null) {
            relation_is_loaded = true;
        } else {
            if (bRelationAdditions) {
                synchronized (this) {
                    relation_is_loaded = false;
                    // create a relation addition list...
                    related_oids = (List)relations_additions.get(relation.getSpec());
                    if (related_oids == null) {
                        related_oids = new LinkedList();
                    } else {
                        if (related_oids.contains(b_oid_obj)) {
                            // throw new eDuplicateRelation("duplicate relation!");
                            Debug.debug("****** Duplicate Relation *******");
                        }
                    }
                }
            } else {
                relation_is_loaded = true;
                // trigger load of the relation
                this.getRelatedItemOIDs(relation);
                related_oids = (List) relations.get(relation_spec);
            }
        }
        
        synchronized (this) {
            insert_index = related_oids.size();
            related_oids.add(b_oid_obj);
        }
        
        Debug.debug(0, "I_addRelatedItem: " + name + " count: " + related_oids.size());
        
        
        if (relation_is_loaded) {
            // add this list directly to the relation
            List a_list;
            synchronized (this) {
                relations.put(relation_spec, related_oids);    
                a_list = (List)relation_change_listeners.get(relation.getSpec());
            }
            
            // Handle Model Notifications
            
           
            if (a_list != null) {
                Iterator itr = a_list.iterator();
                while (itr.hasNext()) {

                    IRelationChangeNotification mdl = (IRelationChangeNotification)((WeakReference)itr.next()).get();

                    if (mdl != null) {
                        mdl.itemAddedAfter(relation,b.item_object,null);
                        // mdl.fireTableRowsInserted(insert_index,insert_index);   
                    } else {
                        // remove empty weakref...
                        itr.remove();
                    }
                }
            }

        } else {
            Debug.debug("added to relation additions: " + b.item_object.get("name"));
            // just squirrel it away...
            relations_additions.put(relation_spec, related_oids);    
        }
        return insert_index;
    }
    
    ////////////////////////////////////
    // 
    // I_removeRelatedItem(ItemRelation relation, SimpleItemStorage b)
    // 
    // THREADS: any
    //
    // This should only be called by SimpleDB.
    
    protected void I_removeRelatedItem(ItemRelation relation, SimpleItemStorage b) {
        addToTransactionContext();
        // tx_context.addItem(b);
        Integer oid_object = new Integer(b.oid);
        int remove_index;
        
        getRelatedItemOIDs(relation); // make sure they are loaded...
        List related_oids = (List)relations.get(relation.getSpec());
        if (related_oids == null) {
            related_oids = new LinkedList();
        }
        
        synchronized (this) {
            remove_index = related_oids.indexOf(oid_object);
            related_oids.remove(oid_object);
        }
        relations.put(relation.getSpec(), related_oids);
        
        Debug.debug(3, "I_removeRelatedItem: " + name + " count: " + related_oids.size());
        
        
        // Handle model notifications
        List a_list;
        synchronized (this) {
            a_list = (List)relation_change_listeners.get(relation.getSpec());
        }
        if (a_list != null) {
            Iterator itr = a_list.iterator();
            while (itr.hasNext()) {
                IRelationChangeNotification mdl = (IRelationChangeNotification)((WeakReference)itr.next()).get();
                if (mdl != null) {
                    mdl.itemRemoved(relation,b.item_object);
                    // mdl.fireTableRowsDeleted(remove_index,remove_index);        
                } else {
                    // empty weak ref
                    itr.remove();
                }
            }
        }
    }
    
    public byte[] getData() {
        // demand load data
        if (raw_data == null) {
            raw_data = db.dataForOID(oid);
        }
        return raw_data;
    }
    public void setData(byte[] data) {
        // this.addToTransactionContext();
        raw_data = data;
        this.markDirty();
    }
    
    public void relateTo(ItemRelation relation,SimpleItemStorage item) {
        db.relateItems(relation,this,item);
    }
    public void unrelateFrom(ItemRelation relation,SimpleItemStorage item) {
        db.unrelateItems(relation,this,item);
    }
    
    
    
   
    
   
    
    public ItemTableModel makeTableModelForRelation(ItemRelation relation) {
        SimpleItemRelationTableModel mdl;
        
        // Instantiate new model..
        
        mdl = new SimpleItemRelationTableModel(this.item_object, relation);
        return mdl;
    }
    
    
    public TreeModel makeTreeModelForRelation(ItemRelation relation) {
        return new SimpleItemRelationTreeModel(relation,this.item_object);
    }
    
    public void noticeTableModel(IPropChangeNotification tm) {
        //Debug.debug("Table model hash code: " + itm.hashCode());
        if (!visible_in_table_models.containsKey(tm)) {
            visible_in_table_models.put(tm, new WeakReference(tm));
        }
    }

    public void notifyOfRelationChange(ItemRelation relation, IRelationChangeNotification ircn) {
        synchronized (this) {
            // put it in our model cache so we can notify it of changes
            List a_list = (List)relation_change_listeners.get(relation.getSpec());
            if (a_list == null) {
                a_list = Collections.synchronizedList(new LinkedList());
                relation_change_listeners.put(relation.getSpec(), a_list);
            }
            a_list.add(new WeakReference(ircn));
        }
    }
   
    private void dataChangedNotice() {
        if (visible_in_table_models!=null) {
            Enumeration elem_enum = visible_in_table_models.elements();

            while(elem_enum.hasMoreElements()) {
                IPropChangeNotification model = (IPropChangeNotification) ((WeakReference)elem_enum.nextElement()).get();
                if (model != null) {
                    // model.fireTableDataChanged();
                    model.itemChanged(this.item_object);
                    // Debug.debug("fire table data changed: ", model);
                }
            }                
        }
            
    }


    ///////////////////////////// end : Tree Model ////////////////////////////

      
    
    ///////////////////////////////// SLOT MODEL /////////////////////////////////////
    
    
    private class MySlotTreeModel implements TreeModel {
        private Vector treeModelListeners = new Vector();
        private ItemSlot rootNode;
        MySlotTreeModel(DefaultItem rootItem) {
            rootNode = new ItemSlot(rootItem,"Item");
        }
        
        private List childrenOfItem(Object o) {
            BaseSlot slot = (BaseSlot)o;
            return slot.childSlots();
        }
        
        public Object getChild(Object parent, int index) {
            return childrenOfItem(parent).get(index);
        }
        public int getChildCount(Object parent) {
            return childrenOfItem(parent).size();
        }
        public int getIndexOfChild(Object parent, Object child) {
            return childrenOfItem(parent).indexOf(child);
        }
        
        public Object getRoot() {
            return rootNode;
        }
        public boolean isLeaf(Object node) {
            return (childrenOfItem(node).size() == 0);
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
        }
        public void addTreeModelListener(TreeModelListener l) {
            treeModelListeners.add(l);
        }
        public void removeTreeModelListener(TreeModelListener l) {
            treeModelListeners.remove(l);
        }
    }
    
       // SLOT INTERFACE TO NODE....
    
    public abstract class BaseSlot {
        String slotName;
        DefaultItem item;
        BaseSlot(DefaultItem item, String slotName) {
            this.item = item;
            this.slotName = slotName;
        }
        abstract List childSlots();
        public String toString() {
            return slotName;
        }
        
        DefaultItem getItem() {
            return item;
        }
    };
    
    class ItemSlot extends BaseSlot {
        ItemSlot(DefaultItem item, String slotName) {
            super(item,slotName);
        }
        List childSlots() {
            return item.getItemStorage().getSlots();
        }
    }
    class PropertySlot extends BaseSlot {
        ItemField field;
        PropertySlot(DefaultItem item, ItemField slotField) {
            super(item,slotField.getTreeNodeName());
            field = slotField;
        }
        List childSlots() {
            return new LinkedList();
        }
        ItemField getField() {
            return field;
        }
    }
    class RelationSlot extends BaseSlot {
        ItemRelation relation;
        RelationSlot(DefaultItem item, ItemRelation relation) {
            super(item,relation.getSpec());
            this.relation = relation;
        }
        List childSlots() {
            List related_items = this.item.getRelatedItems(relation);
            if (related_items.size() > 0) {
                // use the first item as an example!
                DefaultItem item = (DefaultItem) related_items.get(0);
                return item.getItemStorage().getSlots();
            } else {
                return new LinkedList();
            }
        }
    }
    
    public List getSlots() {
        List children = new LinkedList();
        ItemField[] propNames = item_object.getCachedFields();

        for(int i=0;i<propNames.length;i++) {
            children.add(new PropertySlot(this.item_object,propNames[i]));
        }

        List relations = this.getAvailableRelations();
        Iterator itr = relations.iterator();
        while (itr.hasNext()) {
            ItemRelation item_relation = (ItemRelation) itr.next();
            children.add(new RelationSlot(this.item_object, item_relation));
        }
        return children;

    }
    
      
    public TreeModel getTreeModelForProperties() {
        return new MySlotTreeModel(this.item_object);
    }
    
    
    ///////////////////////////////// end: SLOT MODEL ////////////////////////////////
}
