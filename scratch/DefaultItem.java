/*
 * Wintermute - Personal Data Organizer
 *
 * Copyright (C) 2002, by David Jeske and Neotonic Software Corporation.
 *
 * Written by David Jeske <jeske@neotonic.com>.
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * DefaultItem.java
 *
 * Created on October 28, 2002, 8:39 AM
 */

package packrat;

import java.lang.*;
import java.util.*;
/**
 *
 * @author  David Jeske
 */
public class DefaultItem {
    public String body;
    public List classifications; // ClassificationInfo
    
    /** Creates a new instance of DefaultItem */
    public DefaultItem(String body) {
        this.body = body;
        this.classifications = new LinkedList();
    }
    
    public void addToClassification(ClassificationInfo ci) {
        if (!classifications.contains(ci)) {
            classifications.add(ci);
            ci.items().add(this);
        }
        
        
    }
}
