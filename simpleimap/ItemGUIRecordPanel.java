/*
 * ItemGUIRecordPanel.java
 *
 * This is a vertically stacked set of fields.
 *
 * Created on February 25, 2003, 4:02 PM
 */

package simpleimap;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.Point;
import java.awt.Dimension;

/**
 *
 * @author  David Jeske
 */
public class ItemGUIRecordPanel  extends ItemGUIBase implements ItemGUIInterface {
    public static final String TypeID = "gui.ItemGUIRecordPanel";
    JPanel contentView;
    DefaultItem curItem = null;
    ItemField[] displayedFields = null;
    
    /** Creates a new instance of ItemGUIRecordPanel */
    
    private class PopupLabelTextField extends JTextField {
        DefaultItem editing;
        ItemField field;
        boolean mouseIn = false;
        Border emptyBorder = BorderFactory.createEmptyBorder(1,1,1,1);
        Border editBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        Border highlightBorder = BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY);
        
        Popup myFieldNamePopup = null;
        
        PopupLabelTextField(DefaultItem editing, ItemField field) {
            super();
            
            this.editing = editing;
            this.field = field;
            
            String val = editing.get(field);
            if (val == null) { val = ""; }
            this.setText(val);
            this.setBorder(emptyBorder);
            
            this.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent fe) {
                    checkState();
                }
                public void focusLost(FocusEvent fe) {
                    checkState();
                }
            });
            
            this.addMouseListener(new MyMouseListener());
            
            // catch move events
            this.addComponentListener(new MyCompListener());
            this.addHierarchyBoundsListener(new MyHBL());
            
            // catch change events
            this.getDocument().addDocumentListener(new MyDocumentListener());
            
        }
        
        private class MyHBL implements HierarchyBoundsListener {
            public void ancestorMoved(HierarchyEvent e) {
                relocatePanel();
            }
            public void ancestorResized(HierarchyEvent e) {
            }            
        }
        private class MyCompListener implements ComponentListener {
            public void componentHidden(ComponentEvent e) {
            }
            public void componentMoved(ComponentEvent e) {
                relocatePanel();
            }
            public void componentResized(ComponentEvent e) {
            }
            public void componentShown(ComponentEvent e) {
            }
            
        }
        
        private void checkState() {
            boolean showPopup;
            if (this.hasFocus()) {
                setBorder(editBorder);
                showPopup = true;
            } else {
                if (mouseIn) {
                    setBorder(highlightBorder);
                    showPopup = true;
                } else {
                    setBorder(emptyBorder);
                    showPopup = false;
                }
            }
            if (showPopup) {
                if (myFieldNamePopup == null) {
                    Point p = this.getLocationOnScreen();
                    PopupFactory factory = PopupFactory.getSharedInstance();
                    JLabel label = new JLabel(field.get("name"));
                    label.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                    Dimension ps = label.getPreferredSize();
                    myFieldNamePopup = factory.getPopup(this, label, p.x - ps.width,p.y);
                    myFieldNamePopup.show();
                }
            } else {   
                if (myFieldNamePopup != null) {
                    myFieldNamePopup.hide();
                    myFieldNamePopup = null;
                }
            }
        }
        private void relocatePanel() {
            if (myFieldNamePopup != null) {
                Popup old = myFieldNamePopup;
                myFieldNamePopup = null;
                checkState();
                old.hide();
            }
        }
        
        private void writeBack() {
            editing.put(field,this.getText());
        }
        
        private class MyDocumentListener implements DocumentListener {
            public void changedUpdate(DocumentEvent e) {
                writeBack();
            }
            public void insertUpdate(DocumentEvent e) {
                writeBack();
            }
            public void removeUpdate(DocumentEvent e) {
                writeBack();
            }
            
        }
        
        private class MyMouseListener implements MouseListener {
            public void mouseClicked(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
                mouseIn = true;
                checkState();
            }
            public void mouseExited(MouseEvent e) {
                mouseIn = false;
                checkState();
            }
            public void mousePressed(MouseEvent e) {
            }
            public void mouseReleased(MouseEvent e) {
            }
            
        }
        
        
    }
    
    public ItemGUIRecordPanel() {
        contentView = new JPanel();
        contentView.setLayout(new guicomp.VerticalStackedLayout());
        contentView.setName("ItemGUIRecordPanel");
    }
    
    public void onActivate() {
        displayedFields = new ItemField[3];
        displayedFields[0] = WinterMute.my_db.getCommonField("Name");
        displayedFields[1] = WinterMute.my_db.getCommonField("address");
        displayedFields[2] = WinterMute.my_db.getCommonField("phone");
    }
    
    public java.awt.Component getComponent() {
        return contentView;
    }
    
    public void setViewedItem(DefaultItem item) {
        java.util.List subitems; 
        DefaultItem target = null;
        if (item != null) {
            // email -> addr
            // addr -> contact
            subitems = item.getRelatedItems(WinterMute.messageEmailAddressRelation);
            if (subitems.size() > 0) {
                subitems = ((DefaultItem)subitems.get(0)).getRelatedItems(WinterMute.emailAddressContactRelation);
                if (subitems.size() > 0) {
                    target = (DefaultItem)subitems.get(0);
                }
            }
        }
        
        curItem = target;
        initFields();
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIRecordPanel();
            }
        });
    }    
    
    private void initFields() {
        contentView.removeAll();
        if (curItem == null) {
            contentView.setSize(0,0);
            contentView.setName("none");
        } else {
            contentView.setName(curItem.get("Name"));
            // ItemField[] farr = curItem.getCachedFields();
            ItemField[] farr = displayedFields;
            for (int i=0;i<farr.length;i++) {
                contentView.add(new PopupLabelTextField(curItem,farr[i]));
            }
            contentView.revalidate();
        }
    }
}
