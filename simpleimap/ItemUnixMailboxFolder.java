/*
 * ItemUnixMailboxFolder.java
 *
 * Created on November 23, 2002, 4:13 PM
 */

package simpleimap;

import javax.mail.*;
import java.util.*;
import javax.swing.SwingUtilities;

import javax.swing.table.*;

import javax.mail.internet.*;
import java.lang.ref.*;
import java.io.*;

import java.security.*;


/**
 *
 * @author  hassan
 */
public class ItemUnixMailboxFolder extends DefaultItem {
    public static final String TypeID = "UnixMailboxFolder";
    
    /** Creates a new instance of ItemUnixMailboxFolder */
    public ItemUnixMailboxFolder() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemUnixMailboxFolder();
            }
        });
    }
    
    long getSize() {
        return this.getLong("size", 0L);
    }
    
    String getMD5() {
        String hash = this.get("md5");
        return hash;
    }
    
    class SyncItem {
        String md5;
        int size;
        
        public  SyncItem(String md5, int size) {
            this.md5 = md5;
            this.size = size;
        }
        

    }
    
    public LinkedList syncToFolder(ItemUnixMailbox mailmboxitem) {
        
        String folderPath = this.get("path");
        
        MailboxFolder F1 = new MailboxFolder(folderPath);
        
        ItemUnixMailboxFolder F2 = this;
        
        boolean hasFolderChanged = false;
        
        long f1_size = F1.getSize();
        String f1_md5 = F1.getMD5();
        
        Debug.debug("sizes: " + Long.toString(f1_size) + " " + Long.toString(F2.getSize()));
        Debug.debug("md5: <" + f1_md5 + "> <" + F2.getMD5() + ">");
        
        if(f1_size != F2.getSize()) {
            hasFolderChanged = true;
        } else {
            if(!f1_md5.equals(F2.getMD5())) {
                hasFolderChanged = true;
            }
        }
        
        if(hasFolderChanged == false) return null;
        
        Debug.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Folder has changed.");
        
        
        int msgnum = 0;
        LinkedList MB1 = new LinkedList();
        Iterator iter = F1.forEach();
        
        while(iter.hasNext()) {
            msgnum++;
            MailboxMessage msg = (MailboxMessage) iter.next();
            //this.saveMessage(msg);

            SyncItem item = new SyncItem(msg.getMD5(), msg.getSize());            
            MB1.add(item);
        }
        
        LinkedList newChangedItems = this.syncToFolder2(MB1, F1, mailmboxitem);
        
        this.put("md5", f1_md5);
        this.put("size", Long.toString(f1_size));
        
        return newChangedItems;
    }
    
     public LinkedList syncToFolder2(LinkedList MB1, MailboxFolder F1, ItemUnixMailbox mailmboxitem) {
        LinkedList newChangedItems = new LinkedList();

        List msg2items = this.getRelatedItems(WinterMute.containerContainsRelation);
        Iterator iter2 = msg2items.iterator();
        LinkedList MB2 = new LinkedList();
        while(iter2.hasNext()) {
            DefaultItem item2 = (DefaultItem) iter2.next();
            SyncItem item3 = new SyncItem(item2.get("md5"), item2.getInt("size", 0));
            MB2.add(item3);
        }
        
        LinkedList NMB1 = this.findDiff(MB1, MB2);
        LinkedList NMB2 = this.findDiff(MB2, MB1);
        
        Debug.debug("NMB1=#" + NMB1.size());
        Debug.debug("NMB2=#" + NMB2.size());

        
        
        if(NMB1.size() > 0) {

            Iterator iter = F1.forEach();

            while(iter.hasNext()) {
                MailboxMessage msg = (MailboxMessage) iter.next();

                String md5 = msg.getMD5();
                int size = msg.getSize();

                Iterator iter3 = NMB1.iterator();
                boolean found = false;
                SyncItem k=null;
                while(iter3.hasNext()) {
                    k = (SyncItem) iter3.next();
                    if(size == k.size && md5.equals(k.md5)) {
                        found = true;
                        break;
                    }
                }
                if(found == true) {
                    if(k != null) NMB1.remove(k);  
                    DefaultItem newitem = this.addMessage(msg, md5, mailmboxitem);
                    if(newitem != null) newChangedItems.add(newitem);
                } else {
                    //throw new RuntimeException("cannot find element");
                }
            }
        }
        
        return newChangedItems;
    }     
    
    private LinkedList findDiff(LinkedList a, LinkedList b) {
        LinkedList ret = new LinkedList();
        Iterator iter = a.iterator();
        while(iter.hasNext()) {
            SyncItem i = (SyncItem) iter.next();
            Iterator iter2 = b.iterator();
            boolean found = false;
            while(iter2.hasNext()) {
                SyncItem j = (SyncItem) iter2.next();
                if(i.size == j.size && i.md5.equals(j.md5)) {
                    found = true;
                    break;
                }
            }
            if(found == false) {
                ret.add(i);
            }
        }
        return ret;
    }
    
    protected DefaultItem addMessage(MailboxMessage message, String md5, ItemUnixMailbox mailmboxitem) {
        //
        MimeMessage msgobj = message.getMessage();
        ItemUnixMailboxMessage item = null;
        
        try {
            String[] hdrs = { "Message-ID" };
            Enumeration e = msgobj.getMatchingHeaders(hdrs);
            String mid;
            
            int m_size = message.getSize();

            if (e.hasMoreElements()) {
                Header mid_hdr = (Header)e.nextElement();
                mid = mid_hdr.getValue();
                Debug.debug("msg: " + m_size + " : " + mid + " : " + msgobj.getSubject());
            } else {
                // no message-id, need to generate one
                Debug.debug("no MID msg: " + m_size + " : " + e.toString() + " : " + msgobj.getSubject());
                mid = "" + msgobj.getLineCount();
                // throw new MessagingException();
            }
       
            mid = mid.replace('@', '$');  // change the mid to not look like an email address.            
            String m_md5 = message.getBodyMD5();
            String unique_message_id = mid + ":md5=" + m_md5;
            
            // lookup the message.
            DefaultItem ditem = WinterMute.my_db.getItemFromName(unique_message_id);
            if(ditem != null) return null;
            
            // now, create a new message
            item = (ItemUnixMailboxMessage) WinterMute.my_db.newItem(null, ItemUnixMailboxMessage.TypeID, unique_message_id);
            item.put("size", m_size);
            item.put("md5", md5);
            item.setData(message.getData().getBytes());
            
            item.handleMessageHeaders(msgobj);

            this.relateTo(WinterMute.containerContainsRelation,item);
            mailmboxitem.relateTo(WinterMute.containerContainsRelation,item);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("MimeDecode failed!");
        }
        return item;
    }
}
