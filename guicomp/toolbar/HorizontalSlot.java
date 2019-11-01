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
import java.awt.FlowLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * <P> An AWT Container that acts as a horizontal slot within the Toolbar
 * Container class. This horizontal slot is not visible to the user, but is
 * used to layout Toolbar components within the Toolbar Container.
 *
 * @see     ToolbarContainer
 * @see     Toolbar
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class HorizontalSlot extends JPanel
{
  /** The default height for an empty slot */
  public static final int DEFAULT_HEIGHT = 20;
  
  /** Contains a map of toolbar names to Toolbar instances for this slot */
  private Map toolbarMap = new HashMap();
  
  /** The slot number for this Horizontal Slot (zero-based) */
  private int slotNumber = 0;
  
  /** List of listeners for listening to Horizontal Slots for wrap events */
  private List slotWrapListeners = null;
  
  /** Determines the wrapped state of this Slot */
  private boolean wrapped = false;
  
  /** Flag to indicate is Toolbars were just swapped */
  private boolean swapped = false;
  
  /** The preferred height of this slot based on contents */
  private int preferredHeight = DEFAULT_HEIGHT;
  
  private boolean initialized = false;
  
  /**
   * Creates a new HorizontalSlot used to layout Toolbar components.
   *
   * @see Toolbar
   * @see ToolbarContainer
   */
  public HorizontalSlot()
  {
    //super(new FlowLayout(FlowLayout.LEFT, 0, 0));
      super(new ToolbarLayout());
    
    // Set up alignment for the BoxLayout container
    setAlignmentY(CENTER_ALIGNMENT);

    // Set our customized border into this slot
    setBorder(new ToolbarBorder(true));

    // Pre-set a default height
    setPreferredSize(new Dimension(getPreferredSize().width, preferredHeight));
    
    // TESTING ONLY - Test Color so that we can see Slots and Toolbars
    if (ToolbarUtils.isDebug())
      setBackground(new java.awt.Color(220,220,220));
  }

  /**
   * Adds a listener to be informed of Slot Wrap events when they occur.
   *
   * @param a class that implements the SlotWrapListener interface.
   */  
  public void addSlotWrapListener(SlotWrapListener lis)
  {
    if (slotWrapListeners == null)
      slotWrapListeners = new ArrayList();
      
    slotWrapListeners.add(lis);
  }
  
  /**
   * Removes a listener for Slot Wrap events.
   *
   * @param a class that implements the SlotWrapListener interface.
   */  
  public void removeSlotWrapListener(SlotWrapListener lis)
  {
    if (slotWrapListeners != null)
      slotWrapListeners.remove(lis);
      
    if (slotWrapListeners.size() == 0)
      slotWrapListeners = null;
  }

  /**
   * Determines if the specified Toolbar instance will fit into this slot.
   * 
   * @return true if there is room for the toolbar to fit, and false if not.
   */
  public boolean isRoomToFit(Toolbar toolbar)
  {
    int count = getComponentCount();

    if (isVisible() && count > 0)
    {
      Toolbar lastBar = (Toolbar) getComponent(count - 1);
      int     lastX   = lastBar.getLocation().x + lastBar.getPreferredWidth();

      // Make sure there is at least MIN_SIZE remaining to fit this Toolbar
      if (getSize().width - lastX > ToolbarUtils.MIN_SIZE)
      {
        return true;
      }
      
      return false;
    }
    
    // When not visible, only allow one toolbar to each slot.
    return (count < 1);
  }
  
  /**
   * Add a Toolbar instance into this slot.
   * The Toolbar instance is recorded in the Map for later lookups.
   *
   * @param  toolbar the Toolbar instance to add.
   * @return the Toolbar instance.
   */
  public Toolbar add(Toolbar toolbar)
  {
    // Record this Toolbar instance in the Map */
    toolbarMap.put(toolbar.getToolbarName(), toolbar);
    
    int count = getComponentCount();
    if (count > 0)
    {
      Toolbar lastBar = (Toolbar) getComponent(count - 1);
      int     lastX   = lastBar.getLocation().x + lastBar.getPreferredWidth();
      
      lastBar.setToolbarSize(new Dimension( lastBar.getPreferredWidth(),
                                            lastBar.getPreferredHeight()));
      toolbar.setPreferredX(lastX);
      
      // Use remaining slot space
      toolbar.setToolbarSize( new Dimension(getSize().width - getInsets().left
                              - getInsets().right - lastX, 
                              toolbar.getPreferredHeight()) );
      toolbar.invalidate();
    }
    else
    {
      toolbar.setPreferredX(0);
      toolbar.setToolbarSize( new Dimension( getSize().width - 
                              getInsets().left - getInsets().right, 
                              toolbar.getPreferredHeight()));
    }

    // Add to this container
    Toolbar tbar = (Toolbar) super.add(toolbar);
    
    // Size the slot according to the new Toolbar contents
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(getPreferredSize().width, preferredHeight));

    return tbar;
  }
  
  /**
   * A Toolbar wrap has occurred within the Slot.
   */
  private boolean wrapOccurred()
  {
    int crossover = (DEFAULT_HEIGHT / 2);
    int count     = getComponentCount();
    
    for (int i = 0; i < count; i++)
    {
      Toolbar tbar = (Toolbar) getComponent(i);
      if (tbar.getLocation().y > crossover)
        return true;
    }
    
    return false;
  }
  
  /**
   * Fires an event to notify of a Toolbar wrap within this Slot.
   */
  private void fireSlotWrapEvent(boolean wrapped)
  {
    if (slotWrapListeners == null)
      return;
      
    SlotWrapEvent event = new SlotWrapEvent(this, wrapped);
    int           count = slotWrapListeners.size();
    
    for (int i = 0; i < count; i++)
    {
      SlotWrapListener lis = (SlotWrapListener) slotWrapListeners.get(i);
      lis.slotWrapOccurred(event);
    }
  }
  
  /**
   * Local method to calculate the preferred height for this slot, based on
   * the preferred heights of the Toolbars it contains.
   *
   * @return the preferred height.
   */
  private int calculatePreferredHeight()
  {
    int    lastX       = -1;
    int    height      = DEFAULT_HEIGHT;
    int    accumHeight = 0;
    int    count       = getComponentCount();
    Insets insets      = getInsets();
    int    insetHeight = insets.top + insets.bottom;
    int    width       = getSize().width - insets.left - insets.right;
System.out.println ("CalculatePrefHeight");
    for (int i = 0; i < count; i++)
    {
      Toolbar toolbar = (Toolbar) getComponent(i);
      
      if ( !hasSwapped() &&                   // Toolbars weren't just swapped?
           toolbar.getSize().width > 0 &&     // avoid initial layout
           toolbar.getLocation().x <= lastX ) // slot wrap
      {
System.out.println ("  - calculate found a wrap, loc.x = " + toolbar.getLocation().x + ", lastX: " + lastX);
        accumHeight += height;
        insetHeight  = 0;
        height       = 0;
      }
      
      lastX  = toolbar.getLocation().x;
      height = Math.max( height, toolbar.getPreferredHeight() + insetHeight );
    }
    
    return accumHeight + height;
  }
  
  /**
   * Returns the slot number for this slot.
   */
  public int getSlotNumber()
  {
    return slotNumber;
  }
  
  /**
   * Sets the slot number for this slot.
   */
  public void setSlotNumber(int slotNum)
  {
    slotNumber = slotNum;
  }
  
  /**
   * Sets the swapped state for this Slot's toolbars.
   * This flag is cleared once a valid layout has occurred.
   */
  public void setSwapped(boolean swapped)
  {
    this.swapped = swapped;
  }
  
  /** 
   * Returns true if the toolbars have just been swapped.
   */
  public boolean hasSwapped()
  {
    return swapped;
  }
  
  /**
   * Swaps one toolbar with another, so that they switch position.
   *
   * @param leftToolbar  the Toolbar on the left.
   * @param rightToolbar the Toolbar on the right.
   */
  public void swapToolbars(Toolbar leftToolbar, Toolbar rightToolbar)
  {
    setSwapped(true);
    
    int index = getToolbarIndex(leftToolbar);
    remove(leftToolbar);
    
    super.add(leftToolbar, index + 1);
    
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(1, preferredHeight));

    rightToolbar.invalidate();
    validate();
  }
  
  /**
   * Locate the Toolbar that is next to the one supplied, 
   * ie. is to the right of the supplied one.
   *
   * @param  toolbar the toolbar to check right of.
   * @return the next toolbar if there is one, or null.
   */
  public Toolbar getNextToolbar(Toolbar toolbar)
  {
    int count = getComponentCount();
    for (int i = 0; i < count; i++)
    {
      Toolbar tbar = (Toolbar) getComponent(i);
      
      if (tbar == toolbar)
      {
        if (i < count - 1)
          return (Toolbar) getComponent(i + 1);
        else
          break;
      }
    }
    
    return null;
  }
  
  /**
   * Locate the Toolbar that is previous to the one supplied, 
   * ie. is to the left of the supplied one.
   *
   * @param  toolbar the toolbar to check left of.
   * @return the previous toolbar if there is one, or null.
   */
  public Toolbar getPreviousToolbar(Toolbar toolbar)
  {
    int count = getComponentCount();
    for (int i = 0; i < count; i++)
    {
      Toolbar tbar = (Toolbar) getComponent(i);
      
      if (tbar == toolbar)
      {
        if (i > 0)
          return (Toolbar) getComponent(i - 1);
        else
          break;
      }
    }
    
    return null;
  }
  
  /**
   * Finds the Toolbar component index within the Slot, 
   * or returns -1 if not found.
   *
   * @return the int Toolbar index.
   */
  public int getToolbarIndex(Toolbar toolbar)
  {
    int count = getComponentCount();
    for (int i = 0; i < count; i++)
    {
      Toolbar tbar = (Toolbar) getComponent(i);
      
      if (toolbar == tbar)
        return i;
    }
    
    return -1;
  }
  
  /**
   * Returns the preferred height for this slot.
   */
  public int getPreferredHeight()
  {
    return preferredHeight;
  }
  
  /**
   * Retrieve a Toolbar from this HorizontalSlot using its name.
   * If the Toolbar is not found in this slot, null is returned.
   *
   * @param  toolbarName the name of the Toolbar to retrieve.
   * @return a Toolbar instance matching the name, or null.
   */
  public Toolbar getToolbar(String toolbarName)
  {
    return (Toolbar) toolbarMap.get(toolbarName);
  }

  /**
   * Override the layout method to force a resize on Toolbar components.
   */  
  public void doLayout()
  {
    // Changes position of Toolbars in the Slot if prefX signals a change
    boolean restacked = restackToolbars();

    // Resizes Toolbar components within the Slot    
    if (!restacked || !initialized)
      resizeComponents();

    // Default layout      
    initialized = true;
    super.doLayout();
    
    // Test for a Toolbar wrap inside the Slot
    boolean wrapped = wrapOccurred();
    if (this.wrapped || wrapped)
    {
      this.wrapped = wrapped;
      
      // Reset the slot's preferred height
      int prefHeight = calculatePreferredHeight();
      if (prefHeight != preferredHeight)
      {
        preferredHeight = prefHeight;
  System.out.println ("Wrap occurred, height is: " + preferredHeight);
        setPreferredSize(new Dimension(1, preferredHeight));

        fireSlotWrapEvent(wrapped);
      }
    }
    
    // Clear any swap flags once laid out
    setSwapped(false);

    // Layout needs redoing if component restacking has occurred    
    if (restacked)
      invalidate();
  }

  /**
   * Remove this Toolbar from the slot.
   */  
  public void remove(Toolbar toolbar)
  {
    super.remove(toolbar);
    if (getComponentCount() > 0)
    {
      Toolbar tbar = (Toolbar) getComponent(0);

      tbar.setPreferredX(0);
      
      preferredHeight = calculatePreferredHeight();
      setPreferredSize(new Dimension(1, preferredHeight));
    }
    else
    {
      // Keep empty slot height static
      setPreferredSize(new Dimension( 1, toolbar.getPreferredHeight() + 
                                      getInsets().top + getInsets().bottom ));
    }
  }
  
  /**
   * Checks each Toolbar component's preferredX position, and switches Toolbars
   * around where needed.
   */
  public boolean restackToolbars()
  {
    int     prefX;
    int     prevX     = -1;
    int     count     = getComponentCount();
    boolean response  = false;
    boolean restacked = false;
    
    do
    {
      restacked = false;
      for (int i = 0; i < count; i++)
      {
        Toolbar tbar = (Toolbar) getComponent(i);
        prefX = tbar.getPreferredX();
        
        if (i > 0 && prevX > prefX)
        {
          setSwapped(true);
          super.remove(tbar);
          super.add(tbar, i - 1);
          
          restacked = true;
          response  = true;
          prefX     = prevX;
        }
        
        prevX = prefX;
      }
    }
    while (restacked);
    
    if (response)
    {
      preferredHeight = calculatePreferredHeight();
      setPreferredSize(new Dimension(getPreferredSize().width, preferredHeight));
    }
    
    return response;
  }
  
  /**
   * Call to resize Toolbars within this slot to match new dimensions.
   */
  public void resizeComponents()
  {
    int toAlloc = getSize().width - getInsets().left - getInsets().right;
    
    Toolbar toolbar     = null;
    Toolbar nextToolbar = null;
    int     count       = getComponentCount();
    int     height      = 0;
    int     lineHeight  = 0;
    
    for (int i = 0; i < count; i++)
    {
      nextToolbar = (Toolbar) getComponent(i);
     
      if (toolbar != null)
      {
        int newWidth = nextToolbar.getPreferredX() - toolbar.getPreferredX();
        newWidth     = Math.max(newWidth, ToolbarUtils.MIN_SIZE);
        
        toolbar.setToolbarSize( new Dimension(newWidth, 
                                toolbar.getPreferredHeight()) );
        toAlloc -= newWidth;
      }

      toolbar = nextToolbar;
    }
    
    // Size up the last Toolbar
    if (toolbar != null)
    {
      int lastWidth = Math.max(toAlloc, ToolbarUtils.MIN_SIZE);
      
      toolbar.setToolbarSize(
          new Dimension( lastWidth, toolbar.getPreferredHeight() ));
      toAlloc -= lastWidth;
    }
    
    // Retrace from right to left, shrinking toolbars as required
    if (toAlloc < 0)
    {
      for (int i = count - 1; i >= 0; i--)
      {
        toolbar       = (Toolbar) getComponent(i);
        int canRemove = toolbar.getPreferredSize().width - ToolbarUtils.MIN_SIZE;
        
        if (canRemove < 0)
        {
          // This toolbar is already undersize.
          // Make this toolbar the minimum required size.
          toolbar.setToolbarSize( new Dimension(ToolbarUtils.MIN_SIZE, 
                                  toolbar.getPreferredHeight()) );
          toAlloc -= canRemove;
        }
        else if (toAlloc < 0 && canRemove > 0)
        {
          // Toolbar is bigger than minimum, and can be shrunk.
          if (-toAlloc > canRemove)
          {
            // This Toolbar IS NOT wide enough to allocate all needed space.
            // Just allocate what this toolbar is able to.
            toolbar.setToolbarSize( new Dimension(ToolbarUtils.MIN_SIZE,
                                    toolbar.getPreferredHeight()) );
            toAlloc += canRemove;
          }
          else
          {
            // This Toolbar IS wide enough to allocate all needed space.
            int newSize = toolbar.getPreferredSize().width + toAlloc;
            toolbar.setToolbarSize( new Dimension(newSize,
                                    toolbar.getPreferredHeight()) );
            break;
          }
        }
      }
    }
  }
}
