/*
 * SimpleItemRelationTreeModel.java
 *
 * uses ItemTreeNodeInterface
 *
 * All change notifications arrive by watching SimpleItemRelationTableModel. 
 * It has fancy machinery for synchronizing changes with the AWT thread, so we
 * don't have to worry about any of that here! - jeske
 *
 * TODO: we need to remove items from the active_items cache when they are
 *       found to be invalid. - jeske
 *
 * Created on February 27, 2003, 10:29 AM
 */

package simpleimap;

import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;
/**
 *
 * @author  David Jeske
 */

///////////////////////////////////////////////////////////////////////////
// SimpleItemRelationTreeModel
///////////////////////////////////////////////////////////////////////////

public class SimpleItemRelationTreeModel implements TreeModel {

    private Vector treeModelListeners = new Vector();

    DefaultItem rootItem;
    SimpleItemTreeNode rootNode;
    ItemRelation relation;
    
    Hashtable active_nodes;  // Hashtable<Integer oid> = <SimpleItemTreeNode>
    
    SimpleItemRelationTreeModel(ItemRelation relation,DefaultItem rootItem) {
        active_nodes = new Hashtable();
        this.relation = relation;
        
        this.rootItem = rootItem;
        this.rootNode = nodeForItem(rootItem);
    }
    
    private SimpleItemTreeNode nodeForItem(DefaultItem item) {
        SimpleItemTreeNode node;
        synchronized (active_nodes) {
            node = (SimpleItemTreeNode) active_nodes.get(item);
            if (node == null) {
              node = new SimpleItemTreeNode(this,item,relation);
              active_nodes.put(item,node);
            }
        }
        return node;
    }
    
    //////////////// TreeNode /////////////////////////////////////////////////
    //
    // This is necessary for us to keep handles on TableModels
    //
    protected class SimpleItemTreeNode implements ItemTreeNodeInterface, TableModelListener  {
        DefaultItem item;
        ItemTableModel tblmdl;
        SimpleItemRelationTreeModel owner;
        SimpleItemTreeNode(SimpleItemRelationTreeModel owner, DefaultItem item, ItemRelation relation) {
            this.owner = owner;
            this.item = item;
            
            this.tblmdl = item.makeTableModelForRelation(relation);
            tblmdl.addTableModelListener(this); // notice changes!
        }
        
        public javax.swing.Icon getIcon() {
            return item.getIcon();
        }
        public String toString() {
            return "" + item.get_oid() + ":" + item.get("name");
        }
        public String getTreeNodeName() {
            return item.getTreeNodeName();
        }
        
        public ItemTableModel getRelationModel() {
            return tblmdl;
        }
        
        public DefaultItem getTreeItem() {
            return item;
        }
        
        public void tableChanged(TableModelEvent e) {
            if (!validateNode(this)) {
                Debug.debug("SimpleItemTreeNode.tableChanged(" + e.toString() + ") -- node invalid");
                return;
            } else {
                Debug.debug("SimpleItemTreeNode.tableChanged(" + e.toString() + ")");
            }
            
            int type = e.getType();
            int first_row = e.getFirstRow();
            int last_row = e.getLastRow();
            
            if (type == TableModelEvent.INSERT) {
                Debug.debug("added " + first_row);
                owner.fireTreeNodeAdded(this, first_row);
            } else if (type == TableModelEvent.UPDATE) {
                Debug.debug("changed " + first_row);
                owner.fireTreeNodeChanged(this, first_row);
            } else if (type == TableModelEvent.DELETE) {
                Debug.debug("removed " + first_row);
                owner.fireTreeNodeRemoved(this, first_row);
            }
            
        }
        
    }
    
    ///////////////////////////////
    // This checks to see if the node is still in the tree by
    // finding it's top parent and figuring out if it is the
    // root node of this tree or not.
    // 
    // TODO: remove from the active_nodes cache if necessary
    // 
    // Returns:
    //    "true" if the node is still value
    //    "false" if the node is no longer valid
    
    private boolean validateNode(SimpleItemTreeNode node) {
        List nodes = buildPathOfNode(node, null);
        if (nodes.get(0) != this.rootNode) {
            Debug.debug("Tree parent no longer valid: " + node.toString());
            return false;
        } else {
            return true;
        }
    }

    // TODO: buildPathOfNode should check for and avoid dataset cycles
    
    private List buildPathOfNode(SimpleItemTreeNode node, LinkedList a_list) {
        SimpleItemTreeNode parent_node = getParent(node);
        if (a_list == null) { a_list = new LinkedList(); }
        if (parent_node != null) {
            buildPathOfNode(parent_node,a_list);
        }
        a_list.add(node);
        return a_list;
    }

 

    private SimpleItemTreeNode getParent(SimpleItemTreeNode node) {
        DefaultItem obj = node.getTreeItem();
        ItemRelation parent_relation = relation.invert();
        List parent_nodes = obj.getRelatedItemOIDs(parent_relation);
        if (parent_nodes.size() != 0) {
            int oid = ((Integer)parent_nodes.get(0)).intValue();

            try {
                DefaultItem parentitem = WinterMute.my_db.getItem(oid);
                // parentitem.getItemStorage().noticeTreeModel(relation,this);
                return this.nodeForItem(parentitem);
            } catch (eNoSuchItem e) {
                throw new RuntimeException("can't find item inside TreeModel");
            }
        }

        return null;
    }


    ///////////// interface ////////////////////////////////

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }


    public Object getChild(Object parent, int index) {
        SimpleItemTreeNode parent_item = (SimpleItemTreeNode) parent;
        DefaultItem displayeditem = parent_item.getRelationModel().getItem(index);
        return nodeForItem(displayeditem);
    }

    public int getChildCount(Object parent) {
        SimpleItemTreeNode parent_item = (SimpleItemTreeNode) parent;
        int count = parent_item.getRelationModel().getRowCount();
        Debug.debug("SimpleItemRelationTreeModel.getChildCount(" + parent.toString() + ") -> " + count);
        return count;
    }

    public int getIndexOfChild(Object parent, Object child) {
        SimpleItemTreeNode parent_item = (SimpleItemTreeNode) parent;
        if (child instanceof SimpleItemTreeNode) {
            SimpleItemTreeNode child_item = (SimpleItemTreeNode) child;
            return parent_item.getRelationModel().getItemRowIndex(child_item.item);
        } else if (child instanceof DefaultItem) {
            DefaultItem child_item = (DefaultItem) child;
            return parent_item.getRelationModel().getItemRowIndex(child_item);
        } else {
            throw new RuntimeException("unknown child type");
        }
    }
    
    public Object getRoot() {
        return rootNode;
    }

    public boolean isLeaf(Object node) {
        return (getChildCount(node)==0);
    }
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    /** Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>.
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     *
     * @param path path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     *
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /////////////////////////////////////////////////////////////////////
    //
    // event raising functions
    
    private void fireTreeStructureChanged() {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[] {rootNode});
        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).
                    treeStructureChanged(e);
        }
    }
    
    private void fireTreeNodeAdded(SimpleItemTreeNode parent, int child_insert_index) {
        SimpleItemTreeNode child = (SimpleItemTreeNode) this.getChild(parent,child_insert_index);

        // prepare event
        int[] index_arr = { child_insert_index };
        Object[] object_arr = { child };
        TreeModelEvent e = new TreeModelEvent(this, 
                    buildPathOfNode(parent, null).toArray(), index_arr, object_arr);
        // push event
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
           ((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesInserted(e);
        }
    }
    private void fireTreeNodeChanged(SimpleItemTreeNode parent, int child_index) {
        if (child_index < 0) {
            Debug.debug("fireTreeNodeChanged(" + parent.toString() + "," + child_index + ")");
            return;
        }
        
        SimpleItemTreeNode child = (SimpleItemTreeNode) this.getChild(parent, child_index);
        
        // prepare event
        int[] index_arr = { child_index };
        Object[] object_arr = { child };
        TreeModelEvent e = new TreeModelEvent(this, 
              buildPathOfNode(parent, null).toArray(), index_arr, object_arr);

        // push event
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesChanged(e);
         }
    }
    
    private void fireTreeNodeRemoved(SimpleItemTreeNode parent, int child_index) {
        // prepare event
        int[] index_arr = { child_index };
        Object[] object_arr = null;
        TreeModelEvent e = new TreeModelEvent(this, 
              buildPathOfNode(parent, null).toArray(), index_arr, object_arr);

        // push event
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesRemoved(e);
         }
    }
    
    
}

