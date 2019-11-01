/*
 * SimpleTxContext.java
 *
 * Created on November 6, 2002, 7:33 AM
 */

package simpleimap;

import java.util.*;
import com.sleepycat.je.DatabaseException;

/**
 *
 * @author  David W Jeske
 */
public class SimpleTxContext {
    protected List items;
    protected SimpleDB db;
    
    /** Creates a new instance of SimpleTxContext */
    public SimpleTxContext(SimpleDB db, SimpleItemStorage initial_item) {
        this.db = db;
        items = new LinkedList();
        
        addItem(initial_item);
        
    }
    
    protected void finalize () {
        if (items.size() != 0) {
            Debug.debug("SimpleTxContext: asked to free while non-empty!! " + items.size());
           //throw new RuntimeException("SimpleTxContext: asked to free while non-empty!! " + items.size());
        }
    }
    synchronized public int size() {
        return items.size();
    }
    synchronized public void save() {
        Debug.debug(3, "**** SimpleTxContext.save() [itemcount = " + items.size() + "] ****");
    
        // save all objects in this context!
        Iterator itr = items.iterator();
        while (itr.hasNext()) {
            SimpleItemStorage is = (SimpleItemStorage)itr.next();
            try {
                db.saveItem(null,is);
                db.saveItemRelations(null,is);
                is.tx_context = null;
            } catch (DatabaseException dbe) {
                Debug.debug(3, "dbexception: " + dbe.toString());
            }
        }
        
        this.clear();
    }
    
    protected void clear() {
           // clear save list...
        items = new LinkedList();
     
    }
    
    synchronized public void addItem(SimpleItemStorage item_storage) {
        SimpleTxContext merge_context = null;
        
        synchronized (item_storage) {
            if (item_storage.tx_context == this) {
                // he's already in our context!
                Debug.debug(3, "OID: " + item_storage.get_oid() + " already in our context!");
            } else if (item_storage.tx_context == null) {
                Debug.debug(3, "OID: " + item_storage.get_oid() + " adding to our context!");
                item_storage.tx_context = this;
                items.add(item_storage);
            } else {
                Debug.debug(3, "OID: " + item_storage.get_oid() + " need to merge contexts!");
                // we need to merge tx contexts!!!
               merge_context = item_storage.tx_context;
            }
        }
        
        if (merge_context != null) {
            synchronized (merge_context) {
                mergeWith(merge_context);
            }
        }
    }
    
    private void mergeWith(SimpleTxContext merge_context) {
        Iterator itr = merge_context.items.iterator();
        while (itr.hasNext()) {
            SimpleItemStorage is = (SimpleItemStorage) itr.next();
            is.tx_context = this;
            items.add(is);
        }
        merge_context.items = new LinkedList(); // clear list...
    }
    
    
    
}
