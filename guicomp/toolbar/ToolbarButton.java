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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

/**
 * <P> A basic Toolbar Button component to wrap flattened JButtons and other
 * Toolbar components that need a raised/lowered border, and are activated by 
 * mouse moves.
 *
 * @see     Toolbar
 * @see     MoreButton
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarButton extends ToolbarItem implements MouseListener
{
  private static final int DEFAULT_INSET_WIDTH  = 10;
  private static final int DEFAULT_INSET_HEIGHT = 4;
  
  private Border        emptyBorder;
  private ToolbarBorder bevelBorder;
  private Insets        insets;

  /**
   * Creates a floating button for use in the Toolbar.
   */
  public ToolbarButton()
  {
    super();
    
    init(null);
  }
  
  /**
   * Creates a floating button for use in the Toolbar.
   */
  public ToolbarButton(JComponent component)
  {
    super(component);
    
    init(component);
  }

  /**
   * Creates a floating button for use in the Toolbar.
   */
  public ToolbarButton(JComponent component, int hgap, int vgap)
  {
    super(component, hgap, vgap);
    
    init(component);
  }

  /**
   * Allow mouseClicked events to be listened to.
   */
  public void addActionListener(ActionListener l) 
  {
    listenerList.add(ActionListener.class, l);
  }

  /**
    * Removes an <code>ActionListener</code> from the button.
    * If the listener is the currently set <code>Action</code>
    * for the button, then the <code>Action</code>
    * is set to <code>null</code>.
    *
    * @param l the listener to be removed
    */
  public void removeActionListener(ActionListener l) 
  {
    listenerList.remove(ActionListener.class, l);
  }
    
  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the <code>event</code>
   * parameter.
   *
   * @param event  the <code>ActionEvent</code> object
   * @see EventListenerList
   */
  protected void fireActionPerformed(ActionEvent event) 
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    ActionEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) 
    {
      if (listeners[i]==ActionListener.class) 
      {
        // Lazily create the event:
        if (e == null) 
        {
            String actionCommand = event.getActionCommand();
            if (actionCommand == null) 
            {
             actionCommand = "ToolbarButton";
            }
            e = new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED,
                                actionCommand,
                                event.getWhen(),
                                event.getModifiers());
        }
        ((ActionListener)listeners[i+1]).actionPerformed(e);
      }
    }
  }
    
  /**
   * Invoked when the mouse enters this component.
   */
  public void mouseEntered(MouseEvent e)
  {
    if (isVisible() && isEnabled())
    {
      bevelBorder.setRaised(true);
      setBorder(bevelBorder);
    }
  }

  /**
   * Invoked when the mouse exits this component.
   */
  public void mouseExited(MouseEvent e)
  {
    setBorder(emptyBorder);
  }

  /**
   * Invoked when a mouse button has been pressed on this component.
   */
  public void mousePressed(MouseEvent e)
  {
    if (isEnabled() && isVisible())
    {
      bevelBorder.setRaised(false);
      setBorder(bevelBorder);
      repaint();
    }
  }
  
  /**
   * Invoked when a mouse button has been released on this component.
   */
  public void mouseReleased(MouseEvent e)
  {
    if (getBorder() == bevelBorder && isEnabled() && isVisible())
    {
      bevelBorder.setRaised(true);
      setBorder(bevelBorder);
      repaint();
    }
  }

  public void mouseClicked(MouseEvent e) 
  {
    if (isEnabled() && isVisible())
    {
      fireActionPerformed(new ActionEvent(e, e.getID(), "mouseClicked"));
    }
  }
  
  /**
   * Initialize this component.
   */
  private void init(JComponent component)
  {
    // Start out with an empty border
    emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    bevelBorder = new ToolbarBorder(true);
    setBorder(emptyBorder);
    
    // Listen for mouse events to alter the border
    if (component != null)
      component.addMouseListener(this);
    else
      addMouseListener(this);
  }
  
  /**
   * Returns the Insets used to render this component.
   *
  public Insets getInsets()
  {
    return insets;
  }
  
   **
   * Returns the Insets used to render this component.
   * The insets object passed in is populated with the values.
   *
  public Insets getInsets(Insets insets)
  {
    insets.top    = this.insets.top;
    insets.left   = this.insets.left;
    insets.bottom = this.insets.bottom;
    insets.right  = this.insets.right;
    
    return insets;
  }
  */
}
