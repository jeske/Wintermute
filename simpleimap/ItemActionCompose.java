/*
 * ItemActionCompose.java
 *
 * Created on November 17, 2002, 2:17 PM
 */

package simpleimap;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author  hassan
 */
public class ItemActionCompose extends ItemAction {
     public static final String TypeID = "gui.ItemActionCompose";

    /** Creates a new instance of ItemActionCompose */
    public ItemActionCompose() {
    }
    
   public Action getAction() {
     return new AbstractAction("Compose...") {
         public void actionPerformed(java.awt.event.ActionEvent e) {
             new ComposeMessageFrame().show();
         }
       };
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemActionCompose();
            }
        });
    }    
}
