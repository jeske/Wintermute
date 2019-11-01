/*
 * ItemICal.java
 *
 * Created on January 9, 2003, 12:13 AM
 */

package simpleimap;

import java.io.*;
import java.security.*;
import java.util.Properties;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.*;

import java.text.*;


/**
 *
 * @author  hassan
 */
public class ItemICal extends ItemSync {
    public static final String TypeID = "ItemICal";

    /** Creates a new instance of ItemICal */
    public ItemICal() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemICal();
            }
        });
    }
        
    public void SyncServer() {
        String url = this.get("url");
        
        ICalendar cal = new ICalendar(url);
        MultiSyncSource ical_items;
        try {
            ical_items = cal.Import(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // deal with local items
        MultiSyncSource localRecords = new LocalItemSyncSource(this);
        
        MultiSync sync = new MultiSync();
        sync.Sync(ical_items, localRecords);
        
    } 
    
    
    private class LocalItemSyncSource extends MultiSyncSource {
        DefaultItem parent;
        
        public LocalItemSyncSource(DefaultItem parent) {
            super();
            this.parent = parent;
            
            List msg2items = parent.getRelatedItems(WinterMute.containerContainsRelation);
            
            Iterator iter2 = msg2items.iterator();
            while(iter2.hasNext()) {
                DefaultItem item2 = (DefaultItem) iter2.next();
                MultiSyncRecord item3 = new MultiSyncRecord(item2.get("name"), item2, item2.getInt("__LASTUPDATE", 0));
                this.add(item3);
            }
        }
        
 
        public void notify_addItem(MultiSyncRecord rec) {
            Debug.debug("added item id=<" + rec.getID() + ">");
            DefaultItem newitem = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(newitem == null) {
                newitem = (DefaultItem) WinterMute.my_db.newItem(null, "Default", rec.getID());
                this.parent.relateTo(WinterMute.containerContainsRelation, newitem);
            }

            this.copyKeyValues(rec, newitem);
        }
        
        public void notify_updateItem(MultiSyncRecord rec, DefaultItem olditem) {
            Debug.debug("updated item id=<" + rec.getID() + ">");
            DefaultItem item = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(item != null) {
                this.copyKeyValues(rec, item);
            }
        }
        
        public void notify_removeItem(MultiSyncRecord rec) {
            Debug.debug("removed item id=<" + rec.getID() + ">");
            DefaultItem item = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(item != null) {
                this.parent.removeItem(item);
            }
        }
        

    }    
}
