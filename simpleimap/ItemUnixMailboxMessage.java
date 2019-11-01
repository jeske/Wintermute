/*
 * ItemUnixMailboxMessage.java
 *
 * Created on January 13, 2003, 8:28 PM
 */

package simpleimap;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.lang.ref.*;
import java.io.*;


/**
 *
 * @author  hassan
 */
public class ItemUnixMailboxMessage extends ItemEmailMessage {
   
    public static final String TypeID = "UnixMailboxMessage";
    
    /** Creates a new instance of ItemTemplate */
    public ItemUnixMailboxMessage() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemUnixMailboxMessage();
            }
        });
    }
    
}
