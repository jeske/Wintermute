/*
 * IRelationChangeNotification.java
 *
 * Created on February 3, 2003, 7:00 PM
 */

package simpleimap;

/**
 *
 * @author  David W Jeske
 */
public interface IRelationChangeNotification {
    
    /*
     * itemAddedAfter(DefaultItem item, DefaultItem afterItem)
     *
     * Indicates that an item was added to the relation after the item specified in
     * the "afterItem" paramater. If the item as added at the beginning, then the
     * "afterItem" paramater is null.
     *
     */
    
    public void itemAddedAfter(ItemRelation relation, DefaultItem item, DefaultItem afterItem);
    
    /*
     * itemRemoved(DefaultItem item) 
     * 
     * Indicates than an item was removed from the relation.
     *
     */
    
    public void itemRemoved(ItemRelation relation, DefaultItem item);
}
