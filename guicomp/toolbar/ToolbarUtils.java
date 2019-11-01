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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * <P> A collection of small reusable Toolbar utility functions for general use.
 *
 * @see     ToolbarContainer
 * @see     Toolbar
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarUtils
{
  /** Minimum size of any Toolbar. */
  public static final int MIN_SIZE = 100;
  
  /** The disk file to store and restore Toolbar settings */
  public static final String TOOLBAR_FILE = ".toolbar";
  
  /** Turns on debugging features if true */
  public static final boolean DEBUG = false;
  
  /**
   * Unable to instantiate this class, please use the static methods.
   */
  private ToolbarUtils() { }
  
  /**
   * Retrieve the HorizontalSlot component from the Container at the 
   * current point. If the point is not over a slot, then null is returned.
   *
   * @param  c  the Container to locate the slot in.
   * @return pt the point to use for location.
   */
  public static HorizontalSlot getSlotFromMousePoint(Container c, Point pt)
  {
    HorizontalSlot slot     = null;
    Component deepComponent = SwingUtilities.getDeepestComponentAt(c, pt.x, pt.y);

    if (deepComponent != null)
    {
      if (deepComponent instanceof HorizontalSlot)
        slot = (HorizontalSlot) deepComponent;
      else
      {      
        slot = (HorizontalSlot) SwingUtilities.getAncestorOfClass(
                                HorizontalSlot.class, deepComponent );
      }
    }
    
    return slot;
  }
  
  /**
   * Finds the first sub-component encountered in the containment hierarchy
   * that matches the Class passed in. If a matching sub-component is not found,
   * null is retured.
   *
   * @param container the container in which to begin the search.
   * @param findClass the Class to seek out.
   * @param recurse   if true, the containment hierarchy is traversed downward
   *                  in a 'depth first' fashion looking for the findClass.
   */
  public static Component getFirstSubComponent( Container container, 
                                                Class     findClass, 
                                                boolean   recurse   )
  {
    Component found = null;
    
    if (container != null) // Valid container
    {
      // Loop through each sub-component
      int count = container.getComponentCount();
      for (int i = 0; i < count; i++)
      {
        Component c = container.getComponent(i);
        if (c.getClass().equals(findClass))
        {
          // findClass has been located
          found = c;
          break;
        }
        
        // If the sub-component is a Container, recurse downward
        if (recurse && c instanceof Container)
        {
          found = getFirstSubComponent((Container)c, findClass, recurse);
          
          if (found != null)
            break;
        }
      }
    }
    
    return found;
  }
  
  /**
   * Sets the specified Font into each sub-component of the specified Container.
   * This call will traverse down the containment tree recursively if required.
   *
   * @param container the container to traverse.
   * @param font      the Font required setting.
   * @param recurse   if true, sub-containers are also traversed until the 
   *                  deepest is reached.
   */
  public static void setFontForSubComponents( Container container, 
                                              Font      font, 
                                              boolean   recurse   )
  {
    if (container != null) // Valid container
    {
      // Loop through each sub-component
      int count = container.getComponentCount();
      
      // JMenu sub-component traversal is treated differently 
      // than a standard AWT/Swing Container.
      if (container instanceof JMenu)
        count = ((JMenu)container).getMenuComponentCount();
        
      for (int i = 0; i < count; i++)
      {
        Component comp;
        if (container instanceof JMenu)
          comp = ((JMenu)container).getMenuComponent(i);
        else
          comp = container.getComponent(i);
        
        // If it is a JComponent, then set the Font
        if (comp instanceof JComponent)
          ((JComponent)comp).setFont(font);
          
        // If it is a Container, then recurse into it (if requested)
        if (comp instanceof Container && recurse)
          setFontForSubComponents((Container)comp, font, recurse);
      }
    }
  }

  /**
   * Calculate the sum of each component widths.
   *
   *@param componentArray Components whose width should be calculated.
   *@return total width
   */
  public static int sumComponentPrefWidths(Component[] componentArray)
  {
    int sumWidths = 0;
    for (int i=0; i < componentArray.length; i++)
    {
      sumWidths += componentArray[i].getPreferredSize().width;
    }
    return sumWidths;
  }

  /**
   * Calculate the sum of each component widths.
   *
   *@param componentArray Components whose width should be calculated.
   *@return total width
   */
  public static int sumComponentWidths(Component[] componentArray)
  {
    int sumWidths = 0;
    for (int i=0; i < componentArray.length; i++)
    {
      sumWidths += componentArray[i].getSize().width;
    }
    return sumWidths;
  }
  
  /**
   * Attempts to invoke method() with the given parameter types and parameters, on the target object.
   * @return method result Object or null.
   * @throws NoSuchMethodException if the method is not valid for target.
   */
  public static Object invokeMethod(String methodName, Class[] paramTypeArray, Object[] paramArray, Object target)
      throws NoSuchMethodException
  {
    if (methodName == null)
    {
      throw new IllegalArgumentException("Reflect.invokeMethod(): methodName cannot be null!");
    }
    Object result = null;
    Method method = target.getClass().getMethod(methodName, paramTypeArray);
    try
    {
      result = method.invoke(target, paramArray);
    } 
    catch (Throwable t)
    {
      StringBuffer methodSignature = new StringBuffer();
      methodSignature.append(methodName + "(");
      for (int i=0; paramTypeArray != null && i < paramTypeArray.length; i++)
      {
        methodSignature.append(paramTypeArray[i].getClass().getName() + " p" + (i+1));
        if (i+1 < paramTypeArray.length)
        {
          methodSignature.append(", ");
        }
      }
      methodSignature.append(")");
      throw new RuntimeException(
          "Exception while invoking method: " + methodSignature + ", " +
          "On target: " + target + ", " +
          "With parameters: " + toString(paramArray, ", ") + ", " +
          "In: " + ToolbarUtils.class.getName(),
          t
      );
    }
    return result;
  }
  
  /**
   * @returns a String representation of an Array, using delim between elements
   */
  public static String toString(Object[] objectArray, String delim)
  {
    StringBuffer arrayString = new StringBuffer();
    for (int i=0; objectArray != null && i < objectArray.length; i++)
    {
      arrayString.append(objectArray[i]);
      if (i+1 < objectArray.length)
      {
        arrayString.append(delim);
      }
    }
    return arrayString.toString();
  }
  
  /**
   * Return the DEBUG state.
   */
  public static boolean isDebug()
  {
    return DEBUG;
  }
  
  /**
   * Attach drag listeners to the components (and sub-components) supplied.
   */
  public static void addDragListener( Component component, 
                                      MouseInputAdapter dragListener, 
                                      boolean recurse )
  {
    addDragListenerExt( component, dragListener, recurse, false );
  }
  
  /**
   * Attach drag listeners to the components (and sub-components) supplied.
   */
  private static void addDragListenerExt( Component component, 
                                          MouseInputAdapter dragListener, 
                                          boolean recurse,
                                          boolean toolbarParent )
  {
    if (!(component instanceof Toolbar))
    {
      // Do not add mouse listeners to the Toolbar or the direct Toolbar children
      // unless it is the DragBar (ignores MoreButton and ItemSpace).
      if ( !toolbarParent || 
           (toolbarParent && component instanceof DragBar) )
      {      
        // Remove any old previously added listeners
        component.removeMouseListener(dragListener);
        component.removeMouseMotionListener(dragListener);
    
        // Add new listeners to this component
        component.addMouseListener(dragListener);
        component.addMouseMotionListener(dragListener);
      }
    }
    else
    {
      // This component is the Toolbar - prepare for recursion
      toolbarParent = true;
    }
    
    if (recurse && component instanceof Container)
    {
      int count = ((Container)component).getComponentCount();
      for (int  i = 0; i < count; i++)
      {
        // Recurse down Container components
        addDragListenerExt( ((Container)component).getComponent(i), 
                            dragListener, recurse, toolbarParent );
      }
    }
  }
  
  /**
   * Attach an ActionListener to all action components, and downward through
   * any Containers if the recurse flag is set.
   */
  public static void attachActionListener( Component component, 
                                           ActionListener listener, 
                                           boolean recurse )
  {
    if (component instanceof AbstractButton)
      ((AbstractButton)component).addActionListener(listener);
      
    if (recurse && component instanceof Container)
    {
      int count = ((Container)component).getComponentCount();
      for (int  i = 0; i < count; i++)
      {
        // Recurse down Container components
        attachActionListener( ((Container)component).getComponent(i), 
                              listener, recurse );
      }
    }
  }
  
  /**
   * Clone a JMenu and all sub-components of it, for display in the JPopupMenu.
   *
   * @param menu the JMenu object to clone.
   */
  public static JMenu cloneMenu( JMenu          menu, 
                                 ActionListener menuActionListener, 
                                 Map            menuItemMap )
  {
    JMenu menuClone = new JMenu();
    
    menuClone.setText(menu.getText());
    menuClone.setIcon(menu.getIcon());
    
    Component[] subComp = menu.getMenuComponents();
    for (int i = 0; i < subComp.length; i++)
    {
      if (subComp[i] instanceof JMenu)
      {
        menuClone.add(cloneMenu( (JMenu)subComp[i], 
                                 menuActionListener, 
                                 menuItemMap ));
      }
      else if (subComp[i] instanceof JMenuItem)
      {
        JMenuItem item      = (JMenuItem) subComp[i];
        JMenuItem itemClone = new JMenuItem();
        
        itemClone.setText(item.getText());
        itemClone.setIcon(item.getIcon());
        itemClone.addActionListener(menuActionListener);
        
        menuItemMap.put(itemClone, item);
        menuClone.add(itemClone);
      }
    }
    
    return menuClone;
  }
}
