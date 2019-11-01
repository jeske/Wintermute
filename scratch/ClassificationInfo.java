/*
 * Wintermute - Personal Data Organizer
 *
 * Copyright (C) 2002, by David Jeske and Neotonic Software Corporation.
 *
 * Written by David Jeske <jeske@neotonic.com>.
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * ClassificationInfo2.java
 *
 * Created on October 27, 2002, 7:45 PM
 */

package packrat;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import java.util.*;
import java.io.Serializable;
import packrat.*;

/**
 *
 * @author  David Jeske
 */
   public class ClassificationInfo implements Serializable {
     public String name;
     public boolean isType;
     public List propertyList; // string
     public Hashtable propertyValues; // string->string
     
     public ClassificationInfo parent;
     public List childList; // ClassificationInfo
             
     public DataStore ds;
     private int ciID;

     // this should eventually be transient...
     private List itemList; 
     
     public List items() {
         return itemList;
     }
     
     private ClassificationInfo(DataStore a_ds) {
         // connect to the datastore
         ds = a_ds;
         ciID = ds.addClassificationInfo(this);
         
         childList = new LinkedList();
         propertyList = new LinkedList();
         propertyValues = new Hashtable();
         itemList = new LinkedList();
     }
     public ClassificationInfo(DataStore a_ds,String name) {
         this(a_ds);
         this.name = name;

     }
     
     public ClassificationInfo(DataStore a_ds,String name,boolean isType) {
         this(a_ds);
         this.name = name;
         this.isType = isType;
         
     }
       
     
     public void addChild(ClassificationInfo newChild) {
         childList.add(newChild);
         newChild.I_setParent(this);
     }
     
     private void I_setParent(ClassificationInfo newParent) {
         this.parent = newParent;
     }
     
     public String toString() {
          return this.name;
     }
     
     public LinkedList inheritedProperties() {
         LinkedList a_list = new LinkedList();
         if (parent != null) {
            a_list.addAll(parent.inheritedProperties());
         }
         a_list.addAll(propertyList);
         return a_list;
     }
     public TableModel makeTableModelForItems() {
        DefaultTableModel m = new DefaultTableModel();
        m.addColumn("Items");
        Iterator itr = itemList.iterator();
        DefaultItem item;
        Object[] foo = { null };
        while (itr.hasNext()) {
             item = (DefaultItem) itr.next();
             foo[0] = item.body;
             m.addRow(foo);            
            
        }
        
        return m;
     }
     
     public TableModel makeTableModelForProperties() {
         DefaultTableModel m = new DefaultTableModel();
         m.addColumn("Property");
         m.addColumn("Value");
         
         LinkedList totalList = inheritedProperties();
         
         Iterator itr = totalList.iterator();
         String name;
         Object[] foo = { null, null};
         while (itr.hasNext()) {
             name = (String) itr.next();
             foo[0] = name;
             if (propertyValues.containsKey(name)) {
                 foo[1] = propertyValues.get(name);
             } else {
                 foo[1] = "";
             }
             m.addRow(foo);
         }
                 
         return m;
     }
     
     public DefaultMutableTreeNode makeTreeModel() {
        DefaultMutableTreeNode m = new DefaultMutableTreeNode(this);
        
        ClassificationInfo ci_obj;
        Iterator itr = childList.iterator();
        while (itr.hasNext()) {
          ci_obj=(ClassificationInfo)itr.next();
          m.add(ci_obj.makeTreeModel());
        }
        return m;
        
     }
    };
