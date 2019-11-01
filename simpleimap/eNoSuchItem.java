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
 * eNoSuchItem.java
 *
 * Created on October 29, 2002, 6:08 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class eNoSuchItem extends RuntimeException {
    public String info;
    
    /** Creates a new instance of eNoSuchItem */
    public eNoSuchItem() {
        this.info = "";
    }
    public eNoSuchItem(String info) {
        this.info = info;
    }
    public String toString() {
        return "Exception(eNoSuchItem): " + info;
    }
}
