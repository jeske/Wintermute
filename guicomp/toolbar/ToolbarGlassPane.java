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
 * Date:    7 February 2003.
 *
 * Purchased on eLance by David W. Jeske on 2003.02.16
 *
 * ============================================================================= 
 */

package guicomp.toolbar;

import javax.swing.JPanel;

/**
 * <P> A Glass Pane implementation for transparently capturing mouse events 
 * and redispatching to the Toolbar components.
 *
 * <P> Allows for sophisticated Toolbar drag and drop capabilities.
 *
 * @see     ToolbarContainer
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarGlassPane extends JPanel
{
  public ToolbarGlassPane()
  {
    // Make this panel transparent
    setOpaque(false);
  }
}
