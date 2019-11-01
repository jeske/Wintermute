/*
 * ItemUnixMailbox.java
 *
 * Created on November 23, 2002, 2:02 PM
 */

package simpleimap;

import java.io.*;
import java.security.*;
import java.util.Properties;
import javax.mail.*;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.*;

/**
 *
 * @author  hassan
 *
 *  Sync Strategy:
 *
 *  - L1 is a list of folders in the mail directory
 *  - L2 is a list of folders in our db
 *  - NL1 is a list of folders in L1 that are not in L2
 *  - NL2 is a list of folders in L2 that are not in L1
 *
 *  - for each F1 folder in the NL1:
 *    - let F2 = find or create the folder F1
 *    - has the folder F1 changed
 *      - check size of F1 and md5
 *    - if changed:
 *      - scan through F1 and compute size/md5/mesgid hashes
 *        - let MB1 = add to a list of messages in the folder
 *      - let MB2 = list of messages from F2
 *      - let NMB1 be a list of messages that are in MB1 and not in MB2
 *      - let NMB2 be a list of message that are in MB2 and not in MB1 
 *      - remove messages in NMB2 from MB2
 *      - scan through F1 and add all messages from NMB1 to MB2.
 *
 */
public class ItemUnixMailbox extends ItemSync {
    public static String TypeID = "UnixMailbox";

    /** Creates a new instance of ItemUnixMailbox */
    public ItemUnixMailbox() {
       super();
       my_icon = new ImageIcon(ClassLoader.getSystemResource("images/remotehost.png"));
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemUnixMailbox();
            }
        });
    }
    public File getInboxPath() {
       return new File(this.get("inboxPath"));
    }
    
    public File getMailFolderPath() {
        return new File(this.get("mailFolderPath"));
    }
    
    // ********************************************************************* 
    
    public void onCreate() {
        Debug.debug("[" + item_storage.name + "] onCreate()");
       
    }    
    public void onActivate() {
        Debug.debug("[" + item_storage.name + "] onActivate()" );
        
    }

    // ********************************************************************* 
   

    public void SyncServer() {   
         Debug.debug("[" + item_storage.name + "] SyncServer()" );
   
         File inbox = getInboxPath();
         ItemUnixMailboxFolder subitem = findOrCreateFolderItem(inbox,this);
         LinkedList newChangedItems = subitem.syncToFolder(this);    
         
         for(Iterator iter = newChangedItems.iterator(); iter.hasNext(); ) {
             DefaultItem item = (DefaultItem) iter.next();
             // item.buildRelationships();
         }
         
         // syncSubFolders(getMailFolderPath());
    }
    
    public ItemUnixMailboxFolder findOrCreateFolderItem(File folder,DefaultItem parent) {
        ItemUnixMailboxFolder subitem;
        String can_name = folder.getName();
        
        subitem =  (ItemUnixMailboxFolder) parent.getItem(WinterMute.parentChildRelation, can_name);
 
        if(subitem == null) {        
            subitem = (ItemUnixMailboxFolder) item_storage.db.newItem(null, "UnixMailboxFolder", can_name);
            subitem.put("name",can_name);
            subitem.put("path",folder.toString());
            
            parent.addChild(subitem);
        }
        
        return subitem;
        
    }
    
    private void syncSubFolders(File path) {
        File files[];

        
        files = path.listFiles();
        
        
         for (int i=0;i<files.length;i++) {
            String fn = files[i].toString();
            File file = files[i];
            
            if(file.isHidden()) continue;
            if(!file.canRead()) continue;
            if(fn.endsWith("~")) continue;
            if(fn.endsWith(".idx")) continue;
            
            if(file.isFile()) {
                ItemUnixMailboxFolder subitem = findOrCreateFolderItem(file,this);
                subitem.syncToFolder(this);
                
                
            } else if(file.isDirectory()) {
                Debug.debug("Directory: " + file.toString());
            }
        }       

    }
    
    // ********************************************************************* 

}
