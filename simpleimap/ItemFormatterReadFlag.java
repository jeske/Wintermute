/*
 * ItemFormatterReadFlag.java
 *
 * Created on January 16, 2003, 1:41 PM
 */

package simpleimap;

import javax.swing.*;


/**
 *
 * @author  hassan
 */
public class ItemFormatterReadFlag extends DefaultItem implements ItemFormatter {
    public static final String TypeID = "ItemFormatterReadFlag";
    ImageIcon new_icon;
    ImageIcon read_icon;
    ImageIcon deleted_icon;
    ImageIcon blank_icon;
    
    /** Creates a new instance of ItemField */
    public ItemFormatterReadFlag() {
        super();
        new_icon = new ImageIcon(ClassLoader.getSystemResource("images/mail-new.png"));
        read_icon = new ImageIcon(ClassLoader.getSystemResource("images/mail-read.png")); 
        deleted_icon = new ImageIcon(ClassLoader.getSystemResource("images/delete.gif"));       
        blank_icon = new ImageIcon(ClassLoader.getSystemResource("images/dot_clear.gif"));
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
        String status = null;
        
        if (item instanceof ItemEmailMessage) {
            ItemEmailMessage msgitem = (ItemEmailMessage) item;  
            status = msgitem.getStatus();
        }
        
        if(status != null) {
            if(status.equals("Read")) return read_icon;
            else if(status.equals("Deleted")) return deleted_icon;
            else if(status.equals("Seen")) return blank_icon;
        }
        return new_icon;
    }
    
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterReadFlag();
            }
        });
    }
    
    public ConfigPanel getConfigPanel(DefaultItem column_config) {
        return null;
    }    
       
}
