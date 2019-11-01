/*
 * ItemYahooSync.java
 *
 * Created on January 6, 2003, 12:25 PM
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
public class ItemYahooSync extends ItemSync {
    public static final String TypeID = "YahooSync";

    
    /** Creates a new instance of ItemYahooSync */
    public ItemYahooSync() {
        super();
        my_icon = new ImageIcon(ClassLoader.getSystemResource("images/yahooserver.png"));
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemYahooSync();
            }
        });
    }
    
    // ********************************************************************* 
    

    
    private class LocalYahooSyncSource extends MultiSyncSource {
        DefaultItem parent;
        
        public LocalYahooSyncSource(DefaultItem parent) {
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
    
    
    public void SyncItems(DefaultItem parent, MultiSyncSource theYahooRecords) {
        // deal with local items
        MultiSyncSource localRecords = new LocalYahooSyncSource(parent);
        
        MultiSync sync = new MultiSync();
        sync.Sync(theYahooRecords, localRecords);
        
    }
    public void buildFullNamesForAddressBook(MultiSyncSource ab_items) {
        Hashtable items = ab_items.getItems();
        
        Enumeration iter = items.elements();
        while(iter.hasMoreElements()) {
            MultiSyncRecord i = (MultiSyncRecord) iter.nextElement();
            
            PropMapType pmt = i.getItem();
            String lname = pmt.get("Family-Name");
            if(lname == null) lname = "";
            String fname = pmt.get("Given-Name");
            if(fname == null) lname = "";
            
            String name = "";
            if(lname.length() > 0 && fname.length() > 0) {
                name = fname + " " + lname;
            } else {
                name = fname + lname;
            }
            if(name.length() > 0) {
                pmt.put("Full-Name", name);
            }            
            String emailAddress = pmt.get("E-Mail-Work");
            if(emailAddress != null && emailAddress.length() > 0) {
                pmt.put("emailName", name + " <" + emailAddress + ">");
            }

        }
    }
    
    public void SyncServer() {
        String login = this.get("login");
        String password = this.get("password");
        
        // retrieve the yahoo items
       
        YahooIntellisync yi = new YahooIntellisync(login, password);

        MultiSyncSource cal_items = yi.SyncCalendar();               
        DefaultItem calendar = this.findOrCreateFolderItem("Calendar", this);
        this.SyncItems(calendar, cal_items);
 
        MultiSyncSource ab_items = yi.SyncAddressBook();        
        this.buildFullNamesForAddressBook(ab_items);
        
        DefaultItem addressbook = this.findOrCreateFolderItem("Address Book", this);
        this.SyncItems(addressbook, ab_items);
        
        MultiSyncSource todo_items = yi.SyncTodo();        
        DefaultItem todo = this.findOrCreateFolderItem("Todo List", this);
        this.SyncItems(todo, todo_items);
        
        MultiSyncSource notes_items = yi.SyncNotes();        
        DefaultItem notes = this.findOrCreateFolderItem("Notes", this);
        this.SyncItems(notes, notes_items);  
        
        
        //calendar.buildRelationships(WinterMute.containerContainsRelation);
        //addressbook.buildRelationships(WinterMute.containerContainsRelation);
        //todo.buildRelationships(WinterMute.containerContainsRelation);
        //notes.buildRelationships(WinterMute.containerContainsRelation);

    }
    
    public DefaultItem findOrCreateFolderItem(String folder, DefaultItem parent) {
        DefaultItem subitem;
        subitem = parent.getItem(WinterMute.parentChildRelation, folder);
 
        if(subitem == null) {
            subitem = (DefaultItem) item_storage.db.newItem(null, "Default", folder);
            subitem.put("name",folder);
            
            parent.addChild(subitem);
        }
        
        return subitem;
        
    }
    // ********************************************************************* 
    

}
