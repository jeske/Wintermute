/*
 * ItemActionCalendar.java
 *
 * Created on December 13, 2002, 9:02 PM
 */

package simpleimap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

/**
 *
 * @author  hassan
 */
public class ItemActionCalendar extends ItemAction {
     public static final String TypeID = "simpleimap.ItemActionCalendar";

    /** Creates a new instance of ItemActionCalendar */
    public ItemActionCalendar() {
    }
    
    public Action getAction() {
     return new AbstractAction("Calendar View") {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFrame win = new JFrame("Calendar");
                    win.getContentPane().add(new CalendarView());
                    win.pack();
                    win.show();
                   
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
