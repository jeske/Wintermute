/*
 * ItemDefault.java
 *
 * This is one simple type of item which can be instantiated....
 *
 * Created on November 18, 2002, 1:25 PM
 */

package simpleimap;

import javax.swing.AbstractAction;
import java.util.*;

// filechooser action 
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.text.*;
import java.awt.Dimension;

/**
 *
 * @author  David W Jeske
 */
public class ItemDefault extends DefaultItem {
    public static final String TypeID = "Default";
    
    /** Creates a new instance of ItemDe-fault */
    public ItemDefault() {
        super();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemDefault();
            }
        });
    }
    
    ///////////////// ACTIONS /////////////////////////////////////////////
    
    
     class CSVImportAction extends AbstractAction {
        DefaultItem item;
        CSVImportAction(DefaultItem item) {
            super("CSV Import...");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            try {
                UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Create a file chooser
            JFileChooser chooser = new JFileChooser();
            ShortcutsAccessory shortcuts = new ShortcutsAccessory(chooser, "demo");
            chooser.setAccessory(shortcuts);
            Dimension d = new Dimension(700, 400);
            chooser.setMinimumSize(d);
            chooser.setPreferredSize(d);
            //chooser.showOpenDialog(null);

            //JFileChooser fc = new JFileChooser();

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                Thread_ImportCSV t = new Thread_ImportCSV(item, file);
                t.start();
            }
        }
    };
 
    class MySQL_ImportAction extends AbstractAction {
        DefaultItem item;
        MySQL_ImportAction(DefaultItem item) {
            super("MySQL_ImportAction");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
           new MySQL_ImportWizard(null, false, this.item).show();
        }
    };
    
         
    class AddContainedItemAction extends AbstractAction {
        DefaultItem item;
        AddContainedItemAction(DefaultItem item) {
            super("Add Contained Item");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // add item - swh
            DefaultItem newitem = WinterMute.my_db.newItem(null, "Default", null);
            
            item.addItem(newitem);
        }
    };
    
     class ICal_ImportAction extends AbstractAction {
        DefaultItem item;
        ICal_ImportAction(DefaultItem item) {
            super("ICal_ImportAction");
            this.item = item;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
           new ICal_ImportWizard(null, false, this.item).show();
        }
    };
    
    public List getActions() {
        List actions = super.getActions();
        
        actions.add(new AddContainedItemAction(this));
        actions.add(new CSVImportAction(this));
        actions.add(new MySQL_ImportAction(this));
        actions.add(new RDF_ImportAction(this));
        actions.add(new ICal_ImportAction(this));
        
        return actions;
    }
    
    
}
