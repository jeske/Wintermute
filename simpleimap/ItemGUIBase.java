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
 * ItemGUIBase.java
 *
 * Created on November 2, 2002, 10:34 AM
 */

package simpleimap;

import java.util.*;
import javax.swing.*;

/**
 *
 * @author  David Jeske
 */
abstract public class ItemGUIBase extends DefaultItem {
    protected List my_subitems;
    protected List my_viewed_items;
    
    ItemGUIBase() {
        super();
        my_icon = new ImageIcon(ClassLoader.getSystemResource("images/gui/frame.gif"));
    }
    
    protected void onViewedItemChange() {
        // allow subclasses to update...
    }
    protected void onActivate() {
        // we need to keep a reference to our subitems, to assure that our items don't get 
        // deactivated!
        my_subitems = this.getRelatedItems(WinterMute.parentChildRelation);
        
        // we will initialize our viewed items from the stored relation, but it is
        // NOT stored by default!!!
        my_viewed_items = new LinkedList(this.getRelatedItems(WinterMute.viewedItemViewerRelation));
        
    }
    public void addChild(DefaultItem item) {
        super.addChild(item);
        my_subitems = this.getRelatedItems(WinterMute.parentChildRelation);
    }
    
    public void clearViewedItem() {
        my_viewed_items = new LinkedList();
        
        if (my_subitems == null) {
            Debug.debug("NULL SUBITEMS");
            return;
        }
        Iterator itr = my_subitems.iterator();
        while (itr.hasNext()) {
            DefaultItem subitem = (DefaultItem) itr.next();
            if (subitem instanceof ItemGUIInterface) {
                ItemGUIInterface gui_subitem = (ItemGUIInterface) subitem;
                gui_subitem.clearViewedItem();
            }
        }
        onViewedItemChange();
        
    }
    
    public DefaultItem getViewedItem() {
        if (my_viewed_items.size() > 0) {
            return (DefaultItem)my_viewed_items.get(0);
        } else {
            return null;
        }
    }
    
    public void setViewedItem(DefaultItem item) {
        if (my_viewed_items != null && my_viewed_items.size() > 0) {
            if (my_viewed_items.get(0) == item) {
                return;
            }
        }
        my_viewed_items = new LinkedList();
        my_viewed_items.add(item);
        Iterator itr = my_subitems.iterator();
        while (itr.hasNext()) {
            DefaultItem subitem = (DefaultItem) itr.next();
            if (subitem instanceof ItemGUIInterface) {
                ItemGUIInterface gui_subitem = (ItemGUIInterface) subitem;
                gui_subitem.setViewedItem(item);
            }
        }
        
        onViewedItemChange();
    }
    
    public void addViewedItemRelation(DefaultItem item) {
        this.relateTo(WinterMute.viewedItemViewerRelation,item);
        my_viewed_items = new LinkedList(this.getRelatedItems(WinterMute.viewedItemViewerRelation));
        onViewedItemChange();
    }
    
    protected void triggerListeners(DefaultItem item) {
    // second, trigger our listeners...
       List listeners = this.getRelatedItems(WinterMute.setViewedItemSenderRelation);
       Iterator itr = listeners.iterator();
       while (itr.hasNext()) {
           DefaultItem item_listening = (DefaultItem) itr.next();
            if (item_listening instanceof ItemGUIInterface) {
                ItemGUIInterface gui_item_listening = (ItemGUIInterface) item_listening;
                if (item != null) {
                    gui_item_listening.setViewedItem(item);
                } else {
                    gui_item_listening.clearViewedItem();
                }
            }
       }
    }
    
    
}
