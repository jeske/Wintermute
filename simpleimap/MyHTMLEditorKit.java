/*
 * MyHTMLEditorKit.java
 *
 * Created on January 15, 2003, 11:20 PM
 */

package simpleimap;

import javax.swing.text.html.*;
import javax.swing.text.*;

/**
 *
 * @author  hassan
 */
public class MyHTMLEditorKit extends HTMLEditorKit {
    
    public ViewFactory getViewFactory() {
        return new HTMLFactoryX();
    }
    
    
    public static class HTMLFactoryX extends HTMLFactory
    implements ViewFactory {
        
        public View create(Element elem) {
            Object o =
            elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG)
                    return new MyImageView(elem);
            }
            return super.create( elem );
        }
    }
}
