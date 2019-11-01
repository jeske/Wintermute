/*
 * ItemFormatterText.java
 *
 * Created on February 11, 2003, 12:32 PM
 */

package simpleimap;

/**
 *
 * @author  David W Jeske
 */
public class ItemFormatterText extends DefaultItem implements ItemFormatter {
    public static final String TypeID = "ItemFormatterText";
 
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterText();
            }
        });
    }
    /** Creates a new instance of ItemFormatterText */
    public ItemFormatterText() {
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
         return item.get(field);
    }
    
    public ConfigPanel getConfigPanel(DefaultItem column_config) {
        return null;
    }    
}
