/*
 * ItemActionInterface.java
 *
 * Created on November 17, 2002, 12:17 PM
 */

package simpleimap;

import javax.swing.Action;

/**
 *
 * @author  hassan
 */
abstract public class ItemAction extends DefaultItem {
    abstract public Action getAction();
}
