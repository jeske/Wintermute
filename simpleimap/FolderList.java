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


package simpleimap;

import java.io.*;
import java.security.*;
import java.util.Properties;
import javax.mail.*;

import javax.swing.tree.*;

/**
 * Demo app that exercises the Message interfaces.
 * List information about folders using connection to mail storage.
 *
 * Based on folderlist JavaMail example by John Mani and Bill Shannon
 *
 * @author Eugen Kuleshov
 */
public class FolderList {
  static String url = null;
  static String pattern = "%";
  static boolean debug = true;

  private class IMAPMutableTreeNode extends DefaultMutableTreeNode {
      public String name;
      public IMAPMutableTreeNode(String name,Object user_object) {
          super(user_object);
          this.name = name;
      }
      
      public String toString() {
        return this.name;   
      }
  };
  
  public DefaultMutableTreeNode makeTreeModel() {
    String url = "imap://jeske@c1";
    DefaultMutableTreeNode top = new IMAPMutableTreeNode("INBOX",this);
    
    try {
    // Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider());
    // Security.setProperty( "ssl.SocketFactory.provider", "DummySSLSocketFactory");

    // final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    final String SSL_FACTORY = "simpleimap.DummySSLSocketFactory";
    
    // Get a Properties object
    Properties props = System.getProperties();
    props.setProperty( "mail.imap.socketFactory.class", SSL_FACTORY);
    props.setProperty( "mail.pop3.socketFactory.class", SSL_FACTORY);

    props.setProperty( "mail.imap.socketFactory.fallback", "false");
    props.setProperty( "mail.pop3.socketFactory.fallback", "false");

    props.setProperty( "mail.imap.port", "993");
    props.setProperty( "mail.imap.socketFactory.port", "993");

    // props.setProperty( "mail.imap.connectionpool.debug", "true");

    Session session = Session.getInstance( props);
    // session.setDebug( debug);

    URLName urln = new URLName( url);

    Store store = session.getStore(urln);
    store.connect();

    Folder[] folders = store.getPersonalNamespaces();
    
    for( int i = 0; i<folders.length; i++) {
        Folder folder = folders[i];
        top.add(makeNodesRecursive( folder ));
    }

    store.close();
   
    } catch (Exception e) {
        // error contacting server!
        System.out.println("Error contacting server " + e.toString());
    };
    
    return top;
  }
 
  private DefaultMutableTreeNode makeNodesRecursive( Folder folder ) 
    throws Exception {
    
    DefaultMutableTreeNode n = new IMAPMutableTreeNode(folder.getName(),folder);
    if(( folder.getType() & Folder.HOLDS_FOLDERS)!=0) {
      Folder[] f = folder.list();
      for( int i = 0; i<f.length; i++) {
          n.add(makeNodesRecursive(f[i]));
      }
    }
    return n;
  }

  static void dumpFolder( Folder folder, boolean recurse, String tab)
      throws Exception {
    if(( folder.getType() & Folder.HOLDS_FOLDERS)!=0)
      System.out.print( tab+"Directory: "+folder.getName());
    else 
      System.out.print( tab+"Name: " + folder.getName());
    System.out.print( " :: "+folder.getFullName());
    System.out.print( " :: "+folder.getURLName());
    System.out.println();

    if (!folder.isSubscribed())
      System.out.println( tab+"Not Subscribed");

    if(( folder.getType() & Folder.HOLDS_MESSAGES)!=0) {
      if( folder.hasNewMessages()) System.out.println( tab+"Has New Messages");
      System.out.println( tab+"Total Messages:  "+folder.getMessageCount());
      System.out.println( tab+"New Messages:    "+folder.getNewMessageCount());
      System.out.println( tab+"Unread Messages: "+folder.getUnreadMessageCount());
    }
    System.out.println();

    if( recurse) dumpRecursive( folder, recurse, tab);
  }

  static void dumpRecursive( Folder folder, boolean recurse, String tab)
      throws Exception {
    if(( folder.getType() & Folder.HOLDS_FOLDERS)!=0) {
      Folder[] f = folder.list();
      for( int i = 0; i<f.length; i++)
        dumpFolder( f[ i], recurse, tab+"  ");
    }
  }
}