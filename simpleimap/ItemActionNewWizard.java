/*
 * ItemActionNewWizard.java
 *
 * Created on November 17, 2002, 12:16 PM
 */

package simpleimap;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author  hassan
 */
public class ItemActionNewWizard extends ItemAction {
    public static final String TypeID = "gui.ItemActionNewWizard";

    /** Creates a new instance of ItemActionNewWizard */
    public ItemActionNewWizard() {
    }
    
    public Action getAction() {
     return new AbstractAction("New Account...") {
         public void actionPerformed(java.awt.event.ActionEvent e) {
             new NewServerWizard().show();
         }
       };
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemActionNewWizard();
            }
        });
    }
      
}
