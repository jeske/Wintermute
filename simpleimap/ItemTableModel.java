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
 * ItemTableModel.java
 *
 * Created on November 1, 2002, 3:14 PM
 */

package simpleimap;

import javax.swing.table.*;

/**
 *
 * @author  David Jeske
 */
public interface ItemTableModel extends TableModel {
    public DefaultItem getItem(int row);
    public void setColumnModel(DefaultItem colconfig, ItemTableModel tm);
    public TableColumnModel getTableColumnModel();
    public int getItemRowIndex(DefaultItem item);
    
    // these are needed to save back state about stuff like the current selected item
    public DefaultItem getSourceItem();
    public ItemRelation getSourceRelation();
    public TableModel getSourceModel();
}
