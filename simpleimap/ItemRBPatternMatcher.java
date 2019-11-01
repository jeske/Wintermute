/*
 * ItemRBPatternMatcher.java
 *
 * Created on February 24, 2003, 6:11 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public class ItemRBPatternMatcher extends ItemRBBase {
    public static final String TypeID = "base.ItemRBPatternMatcher";

    /** Creates a new instance of ItemRBPatternMatcher */
    public ItemRBPatternMatcher() {
    }
    
    public void processItemChanges(DefaultItem item) {
        RelationshipBuilder.rbuilder.buildRelationships(item);
    }
      public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemRBPatternMatcher();
            }
        });
    }    
    
}
