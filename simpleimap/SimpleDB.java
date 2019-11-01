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
 * SimpleDB.java
 *
 * Created on October 29, 2002, 10:27 AM
 */

package simpleimap;
import com.sleepycat.je.*;

import java.lang.*;
import java.util.*;
import java.lang.ref.*;
import javax.swing.AbstractAction;
import javax.swing.tree.*;
import java.lang.reflect.*;


///**
// *
// * @author  David Jeske
// */
public class SimpleDB {
    private final int clearDB = 0;
    private final boolean txdebug = true;
    
    private DefaultItem rootItem;
    private ItemField nameItemField;
    private ItemField nextOIDField;
    private int next_oid = -1; 
    
    // change notifications need to go here!
    private ItemRBManager rbManager;
    
    private String path;
    Environment dbenv;
    Database itemListsDb;
    Database itemsDb;
    Database itemNameToOIDDb;
    Database metaDb;
    Database dataDb;
    Database relationsDb;
    
    List pending_tx_contexts;
    Hashtable pending_dirty_objects;
    
    Hashtable itemFactories; // Hashtable<String name> -> ItemFactory
    
    Hashtable itemCache;
    Hashtable itemStorageCache;
    Hashtable itemListCache;
    
    Hashtable nameCache;
    

    private class LockResolver extends Thread {
        Environment dbenv;
        LockResolver(Environment dbenv) {
            this.dbenv = dbenv;
            this.setDaemon(true);
            this.setName("LockResolver");
        }
        
        public void run() {
            Debug.debug("wakeup...");
          
            while (true) {
                
                  /* db lock detection **
             try {
                 int reject_count = dbenv.lock_detect(0, Db.DB_LOCK_RANDOM);
                 //int reject_count = dbenv.lock_detect(0, Db.DB_LOCK_YOUNGEST);
                 if (reject_count > 0) {
                     Debug.debug("rejected " + reject_count + " lock(s)");
                 }
                 
                 if (false) {
                     DbLockStat linfo = dbenv.lock_stat(0);
                     System.out.println(format_object(linfo));

                     DbTxnStat txinfo = dbenv.txn_stat(0);
                     System.out.println(format_object(txinfo));
                 }
             } catch (Exception e) {
                 Debug.debug(e);
             }
             */

             
             try {
                 this.sleep(4000);
             } catch (InterruptedException ie) {
                 // pass
             }
          }
            
            
        }
        
        private String format_object(Object info) {
            String str = "";
            Class c = info.getClass();
            Field[] fields = c.getFields();
            
            for (int i=0;i<fields.length;i++) {
                try {
                    str += fields[i].getName() + " = " + fields[i].get(info).toString() + "\n";
                } catch (java.lang.IllegalAccessException e) {
                    str += fields[i].getName() + " = " + "<unavailable>\n";
                }
            }
            
            return str;
        }
    }
    /** Creates a new instance of SimpleDB */
    public SimpleDB(String path) {
        this.path = path;
        
        pending_tx_contexts = new LinkedList();
        pending_dirty_objects = new Hashtable();
        
        itemCache = new Hashtable();
        itemStorageCache = new Hashtable();
        itemListCache = new Hashtable();
        nameCache = new Hashtable();
        
        if(clearDB==1) {
            this.wipeDatabase();
        }
        
        // check for existance of the simpledb directory
        // create the directory if needed.
        java.io.File aPath = new java.io.File(this.path);
        if(!aPath.isDirectory()) {
            aPath.mkdirs();
        }
        
        
        try {
            EnvironmentConfig envcfg = new EnvironmentConfig();
            envcfg.setTransactional(true);
            envcfg.setAllowCreate(true);
            envcfg.setLockTimeout(100);
            envcfg.setTxnTimeout(100);
            dbenv = new com.sleepycat.je.Environment(new java.io.File(path), envcfg);
            
            
            //    dbenv.open(path, Db.DB_INIT_LOCK | Db.DB_INIT_MPOOL | 
            //                 Db.DB_RECOVER  | Db.DB_CREATE | 
            //                 Db.DB_INIT_TXN ,0);

            DatabaseConfig dbcfg = new DatabaseConfig();
            dbcfg.setTransactional(true);
            dbcfg.setAllowCreate(true);
            
            Transaction tx = dbenv.beginTransaction(null, null);
            //Transaction tx = dbenv.txn_begin(null, 0);
            if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : open dbs");
            
            metaDb = dbenv.openDatabase(tx,"meta.db", dbcfg);
            // metaDb.open(tx,"meta.db","db", Db.DB_HASH, Db.DB_DIRTY_READ | Db.DB_CREATE,0);
            
            itemListsDb = dbenv.openDatabase(tx, "itemlists.db", dbcfg);
            // itemListsDb = new Db(dbenv,0);
            // itemListsDb.open(tx,"itemlists.db","db",Db.DB_HASH, Db.DB_DIRTY_READ | Db.DB_CREATE , 0);
            
            itemNameToOIDDb = dbenv.openDatabase(tx, "itemnametooid.db", dbcfg);
            // itemNameToOIDDb = new Db(dbenv,0);
            // itemNameToOIDDb.open(tx,"itemnametooid.db","db",Db.DB_HASH, Db.DB_DIRTY_READ | Db.DB_CREATE, 0);
            
            itemsDb = dbenv.openDatabase(tx, "items.db", dbcfg);
            // itemsDb = new Db(dbenv,0);
            // itemsDb.open(tx,"items.db","db",Db.DB_HASH, Db.DB_DIRTY_READ | Db.DB_CREATE, 0);
            
            dataDb = dbenv.openDatabase(tx, "data.db", dbcfg);
            // dataDb = new Db(dbenv,0);
            // dataDb.open(tx,"data.db","db",Db.DB_HASH, Db.DB_DIRTY_READ | Db.DB_CREATE, 0);
            
            relationsDb = dbenv.openDatabase(tx, "relations.db", dbcfg);
            // relationsDb = new Db(dbenv,0);
            // relationsDb.open(tx,"relations.db", "db", Db.DB_BTREE, Db.DB_CREATE, 0);
            
            if (txdebug) System.out.println("commit (" + tx.toString() + ") : open dbs");
            tx.commit();
            
            
        } catch (Exception e) {
            Debug.debug("error opening berkeleydb");
            Debug.debug(e);
            WinterMute.exitApplication();
        }
        new LockResolver(dbenv).start();
        
        itemFactories = new Hashtable();
        
        
        // register base types!
        ItemRoot.register(this);
        ItemDefault.register(this);
        ItemField.register(this);
        
    }
    
    public void wipeDatabase() {
        java.io.File aPath = new java.io.File(this.path);
        java.io.File files[];
        
        files = aPath.listFiles();
        for (int i=0;i<files.length;i++) {
            files[i].delete();
        }
        aPath.delete();
    }
    
    private void recurActivate(Object obj, TreeModel model) {
        int childcount = model.getChildCount(obj);
        Debug.debug("childcount=" + childcount);
        for(int i=0; i<childcount; i++) {
            Object child = model.getChild(obj, i);
            recurActivate(child, model);
        }
    }
    
    public void load() {
        boolean initUIflag = false;

        // BOOTSTRAP
        try {
            // if (true) throw new eNoSuchItem("foo");
            rootItem = getItem(0);

            nameItemField = (ItemField) getItem(1);
            nextOIDField = (ItemField) getItem(2);
            
            next_oid = Integer.parseInt(rootItem.get(nextOIDField));
            
            TreeModel model = rootItem.makeTreeModel();
            Object root = model.getRoot();
            recurActivate(root, model);

        } catch (eNoSuchItem e) {
            initUIflag = true;
            Debug.debug(3,"must init UI");
        } 
        
        if(initUIflag == true) {
            /////////////////////////////////////// MAKE DEFAULT DB /////////////////////////////////////////
            
            makeRootItem();
            
            if (rootItem.get_oid() != 0) {
                throw new RuntimeException("Root node does not have oid=0 -> " + rootItem.get_oid());
            }
            /////////////////////////////////////// end: MAKE DEFAULT DB /////////////////////////////////////////
        }

        // load and hook rbmanager
        this.rbManager = (ItemRBManager) rootItem.getItem(WinterMute.parentChildRelation,"RelationBuilders");
        
        SimpleGUI guiBuilder = new SimpleGUI(this);
        DefaultItem ui = guiBuilder.buildUI(rootItem);
        
        
        // this.flushPendingTxs();
        
        // start the sync thread!
        my_sync_thread = new SyncThread(this);
        my_sync_thread.start();
    }
    
    private SyncThread my_sync_thread;
    private class SyncThread extends Thread {
        SimpleDB db;
        Date last_flush_time;
        final static int SYNC_TIMEOUT = 120; // in seconds
        
        SyncThread(SimpleDB db) {
            this.db = db;
            this.setDaemon(true);
            this.setName("SimpleDB.SyncThread");
            
        }
        public void run() {
            last_flush_time = new Date();
            Debug.debug("wakeup.");
            db.flushPendingTxs();

            
            while (true) {
                try {
                    Date now = new Date();
                    long time_delta = now.getTime() - last_flush_time.getTime();
                    Debug.debug(3, "check needsync.." + time_delta + " " + now);
                    int dirty_count = 0;
                    synchronized (db.pending_dirty_objects) {
                        dirty_count = db.pending_dirty_objects.size();
                    }
                    
                    if (time_delta > SYNC_TIMEOUT || (db.pendingCount() + dirty_count)  > 150) {
                        db.flushPendingTxs();
                        last_flush_time = new Date();
                    }
                } catch (Exception e) {
                    Debug.debug(e);
                }
                try {
                    this.sleep(2000);
                } catch (java.lang.InterruptedException e) {
                    // pass
                }
            }
        }
    }
    
    
    public void needsTxContext(SimpleItemStorage item_storage) {
        // item needs a new transaction context and needs to be added to it.
        synchronized (pending_tx_contexts) {
            pending_tx_contexts.add(new SimpleTxContext(this,item_storage));
        }
    }
    
    public void registerTypeFactory(String type, ItemFactory factory) {
        itemFactories.put(type,factory);
    }
    
    protected int pendingCount() {
        int pending_count = 0;
        synchronized (pending_tx_contexts) {
            Iterator itr = pending_tx_contexts.iterator();
            while (itr.hasNext()) {
                SimpleTxContext stx = (SimpleTxContext) itr.next();
                pending_count += stx.size();
            }
        }
        return pending_count;
    }
    
    protected void queueItemForWrite(SimpleItemStorage is) {
        // these two things should really be forced to become part
        // of the same transaction at some point... - jeske
        synchronized (pending_dirty_objects) {
            this.pending_dirty_objects.put(is, is);
        }
        if (is.item_object != rootItem && this.rbManager != null) {
            this.rbManager.enqueueItem(is.item_object);
        }
        
    }
    
    protected void flushPendingTxs() {
        Debug.debug(3,"flushing objects...");
        int est_object_count = 0;
        int total_object_count = 0;
        int total_object_relation_count = 0;
        Hashtable my_copy_dirty_objects;
        Hashtable save_relations = new Hashtable();
        
        // get a copy of all hash marked objects
        synchronized (pending_dirty_objects) {
            my_copy_dirty_objects = (Hashtable)pending_dirty_objects.clone();
            pending_dirty_objects.clear();
        }
        
        // add tx context objects 
        synchronized (pending_tx_contexts) {
            Iterator itr = pending_tx_contexts.iterator();
            while (itr.hasNext()) {
                SimpleTxContext tx = (SimpleTxContext) itr.next();
                synchronized (tx) {
                    Iterator si_itr = tx.items.iterator();
                    while (si_itr.hasNext()) {
                        SimpleItemStorage si = (SimpleItemStorage) si_itr.next();
                        my_copy_dirty_objects.put(si,si);
                        save_relations.put(si,si);
                        si.tx_context = null; // remove from context
                    }
                    tx.clear();
                }
                itr.remove(); // remove tx context from pending_tx_contexts
            }
        }
        
        pending_tx_contexts.clear(); // for good measure
        
        // estimate save count...
        est_object_count = my_copy_dirty_objects.size();
        if (est_object_count == 0) {
            // no work to do!
            return;
        }
        
        boolean saved = false;
        
        while (! saved ) {
            Transaction tx = null;
            try {
                Debug.debug("Starting save of " + est_object_count + " objects.");


                try {
                    tx = dbenv.beginTransaction(null,null);
                    // tx = dbenv.txn_begin(null, 0);
                    if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : flushPending");
                } catch (DatabaseException e) {
                    Debug.debug(e);
                    // this is really bad!
                    tx = null;
                }



                //////////////////////////////
                // save
                //

                // open Tx Context


                for (Enumeration e=my_copy_dirty_objects.elements();e.hasMoreElements();) {
                    SimpleItemStorage is = (SimpleItemStorage)e.nextElement();
                    // since this is from the dirty list we only need
                    // to save the key/value/data, not relations...
                    this.saveItem(tx,is);
                    total_object_count++;

                    Debug.debug(3,"saveItem(" + is.get_oid() + ")");

                    if (save_relations.containsKey(is)) {
                        this.saveItemRelations(tx,is);
                        total_object_relation_count++;

                        Debug.debug(3,"saveItemRelations(" + is.get_oid() + ")");
                    }
                }

                Debug.debug("starting TX commit...");
                try {
                    if (txdebug) System.out.println("commit (" + tx.toString() + ") : flushPending");
                    tx.commit();
                    saved=true;
                } catch (DatabaseException e) {
                    Debug.debug(e);
                    // this is really bad!
                }
            } catch (DatabaseException dbe) {
		if (tx != null) {
                    try {
                        if (txdebug) System.out.println("abort (" + tx.toString() + ") : flushpending");
                        tx.abort();
                    } catch (DatabaseException e) {
                        Debug.debug(e);
                    }
                }
                Debug.debug(dbe);
            }
             
        }


        //
        // save done
        ///////////////////////////////////
        
        
        if (total_object_count > 0) {
            Debug.debug("saved " + total_object_count + " objects (" + total_object_relation_count + " with relations)");
            
            
            
            int olditemsize = itemCache.size();
            int oldsize = itemStorageCache.size();
            if (oldsize > 300) {
                Debug.debug("performing GC");
                System.gc();
            }
            
            synchronized (this) {
                // clean itemCache
                synchronized (itemCache) {
                    Set keySet1 = itemCache.keySet();
                    for(Iterator itr1 = keySet1.iterator();itr1.hasNext();) {
                        Object key = itr1.next();
                        Reference s = (Reference)itemCache.get(key);
                        if (s.get() == null) {
                            itr1.remove();
                        }
                    }
                }
                synchronized (itemStorageCache) {
                    // clean itemStorageCache
                    Set keySet = itemStorageCache.keySet();
                    for(Iterator itr = keySet.iterator();itr.hasNext();) {
                        Object key = itr.next();
                        Reference s = (Reference)(itemStorageCache.get(key));
                        if (s.get() == null) {
                            itr.remove();
                        }
                    }
                }
                
            }
            Debug.debug("itemCache.size() = " + itemCache.size() + "   was: " + olditemsize);
            Debug.debug("itemStorageCache.size() = " + itemStorageCache.size() + "  was: " + oldsize);
        }
        
    }
    
    protected void finalize() {
        if(this.dbenv == null) return;
        
        flushPendingTxs();
        
        Debug.debug("closing DBs");
        try {
            metaDb.close();
            itemListsDb.close();
            itemNameToOIDDb.close();
            itemsDb.close();
            dataDb.close();
            relationsDb.close();
            this.dbenv.close();
            
        } catch (DatabaseException e) {
            Debug.debug("error closing database: " + e.toString());
        }
    }
    
    private synchronized int nextOID(Transaction tx) {
        
        if (rootItem == null || next_oid == -1) {
            throw new RuntimeException("can't issue nextOID() because rootItem is not loaded");
        }
        int oid_to_issue = next_oid;
        next_oid++;
        rootItem.put(nextOIDField, "" + (next_oid));
        return oid_to_issue;
    }
    
    public DefaultItem rootNode() {
        return rootItem;
    }
    
    // special case, because we are going to generate our own next_oid
    public void makeRootItem() {
        rootItem = __createItem(null,ItemRoot.TypeID,"Top", 0);
        nameItemField = (ItemField) __createItem(null,ItemField.TypeID,"Field/name", 1);
        nextOIDField  = (ItemField) __createItem(null,ItemField.TypeID,"Field/next_oid", 2);
        rootItem.put(nameItemField,"Top");
        rootItem.put(nextOIDField, "100");
        next_oid = 100;
        nameItemField.put(nameItemField,"Field/name");
        nextOIDField.put(nameItemField,"Field/next_oid");
        
    }
    
    public ItemField getCommonField(String common_name) {
        ItemField field;
        
        if (common_name == "name") {
            return nameItemField;
        }
        
        String name = "Field/" + common_name;
        try {
            int oid = this.getOIDFromName(name);
            field = (ItemField)this.getItem(oid);
        } catch (eNoSuchItem err) {
            // create a new field object!
            field =  (ItemField)this.newItem(null,ItemField.TypeID,name);
            //rootItem.relateTo(new ItemRelation("root","fields"), field);
            rootItem.relateTo(WinterMute.containerContainsRelation,field);
            Debug.debug(3, "created field: " + name);
        }
        
        return field;
    }
    
    public ItemField getItemFieldFromOID(String soid) {
        int oid = Integer.parseInt(soid);
        
        
        try {
            ItemField field = (ItemField)this.getItem(oid);
            return field;
        } catch (eNoSuchItem err) {
            return null;
        }
    }
    
    public DefaultItem newItem(Transaction tx, String type, String name) {
        DefaultItem item = __createItem(tx,type,name,nextOID(tx));
        return item;
    }
    
    private DefaultItem __createItem(Transaction tx,String type, String name, int oid) {
        int next_oid = oid;
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        ItemFactory factory = (ItemFactory)itemFactories.get(type);
        DefaultItem item = null;
        boolean made_tx = false;
        boolean save_name = true;
        
        if(name == null) {
            name = "Item " + next_oid;
            save_name = false;
        }
        
        try {
            if (factory == null) {
                throw new RuntimeException("No Factory for item type: " + type + "  name: " + name);
            }
            
            if (tx == null) {
                made_tx = true;
                // tx = dbenv.txn_begin(null, Db.DB_DIRTY_READ);
                TransactionConfig txcfg = new TransactionConfig();
                txcfg.setReadUncommitted(true);
                tx = dbenv.beginTransaction(null,txcfg);
                if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : createitem");
            }
  
            // save name to item mapping....
            // (we need to remove this to speed up object creation) - jeske
            if (save_name) {
                try {
                    key.setString(name);
                    data.setString("" + next_oid);
                    itemNameToOIDDb.put(tx,key,data);
                    
                } catch (Exception e) {
                    Debug.debug(e);
                }
            }

            SimpleItemStorage itemstorage = new SimpleItemStorage(this, next_oid, type, name);
            synchronized (itemStorageCache) {
                itemStorageCache.put(new Integer(next_oid),new WeakReference(itemstorage));
            }

            item = factory.construct();
            synchronized (itemCache) {
                itemCache.put(new Integer(next_oid),new WeakReference(item));
            }
            item.setStorage(itemstorage);

            if (nameItemField != null) {
                // handle rootItem bootstrap
                item.put(nameItemField,name);
            }

            Debug.debug(3, "SimpleDB: newItem(" + name + "), oid=" + next_oid);
            
        } catch (Exception e) {
            Debug.debug(e);
            if (made_tx && tx != null) {
                try {
                    if (txdebug) System.out.println("commit (" + tx.toString() + ") : createitem");
                    tx.commit();
                } catch (DatabaseException dbe) {
                    Debug.debug(e);
                }
              tx = null;
            }
        } finally {
            if (made_tx && tx != null) {
                try {
                    if (txdebug) System.out.println("commit (" + tx.toString() + ") : createitem");
                    tx.commit();
                } catch (DatabaseException dbe) {
                    Debug.debug(dbe);
                    throw new RuntimeException("Could not create item");
                }
            }
        }
        
        if (item == null) {
            throw new RuntimeException("__createItem(): null item");
        }
        
        if (oid > 5) {
            item.onCreate();
            item.onActivate();
        }
        
        return item;
    }
    
    public DefaultItem getItemFromName(String name) {
        int oid;
        try {
            oid = getOIDFromName(name);
        } catch (eNoSuchItem e) {
            return null;
        }
        
        DefaultItem item;
        
        try {
            item = getItem(oid);
        } catch (eNoSuchItem e) {
            return null;
        }
        return item;
    }
    
    public int getOIDFromName(String name) throws eNoSuchItem {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        int oid = 0;
        
        // try the namecache first
        
        Integer oid_obj = (Integer) nameCache.get(name);
        if (oid_obj != null) {
            oid = oid_obj.intValue();
            return oid;
        }
        
        // otherwise check the table...
        try {
            key.setString(name);
            itemNameToOIDDb.get(null, key, data, LockMode.READ_UNCOMMITTED);
            // itemNameToOIDDb.get(null, key,data, Db.DB_DIRTY_READ);
            oid = Integer.parseInt((String)data.getString());
        } catch (Exception e) {
            throw new eNoSuchItem(name);
        }
        
        nameCache.put(name, new Integer(oid));
        
        return oid;
        
    }
    
    synchronized private SimpleItemStorage _getItemStorage(int oid) throws eNoSuchItem {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        SimpleItemStorage itemstorage = null;
        

        
        // if not, then see if we have the SimpleItemStorage object in cache
        Reference storageref = (Reference)itemStorageCache.get(new Integer(oid));

        if (storageref != null) {
            itemstorage = (SimpleItemStorage)storageref.get();
        }
        
        if (itemstorage == null) {
            // load the itemstorage!
            Transaction tx = null;
            
            try {
                // tx = dbenv.txn_begin(null, Db.DB_DIRTY_READ);
                TransactionConfig txcfg = new TransactionConfig();
                txcfg.setReadUncommitted(true);
                tx = dbenv.beginTransaction(null,txcfg);
                if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : getitemstorage");
                
                key.setString("" + oid);
                if (itemsDb.get(tx, key,data, LockMode.READ_UNCOMMITTED) != OperationStatus.SUCCESS) {
                    throw new eNoSuchItem("oid:" + oid);
                }
                
                itemstorage = (SimpleItemStorage)data.getObject();
                itemstorage.db = this;
                itemstorage.loadCheck(); // HACK
                
                synchronized (itemStorageCache) {
                    itemStorageCache.put(new Integer(itemstorage.get_oid()),new WeakReference(itemstorage));
                }
                
                
            } catch (Exception e) {
                Debug.debug(e);
                throw new eNoSuchItem("oid:" + oid);
            } finally {
                try {
                    if (tx != null) {
                        if (txdebug) System.out.println("commit (" + tx.toString() + ") : getitemstorage");
                        tx.commit();
                    }
                } catch (DatabaseException dbe) {
                    Debug.debug(dbe);
                }
            }
        }    
        return itemstorage;
        
    }
    
    public DefaultItem getItem(int oid) throws eNoSuchItem {
        // construct the Item Object !
        DefaultItem item;
        
        // check the cache for the instantiated item...
        Reference ref;
        synchronized (itemCache) {
             ref = (Reference)itemCache.get(new Integer(oid));
        }
        if (ref != null) {
            item = (DefaultItem)ref.get();
            if (item != null) {
                return item;
            }
        }
        
        SimpleItemStorage itemstorage = _getItemStorage(oid);
        
        ItemFactory factory = (ItemFactory)itemFactories.get(itemstorage.typename);
        item = factory.construct();
        
        synchronized (itemCache) {
            itemCache.put(new Integer(oid),new WeakReference(item));
        }
        
        item.setStorage(itemstorage);
        
        Debug.debug(3, "SimpleDB: loaded item, oid=" + oid);
        
        if (oid > 5) {
            // root item can't be activated normally
            item.onActivate();
        }
        return item;
        
    }
    
    public int[] relatedItemsForOID(int oid,ItemRelation relation) {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        String relation_list_name = "" + oid + ":" + relation.getSpec();
        Transaction tx = null;
        
        boolean have_data = false;
        while (! have_data ) {
            try {
                // tx = dbenv.txn_begin(null, Db.DB_DIRTY_READ);
                TransactionConfig txcfg = new TransactionConfig();
                txcfg.setReadUncommitted(true);
                tx = dbenv.beginTransaction(null, txcfg);
                if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : relateditemsforoid");
                // Debug.debug("Tx ID: " + tx.id());

                try {
                    key.setString(relation_list_name);
                    // remove Db.DB_DIRTY_READ
                    if (relationsDb.get(tx,key,data,null) != OperationStatus.SUCCESS) {
                        Debug.debug(3,"relatedItemsForOID failed!");
                        return null;
                    }
                    have_data = true;
                    int[] oid_arr = (int[])data.getObject();
                    Debug.debug(3, "relatedItemsForOID(" + oid + "," + relation + ") -> " + pp(oid_arr));
                    return oid_arr;
                } catch (java.io.IOException e) {
                    Debug.debug(e);
                    continue; // try again
                } catch (ClassNotFoundException e) {
                    Debug.debug(e);
                    continue; // try again
                }
            //} catch (DatabaseException dle) {  //DbDeadlockException
            //    Debug.debug("caught dle, retrying");
            //    Debug.debug(dle);
            //    continue;
            } catch (DatabaseException e) {
                Debug.debug(e);
                continue;
            } finally { 
                if (tx != null) {
                    try {
                        if (txdebug) System.out.println("commit (" + tx.toString() + ") : relateditemsforoid");
                        tx.commit();
                        tx = null;
                    } catch (DatabaseException e) {
                        Debug.debug(e);
                    }
                }
            }
        }
        return null;
    
    }
    
    public void relateItems(ItemRelation relation, SimpleItemStorage a, SimpleItemStorage b) throws eDuplicateRelation {
        ItemRelation inv_relation = relation.invert();
        int insert_index_a = a.I_addRelatedItem(relation,b);
        int insert_index_b = b.I_addRelatedItem(inv_relation,a);
    }
    
    public void relateItems(ItemRelation relation,DefaultItem ai, DefaultItem bi) throws eDuplicateRelation {
        SimpleItemStorage a = ai.getItemStorage();
        SimpleItemStorage b = bi.getItemStorage();
        // this should happen in a transaction!
        relateItems(relation,a,b);
    }
    
    public void unrelateItems(ItemRelation relation, SimpleItemStorage a, SimpleItemStorage b) {
        ItemRelation inv_relation = relation.invert();
        a.I_removeRelatedItem(relation,b);
        b.I_removeRelatedItem(inv_relation,a);
    }
    
    public  byte[] dataForOID(int oid) {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        
        
        try {
            key.setString("" + oid);
            if (dataDb.get(null,key,data,LockMode.READ_UNCOMMITTED) != OperationStatus.SUCCESS) {
                Debug.debug(3, "No such data chunk for oid " + oid);
                return null;
            }
            byte[] loadeddata = (byte[])data.getObject();
            return loadeddata;
        } catch (java.io.IOException e) {
            Debug.debug(e);
            return null;
        } catch (DatabaseException e) {
            Debug.debug(e);
            return null;
        } catch (java.lang.ClassNotFoundException e) {
            Debug.debug(e);
        } catch (NullPointerException e) {
            Debug.debug(e);
        }
        return null;
        
    }
    public static String pp(long [] ar) {
        String output = "";
        for (int i=0;i<ar.length;i++) {
            output = output + ar[i] + " ";
        }
        return output;
        
    }
    public static String pp(int [] ar) {
        String output = "";
        for (int i=0;i<ar.length;i++) {
            output = output + ar[i] + " ";
        }
        return output;
        
    }
    
    /////////////////// RELATION PERSISTANCE /////////////////////////////
    
    
    public void saveItemRelations(Transaction tx, SimpleItemStorage item) throws DatabaseException {
        Debug.debug(3, "saveItemRelations, OID: " + item.get_oid());
        
        // first we need to apply the relations_additions changes
        
        item.applyRelationChanges();
        
        // second we need to save the relations
        
        Iterator itr = item.getAvailableRelations().iterator();
        while (itr.hasNext()) {
            ItemRelation relation = (ItemRelation)itr.next();
            saveItemRelation(tx, item,relation);
        }
    }
    
    public void saveItemRelation(Transaction tx, SimpleItemStorage item,ItemRelation relation) throws DatabaseException {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        String relationSpec = relation.getSpec();
        
      
        String relation_list_name = "" + item.get_oid() + ":" + relationSpec;
        key.setString(relation_list_name);

        if (item.relations != null) {
            List related_oid_list = (List)item.relations.get(relationSpec);

            if (related_oid_list != null) {
                int[] relations = new int[related_oid_list.size()];

                Iterator itr = related_oid_list.iterator();
                int n = 0;
                while (itr.hasNext()) {
                    int oid = ((Integer)itr.next()).intValue();
                    relations[n] = oid;
                    n = n + 1;
                }
                try {
                    data.setObject(relations);
                } catch (java.io.IOException ioe) {
                    throw new RuntimeException("Io Exception on object serialize");
                }
                relationsDb.put(tx,key,data);
                if (tx == null) { 
                    // relationsDb.sync(0); 
                }

                Debug.debug(3, "saveItemRelation(" + relation.getSpec() + "," + pp(relations));
            }
        }
    
    }
    
    public List getAvailableRelationNamesForOID(int oid) {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        List available_relations = new LinkedList();
        OperationStatus retval = null;
        Cursor cur = null;
        Transaction tx = null;
        
        try {
            try {
                // tx = dbenv.txn_begin(null, 0);
                tx = dbenv.beginTransaction(null,null);
                if (txdebug) System.out.println("txn_begin (" + tx.toString() + ") : getavailablerelationnamesforoid");
                
            } catch (DatabaseException e) {
                Debug.debug(e);
                // this is really bad!
                tx = null;
            }
            
            try {
                // cur = relationsDb.cursor(tx, Db.DB_DIRTY_READ);
                CursorConfig curcfg = new CursorConfig();
                curcfg.setReadUncommitted(true);
                cur = relationsDb.openCursor(tx,curcfg);
            } catch (DatabaseException e) {
                return available_relations;
            }
            String key_name;
            
            key.setString("" + oid + ":");
            try {
                // retval = cur.get(key, data, Db.DB_SET_RANGE | Db.DB_DIRTY_READ);
                retval = cur.getSearchKeyRange(key, data, LockMode.READ_UNCOMMITTED);
            } catch (DatabaseException e) {
                return available_relations;
            }
            while (retval == OperationStatus.SUCCESS) {
                key_name = key.getString();
                String parts[] = key_name.split(":");
                int this_oid = Integer.parseInt(parts[0]);
                if (this_oid != oid) {
                    return available_relations;
                }
                Debug.debug(3, "key: " + key_name);
                available_relations.add(parts[1] + ":" + parts[2]);
                
                try {
                    retval = cur.getNext(key,data,LockMode.READ_UNCOMMITTED);
                    // retval = cur.get(key, data, Db.DB_NEXT | Db.DB_DIRTY_READ);
                } catch (DatabaseException e) {
                    return available_relations;
                }
                
            }
            // Debug.debug(3, "retval:" + retval + " " + dbenv.strerror(retval));
        } finally {
            try {
                
                if (cur!= null) cur.close();
                if (txdebug) System.out.println("commmit (" + tx.toString() + ") : getrelateditemrelationnames");
                if (tx!=null) tx.commit();
            } catch (DatabaseException e) {
                // pass
                Debug.debug(3,"uncaught exception in commit");
            }
        }
        
        return available_relations;
    }
    
    //////////////////////// end: RELATION PERSISTANCE ///////////////////

    
    public class AccountInformation {
        String emailAddress;
        String fullName;
        String smtpServer;
        
        public AccountInformation(String emailAddress, String fullName, String smtpServer) {
            this.emailAddress = emailAddress;
            this.fullName = fullName;
            this.smtpServer = smtpServer;
        }
    }

    static LinkedList theAccounts = null;
    
    public LinkedList getUserAccountEmailAddresses() {
        if(theAccounts == null) {
            DefaultItem root = WinterMute.my_db.rootNode();
            java.util.List items = root.getRelatedItems(WinterMute.parentChildRelation);
            Iterator iter = items.iterator();
            
            LinkedList ret = new LinkedList();
            
            AccountInformation ai;
            
            while(iter.hasNext()) {
                DefaultItem item = (DefaultItem) iter.next();
                
                String emailAddress = item.get("emailAddress");
                
                if(emailAddress != null) {
                    String smtpServer = item.get("smtpServer");
                    String yourName = item.get("yourName");
                    ai = new AccountInformation(emailAddress, yourName, smtpServer);
                    ret.add(ai);
                }
            }
            theAccounts = ret;
        }
        
        return theAccounts;
    }        
    
    
    //////////////////////// ITEM PERSISTANCE //////////////////////////////
    
    
    public void saveItem(Transaction tx, SimpleItemStorage item) {
        MyDbt key = new MyDbt();
        MyDbt data = new MyDbt();
        
        try {
            key.setString("" + item.get_oid());
            data.setObject(item);
            
            itemsDb.put(tx,key,data);
            
            // if (tx == null) { itemsDb.sync(0); }
            
            if (item.raw_data != null) {
                data.setObject(item.raw_data);
                dataDb.put(tx,key,data);
                // if (tx == null) { dataDb.sync(0); }
            }
            
            
        } catch (Exception e) {
            Debug.debug("saveItem error: " + e.toString());
        }
        
    }
    
    public void printDb(Database a_db, String name) throws Exception {
        OperationStatus retval = null;
        MyDbt c_key = new MyDbt();
        MyDbt c_data = new MyDbt();
        
        Debug.debug("-- [ " + name + " ] --");
        // Dbc curs = a_db.cursor(null,0);
        Cursor curs = a_db.openCursor(null,null);
        // retval = curs.get(c_key, c_data, Db.DB_NEXT);
        retval = curs.getFirst(c_key,c_data,null);
        while (retval == OperationStatus.SUCCESS) {
            Debug.debug(c_key  + " = " + c_data);
            
            // retval = curs.get(c_key, c_data, Db.DB_NEXT);
            retval = curs.getNext(c_key,c_data,null);
        }
        curs.close();
        
    }
    
    public void test() throws Exception {
        
        
        byte[] key_b = {'T', 'E', 'S', 'T' };
        byte[] data_b = { 'D', 'A', 'T', 'A' };
        // Dbt key = new Dbt(key_b);
        MyDbt key = new MyDbt("TEST");
        MyDbt data = new MyDbt(data_b);
        MyDbt r_data = new MyDbt();
        MyDbt r_data_1 = new MyDbt();
        OperationStatus retval = null;
        
        
        // read first!
        Debug.debug("---------------------------");
        
        printDb(metaDb,"meta");
        printDb(itemsDb,"items");
        printDb(relationsDb,"relations");
        printDb(itemNameToOIDDb,"names");
        
        
        retval = metaDb.get(null,key,r_data_1,LockMode.READ_UNCOMMITTED);
        Debug.debug("SimpleDB.test: metaDb.get1: retval = " + retval);
        
        if (retval == OperationStatus.SUCCESS) {
            Debug.debug("SimpleDB.test: key (" + key.getSize() + ")= " + key.getData()[0]);
            Debug.debug("SimpleDB.test: data (" + r_data_1.getSize() + ")= " + r_data_1.getData()[0]);
        } else {
            Debug.debug("SimpleDB.test: read error: " + retval.toString());
        }
        
        
        // DbTxn tx = dbenv.txn_begin(null, 0);
        
        Debug.debug("key (" + key.getSize() + ")= " + key.getData()[0]);
        Debug.debug("data = " + data.getData()[0]);
        retval = metaDb.put(null,key,data);
        Debug.debug("metaDB.put: retval = " + retval.toString());
        
        //  tx.commit(0);
        
        
        retval = metaDb.get(null,key,r_data,LockMode.READ_UNCOMMITTED);
        Debug.debug("SimpleDB.test: metaDb.get2: retval = " + retval);
        Debug.debug("SimpleDB.test: key (" + key.getSize() + ")= " + key.getData()[0]);
        Debug.debug("SimpleDB.test: data (" + r_data.getSize() + ")= " + r_data.getData()[0]);
        
        Debug.debug("Available Relations (OID:0)");
        Debug.debug("relationlist size: " + getAvailableRelationNamesForOID(100).size());
        printDb(dataDb,"data");
    }


    
    
    
    
    public static void main(String args[]) throws Exception {
        Debug.start();
        
        
        SimpleDB test_db = new SimpleDB(WMConfig.dbPath);
        //test_db.load();
        test_db.test();
        //System.exit(0);
    }
}
