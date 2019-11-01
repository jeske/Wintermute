/*
 * CalendarModel.java
 *
 * Created on December 14, 2002, 11:15 AM
 */

package simpleimap;

import java.util.*;

/**
 *
 * @author  hassan
 */
public interface CalendarModel {
    public interface CalendarModelListener {
        public void calendarEventsChanged();
    }
    
    public List getEvents(Calendar startCal, Calendar endCal); 
    public DefaultItem getModelItem();
    public Object findItem(DefaultItem item);  
    public void addCalendarListener(CalendarModelListener l);
    public void removeCalendarListener(CalendarModelListener l);
    
}



