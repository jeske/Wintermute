/*
 * PropMap.java
 *
 * Created on January 9, 2003, 9:12 PM
 */

package simpleimap;

import java.util.*;

/**
 *
 * @author  hassan
 */
public class PropMap extends Hashtable implements PropMapType {
    
        public PropMap() {
            super();
        }
        public String get(String key) {
            return (String) super.get((Object)key);
        }
        public void put(String key, String value) {
            super.put((Object) key, (Object) value);
        }

        public java.util.LinkedList keyList() {
            java.util.LinkedList ret = new java.util.LinkedList();
            Enumeration e = this.keys();
            while(e.hasMoreElements()) {
                String key = (String) e.nextElement();
                ret.add(key);
            }
            return ret;
        }
        
}
