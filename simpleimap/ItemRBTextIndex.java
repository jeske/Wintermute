/*
 * ItemRBTextIndex.java
 *
 * Created on February 24, 2003, 5:21 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class ItemRBTextIndex extends ItemRBBase {
    public static final String TypeID = "base.ItemRBTextIndex";
    
    /** Creates a new instance of ItemRBTextIndex */
    public ItemRBTextIndex() {
    }
    
    public void processItemChanges(DefaultItem item) {
        WinterMute.indexmgr.indexItem(item);
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemRBTextIndex();
            }
        });
    }    
    
    
}
