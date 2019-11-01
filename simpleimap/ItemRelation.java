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
 * ItemRelation.java
 *
 * Created on November 1, 2002, 7:08 AM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class ItemRelation {
    private String src,dest;
    // source(flags), destination(flags)
    /** Creates a new instance of ItemRelation */
    public ItemRelation(String src,String dest) {
        this.src = src;
        this.dest = dest;
    }
    public ItemRelation(String spec) {
        // need to decode and check spec...
        String[] parts = spec.split(":");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid RelationSpec: " + spec);
        }
        this.src = parts[0];
        this.dest = parts[1];
        
    }
    public String toString() {
        return "ItemRelation[" + getSpec() + "]";
    }
    
    public ItemRelation invert() {
        return new ItemRelation(dest,src);
    }
    
    public String getSpec() {
        return src + ":" + dest;
    }
    
    public String getSource() { return this.src; }
    public String getDestination() { return this.dest; }
    
    public boolean equals(ItemRelation ir) {
        return this.getSpec().equals(ir.getSpec());
    }
    
}
