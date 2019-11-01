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
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;

import javax.swing.text.JTextComponent;

/**
 * <P> A horizontal Toolbar strip that can be added to the main ToolbarContainer
 * class.  The ToolbarContainer can contain as many Toolbar strips as are
 * required by the application.
 *
 * <P> Each Toolbar is a set of JComponents which are individually added to the
 * Toolbar, ie. JButtons, JTextFields, a JMenuBar, etc.  
 *
 * <P> These Toolbars can be dragged around the main ToolbarContainer using the 
 * mouse, and the toolbars will shrink, grow and resize according to the space 
 * allowances. If the toolbars are shrunk beyond the components, then a 
 * graphical More Button will appear on the right of the toolbar.
 *
 * @see     ToolbarContainer
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class Toolbar extends JPanel
{
  /** Prefix used for name generation when needed */
  private static final String TOOLBAR_NAME = "Toolbar";

  /** Prefix used for ID generation when needed */
  private static final String TOOLBAR_ID   = "TOOLBAR_ID_";
  
  private static final String ARGUMENT_EXCEPTION =
          "The Toolbar only accepts addition of Swing JComponents.";
          
  private static final int GAP = 4;
  
  /** The human readable name of this toolbar, if supplied */
  protected String   toolbarName;
  
  /** The unique ID String used by this toolbar */
  protected String   toolbarId;

  /** Unique class counter for ID generation when needed */  
  private static int toolbarIndex = 1;
  
  /** The visual drag bar to the left of the toolbar (when unlocked) */
  private DragBar    dragBar;
  
  /** Space for toolbar items to be added into */
  private Container  itemSpace;
  
  /** The visual "more" button to the right of the toolbar (when shrunk) */
  private MoreButton moreButton;
  
  /** The preferred X location for this Toolbar in the slot */
  private int        preferredX;
  
  /** The preferred width of this Toolbar based on its contents */
  private int        preferredWidth;
  
  /** The preferred height of this Toolbar based on its contents */
  private int        preferredHeight;
  
  /** The minimum width of this Toolbar based on its contents */
  private int        minimumWidth;

  /** The slot number that this Toolbar belongs to */
  private int        slot;
  
  /** The state of this Toolbar, locked or unlocked */
  private boolean    locked;
  
  /** List of hidden components (when toolbar isn't big enough) */
  private List hiddenList  = new ArrayList();
  
  /** Map of the cloned JMenu objects */
  private Map  menuMap     = new HashMap();
  
  /** Map of the cloned JMenuItem objects for event redispatching */
  private Map  menuItemMap = new HashMap();
  
  /** An ActionListener used to redispatch menu events */
  private MenuActionListener menuActionListener = null;
  
  /**
   * Creates a default Toolbar.
   */
  public Toolbar()
  {
    super();
    init();
  }
  
  /**
   * Creates a Toolbar with the specified name.
   *
   * @param toolbarName the name to use for this toolbar.
   */
  public Toolbar(String toolbarName)
  {
    super();
    this.toolbarName = toolbarName;
    init();
  }
  
  /**
   * Creates a Toolbar with the specified name, and using the 
   * toolbarId supplied.
   *
   * @param toolbarName the name to use for this toolbar.
   * @param toolbarId   the identifier to use for this toolbar.
   */
  public Toolbar(String toolbarName, String toolbarId)
  {
    super();
    this.toolbarName = toolbarName;
    this.toolbarId   = toolbarId;
    init();
  }

  /**
   * Returns true if one or more toolbar items is "stretchy".
   * Stretchy toolbar items can expand to their MaximumSize.   
   */
  public boolean containsStretchableItems()
  {
    return getStretchableComponents().length > 0;
  }
  
  /**
   * Iterate through itemSpace panel and return all stretchy components.
   */
  public Component[] getStretchableComponents()
  {
    List stretchyList = new ArrayList();
    Component[] componentArray = itemSpace.getComponents();
    for (int i=0; i < componentArray.length; i++)
    {
      ToolbarItem tbi = (ToolbarItem) componentArray[i];
      if (tbi.isStretchable())
      {
        stretchyList.add(tbi);
      }
    }
    return (Component[])stretchyList.toArray(new Component[stretchyList.size()]);
  }
  
  public void removeAll() {
      itemSpace.removeAll();
      this.setVisible(false);
  }
  
  /**
   * Add any Action object to this Toolbar.
   *
   * @param  action a class that implements the Action interface.
   * @return the component created to support this Action.
   */
  public Component add(Action action)
  {
    JButton button = new JButton( (String) action.getValue(Action.NAME),
                                  (Icon)   action.getValue(Action.SMALL_ICON) );
    
    button.addActionListener(action);
    return add(button);
  }
  
  /**
   * Add any Swing JComponent to this Toolbar.
   *
   * @param  component the component to add.
   * @return the component just added.
   */
  public Component add(JComponent component)
  {
    JComponent item = component;
    Font       font = item.getFont().deriveFont(Font.PLAIN);

    // Tone down all Fonts to PLAIN only
    item.setFont(font);
    
    if (item instanceof JButton)
    {
      // Turn off the default border
      item.setBorder(BorderFactory.createEmptyBorder());
      item.setFocusable(false);
      item = new ToolbarButton(item, 2 * GAP, GAP);
    }
    else if (item instanceof JLabel)
    {
      ((JLabel)item).setHorizontalAlignment(JLabel.CENTER);
      item = new ToolbarItem(item, GAP, 0);
    }
    else if (item instanceof JComboBox)
    {
      ToolbarItem textBorder = new ToolbarItem(item, true);

      // Make combo boxes white
      ((JComboBox)item).setBackground(Color.white);
      
      // Add a horizontal gap around the text field
      textBorder.setBorder(BorderFactory.createEmptyBorder(2, GAP, 2, GAP));

      item = textBorder;
    }
    else if (item instanceof JMenuBar)
    {
      boolean recurse = true;
      
      // Turn off the default border on the JMenuBar
      item.setBorder(BorderFactory.createEmptyBorder());
      
      // Set the PLAIN font on all sub-components (recursively)
      ToolbarUtils.setFontForSubComponents(item, font, recurse);

      ToolbarItem textBorder = new ToolbarItem(item, true);

      // Add a horizontal gap around the text field
      textBorder.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
      textBorder.add(item);

      item = textBorder;
    }
    else if (item instanceof JMenu)
    {
      boolean  recurse   = true;
      boolean  inToolbar = false;
      JMenuBar bar       = null;
      int      count     = itemSpace.getComponentCount();
      
      if (count > 0)
      {
        // Retrieve the last Toolbar item (looking for a JMenuBar)
        Component comp = itemSpace.getComponent(count - 1);
        if (comp instanceof Container)
        {
          bar       = (JMenuBar) ToolbarUtils.getFirstSubComponent( 
                                 (Container) comp, JMenuBar.class, recurse );
          inToolbar = (bar != null);
        }
      }
       
      if (bar == null)
        bar = new JMenuBar();
        
      bar.add(item);
      
      if (!inToolbar)
        add(bar);
      else
        ToolbarUtils.setFontForSubComponents(item, font, recurse);
        
      item = null;
    }
    else // JTextField, etc
    {
      ToolbarItem textBorder = new ToolbarItem(item, (item instanceof JTextComponent));

      // Add a horizontal gap around the text field
      textBorder.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
      textBorder.add(item);

      item = textBorder;
    }
    
    // Add to the flow layout
    if (item != null)
    {
      preferredHeight = Math.max(preferredHeight, item.getPreferredSize().height);
      itemSpace.add(item);
    }
      
    this.setVisible(true);
    // Re-set the minimum/preferred widths
    preferredWidth = getPreferredSize().width;
    minimumWidth   = getMinimumSize().width;

    return component;
  }
  
  /**
   * Override standard Container add methods and delegate to add(JComponent).
   *
   * @see Container.add(Component)
   */
  public Component add(Component comp)
  {
    if (!(comp instanceof JComponent)) // Must be a JComponent
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((JComponent)comp);
  }
  
  /**
   * Override standard Container add methods and delegate to add(JComponent).
   *
   * @see Container.add(String,Component)
   */
  public Component add(String name, Component comp)
  {
    if (!(comp instanceof JComponent)) // Must be a JComponent
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((JComponent)comp);
  }
  
  /**
   * Override standard Container add methods and delegate to add(JComponent).
   *
   * @see Container.add(Component,int)
   */
  public Component add(Component comp, int index)
  {
    if (!(comp instanceof JComponent)) // Must be a JComponent
      throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
    
    return add((JComponent)comp);
  }
  
  /**
   * Restore this Toolbar's position from the Properties file.
   *
   * @param prop the Toolbar properties file.
   */
  public void restore(Properties prop)
  {
    String prefX = prop.getProperty(getToolbarId() + ".prefX");
    
    try
    {
      if (prefX != null) preferredX = Integer.parseInt(prefX);
    }
    catch (NumberFormatException ex)
    {
    }
  }
  
  /**
   * Popup the right-click menu for Toolbar Container configuration.
   */
  void popupMoreMenu(Component comp, int x, int y)
  {
    JPopupMenu popupMenu = new JPopupMenu();
    int count = hiddenList.size();
    
    for (int i = 0; i < count; i++)
    {
      Component   itemObj = (Component) hiddenList.get(i);
      ToolbarItem item    = null;
      
      if (!(itemObj instanceof JMenu))
        item = (ToolbarItem) itemObj;
        
      if (item == null) // JMenu
        popupMenu.add(itemObj);
      else if (item.canDisplayInPopup()) // other JComponent
        popupMenu.add(item);
    }
    
    // Only display popup if there is at least one menu item added
    if (popupMenu.getComponentCount() > 0)
    {
      MoreItemActionListener listener = new MoreItemActionListener(popupMenu);
      
      ToolbarUtils.attachActionListener(popupMenu, listener, true);
      ToolbarUtils.setFontForSubComponents(popupMenu, 
                   popupMenu.getFont().deriveFont(Font.PLAIN), true);
                   
      popupMenu.show(comp, x, y);
    }
    else
    {
      System.out.println("Nothing to display.");
    }
  }

  /**
   * Sets the locked state of this Toolbar.
   *
   * @param locked true if this Toolbar is locked.
   */
  public void setLocked(boolean locked)
  {
    if (this.locked != locked)
    {
      this.locked = locked;
      
      dragBar.setLocked(locked);
      
      if (locked)
        dragBar.setVisible(dragBar.isDivider());
      else
        dragBar.setVisible(true);
    }
  }
  
  /**
   * Return the locked state of this Toolbar.
   *
   * @return true if this Toolbar is locked.
   */
  public boolean isLocked()
  {
    return locked;
  }
  
  /**
   * Return the original preferred width for this Toolbar.
   */
  public int getPreferredWidth()
  {
    return preferredWidth;
  }
  
  /**
   * Return the original preferred height for this Toolbar.
   */
  public int getPreferredHeight()
  {
    return preferredHeight;
  }
  
  /**
   * Return the preferred X Location for this Toolbar.
   */
  public int getPreferredX()
  {
    return preferredX;
  }
  
  /**
   * Sets the preferred X Location for this Toolbar.
   */
  public void setPreferredX(int x)
  {
    preferredX = x;
    
    // Display the Drag Bar Divider if needed
    dragBar.setDivider(preferredX > 0);
  }
  
  /**
   * Returns the DragBar component used by this Toolbar.
   */
  public DragBar getDragBar()
  {
    return dragBar;
  }
  
  /**
   * Set the slot number that this Toolbar component belongs to.
   */
  public void setSlot(int slotNum)
  {
    slot = slotNum;
  }
  
  /**
   * Returns the slot number that this Toolbar component belongs to.
   */
  public int getSlot()
  {
    return slot;
  }
  
  /**
   * Sets this toolbar up with a new Preferred Size, so that the HorizontalSlot
   * can lay it out correctly. Toolbar items are hidden or shown depending on
   * the new size.
   *
   * @param newSize the new Preferred Size.
   */
  public void setToolbarSize(Dimension newSize)
  {
    if (isVisible() && getSize().width > 0)
    {
      boolean visible      = true;
      int     i;
      int     currWidth    = 0;
      int     itemWidth    = 0;
      int     stretchCount = 0;
      int     count        = itemSpace.getComponentCount();
      int     newWidth     = newSize.width - dragBar.getSize().width;

      if (hiddenList.size() > 0)
        newWidth -= moreButton.getSize().width;
        
      //
      // REMOVE items that will no longer fit.
      //
            
      for (i = 0; i < count; i++)
      {
        ToolbarItem item = (ToolbarItem) itemSpace.getComponent(i);
        
        if (item.isStretchable())
        {
          stretchCount++;
          itemWidth  = item.getMinimumWidth();
          currWidth += itemWidth;
        }
        else
        {
          itemWidth  = item.getPreferredWidth();
          currWidth += itemWidth;
        }

        if (currWidth > newWidth)
        {
          currWidth -= itemWidth;
          break; // found the first non-visible item
        }
      }
      
      // Start removing non-visible components
      int hiddenIndex = 0;
      for ( ; i < itemSpace.getComponentCount(); )
      {
        if (hiddenList.size() == 0) // More button is now needed
          newWidth -= moreButton.getSize().width;
          
        hiddenList.add(hiddenIndex++, itemSpace.getComponent(i));
        itemSpace.remove(i);
      }
      
      //
      // INSERT items that may now fit.
      //
      
      int index = 0;
      while (hiddenList.size() > index)
      {
        itemWidth = 0;
        Object itemObj = hiddenList.get(index);
        
        if (itemObj instanceof JMenu)
        {
          index++;
        }
        else // other JComponents
        {
          ToolbarItem item = (ToolbarItem) itemObj;
          
          if (item.isStretchable())
            itemWidth = item.getMinimumWidth();
          else
            itemWidth = item.getPreferredWidth();
            
          if (currWidth + itemWidth < newWidth)
          {
            currWidth += itemWidth;
            itemSpace.add(item);
            hiddenList.remove(0);
            
            if (item.isStretchable())
              stretchCount++;
          }
          else
            break;
        }
      }
      
      //
      // STRETCH remaining components (where possible)
      //
      
      if (stretchCount > 0)
      {
        int stretchWidth = (newWidth - currWidth) / stretchCount;
        for (i = 0; i < itemSpace.getComponentCount(); i++)
        {
          ToolbarItem item = (ToolbarItem) itemSpace.getComponent(i);
          
          if (item.isStretchable())
          {
            if (item.getWrappedComponent() instanceof JMenuBar)
            {
              resizeMenuBar(item.getMinimumWidth() + stretchWidth, item);
            }
            else
            {
              item.setPreferredSize(new Dimension(item.getMinimumWidth() + 
                                    stretchWidth, item.getPreferredSize().height));
            }
          }
        }
      }
    }
    
    // Set the "more" button visible if it's needed
    boolean needMoreButton = needMoreButton();
    if (needMoreButton != moreButton.isVisible())
      moreButton.setVisible(needMoreButton);
    
    // Now set the new preferred size
    setPreferredSize(newSize);
  }
  
  /**
   * Counts the leading stretchable components up to but not including 
   * the last component. 
   */
  private int getLeadingStretchCount()
  {
    Component[] stretch = getStretchableComponents();
    int         leading = 0;
    int         count   = stretch.length;

    // If the last component in the Toolbar is stretchable, dont use this in
    // the leading space calculation.
    if (itemSpace.getComponentCount() > 0)
    {
      ToolbarItem item = (ToolbarItem) itemSpace.getComponent(
                         itemSpace.getComponentCount() - 1);
  
      if (item.isStretchable())
        count--;
    }
      
    return count;
  }
  
  /**
   * Calculates any additional space occupied by stretchable components leading
   * up to but not including the last component. Additional space is calculated 
   * by the "current size" - "minimum size".
   */
  private int getLeadingStretchSpace(Component[] stretch, int leadCount)
  {
    int leading = 0;
    
    for (int i = 0; i < leadCount; i++)
    {
      ToolbarItem item = (ToolbarItem) stretch[i];
      
      leading += (item.getSize().width - item.getMinimumWidth());
    }

    return leading;
  }
  
  /**
   * Find the last JMenuBar, or null if there is none.
   */
  private JMenuBar getLastMenuBar()
  {
    int count = itemSpace.getComponentCount();
    for (int i = count - 1; i >= 0; i--)
    {
      Component comp = itemSpace.getComponent(i);
      if (comp instanceof JMenuBar)
        return (JMenuBar) comp;
    }
    
    return null;
  }
  
  /**
   * Helper method to shrink down the JMenuBar to a size small enough to be
   * fully visible. Additional JMenu items are placed in the hidden list,
   * and a "more" button will appear.
   *
   * @param newBarWidth the new desired width of the menu bar.
   * @param item        a ToolbarItem that contains a JMenuBar.
   */
  private void resizeMenuBar(int newBarWidth, ToolbarItem item)
  {
    boolean  first       = true;
    JMenuBar bar         = (JMenuBar) item.getWrappedComponent();
    int      menuCount   = bar.getMenuCount();
    int      hiddenCount = hiddenList.size();
    
    if (menuCount > 0)
    {
      // Remove all hidden items from the list that are now visible on the menu bar
      int i = 0;
      while (hiddenList.size() > i)
      {
        Object itemObj = hiddenList.get(i);
        if (itemObj instanceof JMenu)
        {
          // Retrieve the real JMenu item (matched with clone)
          JMenu jitem = (JMenu) menuMap.get(itemObj);
          if (jitem.getLocation().x + jitem.getSize().width < newBarWidth)
          {
            hiddenList.remove(i);
            menuMap.remove(itemObj);
          }
          else
            i++;
        }
        else
        {
          i++;
        }
      }
    
      // Now add hidden items to the list if they are no longer visible
      int   hiddenIndex = 0;
      JMenu lastMenu    = bar.getMenu(menuCount - 1);
      
      while (lastMenu.getLocation().x + lastMenu.getSize().width > newBarWidth)
      {
        if (!menuMap.containsValue(lastMenu))
        {
          if (menuActionListener == null)
            menuActionListener = new MenuActionListener();
            
          JMenu clonedMenu = ToolbarUtils.cloneMenu(
                             lastMenu, menuActionListener, menuItemMap );
          hiddenList.add(hiddenIndex++, clonedMenu);
          
          menuMap.put(clonedMenu, lastMenu);
        }
          
        menuCount--;
        
        if (menuCount < 0)
          break;
          
        lastMenu = bar.getMenu(menuCount - 1);
      }
    }
  }
  
  /**
   * Attempts to shrink the Toolbar component width by the amount specified.
   * If shrinking is not possible, or it cannot be shrunk completely, the
   * remaining unallocated width is returned.
   *
   * @return any width unallocation (not shrunk on this component).
   */
  public int shrinkWidth(int toRemove)
  {
    int ableToShrink = 0;
    int width        = getSize().width;

    if (width - toRemove < ToolbarUtils.MIN_SIZE) // Cannot shrink full amount
      ableToShrink = width - ToolbarUtils.MIN_SIZE;
    else
      ableToShrink = toRemove;

    setToolbarSize(new Dimension(width - ableToShrink, getPreferredHeight()));
    
    return toRemove - ableToShrink;
  }
  
  /**
   * Determines if the "more" button is required based on the contents of the
   * hidden list.
   *
   * @return true if the more button is required.
   */
  private boolean needMoreButton()
  {
    for (int i = 0; i < hiddenList.size(); i++)
    {
      Component itemObj = (Component) hiddenList.get(i);
      if (itemObj instanceof JMenu)
        return true;
        
      ToolbarItem item = (ToolbarItem) itemObj;
      if (item.canDisplayInPopup())
        return true;
    }
    
    return false;
  }
  
  /**
   * Retrieves the name given to this Toolbar.
   *
   * @return a String name.
   */
  public String getToolbarName()
  {
    return toolbarName;
  }
  
  /**
   * Retrieves a unique ID given or generated for this Toolbar.
   *
   * @return a unique String ID.
   */
  public String getToolbarId()
  {
    return toolbarId;
  }
  
  /**
   * String representation of this Toolbar, used for testing.
   */
  public String toString()
  {
    return "[" + toolbarId + "] " + toolbarName + " (in slot " + slot + ")";
  }
  
  /**
   * Initialize this toolbar instance before use.
   */
  protected void init()
  {
    setLayout(new BorderLayout());
    
    slot           = 0;
    locked         = false;
    preferredX     = 0;
    preferredWidth = getPreferredSize().width;
    minimumWidth   = getMinimumSize().width;

    // itemSpace = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    itemSpace = new JPanel(new ToolbarLayout());
    super.add(itemSpace, BorderLayout.CENTER);
    
    dragBar = new DragBar();
    super.add(dragBar, BorderLayout.WEST);
    
    moreButton = new MoreButton();
    moreButton.addActionListener(new MoreActionListener());
    super.add(moreButton, BorderLayout.EAST);
    
    // If the toolbar has not been given a name, generate one.
    boolean increment = false;
    if (toolbarName == null)
    {
      increment   = true;
      toolbarName = TOOLBAR_NAME + toolbarIndex;
    }
    
    // If the toolbar has not been given an ID, generate a unique one.
    if (toolbarId == null)
    {
      increment = true;
      toolbarId = TOOLBAR_ID + toolbarIndex;
    }
    
    if (increment) {
      toolbarIndex++;
    }
    this.setVisible(false); // it should only be visible where there is stuff in it!
  }

  /**
   * Extension of JMenuItem that permits a toolbarElement reference to be supplied.
   */
  class MoreMenuItem extends JMenuItem implements ActionListener
  {
    ToolbarItemIntf toolbarItem_; // reference to corresponding toolbar item
    
    public MoreMenuItem(String label, ToolbarItemIntf toolbarItem)
    {
      super(label); // toolbarElement.getIcon()
      toolbarItem_ = toolbarItem;
      addActionListener(this);
    }
    
    /**
     * Invoked whenever a MoreMenuItem is pressed.  
     * Relays click to toolbarItem instance.
     */
    public void actionPerformed(ActionEvent actionEvent)
    {
      getToolbarItem().actionPerformed(actionEvent);
    }
    
    public ToolbarItemIntf getToolbarItem()
    {
      return toolbarItem_;
    }
  }
  
  /** Local helper class to capture More button actions. */
  private class MoreActionListener implements ActionListener
  {
    /** More button has been activated */
    public void actionPerformed(ActionEvent event)
    {
      Component comp = (Component) event.getSource();
      popupMoreMenu( comp.getParent(), 
                     comp.getLocation().x, 
                     comp.getLocation().y + comp.getSize().height );
    }
  }
  
  /** Local helper class to capture More button sub-item actions. */
  private class MoreItemActionListener implements ActionListener
  {
    private JPopupMenu popupMenu;
    
    private MoreItemActionListener(JPopupMenu popupMenu)
    {
      this.popupMenu = popupMenu;
    }
    
    public void actionPerformed(ActionEvent event)
    {
      // Hide the popup menu after an Action Event occurs
      popupMenu.setVisible(false);
    }
  }
  
  /** Local helper class to capture and redispatch menu action events */
  private class MenuActionListener implements ActionListener
  {
    public MenuActionListener() { }
    
    public void actionPerformed(ActionEvent event)
    {
      JMenuItem item       = (JMenuItem) event.getSource();
      JMenuItem realItem   = (JMenuItem) menuItemMap.get(item);
      ActionListener[] lis = realItem.getActionListeners();
      
      for (int i = 0; i < lis.length; i++)
      {
        // Redispatch to the real ActionListeners
        lis[i].actionPerformed(event);
      }
    }
  }
}
