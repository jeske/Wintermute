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
 * ItemIMAPFolder.java
 *
 * Created on November 1, 2002, 9:35 AM
 */

package simpleimap;

import javax.mail.*;
import java.util.*;
import javax.swing.SwingUtilities;

import javax.swing.table.*;
// this dependency shouldn't exist
// import com.sleepycat.je.*;

import javax.swing.AbstractAction;


import javax.mail.internet.*;
import java.lang.ref.*;
import java.io.*;

/**
 *
 * @author  David Jeske
 */
public class ItemIMAPFolder extends DefaultItem implements ItemIMAP {
    SimpleDB db;
    ItemIMAPServer item_imap_server;
    
    WeakReference message_fetch_thread;
    
    
    /** Creates a new instance of ItemIMAPFolder */
    public ItemIMAPFolder() {
        super();
    }
    public void onActivate() {
        db = item_storage.db;
        message_fetch_thread = new WeakReference(null);
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory("IMAPFolder", new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemIMAPFolder();
            }
        });
    }
    
    public Folder getFolder() {
        ItemIMAPServer server = getServer();
        // we need to be sure to get a new folder object so threading works!
        return server.getFolderObject(this.get("fullname"));
        
    };
    
    
    private void syncFolderNow() {
        if (false) {
            Thread syncthread = syncThread();
            if (syncthread != null) {
                syncthread.start();
            }
        }
    }
    
    public ItemTableModel makeTableModel() {
        if (this.getServer().isConnected()) {
            syncFolderNow();
        }
        return super.makeTableModel();
    }
    
    public ItemIMAPServer getServer() {
        if (item_imap_server == null) {
            List parents = this.getRelatedItems(new ItemRelation("child","parent"));
            if (parents == null || parents.size() < 1) {
                throw new RuntimeException("ItemIMAPFolder has no parent!");
            }
            ItemIMAP parent = (ItemIMAP)parents.get(0);
            item_imap_server = (ItemIMAPServer)parent.getServer();
        }
        return item_imap_server;
        
    }
    
    private class MyMessageFetchThread extends Thread {
        ItemIMAPFolder f;
        LinkedList queued_messages;
        MyMessageFetchThread(ItemIMAPFolder f) {
            this.f = f;
            this.setDaemon(true);
            this.setPriority(5);
            this.setName("MyMessageFetchThread");
            queued_messages = new LinkedList();
        }
        
        public void enqueue(ItemIMAPMessage msg) {
            
            synchronized (queued_messages) {
                if (!queued_messages.contains(msg)) {
                    if (false) {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            Debug.debug("***************** ENQUEUE REQUEST : " + msg.get_oid() + " *************");
                            Debug.debug(e);    
                        }
                    }
                    
                    queued_messages.addFirst(msg);
                } else {
                    if (false) {
                        Debug.debug("**** ENQUEUE of object already fetching: " + msg.get_oid());
                    }
                }
            }
            
            synchronized (this) {
                notifyAll();
            }
        }
        public void run() {
            ItemIMAPMessage next_fetch = null;
            while (true) {
                synchronized (queued_messages) {
                    if (queued_messages.size() > 0) {
                        next_fetch = (ItemIMAPMessage) queued_messages.get(0);
                    }
                }
                if (next_fetch != null) {
                    try {
                        f.do_fetchMessageData(next_fetch);
                        Debug.debug("Fetched oid: " + next_fetch.get_oid() + " - " + next_fetch.get("Subject"));
                        synchronized (queued_messages) {
                            queued_messages.remove(next_fetch);
                        }
                        next_fetch = null;
                    } catch (Exception e) {
                        Debug.debug("Exception fetching message");
                        Debug.debug(e);
                        try {
                            synchronized (this) {
                                wait(10000);
                            }
                        } catch (java.lang.InterruptedException e2) {
                            // pass
                        }
                    }
                    
                } else {
                    try {
                        synchronized (this) {
                            Debug.debug("sleeping...");
                            wait(10000);
                            Debug.debug("wakeup.");
                        }
                    } catch (java.lang.InterruptedException e) {
                        // good, we were interrupted!
                    }
                }
            }
        }
    }

    private MyMessageFetchThread fetchthread = null;
    public void fetchMessageData(ItemIMAPMessage msg) {
        if (fetchthread == null) {
            fetchthread = new MyMessageFetchThread(this);
            fetchthread.start();
        }
        fetchthread.enqueue(msg);

    }
    private void do_fetchMessageData(ItemIMAPMessage msg) {
        Folder f = getFolder();
        
        // this was causing every thread to connect!
        if (false) {
            // be sure to connect if we need to...
            Store s = f.getStore();
            if (!s.isConnected()) {
                try {
                    s.connect();
                } catch (Exception e) {
                    Debug.debug(e);
                    return;
                }
            }
        }

        UIDFolder folder = (UIDFolder)f;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String uid_validity = msg.get("UID_VALIDITY." + this.get_oid());
        
        if (uid_validity == null) {
            // the requester should probably catch this and ask another folder if
            // possible...
            throw new RuntimeException("Unable to fetch message, missing UID_VALIDITY info");
        } 
        String[] parts = uid_validity.split(":");
        
        long validity = Long.parseLong(parts[0]);
        long m_uid = Long.parseLong(parts[1]);
        
        try {
            if (validity != folder.getUIDValidity()) {
                throw new RuntimeException("the message has an out of sync UID validity!");
            }
            f.open(Folder.READ_ONLY);
            Message m = folder.getMessageByUID(m_uid);
            m.writeTo(data);
            msg.setData(data.toByteArray());
            // msg.save(); // save the data!
            f.close(false);
        } catch (IOException e2) {
            // unable to get message data
            e2.printStackTrace();
        } catch (MessagingException e3) {
            e3.printStackTrace();
        }
    }
    
    // -------------------------------------------------------
    // utility functions
    
        private void addItemFromMessage(javax.mail.Message m,String uid_validity) throws MessagingException {
        
            int m_size = m.getSize();
            
            String[] hdrs = { "Message-ID" };
            Enumeration e = m.getMatchingHeaders(hdrs);
            String mid;
            
            if (e.hasMoreElements()) {
                Header mid_hdr = (Header)e.nextElement();
                mid = mid_hdr.getValue();
                Debug.debug("msg: " + m_size + " : " + mid + " : " + m.getSubject());
            } else {
                // no message-id, need to generate one
                Debug.debug("no MID msg: " + m_size + " : " + e.toString() + " : " + m.getSubject());
                mid = "" + m.getLineCount();
                // throw new MessagingException();
                
            }
            mid = mid.replace('@', '$');  // change the mid to not look like an email address.
            
            String unique_message_id = mid + ":size=" + m_size;
            ItemIMAPMessage item;
            
            try {
                int oid = db.getOIDFromName(unique_message_id);
                
                // we already know this item, but we should update uid_validity
                if (uid_validity != null) {
                    item = (ItemIMAPMessage)db.getItem(oid);
                    
                    if (item.contains("UID_VALIDITY")) {
                        if (!item.get("UID_VALIDITY").equals(uid_validity)) {
                            item.put("UID_VALIDITY." + this.get_oid(), uid_validity);
                            //item.save();
                        }
                    } else {
                        // it's missing UID_VALIDITY so update!
                        item.put("UID_VALIDITY." + this.get_oid(), uid_validity);
                        //item.save();
                    }
                    
                    // .. and make sure it's in this folder!
                    this.addItem(item);
                }
                
                
            } catch (eNoSuchItem er) {
                item = (ItemIMAPMessage) db.newItem(null,ItemIMAPMessage.TypeID,unique_message_id);
                
                Flags flags = m.getFlags();
                
                if(flags.contains(Flags.Flag.SEEN) == true) {
                    item.setMessageSeen();
                }
                
                if(flags.contains(Flags.Flag.DELETED) == true) {
                    item.setMessageDeleted();                    
                }     
                
                if (flags.contains(Flags.Flag.ANSWERED) == true) {
                    item.setMessageAnswered();
                }
                
                item.handleMessageHeaders(m);
                
                
                if (uid_validity != null) {
                    item.put("UID_VALIDITY." + this.get_oid(), uid_validity);
                }
                
                //item.save();
                
                addItem(item);
                
            }

        }

    
    
    // -------------------------------------------------------
    // background thread operations!
    
        // split an array of longs into a List of arrays by chunksize....
        List split_long_arr(long[] arr,int chunksize) {
            int x = 0;
            List chunked_arrays = new LinkedList();
            while (x < arr.length) {
                int remaining_items = arr.length - x;
                long[] new_arr;
                int fill_size;
                if (remaining_items < chunksize) {
                    fill_size = remaining_items;
                } else {
                    fill_size = chunksize;
                }
                new_arr = new long[fill_size];
                for (int i=0;i<fill_size;i++) {
                    new_arr[i] = arr[x];
                    x++;
                }
                chunked_arrays.add(new_arr);
            }
            return chunked_arrays;
        }
        
       synchronized public void syncToUIDFolder(UIDFolder uid_folder) {
        
        Folder a_folder = (Folder)uid_folder;
        FetchProfile fp;
        
        Debug.debug("syncToUIDFolder: starting");
        
        long uid_validity;
        
        try {
           
            // first, we need to get back a list of UIDs for the folder..
            Hashtable folder_uids = new Hashtable();
            a_folder.open(Folder.READ_ONLY);
            uid_validity = uid_folder.getUIDValidity();
            fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            Message[] messages = a_folder.getMessages();
            a_folder.fetch(messages,fp);
            for (int i=0;i<messages.length;i++) {
                Message m = messages[i];
                long m_uid = uid_folder.getUID(m);
                
                String validity_token = "" + uid_validity + ":" + m_uid;
                
                Debug.debug("Folder UID item '" + validity_token + "'");
                
                folder_uids.put(validity_token,"" + m_uid);
                
            }
            Debug.debug("");
            
            
            // second, we should compare those with the UIDs we have...
            List oid_list = this.containedItemOIDs();
            Iterator itr = oid_list.iterator();
            List toRemoveList = new LinkedList();
            while (itr.hasNext()) {
                int oid = ((Integer)itr.next()).intValue();
                try {
                    DefaultItem item = db.getItem(oid);
                    String item_uid_validity = item.get("UID_VALIDITY." + this.get_oid());
                    if (item_uid_validity != null) {
                        Debug.debug("UID VALIDITY: " + item_uid_validity + " ");
                        
                        if (folder_uids.containsKey(item_uid_validity)) {
                            // we already have the item in the folder!
                            folder_uids.remove(item_uid_validity);
                            Debug.debug("FOUND");
                        } else {
                            // the item is no longer in the folder...
                            toRemoveList.add(item);
                            Debug.debug("NOTFOUND");
                        }
                    } 
                } catch (eNoSuchItem e) {
                    Debug.debug("no such OID: " + oid);
                    // pass
                }
            }
            
            // remember to remove the items we are supposed to remove
            itr = toRemoveList.iterator();
            while (itr.hasNext()) {
                DefaultItem remove_item = (DefaultItem)itr.next();
                
                removeItem(remove_item);
                
            }
            
            // third, build an array of UIDs to fetch
            Iterator fitr = folder_uids.values().iterator();
            long[] fetch_new_uids = new long[folder_uids.values().size()];
            int n = 0;
            while (fitr.hasNext()) {
                long uid = Long.parseLong((String)fitr.next());
                fetch_new_uids[n] = uid;
                n = n + 1;
                Debug.debug("Should fetch UID = " + uid);
                
            }
            
            // fourth, fetch the details for these Messages
            
            List uid_chunks = split_long_arr(fetch_new_uids,50);
            
            Iterator uid_chunk_itr = uid_chunks.iterator();
            while (uid_chunk_itr.hasNext()) {
            
                
                long[] fetch_uids = (long[])uid_chunk_itr.next();
                
                Debug.debug("Fetching UIDs: " + SimpleDB.pp(fetch_uids));
                
                messages = uid_folder.getMessagesByUID(fetch_uids);
                Debug.debug("Fetching done. ");
                fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(FetchProfile.Item.CONTENT_INFO);
                fp.add("Message-ID");
                a_folder.fetch(messages,fp);
                for (int i=0;i<messages.length;i++) {
                    Message m = messages[i];
                    long m_uid = uid_folder.getUID(m);
                    String validity_token = "" + uid_validity + ":" + m_uid;
                    addItemFromMessage(m,validity_token);
                    
                }
            }
            
        } catch (MessagingException e) {
          Debug.debug("Error accessing Folder " + a_folder.getName() + ":" + e.toString());
          return;
        } finally {
            try {
                a_folder.close(false);
            } catch (MessagingException e) {
                // pass 
            }
        }
        
    }
    
    
    
    private class SyncThread extends Thread {
        ItemIMAPFolder folder;
        SyncThread(ItemIMAPFolder a_folder) {
            this.folder = a_folder;
            this.setDaemon(true);
            
            String name = folder.getClass().getName();
            if(name != null) this.setName(name);
        }
        public void run() {
            folder.doFolderSync();
        }
    }
    
    public void doFolderSync() {
        Folder imap_folder = this.getFolder();
        this.syncToFolder(imap_folder);
    }
    
    WeakReference last_sync_thread;
    
    public Thread syncThread() {
        if (last_sync_thread == null || last_sync_thread.get() == null) {
            Thread syncthread = new SyncThread(this);
            last_sync_thread = new WeakReference(syncthread);
            Debug.debug("Created SyncThread");
            return syncthread;
        } else {
            Debug.debug("Didn't create SyncThread");
            return null;
        }       
    }
    
    public void syncFolderTreeRecurse() {
        ItemIMAPFolder parent = this;
        ItemIMAPServer server = getServer();
        Folder f = getFolder();
        
        try {
            Folder[] subfolders = f.listSubscribed();

            for (int i=0;i<subfolders.length;i++) {
                ItemIMAPFolder subitem = server.findOrCreateFolderItem(subfolders[i],parent);
                subitem.syncFolderTreeRecurse();
            }
        } catch (MessagingException e) {
            // pass
        }
    }
    
    synchronized public void syncToFolder(javax.mail.Folder a_folder) {
        // we need to resolve each message with the 
        // "REAL" version of the message, and then make sure it 
        // is in our OID list...
        
        try {
            syncToUIDFolder((UIDFolder)a_folder);
            return;
        } catch (java.lang.ClassCastException e) {
            // pass
            
        }
        Debug.debug("syncToFolder: starting");
        
        // otherwise use the normal sync code!
        
        try {
            a_folder.open(Folder.READ_ONLY);
            Message[] messages = a_folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add("Message-ID");
            a_folder.fetch(messages,fp);
            Object[] header_line = { null, null, null };
            for (int i=0;i<messages.length;i++) {
                Message m = messages[i];
                // compute our message "unique ID"
                
                addItemFromMessage(m,null);
            }
        } catch (MessagingException e) {
            Debug.debug("Error accessing Folder " + a_folder.getName() + ":" + e.toString());
        } finally {
            try {
                a_folder.close(false);
            } catch (MessagingException e) {
                // pass 
            }
        }
        
    }
    private class SyncFolderAction extends AbstractAction {
        ItemIMAPFolder myfolder;
        SyncFolderAction(ItemIMAPFolder a_folder) {
            super("Sync");
            myfolder = a_folder;
        }
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(java.awt.event.ActionEvent e) {
            myfolder.syncFolderNow();
        }
        
    };
    public List getActions() {
        List actions = super.getActions();

        actions.add(new SyncFolderAction(this));
        
        return actions;
    }    
    
}
