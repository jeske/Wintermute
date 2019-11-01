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
 * BGThreadManager.java
 *
 * Created on October 30, 2002, 11:24 AM
 */

package simpleimap;

import java.util.*;

/**
 *
 * @author  David Jeske
 */
public class BGThreadManager {
    Hashtable threads;
    
    
    /** Creates a new instance of BGThreadManager */
    public BGThreadManager() {
        threads = new Hashtable();
    }
    
    public void add(Thread a_thread, String title) {
        if (a_thread != null) {
            threads.put(title,a_thread);
            a_thread.start();
            System.out.println("Started thread: " + title);
        }
    }
}
