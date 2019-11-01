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
 * ItemGUIInterface.java
 *
 * Created on November 2, 2002, 8:21 AM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
public interface ItemGUIInterface {
    java.awt.Component getComponent();
    public void setViewedItem(DefaultItem item);
    public void clearViewedItem();
}
