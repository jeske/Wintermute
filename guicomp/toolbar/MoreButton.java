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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.UIManager;

/**
 * <P> A "more" button that appears in the right of the Toolbar when this
 * Toolbar is shrunk down smaller than the size of the components that 
 * it contains. Identical to that which appears in the IE6 Toolbar.
 *
 * @see     Toolbar
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class MoreButton extends ToolbarButton
{
  private static final int CHEVRON_WIDTH    = 4;
  private static final int PREFERRED_WIDTH  = (2 * CHEVRON_WIDTH) + (2 * GAP);
  
  boolean visible_ = true;
  
  /**
   * Creates a graphical "more" button for use in the Toolbar.
   */
  public MoreButton()
  {
    super();

    // Set the preferred width of this drag bar
    setPreferredSize(new Dimension(PREFERRED_WIDTH, getPreferredSize().height));
    
    // TESTING ONLY - Test Color so that we can see Slots and Toolbars
    if (ToolbarUtils.isDebug())
      setBackground(new java.awt.Color(190,190,190));
  }
  
  /**
   * Override default setVisible().  
   * This prevents the button ever becoming invisible which makes it easier to 
   * calculate Toolbar width.
   * 
   * When visible is false, the chevron is not painted and the button is 
   * disabled to prevent actionPerformed triggering.
   *
   * @param visible - when true indicates there no hidden toolbar items.
   *
  public void setVisible(boolean visible)
  {
    visible_ = visible;
  }
  */
  
  /**
   * @return true if component is visible.
   *
  public boolean isVisible()
  {
    return visible_;
  }
  */
  
  /**
   * Paints the "more" chevrons graphics.
   */
  public void paintComponent(Graphics g)
  {
    // Perform default painting first
    super.paintComponent(g);
    
    // Draw the "more" chevrons
    if (isEnabled())
    {
      g.setColor(UIManager.getDefaults().getColor("TextField.foreground"));
    }
    else
    {
      // display chevrons in light grey to indicate components are missing, 
      // but none are eligible for display on the popup menu.
      g.setColor(UIManager.getDefaults().getColor("TextField.inactiveForeground"));
    }
    
    if (isVisible()) // don't display chevron if button is "invisible"
    {
      int x = GAP;
      int y = GAP;
      for ( ; x < GAP + CHEVRON_WIDTH - 1; x++)
      {
        g.drawLine(x, y, x + 1, y);
        g.drawLine(x + CHEVRON_WIDTH, y, x + CHEVRON_WIDTH + 1, y++);
      }

      for (x -= 2; x >= GAP; x--)
      {
        g.drawLine(x, y, x + 1, y); 
        g.drawLine(x + CHEVRON_WIDTH, y, x + CHEVRON_WIDTH + 1, y++); 
      }
    }
  }
}
