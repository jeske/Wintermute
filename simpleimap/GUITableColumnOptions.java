/*
 * GUITableColumnOptions.java
 *
 * Created on February 10, 2003, 12:19 PM
 */

package simpleimap;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.BorderLayout;

import javax.swing.event.*;
import java.awt.event.*;
import java.beans.*;

/**
 *
 * @author  David W Jeske
 */
public class GUITableColumnOptions  extends JDialog  {
    
    DefaultItem my_column;
    JTable formattable;
    JPanel formatDetails;
    
    
    // appearance
    JTextField headingLabelField;
    
    
    // listeners
    private class MyDL implements DocumentListener {
        DefaultItem column;
        String fieldname;
        JTextField field;
        MyDL(DefaultItem my_column,String fieldname,JTextField field) {
            this.column = my_column;
            this.fieldname = fieldname;
            this.field = field;
        }
 
        private void changed() {
            column.put(fieldname,field.getText());
        }
        public void changedUpdate(DocumentEvent e) {
            changed();
        }
        
        public void insertUpdate(DocumentEvent e) {
            changed();
        }
        
        public void removeUpdate(DocumentEvent e) {
            changed();
        }
        
    }
    
    private class MyAL implements ActionListener {
        DefaultItem column;
        String fieldname;
        String newvalue;
        MyAL(DefaultItem column,String fieldname,String newvalue) {
            this.column = column;
            this.fieldname = fieldname;
            this.newvalue = newvalue;
        }
        public void actionPerformed(ActionEvent e) {
            column.put(fieldname,newvalue);
        }
        
    }
    
    /** Creates a new instance of GUITableColumnOptions */
    public GUITableColumnOptions(DefaultItem my_column, JDialog owner) {
        super(owner);
        this.my_column = my_column;  // the column to edit
        
        initComponents();
        
        this.setTitle("Configure: " + my_column.get("name"));
        String title = my_column.get("Title");
        if (title == null) {
            title = my_column.get("name");
            if (title == null) { title = ""; }
        }
        
        this.headingLabelField.setText(title);
        this.headingLabelField.getDocument().addDocumentListener(new MyDL(my_column,"Title",headingLabelField));
    }
    
    private void initComponents() {
        java.awt.Container root = getContentPane();
        
        JTabbedPane tabs = new JTabbedPane();
        
        ////////////////////////////////////////
        //
        // Format tab
        //
        
        // left side
        JPanel formatPanel = new JPanel();
        formatPanel.setLayout(new BorderLayout());
        
        // jtable of formatters
        DefaultItem formatters = WinterMute.my_db.rootNode().getItem(WinterMute.parentChildRelation,"Formatters");
        ItemTableModel ftm = formatters.makeTableModelForRelation(WinterMute.containerContainsRelation);
        formattable = new JTable();
        formattable.setModel(ftm);
       
        JScrollPane tscroll = new JScrollPane(formattable);
        tscroll.getViewport().setBackground(java.awt.Color.white);
        formatPanel.add(tscroll,BorderLayout.CENTER);
        
        formattable.setRowSelectionAllowed(true);
        ListSelectionModel rowSM = formattable.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // figure out current formatter
        List cur_fl = my_column.getRelatedItems(WinterMute.colconfigFormatterRelation);
        
        if (cur_fl.size() > 0) {
            DefaultItem cur_f = (DefaultItem) cur_fl.get(0);
            // select current formatter
            int curindex = ftm.getItemRowIndex(cur_f);
            // Debug.debug("Formatter oid: " + );
            if (curindex != -1) {
                rowSM.addSelectionInterval(curindex,curindex);
            }
        } else {
            // select the Raw Text Formatter
            
        }
        
        // listen to selection changes
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent  evt) {
                selectionChanged(evt);
            }
        });
        
        
        // right side
        formatDetails = new JPanel();
        formatDetails.setLayout(new BorderLayout());
        formatDetails.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        
        JSplitPane formatsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                   formatPanel, formatDetails);
        formatsplit.setResizeWeight(0.3);
        formatsplit.setBorder(BorderFactory.createEmptyBorder());
        tabs.add("Format", formatsplit);
        
        //
        /////////////////////////////////////////
        //
        // Appearance
        //
        
        JPanel sal = new JPanel();
        sal.setLayout(new guicomp.VerticalStackedLayout());
        
        // heading label
        JPanel headingPanel = new JPanel();
        headingPanel.setBorder(BorderFactory.createTitledBorder("Heading"));
        headingPanel.setLayout(new guicomp.VerticalStackedLayout());
        sal.add(headingPanel);
        
        headingLabelField = new JTextField();
        headingPanel.add(headingLabelField);
        
        // size
        JPanel sizePanel = new JPanel();
        sizePanel.setBorder(BorderFactory.createTitledBorder("Size"));
        sal.add(sizePanel);
        
        ButtonGroup s_bg = new ButtonGroup();
        sizePanel.setLayout(new guicomp.VerticalStackedLayout());
        JRadioButton arb;
        
        String sizeMethod = my_column.get("SizeMethod");
        if (sizeMethod == null) { sizeMethod = "auto"; }
        
        arb = new JRadioButton("Best Fit");
        s_bg.add(arb);
        sizePanel.add(arb);
        if (sizeMethod.equals("auto")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column,"SizeMethod", "auto"));
        
        arb = new JRadioButton("Specific Width: ");
        s_bg.add(arb);
        sizePanel.add(arb);
        if (sizeMethod.equals("fixed")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column,"SizeMethod",  "fixed"));
        
        arb = new JRadioButton("Minimum Width");
        s_bg.add(arb);
        sizePanel.add(arb);
        if (sizeMethod.equals("min")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column,"SizeMethod",  "min"));
        
        // alignment
        String alignment = my_column.get("Alignment");
        if (alignment == null) { alignment = "Center"; }
        
        JPanel alignPanel = new JPanel();
        alignPanel.setBorder(BorderFactory.createTitledBorder("Alignment"));
        sal.add(alignPanel);

        alignPanel.setLayout(new java.awt.GridLayout(1,3));
        ButtonGroup a_bg = new ButtonGroup();
        
        arb = new JRadioButton("Left");
        arb.setHorizontalAlignment(JRadioButton.CENTER);
        a_bg.add(arb);
        alignPanel.add(arb);
        if (alignment.equals("Left")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column, "Alignment",  "Left"));
       
        arb = new JRadioButton("Center");
        arb.setHorizontalAlignment(JRadioButton.CENTER);
        a_bg.add(arb);
        alignPanel.add(arb);
        if (alignment.equals("Center")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column, "Alignment", "Center"));
        
        arb = new JRadioButton("Right");
        arb.setHorizontalAlignment(JRadioButton.CENTER);
        a_bg.add(arb);
        alignPanel.add(arb);
        if (alignment.equals("Right")) { arb.setSelected(true); }
        arb.addActionListener(new MyAL(my_column,"Alignment", "Right"));
        
        tabs.add("Appearance", sal);
        //
        ////////////////////////////////////////////

        root.add(tabs);
        pack();
    }
    
    private void selectionChanged(ListSelectionEvent e) {
        DefaultItem selectedItem = null;
        Debug.debug("GUITableColumnOptions: select row");
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;
        
        ItemFormatter.ConfigPanel cp = null;
        
        ListSelectionModel lsm =
            (ListSelectionModel)formattable.getSelectionModel();
       
        if (!lsm.isSelectionEmpty()) {
            int selectedRow = lsm.getMinSelectionIndex();
            TableModel tm = formattable.getModel();
            if (tm instanceof ItemTableModel) {
                ItemTableModel itm = (ItemTableModel)tm;
                selectedItem = itm.getItem(selectedRow);
                
                if (selectedItem instanceof ItemFormatter) {
                    ItemFormatter ifmt = (ItemFormatter) selectedItem;
                    
                    // make this new config panel active
                    cp = ifmt.getConfigPanel(my_column);
                    
                    // make this new formatter active
                    my_column.unrelateFromAll(WinterMute.colconfigFormatterRelation);
                    my_column.relateTo(WinterMute.colconfigFormatterRelation, selectedItem);
                    
                    // HACK , this will refresh the column model.
                    
                    my_column.put("ping","pong");
                } 
            }
        }
        
        formatDetails.removeAll();
        if (cp != null) {
            formatDetails.add(cp);
        } else {
            JLabel lbl = new JLabel("no config panel available");
            lbl.setHorizontalAlignment(JLabel.CENTER);
            formatDetails.add(lbl);
        }
        formatDetails.revalidate();
    }

    
}
