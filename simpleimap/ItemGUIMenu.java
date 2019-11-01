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
 * ItemGUIMenu.java
 *
 * Created on November 3, 2002, 2:03 PM
 */

package simpleimap;

import javax.swing.*;
import java.util.*;

/**
 *
 * @author  David Jeske
 */
public class ItemGUIMenu extends DefaultItem implements IRelationChangeNotification {
    public static final String TypeID = "gui.ItemGUIMenu";
    JMenu my_menu;
    private ItemRelation actionRelation = new ItemRelation("action","menu");
    
    /** Creates a new instance of ItemTemplate */
    public ItemGUIMenu() {
        super();
    }
    
    public void onActivate() {
        super.onActivate();

        my_menu = new JMenu(item_storage.name);
        
        this.item_storage.notifyOfRelationChange(this.actionRelation,this);

        this.rebuildMenu();
        

    }
    
    public JMenu getMenu() {
        return my_menu;
    }
    
    private void rebuildMenu() {
        my_menu.removeAll();
        List items = this.getRelatedItems(this.actionRelation);
        Iterator itr = items.iterator();
        while(itr.hasNext()) {
            ItemAction action = (ItemAction) itr.next();
            my_menu.add(action.getAction());
        }
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIMenu();
            }
        });
    }
    
    public void addAction(ItemAction action) {
        if(this.isRelated(WinterMute.actionMenuRelation, action)) return;
        
        this.relateTo(WinterMute.actionMenuRelation, action);
        // my_menu.add(action.getAction());
    }
    
    public void itemAddedAfter(ItemRelation relation, DefaultItem item, DefaultItem afterItem) {
        if (relation.equals(this.actionRelation)) {
            rebuildMenu();
        }
    }    
    
    public void itemRemoved(ItemRelation relation, DefaultItem item) {
        if (relation.equals(this.actionRelation)) {
            rebuildMenu();
        }
    }
    
}
