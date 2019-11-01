/*
 * MultiSync.java
 *
 * Created on January 8, 2003, 9:07 PM
 */

package simpleimap;

import java.util.*;
/**
 *
 * @author  hassan
 */
public class MultiSync {
    
    /** Creates a new instance of MultiSync */
    public MultiSync() {
    }
    
    private class SyncResult {
        LinkedList updates;
        LinkedList additions;
        LinkedList deletions;
        
        public SyncResult() {
            this.updates = new LinkedList();
            this.additions = new LinkedList();
            this.deletions = new LinkedList();
        }
    }
    private SyncResult findDiff(Hashtable a, Hashtable b) {
        SyncResult ret = new SyncResult();

        Enumeration iter = a.elements();
        while(iter.hasMoreElements()) {
            MultiSyncRecord i = (MultiSyncRecord) iter.nextElement();
            
            MultiSyncRecord j = (MultiSyncRecord) b.get(i.getID());

            if(i.isDeleted()) {
                if(j!=null && !j.isDeleted()) {
                    ret.deletions.add(i);
                }
            } else if(j == null) {
               ret.additions.add(i);
            } else if(i.isNewer(j) == false) {
                ret.updates.add(i);
            }
        }
        
        
        return ret;
    }
    
    public void Sync(MultiSyncSource a, MultiSyncSource b) {
        Debug.debug("len a=" + a.getItems().size());
        Debug.debug("len b=" + b.getItems().size());
        SyncResult NMB = this.findDiff(a.getItems(),b.getItems());
        SyncResult NMA = this.findDiff(b.getItems(),a.getItems());
        
        Debug.debug("NMB.additions=#" + NMB.additions.size());
        Debug.debug("NMB.updates=#" + NMB.updates.size());
        Debug.debug("NMB.deletes=#" + NMB.deletions.size());
        
        Debug.debug("NMA.additions=#" + NMA.additions.size());
        Debug.debug("NMA.updates==#" + NMA.updates.size());
        Debug.debug("NMA.deletes=#" + NMA.deletions.size());
        
        if(NMB.additions.size() > 0) {
            Iterator iter = NMB.additions.iterator();
            
            while(iter.hasNext()) {
                MultiSyncRecord rec = (MultiSyncRecord) iter.next();
                b.notify_addItem(rec);
            }
        }
        if(NMB.deletions.size() > 0) {
            Iterator iter = NMB.deletions.iterator();
            
            while(iter.hasNext()) {
                MultiSyncRecord rec = (MultiSyncRecord) iter.next();
                b.notify_removeItem(rec);
            }
        }
        if(NMB.updates.size() > 0) {
            Iterator iter = NMB.updates.iterator();
            
            while(iter.hasNext()) {
                MultiSyncRecord rec = (MultiSyncRecord) iter.next();
                b.notify_updateItem(rec);
            }
        }        
    }
    
}
