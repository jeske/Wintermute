/*
 * =============================================================================
 *
 * Internet Explorer 6 Style Toolbar Component for Java Swing
 *
 * =============================================================================
 *
 * This package contains the Java source for a Test Application using the
 * Internet Explorer 6 style toolbar component. This component replicates the 
 * functionality of the IE6 toolbar as closely as possible.
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

// import guicomp.toolbar.ToolbarContainer;
// import guicomp.toolbar.Toolbar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A Test Application for the Internet Explorer 6 style toolbar component.
 *
 * @see     esp.toolbar.ToolbarContainer
 * @author  Paul Atkinson, Exponential Software Professionals Group.
 */
public class TestApp implements ActionListener
{
  public TestApp()
  {
  }
  
  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest1()
  {
    //
    // Sample code from TOOLBAR.TXT specification
    //
    
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);

    Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1");
    tb_1.add(new JButton("Back"));
    tb_1.add(new JButton("Forward"));
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Email");
    tb_2.add(new JButton("Reply"));
    tb_2.add(new JButton("Reply All"));
    tc.add(tb_2);
    
    //
    // End sample code from TOOLBAR.TXT specification
    //

    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the lower work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }
  
  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest2()
  {
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);

    Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1");
    tb_1.add(new JButton("Back"));
    tb_1.add(new JButton("Forward"));
    tb_1.add(new JButton("Stop"));
    tb_1.add(new JButton("Refresh"));
    tb_1.add(new JButton("Home"));
    tb_1.add(new JLabel("Address"));
    tb_1.add(new JTextField("http://www.google.com", 20));
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Email");
    tb_2.add(new JButton("Reply"));
    tb_2.add(new JButton("Reply All"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Print"));
    tc.add(tb_2);
    
    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest3()
  {
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);

    Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1");
    tb_1.add(new JButton("Back"));
    tb_1.add(new JButton("Forward"));
    tb_1.add(new JButton("Stop"));
    tb_1.add(new JButton("Refresh"));
    tb_1.add(new JButton("Home"));
    tb_1.add(new JLabel("Address"));
    tb_1.add(new JTextField("http://www.google.com/advanced_search?hl=en", 30));
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Email");
    tb_2.add(new JButton("Reply"));
    tb_2.add(new JButton("Reply All"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Print"));
    
    String[] items = { "Main Identity", "Bush, George W.", 
                       "Clinton, Bill", "Reagon, Ronald" };
    tb_2.add(new JComboBox(items));
    tc.add(tb_2);
    
    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest4()
  {
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);

    Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1");
    tb_1.add(new JButton("Back"));
    tb_1.add(new JButton("Forward"));
    tb_1.add(new JButton("Stop"));
    
    JButton refresh = new JButton("Refresh");
    tb_1.add(refresh);
    refresh.addActionListener(this);
    
    tb_1.add(new JButton("Home"));
    tb_1.add(new JLabel("Address"));
    tb_1.add(new JTextField("http://www.google.com/advanced_search?hl=en", 30));
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Email");
    tb_2.add(new JButton("Reply"));
    tb_2.add(new JButton("Reply All"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Print"));
    tc.add(tb_2);
    
    Toolbar tb_3 = new Toolbar("Selection");
    String[] items = { "Main Identity", "Bush, George W.", 
                       "Clinton, Bill", "Reagon, Ronald" };
    tb_3.add(new JLabel("Account"));
    tb_3.add(new JComboBox(items));

    String[] priority = { "High", "Medium", "Normal", "Low" };
    tb_3.add(new JLabel("Priority"));
    tb_3.add(new JComboBox(priority));
    
    String[] attachment = { "Document", "Text", "Image", "Spreadsheet", "Business Card" };
    tb_3.add(new JLabel("Attachment"));
    tb_3.add(new JComboBox(attachment));
    tc.add(tb_3);

    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest5()
  {
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);
    
    JMenuBar menuBar    = new JMenuBar();
    JMenu    fileMenu   = new JMenu("File");
    JMenu    editMenu   = new JMenu("Edit");
    JMenu    searchMenu = new JMenu("Search");
    JMenu    viewMenu   = new JMenu("View");
    JMenu    windowMenu = new JMenu("Window");
    JMenu    helpMenu   = new JMenu("Help");
    JMenu    reopenMenu = new JMenu("Reopen");
    
    fileMenu.add("Open...");
    fileMenu.add(reopenMenu);
    fileMenu.add("Close");
    fileMenu.addSeparator();
    fileMenu.add("Exit");
    
    reopenMenu.add("XYZ.txt");
    reopenMenu.add("toolbar.txt");
    reopenMenu.add("Swing and AWT.doc");

    editMenu.add("Cut");
    editMenu.add("Copy");
    editMenu.add("Paste");
    editMenu.addSeparator();
    editMenu.add("Select All");
    
    searchMenu.add("Find...");
    searchMenu.add("Find Next");
    searchMenu.add("Find Previous");
    
    viewMenu.add("Toolbar");
    viewMenu.add("Status Bar");
    
    windowMenu.add("Cascade");
    windowMenu.add("Tile");
    windowMenu.add("Split");
    
    helpMenu.add("Help Contents...");
    helpMenu.addSeparator();
    helpMenu.add("About TestApp...");
        
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    menuBar.add(searchMenu);
    menuBar.add(viewMenu);
    menuBar.add(windowMenu);
    menuBar.add(helpMenu);
    
    Toolbar tb_1 = new Toolbar("Menu", "NAV_ID_1");
    tb_1.add(menuBar);
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Navigation","NAV_ID_2");
    tb_2.add(new JButton("Back"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Stop"));
    tb_2.add(new JButton("Refresh"));
    tb_2.add(new JButton("Home"));
    tb_2.add(new JLabel("Address"));
    tb_2.add(new JTextField("http://www.google.com/advanced_search?hl=en", 30));
    tc.add(tb_2);

    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest6()
  {
    JFrame frm = new JFrame();
    ToolbarContainer tc = new ToolbarContainer();
    tc.addToFrame(frm);
    
    JMenu    fileMenu   = new JMenu("File");
    JMenu    editMenu   = new JMenu("Edit");
    JMenu    searchMenu = new JMenu("Search");
    JMenu    viewMenu   = new JMenu("View");
    JMenu    windowMenu = new JMenu("Window");
    JMenu    helpMenu   = new JMenu("Help");
    JMenu    reopenMenu = new JMenu("Reopen");
    
    fileMenu.add("Open...");
    fileMenu.add(reopenMenu);
    fileMenu.add("Close");
    fileMenu.addSeparator();
    fileMenu.add("Exit");
    
    reopenMenu.add("XYZ.txt");
    reopenMenu.add("toolbar.txt");
    reopenMenu.add("Swing and AWT.doc");

    editMenu.add("Cut");
    editMenu.add("Copy");
    editMenu.add("Paste");
    editMenu.addSeparator();
    editMenu.add("Select All");
    
    searchMenu.add("Find...");
    searchMenu.add("Find Next");
    searchMenu.add("Find Previous");
    
    viewMenu.add("Toolbar");
    viewMenu.add("Status Bar");
    
    windowMenu.add("Cascade");
    windowMenu.add("Tile");
    windowMenu.add("Split");
    
    helpMenu.add("Help Contents...");
    helpMenu.addSeparator();
    helpMenu.add("About TestApp...");
        
    Toolbar tb_1 = new Toolbar("Menu", "NAV_ID_1");
    tb_1.add(fileMenu);
    tb_1.add(editMenu);
    tb_1.add(searchMenu);
    tb_1.add(viewMenu);
    tb_1.add(helpMenu);
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Navigation","NAV_ID_2");
    tb_2.add(new JButton("Back"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Stop"));
    tb_2.add(new JButton("Refresh"));
    tb_2.add(new JButton("Home"));
    tb_2.add(new JLabel("Address"));
    tb_2.add(new JTextField("http://www.google.com/advanced_search?hl=en", 30));
    tc.add(tb_2);

    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * TEST CODE FOR IE6 STYLE TOOLBAR.
   */
  public void runTest7()
  {
    String SELECTED_ICON     = "guicomp/images/selected.gif";
    String UNSELECTED_ICON   = "guicomp/images/unselected.gif";
    
    String TBAR_BACK_ICON    = "guicomp/images/tb1.gif";
    String TBAR_FWD_ICON     = "guicomp/images/tb2.gif";
    String TBAR_STOP_ICON    = "guicomp/images/tb3.gif";
    String TBAR_REFRESH_ICON = "guicomp/images/tb4.gif";
    String TBAR_HOME_ICON    = "guicomp/images/tb5.gif";

    JFrame  frm = new JFrame();
    boolean autoRestore = true;
    ToolbarContainer tc = new ToolbarContainer(autoRestore);
    tc.addToFrame(frm);

    JMenu fileMenu   = new JMenu("File");
    JMenu editMenu   = new JMenu("Edit");
    JMenu searchMenu = new JMenu("Search");
    JMenu viewMenu   = new JMenu("View");
    JMenu windowMenu = new JMenu("Window");
    JMenu helpMenu   = new JMenu("Help");
    JMenu reopenMenu = new JMenu("Reopen");
    
    // Load the tick images
    Icon selectedIcon   = new ImageIcon(ClassLoader.getSystemResource(SELECTED_ICON));
    Icon unselectedIcon = new ImageIcon(ClassLoader.getSystemResource(UNSELECTED_ICON));

    Icon backIcon    = new ImageIcon(ClassLoader.getSystemResource(TBAR_BACK_ICON));
    Icon fwdIcon     = new ImageIcon(ClassLoader.getSystemResource(TBAR_FWD_ICON));
    Icon stopIcon    = new ImageIcon(ClassLoader.getSystemResource(TBAR_STOP_ICON));
    Icon refreshIcon = new ImageIcon(ClassLoader.getSystemResource(TBAR_REFRESH_ICON));
    Icon homeIcon    = new ImageIcon(ClassLoader.getSystemResource(TBAR_HOME_ICON));

    fileMenu.setMnemonic('F');
    editMenu.setMnemonic('E');
    searchMenu.setMnemonic('S');
    viewMenu.setMnemonic('V');
    windowMenu.setMnemonic('W');
    helpMenu.setMnemonic('H');
    
    ActionListener menuListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        System.out.println("Test Menu Action Listener fired: " + event);
      }
    };
    
    JMenuItem item;
    item = fileMenu.add("Open...");
    item.addActionListener(menuListener);
    item.setMnemonic('O');
    
    item = fileMenu.add(reopenMenu);
    item.setMnemonic('R');
    
    item = fileMenu.add("Close");
    item.addActionListener(menuListener);
    item.setMnemonic('C');
    
    fileMenu.addSeparator();
    item = fileMenu.add("Exit");
    item.addActionListener(menuListener);
    item.setMnemonic('x');
    
    reopenMenu.add("XYZ.txt")           .addActionListener(menuListener);
    reopenMenu.add("toolbar.txt")       .addActionListener(menuListener);
    reopenMenu.add("Swing and AWT.doc") .addActionListener(menuListener);

    editMenu.add("Cut")       .addActionListener(menuListener);
    editMenu.add("Copy")      .addActionListener(menuListener);
    editMenu.add("Paste")     .addActionListener(menuListener);
    editMenu.addSeparator();
    editMenu.add("Select All").addActionListener(menuListener);
    
    searchMenu.add("Find...")      .addActionListener(menuListener);
    searchMenu.add("Find Next")    .addActionListener(menuListener);
    searchMenu.add("Find Previous").addActionListener(menuListener);
    
    item = viewMenu.add("Toolbar");
    item.addActionListener(menuListener);
    item.setIcon(selectedIcon);
    
    item = viewMenu.add("Status Bar");
    item.addActionListener(menuListener);
    item.setIcon(unselectedIcon);
    
    item = windowMenu.add("Cascade");
    item.addActionListener(menuListener);
    item.setMnemonic('C');
    
    item = windowMenu.add("Tile");
    item.addActionListener(menuListener);
    item.setMnemonic('T');
    
    item = windowMenu.add("Split");
    item.addActionListener(menuListener);
    item.setMnemonic('S');
    
    item = helpMenu.add("Help Contents...");
    item.addActionListener(menuListener);
    item.setMnemonic('C');
    
    helpMenu.addSeparator();
    item = helpMenu.add("About TestApp...");
    item.addActionListener(menuListener);
    item.setMnemonic('A');
        
    Toolbar tb_4 = new Toolbar("Menu", "NAV_ID_2");
    tb_4.add(fileMenu);
    tb_4.add(editMenu);
    tb_4.add(searchMenu);
    tb_4.add(viewMenu);
    tb_4.add(helpMenu);
    tc.add(tb_4);

    Toolbar tb_1 = new Toolbar("Navigation","NAV_ID_1");

    // Try adding an Action
    tb_1.add(new BackAction());

    tb_1.add(new JButton("Forward", fwdIcon));
    tb_1.add(new JButton("Stop", stopIcon));
    
    JButton refresh = new JButton("Refresh", refreshIcon);
    tb_1.add(refresh);
    refresh.addActionListener(this);
    
    tb_1.add(new JButton("Home", homeIcon));
    tb_1.add(new JLabel("Address"));
    tb_1.add(new JTextField("http://www.google.com/advanced_search?hl=en", 30));
    tc.add(tb_1);

    Toolbar tb_2 = new Toolbar("Email");
    tb_2.add(new JButton("Reply"));
    tb_2.add(new JButton("Reply All"));
    tb_2.add(new JButton("Forward"));
    tb_2.add(new JButton("Print"));
    tc.add(tb_2);
    
    Toolbar tb_3 = new Toolbar("Selection");
    String[] items = { "Main Identity", "Bush, George W.", 
                       "Clinton, Bill", "Reagon, Ronald" };
    tb_3.add(new JLabel("Account"));
    JComboBox cb1 = new JComboBox(items);
    cb1.setMinimumSize(new Dimension(10, cb1.getPreferredSize().height));
    tb_3.add(cb1);

    String[] priority = { "High", "Medium", "Normal", "Low" };
    tb_3.add(new JLabel("Priority"));
    JComboBox cb2 = new JComboBox(priority);
    cb2.setMinimumSize(new Dimension(10, cb2.getPreferredSize().height));
    tb_3.add(cb2);
    
    String[] attachment = { "Document", "Text", "Image", "Spreadsheet", "Business Card" };
    tb_3.add(new JLabel("Attachment"));
    JComboBox cb3 = new JComboBox(attachment);
    cb3.setMinimumSize(new Dimension(10, cb3.getPreferredSize().height));
    tb_3.add(cb3);
    tc.add(tb_3);

    // Setup and show the Frame
    frm.setTitle("Internet Explorer 6 Style Toolbar Component - Test Application");
    frm.setDefaultCloseOperation(frm.DISPOSE_ON_CLOSE);
    frm.setSize(800, 600);
    
    // Fill the work area
    JPanel    mainArea = new JPanel(new BorderLayout());
    JTextArea text     = new JTextArea();
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    text.setFocusable(false);
    mainArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 1));
    mainArea.add(text);    
    frm.getContentPane().add(mainArea, BorderLayout.CENTER);
    
    frm.setVisible(true);
  }

  /**
   * Main Test Application Entry Point.
   */
  public static void main(String[] args)
  {
    // Start the test application
    new TestApp().runTest7();
  }
  
  /** Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e) 
  {
    System.out.println("TestApp.actionPerformed() fired: " + e);
  }
  
  /** Test Action object for the Toolbar */
  private class BackAction extends AbstractAction
  {
    private static final String TBAR_BACK_ICON = "guicomp/images/tb1.gif";
    
    private BackAction()
    {
      super("Back", new ImageIcon(ClassLoader.getSystemResource(TBAR_BACK_ICON)));
    }
    
    public void actionPerformed(ActionEvent event)
    {
      System.out.println("Back Action object fired");
    }
  }
}
