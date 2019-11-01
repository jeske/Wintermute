/*
 * CalendarItemModel.java
 *
 * Created on December 14, 2002, 11:18 AM
 */

package simpleimap;

import java.util.*;
import java.text.*;

import javax.swing.event.*;


/**
 *
 * @author  hassan
 */
public class CalendarItemModel implements CalendarModel, TableModelListener {
    DefaultItem item;
    LinkedList modelItems;
    ItemTableModel my_table_model = null;
    Vector listeners;
    
    /** Creates a new instance of CalendarItemModel */
    public CalendarItemModel(DefaultItem item) {
        this.item = item;
        this.modelItems = null;
        this.my_table_model = this.item.makeTableModelForRelation(WinterMute.containerContainsRelation);
        this.my_table_model.addTableModelListener(this);
        
        this.listeners = new Vector();

        this.buildModel();
    }
    
    class ModelItem {
        // this causes memory usage to explode!
        // public DefaultItem item;  
        public int item_oid;
        public long date;
        
        public ModelItem(DefaultItem item, long date) {
            this.item_oid = item.get_oid();
            this.date = date;
        }
        public DefaultItem getItem() {
            return WinterMute.my_db.getItem(this.item_oid);
        }
    }
    private void buildRelatedModel(LinkedList modelItems) {
        Calendar c = WinterMute.now();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        List relations = this.item.getAvailableRelations();
        for(Iterator iter = relations.iterator(); iter.hasNext(); ) {
            ItemRelation relation = (ItemRelation) iter.next();
            String spec = relation.getDestination();
            if(spec.startsWith("event=")) {
                String daystr = spec.substring(6);
                
                int year = Integer.parseInt(daystr.substring(0, 4));
                int month = Integer.parseInt(daystr.substring(5,7));
                int day = Integer.parseInt(daystr.substring(8,10));
                
                //Debug.debug("" + year + " " + month + " " + day);
                
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month-1);
                c.set(Calendar.DATE, day);

                Date date = c.getTime();
                
                long datenum = date.getTime();
                //Debug.debug("" + datenum);
                
                List items = this.item.getRelatedItems(relation);
                
                for(Iterator iter2 = items.iterator(); iter2.hasNext();) {
                    DefaultItem item = (DefaultItem) iter2.next();
                    
                    ModelItem mi = new ModelItem(item, datenum);
                    
                    this.modelItems.add(mi);
                }
                
            }
        }
    }
    
    private void buildModel() {
        this.modelItems = new LinkedList();
        
        //this.buildRelatedModel(modelItems);
        
        java.util.List items = this.item.getRelatedItems(WinterMute.containerContainsRelation);
        Iterator iter = items.iterator();

        while(iter.hasNext()) {
            DefaultItem item = (DefaultItem) iter.next();
            
            LinkedList keys = item.keyList();
            for(Iterator iter2 = keys.iterator(); iter2.hasNext(); ) {
                String key = (String) iter2.next();
                
                if(key.startsWith("#Date.") == true) {
                    
                    long datenum = item.getLong(key, 0);
                    
                    if(datenum != 0) {
                        ModelItem mi = new ModelItem(item, datenum);
                        
                        this.modelItems.add(mi);
                    }
                }
            }
        }        
        
        notifyListeners();
    }
    
    public Object findItem(DefaultItem item) {
        for(Iterator iter = this.modelItems.iterator(); iter.hasNext(); ) {
            ModelItem mi = (ModelItem) iter.next();
            int item_oid = item.get_oid();
            if (mi.item_oid == item_oid) {
                return mi.getItem();
            }
        }
        return null;
    }
    
    public class CalendarEventObject {
        public DefaultItem item;
        public Date date;
    }
    
    public List getEvents(Calendar startCal, Calendar endCal) {
        TimeZone tz = startCal.getTimeZone();
        long start = startCal.getTime().getTime();
        long tzoffset = tz.getOffset(start);
        
        long end = endCal.getTime().getTime();
        
        //start += tzoffset;
        //end += tzoffset;
        
//        Debug.debug("tzoffset=" + tzoffset);
//        
//        Debug.debug("start=" + start);
//        Debug.debug("end=" + end);
//        Debug.debug("diff=" + Long.toString(end-start));
        
        LinkedList retlist = new LinkedList();


        Iterator iter = this.modelItems.iterator();
        
        Hashtable ht = new Hashtable();
        
        while(iter.hasNext()) {
            ModelItem mi = (ModelItem) iter.next();
            
            //long t = mi.date - tzoffset;
            long t = mi.date;
            
            if(t >= start && t < end) {
                if(ht.get(new Integer(mi.item_oid)) == null) {
                    //retlist.add(mi);
                    ht.put(new Integer(mi.item_oid), mi);
                } else {
                    ModelItem mi2 = (ModelItem) ht.get(new Integer(mi.item_oid));
                    long t2 = mi2.date;
                    if(t < t2) {
                        ht.put(new Integer(mi.item_oid), mi); 
                    }
                }
            }

        }
        
        
        for(Enumeration elem_enum = ht.elements(); elem_enum.hasMoreElements();) {
            ModelItem mi = (ModelItem) elem_enum.nextElement();
            retlist.add(mi);
        }
        
        return retlist;      
    }
    
    static void relateCalendarItem(DefaultItem item) {
        DefaultItem root = WinterMute.my_db.rootNode();
        DefaultItem mainCalendar = root.getItem(WinterMute.parentChildRelation, "Calendar");
        if(mainCalendar == null) {
            mainCalendar = WinterMute.my_db.newItem(null, "Default", "Calendar");
        }     
        
        root.relateToOnce(WinterMute.parentChildRelation, mainCalendar);
        
        mainCalendar.relateToOnce(WinterMute.containerContainsRelation, item);
    }
    
    public DefaultItem getModelItem() {
        return this.item;
    }
    
    /** This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     *
     */
    public void tableChanged(TableModelEvent e) {
        this.buildModel(); // FIX HACK rebuild the whole model - jeske
    }
    
    public void addCalendarListener(CalendarModelListener l) {
        listeners.addElement(l);
    }
    public void removeCalendarListener(CalendarModelListener l) {
        listeners.removeElement(l);
    }
    
    private void notifyListeners() {
        int len = listeners.size();
        
        for (int x=0;x<len;x++) {
            ((CalendarModelListener)listeners.elementAt(x)).calendarEventsChanged();
        }
    }
}
