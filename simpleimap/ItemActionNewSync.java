/*
 * ItemActionNewSync.java
 *
 * Created on January 6, 2003, 12:32 PM
 */

package simpleimap;


import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author  hassan
 */
public class ItemActionNewSync extends ItemAction {
    public static final String TypeID = "gui.ItemActionNewSync";
    
    /** Creates a new instance of ItemActionNewSync */
    public ItemActionNewSync() {
        super();
    }
    public Action getAction() {
     return new AbstractAction("New Sync...") {
         public void actionPerformed(java.awt.event.ActionEvent e) {
             new NewSyncWizard().show();
             
         }
       };
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemActionNewSync();
            }
        });
    }    
    
}
