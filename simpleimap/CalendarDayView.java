/*
 * CalendarDayView.java
 *
 * Created on January 24, 2003, 9:11 PM
 */

package simpleimap;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.Dimension;

/**
 *
 * @author  hassan
 */
public class CalendarDayView extends JPanel {

    private Color nextMonthColor;
    private Color weekendColor;
    private Color borderColor;
    private Color selectedColor;
    private Color selectedBorderColor;
    private Color highlightedBorderColor;
    private Color textColor;
    private Color labelColor;
    private Color headerColor;
    
    private Color backgroundColor;
    
    private Border selectedBorder;
    private Border regularDayBorder;
    private Border highlightedBorder;
    
    private Calendar calendar;
    private Calendar today;
    private Calendar selectedDate;

    private Locale locale;
    private boolean initialized = false;
    
    DefaultItem item;
    
    CalendarModel model;
    Color unselectedDayColor;
    Color selectedDayColor;    

    private javax.swing.JPanel thePanel;
    private javax.swing.JList dayList;
    private javax.swing.JPanel DaysHeaderPanel;
    private javax.swing.JLabel yearText;
    private javax.swing.JPanel monthHeaderPanel;
    private javax.swing.JPanel DaysPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel MonthViewPanel;
    private javax.swing.JPanel MonthPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel monthText;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton todayButton;
    
    private aListModel listModel;
    
    /** Creates a new instance of CalendarDayView */
    public CalendarDayView() {
        super();
        this.thePanel = this;
        initComponents();

        locale = Locale.getDefault();
        calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        TimeZone tz = this.calendar.getTimeZone();
        Debug.debug("TIMEZone=" + tz.toString());
        
        this.today = (Calendar) calendar.clone();
        

    }

    public void initComponents() {
        this.dayList = new JList();
        this.dayList.setFocusable(false);
        this.listModel = new aListModel();
        this.dayList.setModel(this.listModel);

        this.thePanel.add(this.dayList);
    }

    
    public class aListModel extends javax.swing.AbstractListModel {
        LinkedList strings;
        
        public aListModel() {
            this.strings = new LinkedList();
        }
        
        public class aListObject {
            String title;
            DefaultItem item;
            aListObject(String title, DefaultItem item) {
                this.title = title;
                this.item = item;
            }
            
        }
        
        public void add(String title, DefaultItem item) {
            aListObject obj = new aListObject(title, item);
            strings.add(obj);
        }
        
        
        public int getSize() {
            return strings.size();
        }
        
        public aListObject getObjAt(int i) {
            aListObject obj = (aListObject) strings.get(i);
            return obj;
        }
        
        public Object getElementAt(int i) {
            aListObject obj = (aListObject) strings.get(i);
            
            return obj.title;
        }
        
        public void clearEvents() {
            this.strings.clear();
            
        }
    }
    
    
    public void setModel(CalendarModel model) {
        this.model = model;
        this.modelChanged();
    }
    
    public CalendarModel getModel() {
        return this.model;
    }
    
    public void setViewedItem(CalendarItemModel.ModelItem mi) {
        Date date = new Date();
        date.setTime(mi.date);
        //this.showDate(date);
    }

    private void todayButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        if(!this.calendar.equals(this.today)) {
            this.calendar = (Calendar) this.today.clone();
            this.modelChanged();
        }
    }    
    
    public void showDate(Date date) {
       this.calendar.setTime(date);
       //this.selectedDate = (Calendar) this.calendar.clone();
       this.modelChanged();       
    }
    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        this.calendar.add(Calendar.DATE, 1);
        this.modelChanged();
    }

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        this.calendar.add(Calendar.DATE, -1);
        this.modelChanged();

    }    
    
    private void modelChanged() {
        this.drawDay();
    }

    private void drawDay() {
        Calendar tmpCalendar = (Calendar) this.calendar.clone();
        Calendar nextDay = (Calendar) tmpCalendar.clone();
        nextDay.add(Calendar.DATE, 1);
        
        //theDay.clearEvents();
        this.listModel.clearEvents();
        
        if(this.model != null) {
            java.util.List events = this.model.getEvents(tmpCalendar, nextDay);
            
            for(Iterator iter = events.iterator(); iter.hasNext();) {
                CalendarItemModel.ModelItem mi = (CalendarItemModel.ModelItem) iter.next();
                
                //String s = item.get("SUMMARY");
                DefaultItem mi_item = mi.getItem();
                String title = mi_item.get("SUMMARY");
                if(title == null) title = mi_item.get("Subject");
                if(title == null) title = mi_item.get("name");
                
                if(title != null) {
                    Date date = new Date();
                    date.setTime(mi.date);
                    
                    SimpleDateFormat date_formatter = new SimpleDateFormat("HH:mmaa");
                    String datestr = date_formatter.format(date);
                    
                    String text = datestr + " " + title;
                    this.listModel.add(text, mi_item);
                }
            }
        }        
        this.thePanel.revalidate();
        this.thePanel.repaint();        
    }
    
 
    
    public static void main(String args[]) throws Exception {
        Debug.start();
        WinterMute.setupCalendar();

        RelationshipBuilder.init();
        
        JFrame frame = new JFrame();
        CalendarDayView cdv = new CalendarDayView();
        
        frame.getContentPane().add(cdv);
        frame.show();
    }    
    
        
}
