/*
 * ItemSync.java
 *
 * Created on January 9, 2003, 12:14 AM
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
public abstract class ItemSync  extends DefaultItem {
    public static String TypeID = "ItemSync";

    private MySyncThread sync_thread=null;
    
    /** Creates a new instance of ItemSync */
    public ItemSync() {
        super();
        this.sync_thread=null;
    }
    
    public void SyncServer() {
    }
    
    // ********************************************************************* 
    
    class MySyncThread extends Thread {
        ItemSync server;
        MySyncThread(ItemSync server) {
            this.server = server;
            this.setDaemon(true);
            this.setPriority(1);
            
            String name = server.getClass().getName();
            
            if(name != null) this.setName(name);
        }
        
        public void run() {
            try {
                server.SyncServer();
            } catch (Exception e) {
                Debug.debug(e);
            }
            
            server.sync_thread = null;
        }
        
    }
    
    public void startSyncThread() {
        if(sync_thread == null) {
            sync_thread = new MySyncThread(this);
            sync_thread.start();
        }
    }
    
    
    class MyAction extends AbstractAction {
        ItemSync srv;
        MyAction(ItemSync srv) {
            super("Sync");
            this.srv = srv;
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            srv.startSyncThread();
        }
    }
    
    public List getActions() {
        List actions = super.getActions();
        actions.add(new MyAction(this));
        return actions;      
    }    
    
        
}
