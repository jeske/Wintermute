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
import java.awt.event.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 * <P> The top level component for the Intenet Explorer 6 style toolbar component.
 * This Toolbar Container component can be added to any Container, such as a
 * Java Swing JFrame. 
 *
 * <P> This Toolbar Container can contain any number of Toolbar 
 * components within it, which may be dragged around the container using the
 * mouse.
 *
 * @see     Toolbar
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class ToolbarContainer extends JPanel implements SlotWrapListener
{
  private static final String ARGUMENT_EXCEPTION =
          "The ToolbarContainer only accepts addition of Toolbar components.";
          
  private static final String SELECTED_ICON   = "guicomp/images/selected.gif";
  private static final String UNSELECTED_ICON = "guicomp/images/unselected.gif";
  
  private static final String ACTION_LOCK_TOOLBARS = "Lock the Toolbars";
          
  private static Icon  selectedIcon   = null;
  private static Icon  unselectedIcon = null;
  
  /** The parent JFrame that this container is added to */
  private JFrame parent = null;
  
  /** Captures mouse events in slots for right-click popup menu */
  private SlotMouseListener slotMouseListener = null;
  
  /** Listens for drag events for Toolbar dragging */
  private DragListener dragListener = null;
  
  /** A map of toolbar names to slots - for easy lookup */
  private Map toolbarSlotMap = new HashMap();
  
  /** A map of toolbar names currently hidden - for easy lookup */
  private Map toolbarHiddenMap = new HashMap();
  
  /** Keeps the state of Toolbar locking for this Container */
  private boolean toolbarsLocked   = false;
  
  /** Determines if a drag is in progress */
  private boolean isDragging = false;
  
  /** Small offset needed from left edge of Toolbar to the mouse in DragBar */
  private int dragOffset;
  
  /** The required height for this Container */
  private int preferredHeight;
  
  private HorizontalSlot dragFromSlot;
  private Toolbar        dragToolbar;
  
  /** Auto Restore allows automatic saving and restoring of Toolbar positions */
  private boolean        autoRestore;
  private Properties     toolbarProperties = null;
  
  /** Indicates when the Toolbar sizes have been initialized */
  private boolean initialized = false;
  
  /**
   * Creates the main ToolbarContainer used for adding 
   * individual Toolbar strips into it.
   *
   * @see Toolbar
   */
  public ToolbarContainer()
  {
    super();

    init();    
  }
  
  /**
   * Creates the main ToolbarContainer used for adding Toolbar strips into it. 
   * Allows enabling/disabling of the Auto Restore feature for 
   * Toolbar positions.
   *
   * @see Toolbar
   */
  public ToolbarContainer(boolean autoRestore)
  {
    super();

    // Set the Auto Restore state
    this.autoRestore = autoRestore;
    
    init();    
  }

  private void init()
  {
    // Use Box Layout for each horizontal slot
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    // Set our customized border into the main Container
    setBorder(new ToolbarBorder(false));    

    // Listens for mouse menu popup events
    slotMouseListener = new SlotMouseListener();
    
    // Listens for drag events on the Toolbars
    dragListener = new DragListener();

    // Pre-create the first horizontal slot
    addHorizontalSlot();    
  }
  
  /**
   * Add a new Horizontal Slot to the Toolbar Container for additional Toolbars.
   *
   * @return the new HorizontalSlot container just created and added.
   */
  private HorizontalSlot addHorizontalSlot()
  {
    return addHorizontalSlot(HorizontalSlot.DEFAULT_HEIGHT);
  }
  
  /**
   * Add a new Horizontal Slot to the Toolbar Container for additional Toolbars,
   * using the specified preferred height.
   *
   * @return the new HorizontalSlot container just created and added.
   */
  private HorizontalSlot addHorizontalSlot(int preferredHeight)
  {
    HorizontalSlot slot = new HorizontalSlot();
    
    // Capture mouse events in slots for the right-click popup
    slot.addMouseListener(slotMouseListener);
    slot.addSlotWrapListener(this);
    slot.setSlotNumber(getComponentCount());
    
    super.add(slot);
    
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(1, preferredHeight));
    
    return slot;
  }
  
  /**
   * Add this toolbar container to the Java Swing JFrame provided.
   *
   * @param frame the JFrame instance of add to.
   */
  public void addToFrame(JFrame frame)
  {
    Container     contentPane = frame.getContentPane();
    LayoutManager layoutMgr   = contentPane.getLayout();

    if (layoutMgr instanceof BorderLayout)
    {
      // If Border, add to the NORTH region
      contentPane.add(this, BorderLayout.NORTH);
      
      // Keep a reference to the parent JFrame
      parent = frame;

      // Capture JFrame resize events
      // frame.addComponentListener(new FrameComponentListener());
    }
    else
    {
      // TODO: Ask if we need to set a BorderLayout if it is not already?
      throw new IllegalArgumentException(
                "The JFrame's Content Pane does not use a BorderLayout.");
    }
  }
  
  /**
   * Overriden to capture the initial display of the ToolbarContainer.
   */
  public void doLayout()
  {
    // Perform the default behaviour first
    super.doLayout();

    if (!initialized) 
    {
      // First toolbar initialization
      initialized = true;
      
      ToolbarUtils.addDragListener(this, dragListener, true);      
    }    
  }
  
  /**
   * Add a Toolbar component into this Toolbar Container.
   *
   * @param  toolbar the Toolbar component to add.
   * @return the Toolbar instance just added.
   */
  public Toolbar add(Toolbar toolbar)
  {
    return add(toolbar, -1);
  }
  
  /**
   * Add a Toolbar component into this Toolbar Container.
   *
   * @param toolbar the Toolbar component to add.
   * @param index   the slot index to add to, or -1 to add to the end.
   */
  public Toolbar add(Toolbar toolbar, int index)
  {
    int slotNum = index;
    
    // Start with the first slot if position undetermined
    if (slotNum == -1)
      slotNum = 0;
      
    // Unknown slot, try adding to the end
    if (slotNum >= getComponentCount())
      slotNum = getComponentCount() - 1;
      
    // If Auto Restore is activated, retrieve the slot position for
    // this Toolbar component
    boolean autoSlot = false;
    if (autoRestore)
    {
      int autoSlotNum = restoreToolbarSlot(toolbar); 
      autoSlot        = (autoSlotNum != -1);
      
      if (autoSlot)
      {
        slotNum = autoSlotNum;
        
        // Create extra slots if necessary
        while (getComponentCount() <= slotNum)
          addHorizontalSlot();          
      }
    }
      
    // Retrieve the last slot
    HorizontalSlot slot = (HorizontalSlot) getComponent(slotNum);
    
    // Check if there is room to fit this Toolbar into the specified slot.
    // If there is not room, move to the next slot.
    // If we run out of slots, create a new slot and add into that.
    // If an auto slot has already been determined, bypass this.
    while (!autoSlot && !slot.isRoomToFit(toolbar))
    {
      slotNum++;
      
      if (slotNum >= getComponentCount()) // Slots ran out
      {
        slot = addHorizontalSlot();
      }
      else // Retrieve slot by number
      {
        slot = (HorizontalSlot) getComponent(slotNum);
      }
    }
    
    // Link the toolbar name to this slot
    toolbarSlotMap.put(toolbar.getToolbarName(), slot);

    // Add to the slot's flow
    toolbar.setSlot(slot.getSlotNumber());
    toolbar.setLocked(isLocked());
    slot.add(toolbar);
    
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(1, preferredHeight));
    
    // Auto restore the Toolbar position also
    if (autoRestore)
      toolbar.restore(getToolbarProperties());

    // Force a layout if toolbars are currently visible
    if (parent != null && parent.isVisible())
    {
      parent.validate();
      repaint();
    }
      
    return toolbar;
  }
  
  /**
   * Restore the previous slot position for this Toolbar component.
   * If the previous slot position is not available, then just returns -1.
   *
   * @param  toolbar     the Toolbar component to retrieve.
   * @return the restored slot position, or -1 if not found.
   */
  private int restoreToolbarSlot(Toolbar toolbar)
  {
    int        iSlot = -1;    
    Properties prop  = getToolbarProperties();
    
    // Attempt to read the slot position
    String slot = toolbarProperties.getProperty(toolbar.getToolbarId() + ".slot");
    
    try
    {
      if (slot != null) iSlot = Integer.parseInt(slot);
    }
    catch (NumberFormatException ex)
    {
      // Ignore
    }
    
    return iSlot;
  }
  
  /**
   * Retrieve the Toolbar Properties, load them if necessary.
   *
   * @return the Toolbar Properties object.
   */
  private Properties getToolbarProperties()
  {
    if (toolbarProperties == null)
    {
      // Load the properties if needed
      toolbarProperties = new Properties();
      
      try
      {
        FileInputStream fis = new FileInputStream(
            System.getProperties().getProperty("user.home") + 
            File.separator + ToolbarUtils.TOOLBAR_FILE );
            
        toolbarProperties.load(fis);
      }
      catch (Exception fex)
      {
        // Ignore
      }
    }
    
    return toolbarProperties;
  }
  
  /**
   * Record and store current Toolbar positions to disk.
   */
  private void storeToolbarProperties()
  {
    int        count = getComponentCount();
    Properties prop  = getToolbarProperties();
    
    for (int i = 0; i < count; i++)
    {
      HorizontalSlot slot = (HorizontalSlot) getComponent(i);
      
      for (int j = 0; j < slot.getComponentCount(); j++)
      {
        Toolbar tbar = (Toolbar) slot.getComponent(j);
        
        prop.setProperty(tbar.getToolbarId() + ".slot",  Integer.toString(i));
        prop.setProperty(tbar.getToolbarId() + ".prefX", Integer.toString(
                                                         tbar.getPreferredX() ));
      }
    }
    
    // Write to disk
    writeToolbarProperties();
  }
  
  /**
   * Write the Toolbar Properties to disk, if available.
   */
  private void writeToolbarProperties()
  {
    if (toolbarProperties != null)
    {
      try
      {
        FileOutputStream fos = new FileOutputStream(
            System.getProperties().getProperty("user.home") + 
            File.separator + ToolbarUtils.TOOLBAR_FILE, false );
            
        toolbarProperties.store(fos, 
        "IE6 Style Toolbar Component Settings for Java Swing" );
      }
      catch (Exception ex)
      {
        // Ignore
      }
    }
  }

  /**
   * Override standard Container add methods 
   * to warn against non-Toolbar additions.
   *
   * @see Container.add(Component)
   */
  public Component add(Component comp)
  {
    if (!(comp instanceof Toolbar)) // Must be Toolbar
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((Toolbar)comp);
  }
  
  /**
   * Override standard Container add methods 
   * to warn against non-Toolbar additions.
   *
   * @see Container.add(String,Component)
   */
  public Component add(String name, Component comp)
  {
    if (!(comp instanceof Toolbar)) // Must be Toolbar
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((Toolbar)comp);
  }
  
  /**
   * Override standard Container add methods 
   * to warn against non-Toolbar additions.
   *
   * @see Container.add(Component,int)
   */
  public Component add(Component comp, int index)
  {
    if (!(comp instanceof Toolbar)) // Must be Toolbar
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((Toolbar)comp, index);
  }
  
  /**
   * Informs of a slot wrap event.
   */
  public void slotWrapOccurred(SlotWrapEvent event)
  {
    Object source = event.getSource();
    
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(1, preferredHeight));

  System.out.println ("Slot Wrap event caught");
      //  ((Container)source).invalidate();
        
       // validate();
       parent.getContentPane().validate();
  }
  
  /**
   * Set the state of Toolbar Locking.
   *
   * @param toolbarsLocked true if they are locked, and false if not.
   */
  public void setLocked(boolean toolbarsLocked)
  {
    this.toolbarsLocked = toolbarsLocked;
    
    int slotCount       = getComponentCount();
    int slotItemCount   = 0;

    for (int i = 0; i < slotCount; i++)
    {
      HorizontalSlot slot = (HorizontalSlot) getComponent(i);
      slotItemCount       = slot.getComponentCount();
      
      for (int j = 0; j < slotItemCount; j++)
      {
        Toolbar toolbar = (Toolbar) slot.getComponent(j);
        toolbar.setLocked(toolbarsLocked);
      }
    }
    
    validate();
  }
  
  /**
   * Returns the state of Toolbar Locking.
   *
   * @return true if they are locked, and false if not.
   */
  public boolean isLocked()
  {
    return toolbarsLocked;
  }
  
  /**
   * Popup the right-click menu for Toolbar Container configuration.
   */
  private void popupToolbarMenu(Component comp, int x, int y)
  {
    JPopupMenu popupMenu     = new JPopupMenu();
    Font       menuFont      = null;
    int        slotCount     = getComponentCount();
    int        slotItemCount = 0;
    int        toolbarCount  = 0;

    // Load the tick images
    selectedIcon   = new ImageIcon(ClassLoader.getSystemResource(SELECTED_ICON));
    unselectedIcon = new ImageIcon(ClassLoader.getSystemResource(UNSELECTED_ICON));

    // Create a listener to wait for menu actions        
    MenuActionListener menuListener = new MenuActionListener();

    boolean  hidden = false;
    Set      keySet = toolbarSlotMap.keySet();
    String[] names  = (String[]) keySet.toArray(new String[0]);
    
    // Loop over each list of Toolbar names
    while (names != null)
    {
      // Loop over the current list of Toolbar names
      for (int i = 0; i < names.length; i++)
      {
        // Locate the Toolbar instance from the maps
        HorizontalSlot slot   = (HorizontalSlot) toolbarSlotMap.get(names[i]);
        Toolbar        toolbar;
        if (slot != null)
          toolbar = slot.getToolbar(names[i]);
        else
          toolbar = (Toolbar) toolbarHiddenMap.get(names[i]);
          
        if (toolbar != null) // Valid Toolbar
        {
          String    name = toolbar.getToolbarName();
          JMenuItem item = new JMenuItem(name);
          
          toolbarCount++;
          item.setIcon(hidden ? unselectedIcon : selectedIcon);
          item.addActionListener(menuListener);
          
          popupMenu.add(item);
        }
      }
      
      if (hidden)
        break; // break after finished processing hidden items
      
      keySet = toolbarHiddenMap.keySet();
      names  = (String[]) keySet.toArray(new String[0]);
      hidden = true; // now processing hidden items
    }
    
    if (toolbarCount > 0)
      popupMenu.addSeparator();
    
    // Add the Lock Toolbars item
    JMenuItem item = new JMenuItem(ACTION_LOCK_TOOLBARS);
    item.setIcon(isLocked() ? selectedIcon : unselectedIcon);
    item.addActionListener(menuListener);
    popupMenu.add(item);
    
    // Reset to PLAIN fonts throughout
    ToolbarUtils.setFontForSubComponents(popupMenu, 
                 popupMenu.getFont().deriveFont(Font.PLAIN), true);
                 
    popupMenu.show(comp, x, y);
  }
  
  /**
   * Compacts empty slots by removing any slots that do not contain any
   * Toolbars. This may shrink the overall size of the Toolbar.
   */
  private void compactSlots()
  {
    boolean doLoop;
    boolean removed   = false;
    int     decrement = 0;
    int     count     = getComponentCount();
    
    if (count > 1) // We need at least one slot (empty or otherwise)
    {
      for (int slotNum = 0; slotNum < count; slotNum++)
      {
        doLoop = true;
        while (doLoop)
        {
          HorizontalSlot slot = (HorizontalSlot) getComponent(slotNum);
          if (slot.getComponentCount() == 0)
          {
            remove(slot);
            count--;
            decrement++;
            removed = true;
            doLoop  = (slotNum < count);
          }
          else
          {
            // Need to decrement the remaining slot numbers
            if (decrement > 0)
            {
              int tbCount = slot.getComponentCount();
              slot.setSlotNumber(slot.getSlotNumber() - decrement);
              
              // Also need to re-set slot numbers in Toolbar children
              for (int tb = 0; tb < tbCount; tb++)
              {
                Toolbar tbar = (Toolbar) slot.getComponent(tb);
                tbar.setSlot(slot.getSlotNumber());
              }
            }
              
            doLoop = false;
          }
        }
      }
    }
      
    preferredHeight = calculatePreferredHeight();
    setPreferredSize(new Dimension(1, preferredHeight));
    
    if (removed && parent != null)
      parent.getContentPane().validate();
    else
    {
      validate();
      repaint();
    }
  }
  
  /**
   * Calculate the preferred height of this Container based on its contents.
   */
  private int calculatePreferredHeight()
  {
    int height = getInsets().top + getInsets().bottom;
    int count  = getComponentCount();
    
    for (int i = 0; i < count; i++)
      height += ((HorizontalSlot)getComponent(i)).getPreferredHeight();
      
    return height;
  }
  
  /** Local helper class for capturing popup menu item actions. */
  private class MenuActionListener implements ActionListener
  {
    /**
     * A popup menu item has been selected.
     */
    public void actionPerformed(ActionEvent e)
    {
      String cmd = e.getActionCommand();
      
      if (cmd.equals(ACTION_LOCK_TOOLBARS))
      {
        // Toggle the Toolbar Lock
        setLocked(!isLocked());
      }
      else // Toolbar name selected
      {
        Toolbar        toolbar;
        HorizontalSlot slot   = (HorizontalSlot) toolbarSlotMap.get(cmd);
        
        if (slot != null) // Found slot, Toolbar is currently visible
        {
          // Remove this toolbar from the slot
          toolbarSlotMap.remove(cmd);
          toolbar = slot.getToolbar(cmd);
          slot.remove(toolbar);
          
          // Add to the hidden toolbars list
          toolbarHiddenMap.put(toolbar.getToolbarName(), toolbar);
          
          // Compact (and remove) any empty slots
          compactSlots();
        }
        else // Toolbar is not currently visible
        {
          toolbar = (Toolbar) toolbarHiddenMap.get(cmd);
          if (toolbar != null) // Found hidden
          {
            // Re-add the hidden Toolbar (make it visible)
            toolbarHiddenMap.remove(cmd);
            add(toolbar);
          }
          else
          {
            // Just in case the impossible happens
            throw new RuntimeException(
                  "Logic Error: Popup menu toolbar name was not found in the " + 
                  "slotMap or hiddenMap - " + cmd );
          }
        }
      }
    }
  }
  
  /** Local helper class for capturing resize events. *
  private class FrameComponentListener extends ComponentAdapter
  {
     **
     * Invoked whenever the parent Component is resized.
     *
     * @param  componentEvent the Component resize event.
     * 
    public void componentResized(ComponentEvent componentEvent) 
    {
      // Initial sizing may occur on the JFrame before it is visible
      if (componentEvent.getComponent().isVisible())
      {
        // Resize the slots
        handleFrameResize();
      }
    }
  }
  */
  
  /** Local helper class for capturing HorizontalSlot mouse actions. */
  private class SlotMouseListener extends MouseAdapter
  {
    /**
     * Invoked when a mouse button has been pressed on this component.
     */
    public void mousePressed(MouseEvent e)
    {
      // Different platforms will use different mouse buttons for popup menus
      if (e.isPopupTrigger())
        popupToolbarMenu(e.getComponent(), e.getX(), e.getY());
    }
    
    /**
     * Invoked when a mouse button has been released on this component.
     */
    public void mouseReleased(MouseEvent e)
    {
      // Different platforms will use different mouse buttons for popup menus
      if (e.isPopupTrigger())
        popupToolbarMenu(e.getComponent(), e.getX(), e.getY());
    }
  }
  
  /** Local helper class for capturing mouse events and redispatching. */
  private class DragListener extends MouseInputAdapter
  {
    public void mouseMoved(MouseEvent e) 
    {
    }

    /**
     * Captures mouse drag events for Toolbar repositioning.
     *
     * @param e the MouseEvent for the drag.
     */
    public void mouseDragged(MouseEvent e) 
    {
      if (isDragging)
      {
        boolean slotSwitch = false;
        Point   tbcPoint   = SwingUtilities.convertPoint(e.getComponent(), 
                             e.getPoint(), ToolbarContainer.this);
                             
        tbcPoint.x -= dragOffset;
        
        // Check for drag below the toolbar container area
        if (tbcPoint.y > ToolbarContainer.this.getSize().height)
        {
          HorizontalSlot lastSlot = (HorizontalSlot) ToolbarContainer.this.
                                     getComponent(ToolbarContainer.this.
                                     getComponentCount() - 1);
                                     
          // Check if a new slot needs to be added
          if (lastSlot.getComponentCount() > 1)
          {
            addHorizontalSlot(lastSlot.getPreferredHeight());
            parent.getContentPane().validate();
            return;
          }
        }
                         
        HorizontalSlot slot = ToolbarUtils.getSlotFromMousePoint(
                              ToolbarContainer.this, tbcPoint);
                              
        if (slot != null)
        {
          // Now in different slot (need to switch)
          if (slot.getSlotNumber() != dragFromSlot.getSlotNumber())
          {
            slotSwitch = true;
            HorizontalSlot oldSlot = dragFromSlot;
            
            oldSlot.remove(dragToolbar);
            dragToolbar.setPreferredX(tbcPoint.x);
            slot.setSwapped(true);
            slot.add(dragToolbar);
            
            dragFromSlot = slot;
            
            preferredHeight = calculatePreferredHeight();
            setPreferredSize(new Dimension(1, preferredHeight));
            
            oldSlot.repaint();
            ToolbarContainer.this.validate();
            parent.getContentPane().validate();
          }
        }
  
        if (!slotSwitch) // Dragging around inside the slot
        {
          Toolbar prevToolbar = dragFromSlot.getPreviousToolbar(dragToolbar);
          Toolbar nextToolbar = dragFromSlot.getNextToolbar(dragToolbar);

          // Allow horizontal dragging if not the only Toolbar on this line
          if (prevToolbar != null || nextToolbar != null)
          {
            boolean dragLeft  = (dragToolbar.getLocation().x - tbcPoint.x > 0);
            boolean dragRight = (dragToolbar.getLocation().x - tbcPoint.x < 0);
            
            // In order to drag left, a previous toolbar MUST be present
            if (dragLeft && prevToolbar != null)
            {
              int prevWidth = tbcPoint.x - prevToolbar.getLocation().x;
              if (prevWidth < 0) // Swap the Toolbars
              {
                int dragWidth = dragToolbar.getPreferredSize().width;
                int pvWidth   = prevToolbar.getPreferredSize().width;
                
                int dragX = dragToolbar.getPreferredX();
                dragToolbar.setPreferredX(prevToolbar.getPreferredX());
                prevToolbar.setPreferredX(dragX);
                
                dragFromSlot.swapToolbars(prevToolbar, dragToolbar);

                prevToolbar.invalidate();
                ToolbarContainer.this.validate();
              }
              else
              {
                prevWidth = Math.max(prevWidth, ToolbarUtils.MIN_SIZE);
                prevToolbar.setToolbarSize( new Dimension(prevWidth, 
                                            prevToolbar.getPreferredHeight()) );
  
                dragToolbar.setPreferredX(prevToolbar.getLocation().x + prevWidth);
  
                // If there is a next toolbar, resize the drag toobar to fill gap
                if (nextToolbar != null)
                {
                  dragToolbar.setToolbarSize(new Dimension(
                                             nextToolbar.getLocation().x - 
                                             dragToolbar.getPreferredX() - 1,
                                             dragToolbar.getPreferredHeight() ));
                }
                else
                {
                  dragToolbar.setToolbarSize(new Dimension(
                    dragFromSlot.getSize().width - dragFromSlot.getInsets().left -
                    dragFromSlot.getInsets().right - dragToolbar.getPreferredX() - 1,
                    dragToolbar.getPreferredHeight() ));
                }
                            
                dragToolbar.invalidate();
                ToolbarContainer.this.validate();
              }
            }
            
            if (dragRight)
            {
              int     toRemove = 0;
              boolean rightEdge = false;
              
              if (nextToolbar != null && prevToolbar != null)
              {
                int dragDist = tbcPoint.x - dragToolbar.getLocation().x;
                toRemove     = dragDist;
                dragToolbar.setPreferredX(dragToolbar.getPreferredX() + dragDist);
                
                toRemove = dragToolbar.shrinkWidth(toRemove);
                
                // Allocate extra size reduction to the next few toolbars
                Toolbar rToolbar = nextToolbar;
                while (rToolbar != null && toRemove > 0)
                {
                  toRemove = rToolbar.shrinkWidth(toRemove);
                  rToolbar.setPreferredX(rToolbar.getPreferredX() + (dragDist - toRemove));
                  
                  rToolbar = dragFromSlot.getNextToolbar(rToolbar);
                }
                
                rightEdge = (toRemove > 0);
              }
              else if (nextToolbar == null && prevToolbar != null)
              {
                toRemove  = tbcPoint.x - dragToolbar.getLocation().x;
                toRemove  = dragToolbar.shrinkWidth(toRemove);
                rightEdge = (toRemove > 0); // against the right edge                  
              }

              boolean swapped = false;
              if (nextToolbar != null)
              {
                if (tbcPoint.x > nextToolbar.getLocation().x)
                {
                  // Ensure there is at least MIN_SIZE between X positions
                  int diff = dragToolbar.getPreferredX() - nextToolbar.getPreferredX();
                  if (diff < ToolbarUtils.MIN_SIZE)
                  {
                    nextToolbar.setPreferredX( nextToolbar.getPreferredX() - 
                                               ToolbarUtils.MIN_SIZE + diff);
                  }
                  
                  // Swap this toolbar with the next
                  swapped = true;
                  dragFromSlot.swapToolbars(dragToolbar, nextToolbar);
                }
              }
              
              if (!swapped && prevToolbar != null)
              {
                prevToolbar.setToolbarSize(new Dimension(
                  tbcPoint.x - prevToolbar.getLocation().x - toRemove, 
                  prevToolbar.getPreferredHeight() ));
                
                dragToolbar.setPreferredX(tbcPoint.x - toRemove);
              }
              
              dragToolbar.invalidate();
              ToolbarContainer.this.validate();
            }
          }
        }
      }
    }

    public void mouseClicked(MouseEvent e) 
    {
    }

    public void mouseEntered(MouseEvent e) 
    {
    }

    public void mouseExited(MouseEvent e) 
    {
    }

    /**
     * Captures the mouse pressed event in the ToolbarContainer.
     * If the mouse has been pressed on a DragBar, then a drag operation is
     * initiated.
     *
     * @param e a MouseEvent for the press.
     */
    public void mousePressed(MouseEvent e) 
    {
      if ( e.getComponent() instanceof DragBar &&
           e.getButton() == e.BUTTON1 )
      {
        isDragging = true;
        dragOffset = e.getX();
        
        dragToolbar  = (Toolbar) e.getComponent().getParent();
        dragFromSlot = (HorizontalSlot) dragToolbar.getParent();
        
        dragToolbar.getDragBar().setDragging(isDragging);
      }
    }

    /**
     * Captures the mouse released event is the ToolbarContainer.
     * If the mouse has been released during a drag operation, final drop
     * processing is performed.
     *
     * @param e a MouseEvent for the release.
     */
    public void mouseReleased(MouseEvent e) 
    {
      if (isDragging) // Mouse drop in progress
      {
        isDragging = false;
        
        if (dragToolbar != null)
          dragToolbar.getDragBar().setDragging(isDragging);
          
        compactSlots();
        
        // Write new Toolbar positions to disk
        if (autoRestore)
          storeToolbarProperties();
      }
    }
  }
}
