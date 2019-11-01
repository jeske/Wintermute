/*
 * ItemFormatterAddressedToFlag.java
 *
 * Created on January 17, 2003, 5:29 PM
 */

package simpleimap;


import java.util.*;
import java.text.*;
import java.util.regex.*;

/**
 *
 * @author  hassan
 */
public class ItemFormatterAddressedToFlag extends DefaultItem  implements ItemFormatter {
    public static final String TypeID = "ItemFormatterAddressedToFlag";

    
    /** Creates a new instance of ItemField */
    public ItemFormatterAddressedToFlag() {
        super();        
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
        String status = null;
        
        if (item instanceof ItemEmailMessage) {
            ItemEmailMessage msgitem = (ItemEmailMessage) item; 
            
            String toField = msgitem.get("To");
            String ccField = msgitem.get("Cc");
            
            Pattern emailPat = Pattern.compile("(hassan(-[a-zA-Z0-9]*)?@dotfunk.com|scott@hassan.com)", Pattern.CASE_INSENSITIVE);
            
            if(toField != null) {
                Matcher m = emailPat.matcher(toField);
                if(m.find() == true) {
                    return "To";
                }
            }
            if(ccField != null) {
                Matcher m2 = emailPat.matcher(ccField);
                if(m2.find() == true) {
                    return "cc";
                }
            }
                        
        }
        return " ";
    }
    
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterAddressedToFlag();
            }
        });
    }
    
    public ConfigPanel getConfigPanel(DefaultItem column_config) {
        return null;
    }
       
}

