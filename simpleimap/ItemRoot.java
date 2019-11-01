/*
 * ItemRoot.java
 *
 * Created on November 18, 2002, 1:38 PM
 */

package simpleimap;

import javax.swing.*;
import java.util.*;

/**
 *
 * @author  David W Jeske
 */
public class ItemRoot extends DefaultItem {
    public static final String TypeID = "Root";
    
    /** Creates a new instance of ItemRoot */
    public ItemRoot() {
         super();
         my_icon = new ImageIcon(ClassLoader.getSystemResource("images/localhost.png"));
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemRoot();
            }
        });
    }
    
    /////////////////// ACTIONS /////////////////////////////
    
      public List getActions() {
        List actions = new LinkedList();
        
        actions.add(new AddItemAction(this));

        return actions;
    }
    
}
