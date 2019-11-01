/*
 * ItemActionExit.java
 *
 * Created on November 17, 2002, 12:22 PM
 */

package simpleimap;

import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author  hassan
 */
public class ItemActionExit  extends  ItemAction  {
     public static final String TypeID = "gui.ItemActionExit";
   
    /** Creates a new instance of ItemActionExit */
    public ItemActionExit() {
    }
    
    public Action getAction() {
     return new AbstractAction("Exit") {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    WinterMute.exitApplication();
                }
      };
    }
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemActionExit();
            }
        });
    }
    
    
}
