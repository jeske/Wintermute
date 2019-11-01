/* =============================================================================
 *
 * Internet Explorer 6 Style Toolbar Component for Java Swing
 *
 * =============================================================================
 *
 * This package (and all sub-packages) contain the Java source implementation
 * of an Internet Explorer 6 style toolbar component, that can be added to any
 * AWT Container, or more specifically, a Java Swing JFrame. This component 
 * replicates the functionality of the toolbar as closely as possible.
 *
 * =============================================================================
 *
 * Author:  Theo Cleminson.
 * Company: Exponential Software Professionals Group.
 * Date:    31 January 2003.
 *
 * Purchased on eLance by David W. Jeske on 2003.02.16
 *
 * ============================================================================= 
 */

package guicomp.toolbar;

import java.awt.event.ActionEvent;
import javax.swing.JComponent;

/**
 * <P> An interface that defines the method signatures for a toolbar item.
 *
 * @see     ToolbarItem
 * @author  Theo Cleminson, Exponential Software Professionals Group.
 */
public interface ToolbarItemIntf 
{
  /** can this component expand to fill spare toolbar space ? */
  public boolean isStretchable();
  
  /** allow some components to expand to fill spare toolbar space */
  public void setStretchable(boolean isStretchable);
  
  /** return text held by this toolbar element's delegate/child component.  Null if N/A */
  public String getText();
  
  /** return true if the underlying component can be displayed in a popup menu. */
  public boolean canDisplayInPopup();
  
  /** Provide access to wrapped JComponent */
  public JComponent getWrappedComponent();
  
  /** method to invoke when popup menu item clicked */
  public void actionPerformed(ActionEvent actionEvent);
}
