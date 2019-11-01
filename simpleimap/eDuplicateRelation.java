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
 * eDuplicateRelation.java
 *
 * Created on November 2, 2002, 7:07 AM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class eDuplicateRelation extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>eDuplicateRelation</code> without detail message.
     */
    public eDuplicateRelation() {
    }
    
    
    /**
     * Constructs an instance of <code>eDuplicateRelation</code> with the specified detail message.
     * @param msg the detail message.
     */
    public eDuplicateRelation(String msg) {
        super(msg);
    }
}
