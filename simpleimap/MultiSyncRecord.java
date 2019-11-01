/*
 * MultiSyncRecord.java
 *
 * Created on January 8, 2003, 9:09 PM
 */

package simpleimap;

import java.util.*;


/**
 *
 * @author  hassan
 */
public class MultiSyncRecord {
    protected String id;
    protected PropMapType item;
    protected int lastupdated;
    
    /** Creates a new instance of MultiSyncRecord */
    public MultiSyncRecord() {
        this.id = null;
        this.lastupdated = 0;
        this.item = null;
    }
    
    public MultiSyncRecord(String id, PropMapType item, int lastupdated) {
        this.id = id;
        this.item = item;
        this.lastupdated = lastupdated;
    }
    
    public String getID() {
        return this.id;
    }
    
    public PropMapType getItem() {
        return this.item;
    }
   
    
    public boolean isDeleted() {
        if(item.get("__DELETED") != null) {
            return true;
        }
        return false;
    }
    
    public boolean isNewer(MultiSyncRecord rec) {
        if(this.lastupdated <= rec.lastupdated) {
            return true;
        }
        return false;
    }
    
    
    
}
