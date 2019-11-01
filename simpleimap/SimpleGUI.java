/*
 * SimpleGUI.java
 *
 * Created on January 10, 2003, 10:59 AM
 */

package simpleimap;

/**
 *
 * @author  hassan
 */
public class SimpleGUI {
    SimpleDB db;
    
    /** Creates a new instance of SimpleGUI */
    public SimpleGUI(SimpleDB db) {
        this.db = db;
    }
    
    public DefaultItem buildUI(DefaultItem rootItem) {
        DefaultItem ui = this.findItem(rootItem, WinterMute.parentChildRelation, "Default", "GUI");
        ui.put("icon_path","images/gui/workspace.gif");
        rootItem.relateToOnce(WinterMute.parentChildRelation, ui);

        // GUI
        this._buildUI(rootItem, ui);
        
        
        // Fields 
        
        // Formatters
        this._buildFormatters(rootItem);
        
        // RBManager
        this._buildRelationBuilders(rootItem);

        // Top level Contacts
        DefaultItem contacts = (DefaultItem) this.findChildItem(rootItem, "Default", "Contacts");
        contacts.put("icon_path","images/helpset.gif");
        
        // Top Level Calendar
        DefaultItem calendar = (DefaultItem) this.findChildItem(rootItem, "Default", "Calendar");
        calendar.put("icon_path","images/itemcal.png");
        
        return ui;
    }
    
   
    
    private DefaultItem findItem(DefaultItem parent, ItemRelation relation, String type, String name) {
        DefaultItem item = parent.getItem(relation, name);
        if(item == null) {
            item = db.newItem(null, type, name);
            parent.relateToOnce(relation, item);
        }
        return item;
    }
    
    private DefaultItem findChildItem(DefaultItem parent, String type, String name) {
        DefaultItem item = parent.getItem(WinterMute.parentChildRelation, name);
        if(item == null) {
            item = db.newItem(null, type, name);
            parent.addChild(item);
            //parent.relateTo(WinterMute.parentChildRelation, item);
        }
        return item;
    }
    
    
    private DefaultItem build_itemviewer(DefaultItem ui) {
        DefaultItem itemvtop = this.findChildItem(ui, ItemGUISplitPane.TypeID, "item_viewer");
        
        itemvtop.put("vsplit","yes");
        //itemvtop.put("resizeWeight","0.30");
        itemvtop.put("dividerLocation", 100);
        
        ItemGUITable item_table = (ItemGUITable) this.findChildItem(itemvtop, ItemGUITable.TypeID, "item_table");
        item_table.put("relation_to_view",WinterMute.containerContainsRelation.getSpec());
        
        ItemGUITable itemview = (ItemGUITable) this.findChildItem(itemvtop, ItemGUITable.TypeID, "item_view");
        itemview.put("relation_to_view","SELF");
        
        item_table.relateToOnce(WinterMute.setViewedItemSenderRelation, itemview);
        
        
        return itemvtop;
    }
    private DefaultItem build_calendar_viewer(DefaultItem ui) {
        
        DefaultItem caltop  = this.findChildItem(ui, ItemGUISplitPane.TypeID, "calendar_viewer");
        caltop.put("vsplit","yes");
        //caltop.put("resizeWeight","0.30");
        caltop.put("dividerLocation", 119);
        
        
        ItemGUITable callist = (ItemGUITable) this.findChildItem(caltop, ItemGUITable.TypeID, "calevent_table");
        //callist.put("relation_to_view", WinterMute.containerContainsRelation.getSpec());
        
        DefaultItem calviewsplit = this.findChildItem(caltop, ItemGUISplitPane.TypeID, "calview_split");
                
        DefaultItem calview = this.findChildItem(calviewsplit, ItemGUICalendar.TypeID, "custom_calview");
        DefaultItem calmv   = this.findChildItem(calviewsplit, ItemGUIMultiView.TypeID, "custom_calmv");
        
        callist.relateToOnce(WinterMute.setViewedItemSenderRelation, calview);

        // multiview contents
        
        DefaultItem caloverlay = this.findChildItem(calmv, ItemGUITable.TypeID, "overlay_placeholder");
        DefaultItem caldayview = this.findChildItem(calmv, ItemGUITable.TypeID, "selected_day_items");
        
        if(true) {
            //callist.unrelateFromAll(WinterMute.tableColumnsetRelation);
            
            DefaultItem calcolumnset = this.findItem(callist, WinterMute.tableColumnsetRelation,
            ItemDefault.TypeID, "calcolumnset");
            this.buildColumn(calcolumnset, "date", "Date", 50);
            this.buildColumn(calcolumnset, "Summary", "Summary", 50);
            
            callist.relateToOnce(WinterMute.tableColumnsetRelation, calcolumnset);
            callist.relateToOnce(WinterMute.parentChildRelation, calcolumnset);
            
        }
        return caltop;
    }
    
    private DefaultItem build_contact_viewer(DefaultItem ui, DefaultItem tree) {
        DefaultItem right_vsplitter = this.findChildItem(ui, ItemGUISplitPane.TypeID, "contact_viewer");
        right_vsplitter.put("vsplit","yes");
        //right_vsplitter.put("resizeWeight", "0.10");
        right_vsplitter.put("dividerLocation", 121);
        
        ItemGUITable index_tbl = (ItemGUITable) this.findChildItem(right_vsplitter, ItemGUITable.TypeID, "table");
        ItemGUITable index_tbl2 = (ItemGUITable) this.findChildItem(right_vsplitter, ItemGUITable.TypeID, "table2");
        index_tbl2.put("relation_to_view","SELF");
        index_tbl.relateToOnce(WinterMute.setViewedItemSenderRelation, index_tbl2);
        
        ItemGUITable index_tbl3 = (ItemGUITable) this.findChildItem(right_vsplitter, ItemGUITable.TypeID, "table3");
        index_tbl3.put("relation_to_view",WinterMute.contactEmailAddressRelation.getSpec());
        
        index_tbl.relateToOnce(WinterMute.setViewedItemSenderRelation, index_tbl3);
        
        return right_vsplitter;
    }    
    
    private DefaultItem build_folder_viewer(DefaultItem ui, DefaultItem tree) {
        DefaultItem right_vsplitter = this.findChildItem(ui, ItemGUISplitPane.TypeID, "folder_viewer");
        right_vsplitter.put("vsplit","yes");
        //right_vsplitter.put("resizeWeight", "0.10");
        right_vsplitter.put("dividerLocation", 121);
        
        //hsplitter.addChild(right_vsplitter);
                
        // index
        ItemGUITable index_tbl = (ItemGUITable) this.findChildItem(right_vsplitter, ItemGUITable.TypeID, "table");
        index_tbl.put("should_auto_select","1");
        
        if(true) {
            // column config...
            DefaultItem columnset = (DefaultItem) this.findItem(index_tbl, WinterMute.tableColumnsetRelation, 
                                                                ItemDefault.TypeID, "columnset");

            // column Status
            DefaultItem columnconfig = this.buildColumn(columnset, "status", "Status", 10);

            ItemFormatterReadFlag formatter = (ItemFormatterReadFlag) this.findItem(columnconfig, 
                                                  WinterMute.colconfigFormatterRelation,
                                                  ItemFormatterReadFlag.TypeID, "readFormatter");
            
            columnconfig.relateToOnce(WinterMute.colconfigFormatterRelation, formatter);

            // column Action
            DefaultItem columnconfig2 = this.buildColumn(columnset, "addressedTo", " ", 10);

            ItemFormatterAddressedToFlag formatter2 = (ItemFormatterAddressedToFlag) this.findItem(columnconfig2, 
                                                  WinterMute.colconfigFormatterRelation,
                                                  ItemFormatterAddressedToFlag.TypeID, "addressedToFormatter");
            
            columnconfig2.relateToOnce(WinterMute.colconfigFormatterRelation, formatter2);
            
            // column From Name
            DefaultItem columnconfig3 = this.buildColumn(columnset, "From", "From", 50);
            
            ItemFormatterRegEx f = (ItemFormatterRegEx) this.findItem(columnconfig3,
                                   WinterMute.colconfigFormatterRelation, 
                                   ItemFormatterRegEx.TypeID, "fromnameformatter");
            columnconfig3.relateToOnce(WinterMute.colconfigFormatterRelation, f);

            // column formatted Date
            DefaultItem columnconfig4 = this.buildColumn(columnset, "#Date.Date", "Date",50);
            ItemFormatterDateType dtf = (ItemFormatterDateType) this.findItem(columnconfig4, 
                                    WinterMute.colconfigFormatterRelation,
                                    ItemFormatterDateType.TypeID, "dateformatter");
            
            
            // old unformatted date
            // this.buildColumn(columnset, "Date", "Date", 75);

            this.buildColumn(columnset, "From", "From", 50);
            this.buildColumn(columnset, "Subject", "Subject", 200);

            index_tbl.relateToOnce(WinterMute.tableColumnsetRelation, columnset);
            index_tbl.relateToOnce(WinterMute.parentChildRelation, columnset);

        }
        
        // RIGHT SIDE BOTTOM
        DefaultItem msg_hsplit = this.findChildItem(right_vsplitter, ItemGUISplitPane.TypeID,"msghsplit");
        msg_hsplit.put("resizeWeight", "0.95");
        //msg_hsplit.put("dividerLocation", 452);
        
        
        // header/message splitter
        DefaultItem header_msg_split = this.findChildItem(msg_hsplit, ItemGUISplitPane.TypeID,"headermsgsplit");
        header_msg_split.put("vsplit","yes");
        header_msg_split.put("dividerSize", "2");
        //header_msg_split.put("resizeWeight", "0.20");
        header_msg_split.put("dividerLocation", 205);
                

        
        
        //ItemGUITable header = (ItemGUITable) this.findChildItem(header_msg_split, ItemGUITable.TypeID, "msg_header");
        //header.put("relation_to_view","SELF");
        
        
        // message
        ItemGUIEmailMessage msg = (ItemGUIEmailMessage) this.findChildItem(header_msg_split, ItemGUIEmailMessage.TypeID, "message");
        
        DefaultItem cust_vsplit;
        if (false) {
            // customer vsplitter
            cust_vsplit = this.findChildItem(msg_hsplit, ItemGUISplitPane.TypeID,"custvsplit");
            cust_vsplit.put("vsplit","yes");
            //cust_vsplit.put("resizeWeight", "0.30");
            cust_vsplit.put("dividerLocation", 205);
        } else {
            // customer MultiView!
            cust_vsplit = this.findChildItem(msg_hsplit, ItemGUIMultiView.TypeID,"custMV");
        }

        // HTML Cust Details
        DefaultItem cust_details_html = this.findChildItem(cust_vsplit, ItemGUIHTML.TypeID, "custhtml");
        msg.relateToOnce(WinterMute.setViewedItemSenderRelation, cust_details_html);
        // relation to view
        
        // CustomerRecordPanel
        DefaultItem cust_details_recordpanel = this.findChildItem(cust_vsplit, ItemGUIRecordPanel.TypeID,  "custrecordpanel");
        msg.relateToOnce(WinterMute.setViewedItemSenderRelation, cust_details_recordpanel);
        
        // cust details
        DefaultItem cust_details = this.findChildItem(cust_vsplit, ItemGUITable.TypeID, "custtbl");
        //cust_details.put("relation_to_view", "email:contact");        
        cust_details.put("relation_to_view", WinterMute.messageEmailAddressRelation.getSpec());
        // cust_details.put("relation_to_view","SELF");
                
        if(true) {
            // column config...
            DefaultItem columnset = (ItemDefault) this.findItem(cust_details, WinterMute.tableColumnsetRelation,
                                                    ItemDefault.TypeID, "columnset2");
            cust_details.relateToOnce(WinterMute.tableColumnsetRelation, columnset);
            cust_details.relateToOnce(WinterMute.parentChildRelation, columnset);
            
            // column From
            this.buildColumn(columnset, "name", "Name", 50);
            this.buildColumn(columnset, "email", "Email", 200);
        }
        

        DefaultItem contactRecordsTable = this.findChildItem(cust_vsplit, ItemGUITable.TypeID, "contactRecordsTable");
        contactRecordsTable.put("relation_to_view",WinterMute.emailAddressContactRelation.getSpec());

        
       if(true) {
            // column config...
            DefaultItem columnset = (ItemDefault) this.findItem(contactRecordsTable, WinterMute.tableColumnsetRelation,
                                                    ItemDefault.TypeID, "columnset4");
            contactRecordsTable.relateToOnce(WinterMute.tableColumnsetRelation, columnset);
            contactRecordsTable.relateToOnce(WinterMute.parentChildRelation, columnset);
            
            // column Subject
            this.buildColumn(columnset, "Name", "Name", 200);           
        }                
        
        
        
        // cust messages
        DefaultItem cust_messages = this.findChildItem(cust_vsplit, ItemGUITable.TypeID, "custmsgs");
        cust_messages.put("relation_to_view",WinterMute.emailAddressMessageRelation.getSpec());        
        
        
        if(true) {
            // column config...
            DefaultItem columnset = (ItemDefault) this.findItem(cust_messages, WinterMute.tableColumnsetRelation,
                                                    ItemDefault.TypeID, "columnset3");
            cust_messages.relateToOnce(WinterMute.tableColumnsetRelation, columnset);
            cust_messages.relateToOnce(WinterMute.parentChildRelation, columnset);
            
            // column Subject
            this.buildColumn(columnset, "Subject", "Subject", 200);           
        }
        

        // index should view a message
        index_tbl.relateToOnce(WinterMute.setViewedItemSenderRelation, header_msg_split);
        
        // message should view customer information
        msg.relateToOnce(WinterMute.setViewedItemSenderRelation, cust_details);
        
        
       
        
        // customer record should view customer messages
        cust_details.relateToOnce(WinterMute.setViewedItemSenderRelation, cust_messages);
        cust_details.relateToOnce(WinterMute.setViewedItemSenderRelation, contactRecordsTable);
//        contactRecordsTable.relateToOnce(WinterMute.setViewedItemSenderRelation, cust_details);

        
        // cust_messages.relateToOnce(WinterMute.setViewedItemSenderRelation, header_msg_split);
        
        
        
        return     index_tbl;
        
    }

    private void buildMenus(ItemGUIFrame frm)
    {
        ItemGUIMenu file_menu = (ItemGUIMenu) this.findItem(frm, WinterMute.menuFrameRelation, 
                                                            ItemGUIMenu.TypeID, "File");
        frm.addMenu(file_menu);
        frm.relateToOnce(WinterMute.parentChildRelation, file_menu);
        
        ItemAction action;
        
        action = (ItemAction) this.findItem(file_menu, WinterMute.actionMenuRelation, 
                                            ItemActionNewWizard.TypeID, "Menu.File.Action.NewWizard");
        file_menu.addAction(action);
        file_menu.relateToOnce(WinterMute.parentChildRelation, action);
        
        action = (ItemAction) this.findItem(file_menu, WinterMute.actionMenuRelation, 
                                            ItemActionNewSync.TypeID, "Menu.File.Action.NewSync");
        file_menu.addAction(action);
        file_menu.relateToOnce(WinterMute.parentChildRelation, action);
        
        action = (ItemAction) this.findItem(file_menu, WinterMute.actionMenuRelation, 
                                            ItemActionCompose.TypeID, "Menu.File.Action.Compose");
        file_menu.addAction(action);
        file_menu.relateToOnce(WinterMute.parentChildRelation, action);

        action = (ItemAction) this.findItem(file_menu, WinterMute.actionMenuRelation, 
                                            ItemActionWipeDB.TypeID, "Menu.File.Action.WipeDB");
        file_menu.addAction(action);
        file_menu.relateToOnce(WinterMute.parentChildRelation, action);
        
        action = (ItemAction) this.findItem(file_menu, WinterMute.actionMenuRelation, 
                                            ItemActionExit.TypeID, "Menu.File.Action.Exit");
        file_menu.addAction(action);
        file_menu.relateToOnce(WinterMute.parentChildRelation, action);

        
        ItemGUIMenu window_menu = (ItemGUIMenu) this.findItem(frm, WinterMute.menuFrameRelation, 
                                                            ItemGUIMenu.TypeID, "Window");
        frm.addMenu(window_menu);
        frm.relateToOnce(WinterMute.parentChildRelation, window_menu);
                            
        action = (ItemAction) this.findItem(window_menu, WinterMute.actionMenuRelation, 
                                            ItemActionCalendar.TypeID, "Menu.Window.Action.Calendar");
        window_menu.addAction(action);
        window_menu.relateToOnce(WinterMute.parentChildRelation, action);
           
    }
    
    private DefaultItem buildColumn(DefaultItem columnset, String columnName, String columnTitle, int columnWidth)
    {
        DefaultItem field = db.getCommonField(columnName);
        DefaultItem columnconfig = (ItemDefault) this.findItem(columnset, WinterMute.columnsetColconfigRelation,
                                                               ItemDefault.TypeID, "Column/" + columnName);
        columnset.relateToOnce(WinterMute.columnsetColconfigRelation,columnconfig);        
        field.relateToOnce(WinterMute.fieldColconfigRelation, columnconfig);
        
        columnconfig.put("Title", columnTitle);
        columnconfig.put("width", columnWidth);
        
        columnset.relateToOnce(WinterMute.parentChildRelation, columnconfig);
        
        return columnconfig;
    }
            
    
    private void _buildUI(DefaultItem rootItem, DefaultItem ui) {
        // main window
        ItemGUIFrame frm = (ItemGUIFrame) this.findChildItem(ui, ItemGUIFrame.TypeID, "Wintermute Mail");
        frm.put("addtoolbar","yes");
        
        this.buildMenus(frm);
        
        DefaultItem hsplitter = this.findChildItem(frm, ItemGUISplitPane.TypeID, "splitter");
        //hsplitter.put("resizeWeight", "0.15");
        hsplitter.put("dividerLocation", 181);
        
        // LEFT SIDE
        DefaultItem left_vsplitter = this.findChildItem(hsplitter, ItemGUISplitPane.TypeID, "left_vsplitter");
        left_vsplitter.put("vsplit","yes");
        left_vsplitter.put("resizeWeight", "0.85");
        left_vsplitter.put("dividerLocation", 337);

        
        ItemGUITree tree = (ItemGUITree) this.findChildItem(left_vsplitter, ItemGUITree.TypeID, "tree");
        if(!tree.isRelated(WinterMute.viewedItemViewerRelation, rootItem)) {
            tree.addViewedItemRelation(rootItem);
        }

        ItemGUITable itemtbl = (ItemGUITable) this.findChildItem(left_vsplitter, ItemGUITable.TypeID, "itemtable");
        itemtbl.put("relation_to_view","SELF");
                
        // RIGHT SIDE Document Type Handler
        ItemGUIDocument doc = (ItemGUIDocument) this.findChildItem(hsplitter, ItemGUIDocument.TypeID, "document");
        
        
        // ------------------------------------------------------------------------------------
        
        
        DefaultItem itemvtop = this.build_itemviewer(ui);
        
        // ------------------------------------------------------------------------------------

        // RIGHT SIDE Calendar View
        DefaultItem caltop = this.build_calendar_viewer(ui);
        
        // ----------------------------------------------------------------------------------
        
        // RIGHT SIDE Folder View

        DefaultItem index_tbl = this.build_folder_viewer(ui, tree);
 
        DefaultItem contact_viewer = this.build_contact_viewer(ui, tree);
        
        // setup the action !
        // tree.relateTo(new ItemRelation("action.setViewedItem","sender"), vsplitter);
        tree.relateToOnce(WinterMute.setViewedItemSenderRelation, doc);
        tree.relateToOnce(WinterMute.setViewedItemSenderRelation, contact_viewer);
        
        tree.relateToOnce(WinterMute.setViewedItemSenderRelation, index_tbl);
        tree.relateToOnce(WinterMute.setViewedItemSenderRelation, itemtbl);

    }
    private void _buildRelationBuilders(DefaultItem rootItem) {
       ItemRBManager rbmanager = (ItemRBManager) this.findChildItem(rootItem, ItemRBManager.TypeID, "RelationBuilders");
       rbmanager.put("icon_path","images/gui/typeChildren.gif");
       
       ItemRBTextIndex rbtx = (ItemRBTextIndex) this.findItem(rbmanager,WinterMute.containerContainsRelation, 
                     ItemRBTextIndex.TypeID, "TextIndexer");
       
       ItemRBPatternMatcher rbptn = (ItemRBPatternMatcher) this.findItem(rbmanager,WinterMute.containerContainsRelation,
                    ItemRBPatternMatcher.TypeID, "PatternMatcher");
       
    }
    private void _buildFormatters(DefaultItem rootItem) {
        DefaultItem formatters = (DefaultItem) this.findChildItem(rootItem, "Default", "Formatters");
        formatters.put("icon_path","images/gui/customize.gif");
        
        
        ItemFormatterText textf = (ItemFormatterText) this.findItem(formatters,
                                    WinterMute.containerContainsRelation,
                                    ItemFormatterText.TypeID, "Text");

        ItemFormatterRegEx f = (ItemFormatterRegEx) this.findItem(formatters, 
                                    WinterMute.containerContainsRelation, 
                                    ItemFormatterRegEx.TypeID, "Name/Email");
        
        ItemFormatterDateType datef = (ItemFormatterDateType) this.findItem(formatters, 
                                    WinterMute.containerContainsRelation, 
                                    ItemFormatterDateType.TypeID, "Date");
        
        
    }
    
}
