/*
 * Wintermute - Personal Data Organizer
 * 
 * Copyright (C) 2002, by David Jeske
 *
 * Written by David Jeske <jeske@neotonic.com>. 
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * DefaultItemTableModel.java
 *
 * Created on November 1, 2002, 10:34 AM
 */

package simpleimap;

import javax.swing.table.*;
/**
 *
 * @author  David Jeske
 */

public class DefaultItemTableModel extends AbstractTableModel {
        DefaultItem item;
        String[] col_names;
        public DefaultItemTableModel(DefaultItem item,String[] col_names) {
            this.item = item;
            this.col_names = col_names;
        }
        public String getColumnName(int col) {
            return col_names[col];
        }
        public int getColumnCount() { return col_names.length; }
        public int getRowCount() { return item.itemCount(); }
        public Object getValueAt(int row,int col) {
            DefaultItem subitem = item.getItemAt(row);
            String a_col = col_names[col];
            return subitem.get(a_col);
        }
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        public DefaultItem getItem(int row) {
            return (DefaultItem) item.getItemAt(row);
        }
        //public void setValueAt(Object value, int row, int col) {
        //}

    };
