/*
 * PropMapType.java
 *
 * Created on January 9, 2003, 9:30 PM
 */

package simpleimap;

/**
 *
 * @author  hassan
 */
public interface PropMapType {
        public String get(String key);
        public void put(String key, String value);

        public java.util.LinkedList keyList();
}
