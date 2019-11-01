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
 * ItemFactory.java
 *
 * Created on November 1, 2002, 7:42 AM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public abstract class ItemFactory {
    protected SimpleDB db;
    
    /** Creates a new instance of ItemFactory */
    public ItemFactory(SimpleDB db) {
        this.db = db;
    }
    abstract public DefaultItem construct();
}
