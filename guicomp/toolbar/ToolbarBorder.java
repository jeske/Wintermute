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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * <P> This Border provides a very thin 3D border around the ToolbarContainer
 * and HorizontalSlot components to simulate the look and feel of the IE6 Toolbar. 
 *
 * <P> Standard Swing Borders do not supply the exact Border requirements.
 *
 * @see     ToolbarContainer
 * @see     HorizontalSlot
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarBorder extends AbstractBorder
{
  private boolean raised;
  
  public ToolbarBorder(boolean raised)
  {
    super();
    
    // Determine if this bevel is raised or lowered
    this.raised = raised;
  }
  
  /**
   * The component insets needed for this Border to paint properly.
   */
  public Insets getBorderInsets(Component c)
  {
    return new Insets(1, 1, 1, 1);
  }
  
  /**
   * Set the raised or lowered state of this Border.
   *
   * @param raised true if this Border should be rendered as raised,
   *        and false for lowered.
   */
  public void setRaised(boolean raised)
  {
    this.raised = raised;
  }
  
  /**
   * Returns the raised or lowered state of this Border.
   *
   * @return true if this Border is rendered as raised, and false for lowered.
   */
  public boolean isRaised()
  {
    return raised;
  }
  
  /**
   * Paint the colors required for this thin border.
   */
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
  {
    if (raised)
    {
      // Draw the lower edge bevel first
      g.setColor(Color.gray);
      drawBottomRightEdge(g, x, y, width, height);
      
      // Draw the top edge bevel last
      g.setColor(Color.white);
      drawTopLeftEdge(g, x, y, width, height);      
    }
    else // lowered
    {
      // Draw the top edge bevel first
      g.setColor(Color.gray);
      drawTopLeftEdge(g, x, y, width, height);
  
      // Draw the lower edge bevel last
      g.setColor(Color.white);
      drawBottomRightEdge(g, x, y, width, height);      
    }
  }
  
  /**
   * Draw the bevel lines that make up the top and left edges 
   * of the bevel border.
   */
  private void drawTopLeftEdge(Graphics g, int x, int y, int width, int height)
  {
    g.drawLine(x, y, x + width - 1, y);
    g.drawLine(x, y, x, y + height - 1);
  }
  
  /**
   * Draw the bevel lines that make up the bottom and right edges 
   * of the bevel border.
   */
  private void drawBottomRightEdge(Graphics g, int x, int y, int width, int height)
  {
    g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
    g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
  }
}
