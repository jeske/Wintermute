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
 * Author:  Paul Atkinson.
 * Company: Exponential Software Professionals Group.
 * Date:    31 January 2003.
 *
 * Purchased on eLance by David W. Jeske on 2003.02.16
 *
 * ============================================================================= 
 */

package guicomp.toolbar;

import java.util.EventObject;

/**
 * The Slot Wrap Event is fired with the SlotWrapListener.
 */
public class SlotWrapEvent extends EventObject
{
  private boolean isWrapped;
  
  /**
   * Creates a new wrap (or unwrap) event.
   */
  public SlotWrapEvent(Object source, boolean wrapped)
  {
    super(source);
    isWrapped = wrapped;
  }
  
  /**
   * Returns whether this event signifies a wrap or unwrap event.
   */
  public boolean isWrapped()
  {
    return isWrapped;
  }
}
