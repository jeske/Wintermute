/*
 * ItemActionWipeDB.java
 *
 * Created on December 3, 2002, 10:38 AM
 */

package simpleimap;

import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author  hassan
 */
public class ItemActionWipeDB extends ItemAction {
     public static final String TypeID = "simpleimap.ItemActionWipeDB";
    
    /** Creates a new instance of ItemActionWipeDB */
    public ItemActionWipeDB() {
    }
    

    public Action getAction() {
     return new AbstractAction("Wipe Database") {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    WinterMute.my_db.finalize();
                    WinterMute.my_db.wipeDatabase();
                    
                    System.exit(0);
                }
      };
    }
    
    public static void register(SimpleDB db) {       
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                try {
                    Class c = Class.forName(TypeID);
                    DefaultItem i = (DefaultItem) c.newInstance();
                    return i;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("cannot create class: " + TypeID);
                }
            }
        });
    }    
    
}
