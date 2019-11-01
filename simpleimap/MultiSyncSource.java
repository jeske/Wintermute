/*
 * MultiSyncSource.java
 *
 * Created on January 8, 2003, 9:09 PM
 */

package simpleimap;


import java.util.*;
import java.text.*;



/**
 *
 * @author  hassan
 */
public class MultiSyncSource {
    protected Hashtable items;
    
    public MultiSyncSource() {
        this.items = new Hashtable();
    }
    
    public void add(MultiSyncRecord rec) {
        items.put(rec.getID(), rec);
    }
    
    public Hashtable getItems() {
        return this.items;
    }
    
    //  *********************************************
    
    public void notify_addItem(MultiSyncRecord rec) {
    }
    
    public void notify_updateItem(MultiSyncRecord rec) {
    }
    
    public void notify_removeItem(MultiSyncRecord rec) {
    }
    
    protected void copyKeyValues(MultiSyncRecord rec, DefaultItem newitem) {
        newitem.put("name",rec.getID());
        PropMapType item = rec.getItem();
        LinkedList keys = item.keyList();
        
        Iterator iter = keys.iterator();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            String val = (String) item.get(key);
            if(val != null) {
                newitem.put(key, val);
            }
        }
    }
    

    


}
