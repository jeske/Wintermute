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
 * ItemTemplate.java
 *
 * Created on November 1, 2002, 3:47 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class ItemTemplate extends DefaultItem {
    public static final String TypeID = "com.neotonic.example.ItemTemplate";
    
    /** Creates a new instance of ItemTemplate */
    public ItemTemplate() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemTemplate();
            }
        });
    }
}
