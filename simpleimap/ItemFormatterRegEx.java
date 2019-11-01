/*
 * ItemFormatterRegEx.java
 *
 * Created on February 10, 2003, 5:49 PM
 */

package simpleimap;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author  David W Jeske
 */
public class ItemFormatterRegEx extends DefaultItem implements ItemFormatter {
    public static final String TypeID = "ItemFormatterRegEx";
    private LinkedList patterns;
    /** Creates a new instance of ItemFormatterRegEx */
    public ItemFormatterRegEx() {
        super();
        initPatterns();
    }
    
    
    private void initPatterns() {
        Pattern p;
        
        LinkedList np = new LinkedList();
        
        p = Pattern.compile("\"([^\"]+)\" <[^ >]+>");
        {
            Object[] producer = { p, "" };
            np.add(producer);
        }
        p = Pattern.compile("([^<]+) <[^>]+>");
        {
            Object[] producer = { p, "" };
            np.add(producer);
        }
        
        patterns = np;
        
    }
    
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input) {
        String data = item.get(field);
        if (data == null) { return null; }
        
        for (Iterator itr=patterns.iterator();itr.hasNext();) {
            Object[] elements = (Object[]) itr.next();
            Pattern p = (Pattern)elements[0];
            String out = (String)elements[1];
            
            Matcher m = p.matcher(data);
            if (m.find()) {
                return m.group(1);
            }
        }
        Debug.debug(0, " raw '" + data + "'");
        
        return data;
    }
     public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemFormatterRegEx();
            }
        });
    }
     
     public ConfigPanel getConfigPanel(DefaultItem column_config) {
         return null;
     }
}
