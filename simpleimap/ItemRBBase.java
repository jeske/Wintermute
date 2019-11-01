/*
 * ItemRBBase.java
 *
 * Created on February 24, 2003, 5:18 PM
 */

package simpleimap;

/**
 *
 * @author  David Jeske
 */
abstract public class ItemRBBase extends DefaultItem{
    /** Creates a new instance of ItemRBBase */
    public ItemRBBase() {
    }

    public abstract void processItemChanges(DefaultItem item);

}
