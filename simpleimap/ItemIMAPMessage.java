
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
 * ItemIMAPMessage.java
 *
 * Created on November 1, 2002, 3:47 PM
 */

package simpleimap;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.lang.ref.*;
import java.io.*;

/**
 *
 * @author  David Jeske
 */
public class ItemIMAPMessage extends ItemEmailMessage {
    public static final String TypeID = "IMAP.ItemIMAPMessage";
    ItemIMAPFolder folder;
    
    /** Creates a new instance of ItemTemplate */
    public ItemIMAPMessage() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemIMAPMessage();
            }
        });
    }
    
    public ItemIMAPFolder getFolder() {
        if (folder == null) {
            List imap_folder_list = this.getRelatedItems(WinterMute.containsContainerRelation);
            
            if (imap_folder_list.size() < 1) {
                throw new RuntimeException("ItemIMAPMessage.getFolder(): I have no IMAP Folder!!!!");
            }
            for(int i=0;i<imap_folder_list.size();i++) {
                DefaultItem fit = (DefaultItem) imap_folder_list.get(i);
                if (fit instanceof ItemIMAPFolder) {
                    folder = (ItemIMAPFolder)fit;
                    return folder;
                } 
                Debug.debug("container is not folder! : " + fit.get_oid() + ":" + fit.get("name"));
            }
        }
        return folder;
    }
    
    public void requestDataFetch() {
        getFolder().fetchMessageData(this);
    }
    
    public boolean isDataOffline() {
        byte[] mydata = super.getData();
        
        if (mydata == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public byte[] getData() {
        //if (isDataOffline()) {
        //    requestDataFetch();   // should we always do this?
        //}
        return super.getData();
    }
}
