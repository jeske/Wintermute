/*
 * ItemRBManager.java
 *
 * Created on February 24, 2003, 5:12 PM
 */

package simpleimap;

import java.util.*;

/**
 *
 * @author  David Jeske
 */
public class ItemRBManager extends DefaultItem  {
    public static final String TypeID = "base.ItemRBManager";
    public static final boolean bRelationBuilderEnabled = true;
    
    private MyRBManagerThread rbThread;
    private ItemRelation itemsToProcessRelation = new ItemRelation("RBManager","itemsToProcess");
    
    
    /** Creates a new instance of ItemRBManager */
    public ItemRBManager() {
        rbThread = new MyRBManagerThread();
        rbThread.start();
    }
 
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemRBManager();
            }
        });
    } 

    /////////////////////////////////
    //
    // handleItem
    //
    // must return "true" if it did work
    // "false" if it did NOT do work
    private boolean handleNext() {
        List itemoids = this.getRelatedItemOIDs(itemsToProcessRelation);
        if (itemoids.size() > 0) {
            int first_oid = ((Integer)itemoids.get(0)).intValue();
            DefaultItem item = this.item_storage.db.getItem(first_oid);

            Debug.debug("ItemRBManager: process changes to: " + item.get("name"));
            
            // for each registered item relation processor
            List rbProcessors = this.getRelatedItems(WinterMute.containerContainsRelation);
            for (Iterator itr = rbProcessors.iterator();itr.hasNext();) {
                ItemRBBase rber = (ItemRBBase) itr.next();
                rber.processItemChanges(item);
            }
            
            // take this item off the list!
            this.unrelateFrom(itemsToProcessRelation, item);
            this.put("itemsToProcessCount", "" + this.getRelatedItemOIDs(itemsToProcessRelation).size());
            return true; // did work!
        }
        
        return false; // no work
    }
    public void enqueueItem(DefaultItem item) {
        if (!bRelationBuilderEnabled) { return;  }
        
        if (item != this) {
            // add to list of items to process
            this.relateToOnce(itemsToProcessRelation,item);
            this.put("itemsToProcessCount", "" + this.getRelatedItemOIDs(itemsToProcessRelation).size());
        }
        rbThread.wakeup();
    }
    
    
    
    private class MyRBManagerThread extends Thread {
        MyRBManagerThread() {
            this.setName("MyRBManagerThread");
            this.setPriority(4);
            this.setDaemon(true);
        }
        public void wakeup() {
            synchronized (this) {
                notifyAll();
            }
        }
        public void run() {
            boolean did_work;
            
            while (true) {
                // do stuff!
                did_work = false;
                try {
                    did_work = handleNext();
                } catch (Exception e) {
                    Debug.debug(e);
                    Debug.debug("sleeping...");
                    try {
                        sleep(10000);
                    } catch (java.lang.InterruptedException ie) {
                        Debug.debug("sleep interrupted..");
                    }
                }

                if (!did_work) {
                     synchronized (this) {
                        Debug.debug("sleeping...");
                        try { 
                            wait(10000); 
                            Debug.debug("wakeup.");
                        } catch (java.lang.InterruptedException ie) {
                            Debug.debug("interrupt.");
                        }
                     }

                }
                                
            }
        }
    }
}
