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
 * DefaultItemMutableTreeNode.java
 *
 * Created on November 1, 2002, 10:28 AM
 */

package simpleimap;

import javax.swing.tree.*;
/**
 *
 * @author  David Jeske
 */
public class DefaultItemMutableTreeNode extends DefaultMutableTreeNode {
      public String name;
      public DefaultItemMutableTreeNode(String name,Object user_object) {
          super(user_object);
          this.name = name;
      }
      
      
      public String toString() {
        return this.name;   
      }
  };
