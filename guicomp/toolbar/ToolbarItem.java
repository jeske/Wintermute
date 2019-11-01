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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/**
 * <P> A basic Toolbar Item component to wrap flattened JButtons and other
 * Toolbar components that need a raised/lowered border.
 *
 * @see     Toolbar
 * @see     ToolbarButton
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarItem extends JPanel implements ToolbarItemIntf
{
  protected static final int GAP = 4;
  
  protected boolean    stretchable;
  protected JComponent wrappedComponent_; // item wrapped in this ToolbarItem instance.
  protected int        preferredWidth;
  protected int        minimumWidth;

  /**
   * Creates an item region for use in the Toolbar.
   */
  public ToolbarItem()
  {
    this(null, false, -1, -1);
  }
  
  /**
   * Creates an item region for use in the Toolbar.
   *
   * @param component the component to add to the region.
   */
  public ToolbarItem(JComponent component)
  {
    this(component, false, -1, -1);
  }

  /**
   * Creates an item region for use in the Toolbar.
   *
   * @param component the component to add to the region.
   */
  public ToolbarItem(JComponent component, int horzGap, int vertGap)
  {
    this(component, false, horzGap, vertGap);
  }

  /**
   * Creates an item region for use in the Toolbar.
   */
  public ToolbarItem(JComponent component, boolean stretchable)
  {
    this(component, stretchable, -1, -1);
  }

  /**
   * Creates an item region for use in the Toolbar.
   */
  public ToolbarItem(JComponent component, boolean stretchable, int horzGap, int vertGap)
  {
    super();
    this.stretchable = stretchable;
    
    init(component, horzGap, vertGap);
  }
  
  /**
   * Returns true if this toolbar item is "stretchable".  Stretchable toolbar 
   * items can expand to their MaximumSize.
   */
  public boolean isStretchable()
  {
    return stretchable;
  }
  
  /**
   * Make a toolbar item "stretchable".  Stretchable toolbar items can expand to 
   * their MaximumSize.
   */
  public void setStretchable(boolean stretchable)
  {
    this.stretchable = stretchable;
  }
  
  /**
   * Provide access to wrapped JComponent.
   */
  public JComponent getWrappedComponent()
  {
    return wrappedComponent_;
  }
  
  /**
   *
   */
  public String getText()
  {
    try 
    {
      return (String) ToolbarUtils.invokeMethod("getText", null, null, wrappedComponent_);
    }
    catch (NoSuchMethodException nsme)
    {
      return null;
    }
  }
  
  /**
   * Returns the preferred width of this ToolbarItem based on the original
   * preferred width of the wrapped component.
   */
  public int getPreferredWidth()
  {
    return preferredWidth;
  }
  
  /**
   * Returns the minimum width of this ToolbarItem based on the original
   * minimum width of the wrapped component.
   */
  public int getMinimumWidth()
  {
    return minimumWidth;
  }
  
  /**
   * Initialize this component.
   *
   * @param component the component to add to the region.
   * @param horzGap   a horizontal gap to add to the component, or -1 if none.
   * @param vertGap   a vertical gap to add to the component, or -1 if none.
   */
  private void init(JComponent component, int horzGap, int vertGap)
  {
    Dimension compMinSize = null;

    wrappedComponent_ = component;
    
    if (component instanceof JMenuBar)
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    else
      setLayout(new BorderLayout());

    // Add the supplied component to the CENTER of this floating button
    if (component != null)
    {
      Dimension compSize = component.getPreferredSize();
      compMinSize        = component.getMinimumSize();
      
      // Add the component
      add(component);

      // Expand the component by horzGap and vertGap if needed
      if (horzGap > -1 && vertGap > -1)
      {
        setPreferredSize( new Dimension( compSize.width + (2 * horzGap), 
                                         compSize.height + (2 * vertGap) ) );
      }
    }
    

    preferredWidth = getPreferredSize().width;
    minimumWidth   = (compMinSize == null ? (2 * horzGap) : 
                      compMinSize.width + (2 * horzGap));
  }
  
  /** return true if the underlying component can be displayed in a popup menu. */
  public boolean canDisplayInPopup() 
  {
    return 
        !(wrappedComponent_ instanceof JTextComponent) &&
        !(wrappedComponent_ instanceof JLabel) &&
        !(wrappedComponent_ instanceof JComboBox);
  }
  
  /** method to invoke when popup menu item clicked  */
  public void actionPerformed(ActionEvent actionEvent) 
  {
    try
    {
      //ToolbarUtils.invokeMethod("actionPerformed", new Class[] { ActionEvent.class }, new Object[] { actionEvent }, wrappedComponent_);
      ToolbarUtils.invokeMethod("doClick", null, null, wrappedComponent_);
    }
    catch (NoSuchMethodException nsme)
    {
      System.out.println("Unable to invoke method doClick() on wrappedComponent: " + wrappedComponent_);
    }
  } 
}
