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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * <P> A simple drag bar component to use for dragging Toolbars around the 
 * main ToolbarContainer when Toolbars are unlocked. 
 *
 * <P> This component is just a visual marker for users to begin drag operations 
 * with the mouse.
 *
 * @see     Toolbar
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class DragBar extends JPanel
{
  private static final int GRIPPER_WIDTH    = 3;
  private static final int DIVIDER_WIDTH    = 2;
  private static final int GAP              = 2;
  private static final int PREFERRED_WIDTH  = GRIPPER_WIDTH + (2 * GAP);
  private static final int LOCKED_WIDTH     = DIVIDER_WIDTH + 1;
  
  private boolean showDivider = false;
  private boolean locked      = false;
  private boolean isDragging  = false;
  
  /**
   * Creates a default Drag Bar.
   */
  public DragBar()
  {
    super(true);
    
    // Set the preferred width of this drag bar
    setPreferredSize(new Dimension(PREFERRED_WIDTH, getPreferredSize().height));
    
    // TESTING ONLY - Test Color so that we can see Slots and Toolbars
    if (ToolbarUtils.isDebug())
      setBackground(new java.awt.Color(223,223,223));
      
    setDragging(false);
  }
  
  /**
   * Sets the dragging state.
   */
  public void setDragging(boolean dragging)
  {
    if (isDragging != dragging)
    {
      isDragging = dragging;
      repaint();
    }
    
    setCursor(Cursor.getPredefinedCursor(isDragging ? Cursor.MOVE_CURSOR :  
                                                      Cursor.W_RESIZE_CURSOR));
  }
  
  /**
   * Returns the dragging state.
   */
  public boolean isDragging()
  {
    return isDragging;
  }
  
  /**
   * Set the divider state on, so that it is drawn before the drag bar.
   *
   * @param show true if divider is drawn.
   */
  public void setDivider(boolean show)
  {
    if (show != showDivider)
    {
      showDivider = show;
      
      resetPreferredWidth();
      repaint();
    }
  }
  
  /**
   * Returns the divider state, true = on, and false = off.
   */
  public boolean isDivider()
  {
    return showDivider;
  }
  
  /**
   * Set the locked state, which turns off the drag bar.
   *
   * @param locked true if locked, and the drag bar is not drawn.
   */
  public void setLocked(boolean locked)
  {
    if (this.locked != locked)
    {
      this.locked = locked;
      
      if (locked)
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      else
        setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
      
      resetPreferredWidth();
      repaint();
    }
  }
  
  /**
   * Return the locked state.
   *
   * @return true if locked, and false if not.
   */
  public boolean isLocked()
  {
    return locked;
  }
  
  /**
   * Paints the drag bar "gripper" graphic.
   */
  public void paintComponent(Graphics g)
  {
    // Perform default drawing first
    super.paintComponent(g);
    
    Dimension size = getSize();
    int offsetX = 0;
    
    if (showDivider)
    {
      // Draw the divider bar when required
      offsetX = DIVIDER_WIDTH;
      g.setColor(Color.gray);
      g.drawLine(0, 0, 0, size.height - 1);
      
      g.setColor(Color.white);
      g.drawLine(1, 0, 1, size.height - 1);
    }
    
    if (!locked)
    {
      // Draw the drag bar highlight
      g.setColor(isDragging ? Color.gray : Color.white);
      g.drawLine(offsetX + GAP, GAP, size.width - GAP - 1, GAP);
      g.drawLine(offsetX + GAP, GAP, offsetX + GAP, size.height - GAP - 1);
      
      // Draw the drag bar shadow
      g.setColor(isDragging ? Color.white : Color.gray);
      g.drawLine( size.width - GAP - 1, GAP, 
                  size.width - GAP - 1, size.height - GAP - 1 );
      g.drawLine( offsetX + GAP, size.height - GAP - 1, 
                  size.width - GAP - 1, size.height - GAP - 1 );
    }
  }
  
  /**
   * Re-sets the preferred width based on current settings.
   */
  private void resetPreferredWidth()
  {
    int width = PREFERRED_WIDTH;
    
    if (showDivider)
      width += DIVIDER_WIDTH;
      
    if (locked)
      width = LOCKED_WIDTH;

    // Set the preferred width of this drag bar
    setPreferredSize(new Dimension(width, getPreferredSize().height));
    
    invalidate();
  }
}
