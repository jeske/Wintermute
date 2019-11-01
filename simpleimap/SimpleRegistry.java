/*
 * SimpleRegistry.java
 *
 * Created on January 20, 2003, 5:03 PM
 */

package simpleimap;

/**
 *
 * @author  hassan
 */
public class SimpleRegistry {
    
    /** Creates a new instance of SimpleRegistry */
    public SimpleRegistry() {
    }
 
    
    static public void init(SimpleDB my_db) {
       ItemRBManager.register(my_db);
       ItemRBTextIndex.register(my_db);
       ItemRBPatternMatcher.register(my_db);
        
       ItemIMAPServer.register(my_db);
       ItemIMAPFolder.register(my_db);
       ItemIMAPMessage.register(my_db);
       
       ItemGUIFrame.register(my_db);
       ItemGUISplitPane.register(my_db);
       ItemGUIMultiView.register(my_db);
       ItemGUITree.register(my_db);
       ItemGUITable.register(my_db);
       ItemGUIRecordPanel.register(my_db);
       ItemGUIEmailMessage.register(my_db);
       ItemGUITextView.register(my_db);
       
       ItemGUIMenu.register(my_db);
       ItemGUIHTML.register(my_db);
       ItemGUIDocument.register(my_db);
       ItemGUICalendar.register(my_db);
       
       ItemFormatterText.register(my_db);
       ItemFormatterImageMap.register(my_db);
       ItemFormatterReadFlag.register(my_db);
       ItemFormatterAddressedToFlag.register(my_db);
       ItemFormatterRegEx.register(my_db);
       ItemFormatterDateType.register(my_db);
       
       ItemActionExit.register(my_db);
       ItemActionNewWizard.register(my_db);
       ItemActionCompose.register(my_db);
       ItemActionWipeDB.register(my_db);
       ItemActionCalendar.register(my_db);
       
       ItemRDFSource.register(my_db);
       ItemICal.register(my_db);
       
       
       ItemUnixMailbox.register(my_db);
       ItemUnixMailboxFolder.register(my_db);
       ItemUnixMailboxMessage.register(my_db);
        
       ItemYahooSync.register(my_db);
       ItemActionNewSync.register(my_db);
    }

}
