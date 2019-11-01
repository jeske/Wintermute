/*
 * ItemField.java
 *
 * This is the item type that represents a field id
 *
 * Created on November 18, 2002, 2:25 PM
 */

package simpleimap;

/**
 *
 * @author  David W Jeske
 */
public class ItemField extends DefaultItem {
    public static final String TypeID = "Field";
    
    /** Creates a new instance of ItemField */
    public ItemField() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemField();
            }
        });
    }
    
    public String getTreeNodeName() {
        return getFieldName();
    }
    
    public String toString() {
        if (item_storage != null) {
            return this.getFieldName();
        } else {
            return "UninitializedItem";
        }
    }
    
    public String getFieldName() {
        // Field/name
        String fieldname = this.get("name");
        if (fieldname == null) { return ""; }
        if(fieldname.length() > 6 && fieldname.charAt(5) == '/') {
            fieldname = fieldname.substring(6);
        }
        return fieldname;
    }
    protected void onPropChange(ItemField field, String oldval, String newval) {
    }
}
