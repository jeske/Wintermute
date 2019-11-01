/*
 * WMSecureProvider.java
 *
 * Created on February 24, 2003, 4:11 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class WMSecureProvider extends java.security.Provider {
    
    /** Creates a new instance of WMSecureProvider */
    public WMSecureProvider() {
        super("WMSecureProvider",1.0,"fast SSL keys");
    }
    
    
}
