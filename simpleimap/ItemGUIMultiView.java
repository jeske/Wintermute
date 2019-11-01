/*
 * ItemGUIMultiView.java
 *
 * Created on January 31, 2003, 12:49 PM
 */

package simpleimap;

import guicomp.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author  David W Jeske
 */
public class ItemGUIMultiView extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUIMultiView";
    MultiView mv;
    JScrollPane mv_scroller;
    /** Creates a new instance of ItemGUIMultiView */
    public ItemGUIMultiView() {
        super();
        mv = new MultiView();
        mv_scroller = new JScrollPane(mv);
        
        mv.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        mv.setVgap(10);
        
        mv_scroller = new JScrollPane(mv);
        
    }
    
    public java.awt.Component getComponent() {
        return mv_scroller;
    }
    
    public void onActivate() {
       super.onActivate();
       initContainedItems();
    }
    
    private void initContainedItems() {
        Iterator itr = my_subitems.iterator();
        int gui_count = 0;

        Object[] subitems = my_subitems.toArray();
        
        // add them all to the multiview
        mv.removeAll(); // start fresh
        
        
        for (int i=0;i<subitems.length;i++) {
            if (subitems[i] instanceof ItemGUIInterface) {
                ItemGUIInterface gui_item = (ItemGUIInterface) subitems[i];
                
                CollapsePanel cp = new CollapsePanel();
                cp.setSubview(gui_item.getComponent());
                mv.add(cp);
            }
        }
    }
     
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIMultiView();
            }
        });
    }

    
}
