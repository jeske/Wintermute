/*
 * ItemGUIHTML.java
 *
 * Created on November 11, 2002, 3:16 PM
 */

package simpleimap;

import javax.swing.*;

/**
 *
 * @author  David W Jeske
 */
public class ItemGUIHTML extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUIHTML";
    private JEditorPane contentView;
    
    /** Creates a new instance of ItemGUIHTML */
    public ItemGUIHTML() {
        super();
        contentView = new JEditorPane();
        contentView.setBorder(BorderFactory.createEmptyBorder());
        contentView.setEditable(false);
        contentView.setContentType("text/html");   
    }
    
    public void onActivate() {
        contentView.setName(this.get("name"));
    }
    
    public void setViewedItem(DefaultItem item) {
        // this should run a template system like ClearSilver or WebMacro...
        java.util.List subitems;
        String newtext = "";
        
        if (item != null) {
            // email -> addr
            // addr -> contact
            subitems = item.getRelatedItems(WinterMute.messageEmailAddressRelation);
            if (subitems.size() > 0) {
                subitems = ((DefaultItem)subitems.get(0)).getRelatedItems(WinterMute.emailAddressContactRelation);
                if (subitems.size() > 0) {
                    DefaultItem contactitem = (DefaultItem)subitems.get(0);
                    newtext = contactitem.get("Name");
                    
                    double num = java.lang.Math.random();
                    int lines = (int)(num * 5.0);
                    for (int i=0;i<lines;i++) {
                        newtext = newtext + "<br>" + i + " random line" ;
                    }
                }
            }
            
            
        }
        contentView.setText(newtext);
        contentView.setSize(0, 0);
        contentView.revalidate();
        contentView.setCaretPosition(0);
    }
    
    public java.awt.Component getComponent() {
        return contentView;
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIHTML();
            }
        });
    }
    
}
