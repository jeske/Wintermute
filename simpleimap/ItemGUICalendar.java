/*
 * ItemGUICalendar.java
 *
 * Created on December 15, 2002, 3:49 PM
 */

package simpleimap;

/**
 *
 * @author  jeske
 */
public class ItemGUICalendar extends DefaultItem implements ItemGUIInterface {
        public static final String TypeID = "gui.ItemGUICalendar";
    CalendarView my_calendar;
    

    /** Creates a new instance of ItemGUICalendar */
    public ItemGUICalendar() {
        this.my_calendar = new CalendarView();
    }
 
    
     public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUICalendar();
            }
        });
    }
   
     public void clearViewedItem() {
     }
     
     public java.awt.Component getComponent() {
         return this.my_calendar;
     }
     
     public void setViewedItem(DefaultItem item) {
         CalendarModel model = this.my_calendar.getModel();
         if(model != null) {
             Object o = model.findItem(item);
             if(o != null && o instanceof CalendarItemModel.ModelItem) {
                 CalendarItemModel.ModelItem mi = (CalendarItemModel.ModelItem) o;
                 this.my_calendar.setViewedItem(mi);
                 return;
             }
         }
         CalendarItemModel aModel = new CalendarItemModel(item);
         this.my_calendar.setModel(aModel);
     }
     
}
