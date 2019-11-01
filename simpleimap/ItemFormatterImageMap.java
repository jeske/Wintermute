package simpleimap;

import javax.swing.*;

/**
 *
 * @author  David W Jeske
 */
public class ItemFormatterImageMap extends DefaultItem implements ItemFormatter {
    public static final String TypeID = "ItemFormatterImageMap";
    ImageIcon my_icon;
    
    /** Creates a new instance of ItemField */
    public ItemFormatterImageMap() {
        super();
        my_icon = null;
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
        return my_icon;
    }
    
    public void setImage(String fn) {
        my_icon = new ImageIcon(ClassLoader.getSystemResource(fn));
        this.put("imagefn", fn);
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterImageMap();
            }
        });
    }
    
    
    public ConfigPanel getConfigPanel(DefaultItem column_config) {
        return null;
    }
}
