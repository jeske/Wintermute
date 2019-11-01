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
 * ItemIMAPServer.java
 *
 * Created on November 1, 2002, 9:35 AM
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
 * @author  David Jeske
 */
public class ItemIMAPServer extends ItemSync implements ItemIMAP {
    public static String TypeID = "IMAPServer";

    
    String server_url;
    private Store my_server; // don't use this directly!!
    private Thread msg_fetch_thread = null;
    
    /** Creates a new instance of ItemIMAPServer */
    public ItemIMAPServer() {
        super();
        my_icon = new ImageIcon(ClassLoader.getSystemResource("images/remotehost.png"));
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemIMAPServer();
            }
        });
    }
    
    public boolean isConnected() {
        if (my_server == null) {
            return false;
        } else {
            return true;
        }
    }
    
    private synchronized Store myServer() {
        if (my_server != null) {
            return my_server; 
        } else {
            my_server = connectToServer();
            return my_server;
        }
    }
    

    
    public void onCreate() {
        Debug.debug("[" + item_storage.name + "] onCreate()");
       
    }
  
    
    public void onActivate() {
        Debug.debug("[" + item_storage.name + "] onActivate()" );
        
    }
    
    // ItemSync call-in Interface from syncthread
    public void SyncServer() {
        syncSubFolders();
        syncFolderHeaders();   
    }
    
    public ItemIMAPFolder findOrCreateFolderItem(Folder folder,DefaultItem parent) {
        ItemIMAPFolder subitem;
        String can_name = server_url + folder.getFullName();
        
        try {
            int oid = item_storage.db.getOIDFromName(can_name);
            subitem = (ItemIMAPFolder) item_storage.db.getItem(oid);
            // we already have the node!
        } catch (eNoSuchItem e) {
            subitem = (ItemIMAPFolder) item_storage.db.newItem(null, "IMAPFolder", can_name);
            subitem.put("name",folder.getName());
            subitem.put("fullname",folder.getFullName());
            
            parent.addChild(subitem);
        }
        
        return subitem;
        
    }
    
    
    
    // This syncs the header contents and builds relationships
    public void syncFolderHeaders() { syncFolderHeaders(null); }
    public void syncFolderHeaders(ItemIMAPFolder curfolder) {
        // server.connect();
        try {
            Folder[] folders;
            if (curfolder == null) {
                folders = myServer().getPersonalNamespaces();
            } else {
                folders = curfolder.getFolder().listSubscribed();
            }

            for( int i = 0; i<folders.length; i++) {
                Folder folder = folders[i];
                ItemIMAPFolder subitem = findOrCreateFolderItem(folder,this);
                
                subitem.doFolderSync();
                syncFolderHeaders(subitem);
                // subitem.buildRelationships(WinterMute.containerContainsRelation);

            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // server.close();
        
        
    }

    
    // This is just to sync the folder tree
    public void syncSubFolders() { syncSubFolders(null); }
    public void syncSubFolders(ItemIMAPFolder curfolder) {
        // server.connect();
        try {
            Folder[] folders;
            if (curfolder == null) {
                folders = myServer().getPersonalNamespaces();
            } else {
                folders = curfolder.getFolder().listSubscribed();
            }

            for( int i = 0; i<folders.length; i++) {
                Folder folder = folders[i];
                ItemIMAPFolder subitem = findOrCreateFolderItem(folder,this);
                syncSubFolders(subitem);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // server.close();
    }
    
    public Store connectToServer() {
        Store store;
        try {
            // Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider());
            // Security.setProperty( "ssl.SocketFactory.provider", "DummySSLSocketFactory");

            server_url = "imap://" + this.get("login") + ":" + this.get("password") + "@" + this.get("server");

            Properties props = System.getProperties();
            
            if ("true".equals(this.get("use_ssl"))) {
                server_url += ":993/";
                
                // final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                final String SSL_FACTORY = "simpleimap.DummySSLSocketFactory";

                // Get a Properties object

                props.setProperty( "mail.imap.socketFactory.class", SSL_FACTORY);
                props.setProperty( "mail.pop3.socketFactory.class", SSL_FACTORY);

                props.setProperty( "mail.imap.socketFactory.fallback", "false");
                props.setProperty( "mail.pop3.socketFactory.fallback", "false");

                props.setProperty( "mail.imap.port", "993");
                props.setProperty( "mail.imap.socketFactory.port", "993");

                // props.setProperty( "mail.imap.connectionpool.debug", "true");

            } else {
                server_url += "/";
            }
            
            Session session = Session.getInstance( props);
            // session.setDebug( debug);

            URLName urln = new URLName( server_url);

            store = session.getStore(urln);
            Debug.debug("connecting to server...");
            store.connect();
            Debug.debug("connected.");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("unable to connect to imap server: " + server_url);
        };
        
        return store;
    }
    
    public ItemIMAPServer getServer() {
        return this;
    }
    
    public Folder getFolderObject(String folder_full_path) {
        try {
            return myServer().getFolder(folder_full_path);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("ItemIMAPServer:getFolderObject(" + folder_full_path +"): unable to find folder");
        }
    }
}
