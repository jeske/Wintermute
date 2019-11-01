/*
 * ItemTreeNodeInterface.java
 *
 * Created on November 17, 2002, 4:50 PM
 */

package simpleimap;

/**
 *
 * @author  hassan
 */
public interface ItemTreeNodeInterface {
    public javax.swing.Icon getIcon();
    public String getTreeNodeName();
    public DefaultItem getTreeItem();
}
