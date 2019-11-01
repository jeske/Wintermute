/*
 * ItemFormatterDateType.java
 *
 * Created on February 10, 2003, 6:55 PM
 */

package simpleimap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;

/**
 *
 * @author  David W Jeske
 */
public class ItemFormatterDateType extends DefaultItem implements ItemFormatter {
    public static final String TypeID = "ItemFormatterDateType";
    /** Creates a new instance of ItemFormatterDateType */
    DateFormat datefmt;

    
    
    public ItemFormatterDateType() {
        super();
        datefmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        // sdf = new SimpleDateFormat("EEE 
    }
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterDateType();
            }
        });
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
        String format_string = column_config.get("DateFormat");
        String data = item.get(field);
        if (data == null) { return null; }
        
        long time_t;
        try {
            time_t = Long.parseLong(data);
        } catch (java.lang.NumberFormatException e) {
            return data;
        }
        
        if (time_t == 0) {
            return "none";
        }
        
        Date dt = new Date(time_t);
        
        if (format_string == null) {
            return datefmt.format(dt);
        } else {
            return getFormatter(format_string).format(dt);
        }
    }
    private String cached_format_str = null;
    private SimpleDateFormat cached_formatter = null;
    private SimpleDateFormat getFormatter(String format) {
        try {
            if (cached_format_str == null || !cached_format_str.equals(format)) {
                cached_format_str = format;
                cached_formatter = new SimpleDateFormat(format);
            }
        } catch (Exception e) {
            cached_format_str = format;
            cached_formatter = new SimpleDateFormat("'error : " + format + "'");
        }
        return cached_formatter;
    }
    
    ///////////////////////////////////////////////////
    //
    // ConfigPanel support
    
    class MyConfigPanel extends ItemFormatter.ConfigPanel {
        String[] formats = { "M/d/y",
                     "MM/dd/yy",
                     "dd-MMM",
                     "dd-MMM-yy",
                     "yyyy.MM.dd",
                     "yyyy.MM.dd hh:mm a" };
        
        String cur_format;
                     
        DefaultListModel dlm;
        JList formatList;
        
        DefaultItem my_column_config;
        MyConfigPanel(DefaultItem column_config) {
            this.my_column_config = column_config;
            // start with a sample date.
            // "NOW" is as good as any
            
            cur_format = my_column_config.get("DateFormat");
            if (cur_format == null) {
                cur_format = formats[0];
            }
            
            Date now = new Date();
            
            // list of date formats
            this.setLayout(new BorderLayout());

            dlm = new DefaultListModel();
            for (int i=0;i<formats.length;i++) {
                dlm.addElement(formats[i]);
            }
            
            formatList = new JList(dlm);
            ListSelectionModel lsm = formatList.getSelectionModel();
            lsm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent  evt) {
                selectionChanged(evt);
            } });
            
            this.add(formatList,BorderLayout.CENTER);
        }
        
        private void selectionChanged(ListSelectionEvent evt) {
            ListSelectionModel lsm = formatList.getSelectionModel();
            if (!lsm.isSelectionEmpty()) {
                int selectionindex = lsm.getMinSelectionIndex();
                cur_format = formats[selectionindex];
            }
            
            Debug.debug("Cur Format: " + cur_format);
            this.my_column_config.put("DateFormat", cur_format);
        }
        
    }
    
    
    public ConfigPanel getConfigPanel(DefaultItem column_config) {
        MyConfigPanel cp = new MyConfigPanel(column_config);
        return cp;
    }
    
}
