/*
 * RelationshipBuilder.java
 *
 * Pattern matching relationship builder currently supporting
 * email addresses and dates.
 *
 * Created on January 11, 2003, 2:38 PM
 */


package simpleimap;

import java.util.*;
import java.text.*;
import java.util.regex.*;

/**
 *
 * @author  hassan
 */
public class RelationshipBuilder {
    public static int debugLevel = 1;

    public static RelationshipBuilder rbuilder;
    
    LinkedList kvparsers;
    LinkedList dataparsers;

    Calendar now;
    
    /** Creates a new instance of RelationshipBuilder */
    public RelationshipBuilder() {
        
        Locale locale = Locale.getDefault();
        this.now = Calendar.getInstance(locale);
        
        this.kvparsers = new LinkedList();
        this.dataparsers = new LinkedList();

        
        RBPattern hourPat = new RBPattern("([0-2]?[0-9])", "hh", Calendar.HOUR_OF_DAY);
        RBPattern minPat = new RBPattern("([0-5]?[0-9])", "mm", Calendar.MINUTE);
        RBPattern secPat = new RBPattern("([0-5]?[0-9])", "ss", Calendar.SECOND);
        RBPattern ampmPat = new RBPattern("(am|pm|AM|PM)", "aa", Calendar.AM_PM);
        
        RBPattern timePat1 = new RBPattern("" + hourPat + minPat + secPat, null, -1);
        RBPattern timePat2 = new RBPattern("" + hourPat + "[ :\\.]?" + minPat + "[ :\\.]?" + secPat, null, -1);
        RBPattern timePat3 = new RBPattern("" + hourPat + ":" + minPat + ":" + secPat, null, -1);
       
        RBPattern yearPat = new RBPattern("([1-2][0-9]{3}+)", "yyyy", Calendar.YEAR);
        RBPattern monthPat = new RBPattern("([01]?[0-9])", "MM", Calendar.MONTH);
        RBPattern monthStrPat = new RBPattern("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)", "MMM", Calendar.MONTH);
        RBPattern longMonthStrPat = new RBPattern("(January|February|March|April|May|June|July|August|September|October|November|December)", "MMMMM", Calendar.MONTH);
        RBPattern dayPat = new RBPattern("([0-3]?[0-9])", "dd", Calendar.DAY_OF_MONTH);
        
        RBPattern dayInWeekPat = new RBPattern("(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday)", "EEE", Calendar.DAY_OF_WEEK);
        
        RBPattern space = new RBPattern("[ ]", null, -1);
        RBPattern optSpace = new RBPattern("[ ]?", null, -1);
        RBPattern seperator = new RBPattern("[ \\.]", null, -1);
        RBPattern dateSeperator = new RBPattern("[- ,.][ ]?", null, -1);
        RBPattern timeSeperator = new RBPattern("[ :]", null, -1);
        

        int[] missingTime = {};

        
        if (true) {
            RBPattern[] patterns1 = {yearPat, monthPat, dayPat, new RBPattern("T", null, -1), hourPat, minPat, secPat};
            DateParser3 parser1 = new DateParser3(100, patterns1);
            kvparsers.add(parser1);
            dataparsers.add(parser1);
            
            RBPattern[] patterns2 = {yearPat, monthPat, dayPat};
            DateParser3 parser2 = new DateParser3(50, patterns2);
            kvparsers.add(parser2);

            RBPattern[] patterns3 = {yearPat, seperator, monthPat, seperator, dayPat, space, 
                                     hourPat, seperator, minPat, seperator, secPat};
            DateParser3 parser3 = new DateParser3(100, patterns3);
            kvparsers.add(parser3);
            dataparsers.add(parser3);   
            
            // 2002-01-02 18:52:17
            RBPattern[] patterns4 = {yearPat, dateSeperator, monthPat, dateSeperator, dayPat, space, 
                                     hourPat, timeSeperator, minPat, timeSeperator, secPat};
            
            DateParser3 parser4 = new DateParser3(100, patterns4);
            kvparsers.add(parser4);
            dataparsers.add(parser4);
            
            // Jan 12 19:56:22 2003            
            RBPattern[] patterns5 = {monthPat, dateSeperator, dayPat, space, 
                                     hourPat, timeSeperator, minPat, timeSeperator, secPat, space, yearPat};
            DateParser3 parser5 = new DateParser3(90, patterns5);
            kvparsers.add(parser5);
            dataparsers.add(parser5);
            
            // 2002-08-08
            RBPattern[] patterns6 = {yearPat, dateSeperator, monthPat, dateSeperator, dayPat};

            DateParser3 parser6 = new DateParser3(50, patterns6);
            kvparsers.add(parser6);
            dataparsers.add(parser6);
            
            

        }
        
        if(true) {
            int[] missingYear = {Calendar.YEAR};
            int[] missingYearTime = {Calendar.YEAR};
            
             // test(" we're hosting on January 12th at 4:00pm.  This wil");   
            RBPattern[] patterns6 = {longMonthStrPat, space, dayPat, 
                                     new RBPattern("th"), space, new RBPattern("at"), space, 
                                     hourPat, timeSeperator, minPat, ampmPat};
            
            dataparsers.add(new DateParser3(80, patterns6));
            
            // Scheduled Delivery: Jan. 3, 2003
            RBPattern[] patterns7 = {monthStrPat, dateSeperator, space, dayPat, dateSeperator, yearPat};
            dataparsers.add(new DateParser3(40, patterns7));            
            
            RBPattern[] patterns8 = {monthStrPat, space, dayPat, space, yearPat};
            dataparsers.add(new DateParser3(40, patterns8));            
            
            // Scheduled Delivery: 15-January-2003
            RBPattern[] patterns9 = {dayPat, dateSeperator, longMonthStrPat, dateSeperator, yearPat};
            dataparsers.add(new DateParser3(40, patterns9));

            //  (Dec. 2003) 
            RBPattern[] patterns10b = {monthStrPat, dateSeperator, yearPat};
            dataparsers.add(new DateParser3(35, patterns10b));
            


            // Jan 12 19:56:22 2003  
            RBPattern[] patterns10f = {monthStrPat, dateSeperator, dayPat, space, hourPat, timeSeperator, minPat, timeSeperator, secPat, space, yearPat};
            dataparsers.add(new DateParser3(90, patterns10f));
            
            // Sent: Thursday, January 09, 2003 9:36 PM
            RBPattern[] patterns10d = {longMonthStrPat, dateSeperator, dayPat, dateSeperator, yearPat, space, hourPat, timeSeperator, minPat, optSpace, ampmPat};
            dataparsers.add(new DateParser3(90, patterns10d));

            RBPattern[] patterns11 = {dayPat, space, monthStrPat, dateSeperator, yearPat, space, hourPat, timeSeperator, minPat, timeSeperator, secPat};
            dataparsers.add(new DateParser3(80, patterns11));  
            
            //$10.61 (January 14, 2002) and $19.55 (April 22, 2002) with an average =   
            RBPattern[] patterns10e = {longMonthStrPat, dateSeperator, dayPat, dateSeperator, yearPat};
            dataparsers.add(new DateParser3(40, patterns10e)); 
            
            // Cirque du Soleil on his birthday (Thurs, Dec. 12).  My thought is that
            RBPattern[] patterns10 = {monthStrPat, dateSeperator, dayPat};
            dataparsers.add(new DateParser3(30, patterns10));
            
            // Subject: housewarming party, Friday Dec. 13 5PM-9PM, RSVP please
            RBPattern[] patterns10c = {monthStrPat, dateSeperator, dayPat, space, hourPat, ampmPat};
            dataparsers.add(new DateParser3(32, patterns10c));
            
            // Sent: Thursday, January 9
            RBPattern[] patterns10g = {longMonthStrPat, dateSeperator, dayPat};
            dataparsers.add(new DateParser3(20, patterns10g));
            
  
            RBPattern[] patterns12 = {monthStrPat, dateSeperator, dayPat, dateSeperator, 
                          new RBPattern("[a-z]*[ ,]?"), hourPat, timeSeperator, minPat, space, ampmPat};
            // San Jose Fri, Feb 14, 1:00 pm, leaving from San Jose Mon, Feb. 17, around 1:00 pm, renting
            dataparsers.add(new DateParser3(35, patterns12));   
            
            // We aren't free on Friday, but we will be all weekend.
            dataparsers.add(new DateParser_DayOfWeek(10, dayInWeekPat));
        }
        
        if(true) {
            // 415-543-5021
            
            
            // http://www.wunderground.com 
            
        }

        kvparsers.add(new EmailAddressNameParser());
        kvparsers.add(new EmailAddressParser());
        kvparsers.add(new URLParser());
        kvparsers.add(new PhoneParser());
        kvparsers.add(new FullNameParser());
        
        dataparsers.add(new EmailAddressParser());
        dataparsers.add(new URLParser());
        dataparsers.add(new PhoneParser());
        dataparsers.add(new FullNameParser());

    }
    
    
    static DateFormatSymbols formatData=null;
    
    public static void initPatterns() {
        Locale locale = Locale.getDefault();
        formatData = new DateFormatSymbols(locale);
    }

    class RBPattern {
        public String pattern;
        public String key;
        public int num;
        
        public RBPattern(String pattern) {
            this(pattern, null, -1);
        }

        
        public RBPattern(String pattern, String key, int num) {
            this.pattern = pattern;
            this.key = key;
            this.num = num;
            
            
        }
        public String toString() {
            return this.pattern;
        }


        public int parse(String str) {
            if(formatData==null) initPatterns();
            int val = -1;
            try {
                val = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                val = -1;
            }
            
            if(this.num == Calendar.DAY_OF_WEEK) {
                str = str.toLowerCase();
                val = matchString(str, 0, Calendar.DAY_OF_WEEK, formatData.getWeekdays());
                if(val == -1) 
                    val = matchString(str, 0, Calendar.DAY_OF_WEEK, formatData.getShortWeekdays());    
            } else if(this.num == Calendar.AM_PM) {
                str = str.toLowerCase();
                val = matchString(str, 0, Calendar.AM_PM, formatData.getAmPmStrings());
                
            } else if(this.num == Calendar.MONTH && val == -1) {
                str = str.toLowerCase();
                val = matchString(str, 0, Calendar.MONTH, formatData.getMonths());
                if(val == -1) val = matchString(str, 0, Calendar.MONTH, formatData.getShortMonths());
            } else if(this.num == Calendar.YEAR) {
                if(str.length() == 2) {
                    if(val > 0 && val < 30) val = val + 2000;
                    else val = val + 1900;
                } else if(str.length() == 4) {
                    if(val > 2100 || val < 1900) val = -1;
                } else {
                    val = -1;
                }
            } else if(this.num == Calendar.MONTH) {
                if(val < 0 || val > 12) val = -1;
                else val--;
                
            } else if(this.num == Calendar.DAY_OF_MONTH) {
                if(val < 0 || val >= 32) val = -1;
            } else if(this.num == Calendar.HOUR_OF_DAY) {
                if(val < 0 || val >= 24) val = -1;
            } else if(this.num == Calendar.MINUTE) {
                if(val < 0 || val >= 60) val = -1;
            } else if(this.num == Calendar.SECOND) {
                if(val < 0 || val >= 60) val = -1;
            }
            return val;            
        }
        
        
        private int matchString(String text, int start, int field, String[] data) {
            int i = 0;
            int count = data.length;
            
            if (field == Calendar.DAY_OF_WEEK) i = 1;
            
            int bestMatchLength = 0, bestMatch = -1;
            for (; i<count; ++i) {
                int length = data[i].length();
                // Always compare if we have no match yet; otherwise only compare
                // against potentially better matches (longer strings).
                if (length > bestMatchLength &&
                text.regionMatches(true, start, data[i], 0, length)) {
                    bestMatch = i;
                    bestMatchLength = length;
                }
            }
            if (bestMatch >= 0) {
                return bestMatch;

            }
            return -1;
        }
    }
    
    
    
    

    public LinkedList match(LinkedList parsers, Calendar now, String key, String val) {
        int i=0;
        
        MatchObject[] matches = new MatchObject[parsers.size()];
        LinkedList retList = new LinkedList();
        
        while(i<val.length()) {
            //Debug.debug("    i=" + i + " -- " + val.length());
            int j=0;
            for(Iterator parserIter = parsers.iterator(); parserIter.hasNext(); ) {
                Parser parser = (Parser) parserIter.next();
                if(matches[j] == null) {
                    MatchObject mo = parser.test(i, now, key, val);
                    //if(mo != null) { Debug.debug("matched", parser); mo.display(); }
                    matches[j] = mo;
                }
                j++;
            }
            int firstMatch=-1;
            for(int k=0; k<matches.length; k++) {
                if(matches[k] != null) {
                    firstMatch = k;
                    break;
                }
            }
            if(firstMatch == -1) {
                // no more matches
                break;
            } 
            int start=matches[firstMatch].start;
            
            for(int k=firstMatch+1; k<matches.length; k++) {
                if(matches[k] == null) continue;
                
                int flag = 0;
                if(matches[k].start == start && matches[k].weight > matches[firstMatch].weight) {
                    flag = 1;
                }
                if(matches[k].start < start) {
                   // if((matches[k].start - matches[firstMatch].end) > 5)
                        flag = 1;
                }

                if(flag == 1) {
                    start = matches[k].start;
                    firstMatch = k;
                }
            }
            MatchObject mo = matches[firstMatch];
            retList.add(mo);
            i = mo.end;

            for(int k=0; k<matches.length; k++) {
                if(matches[k] != null && i > matches[k].start) matches[k] = null;
            }
        }
        //Debug.debug("done");
        return retList;
    }    
    
    public abstract class MatchObject {
        public Parser parser;
        public int weight;
        public int start;
        public int end;
        
        public MatchObject(Parser parser, int start, int end) {
            this.parser = parser;
            this.weight = 1;
            this.start = start;
            this.end = end;
        }
        
        public MatchObject(int weight) {
            this.weight = weight;
        }
        
        public int getWeight() {
            return this.weight;
        }
        
        public void setWeight(int weight) {
            this.weight = weight;
        }
        public void display() {
            Debug.debug("<MatchObject>");
            Debug.debug("</MatchObject>");
        }
        public void relate(String key, String val, DefaultItem item) {
            this.parser.relate(key, val, item, this);
        }
        public String toString() {
            return "";
        }
    }

    
    public class DateMatchObject extends MatchObject {
        Date date;
        Date endDate;


        public DateMatchObject(Parser parser, Date date, Date endDate, int start, int end) {
            super(parser, start, end);
            this.date = date;
            this.endDate = endDate;
        }

        public void display() {
            if(this.date.equals(this.endDate)) {
                Debug.debug("<Date weight=" + this.weight + " " + this.date + " start=" + this.start + ">");
            } else {
                Debug.debug("<Date weight=" + this.weight + " " + this.date + " -- " + this.endDate + " start=" + this.start + ">");

            }
        }
        public String toString() {
            return date.toString();
        }
    }

    public class StringMatchObject extends MatchObject {
        String str;

        public StringMatchObject(Parser parser, String str, int start, int end) {
            super(parser, start,end);
            this.str = str;
        }

        public void display() {
            Debug.debug("<String weight=" + this.weight + " " + this.str + " start=" + this.start + ">");
        }
        public String toString() {
            return this.str;
        }
    }        
    
    public class EmailAddressMatchObject extends MatchObject {
        String emailAddress;

        public EmailAddressMatchObject(Parser parser, String emailAddress, int start, int end) {
            super(parser, start,end);
            this.emailAddress = emailAddress;
        }

        public void display() {
            Debug.debug("<EmailAddress weight=" + this.weight + " " + this.emailAddress + " start=" + this.start + ">");
        }
        public String toString() {
            return this.emailAddress;
        }
    }    
    

    public class EmailAddressNameMatchObject extends MatchObject {
        public String emailAddress;
        public String name;

        public EmailAddressNameMatchObject(Parser parser, String emailAddress, String name, int start, int end) {
            super(parser, start, end);
            this.emailAddress = emailAddress;
            this.name = name;
        }

        public void display() {
            Debug.debug("<EmailAddressName weight=" + this.weight + " " + this.emailAddress + " " + this.name + ">");
        }
        public String toString() {
            return this.emailAddress + " " + this.name;
        }
    }    
    
    
    
    
    
    
    
    
    
    interface Parser {
        public MatchObject test(int start, Calendar now, String key, String val);
        public void relate(String key, String val, DefaultItem item, MatchObject mo) ;
        
    }

    
    public class EmailAddressParser implements Parser {
        Pattern pattern;
        
        public EmailAddressParser() {
            String emailPatternStr = "([A-Za-z][-0-9A-Za-z_=\\.]*@[0-9A-Za-z_][-0-9A-Za-z_\\.]*)";
        
            this.pattern = Pattern.compile(emailPatternStr);
        }
        
        public boolean checkEmailAddress(String emailAddress) {
            String[] parts = emailAddress.split("@");
            if(parts.length == 2) {
                String domain = parts[1];
                String[]dparts = domain.split("\\.");
                if(dparts.length >= 2) {
                    return true;
                }
            }
            return false;
        }

        
        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            
            if(m.find(start)==true) {
                String emailaddress = m.group();
                if(this.checkEmailAddress(emailaddress)) {
                    //Debug.debug("parts", domain, "length=" + dparts.length);
                    EmailAddressMatchObject mo = new EmailAddressMatchObject(this, emailaddress, m.start(), m.end());
                    mo.setWeight(90);
                    return mo;
                }
            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {

            EmailAddressMatchObject mo = (EmailAddressMatchObject) pmo;                 
            
            String emailAddress = mo.emailAddress;
            
            Debug.debug(3, "relateEmailAddress " + emailAddress);
            String recname = "email/" + emailAddress.toLowerCase();
            DefaultItem addr_item;
            try {
                addr_item = WinterMute.my_db.getItem(WinterMute.my_db.getOIDFromName(recname));
            } catch (eNoSuchItem e_noitem) {
                // create a new record!
                addr_item = WinterMute.my_db.newItem(null, "Default", recname);
                addr_item.put("email", emailAddress);
            }
            
            try {
                item.relateToOnce(WinterMute.messageEmailAddressRelation, addr_item);
            } catch (eDuplicateRelation edup) {
                //Debug.debug("Duplicate Relation!!");
            }
            
            

        }
    }    
    
    
    
    class EmailAddressNameParser extends EmailAddressParser {
        Pattern pattern;
        
        public EmailAddressNameParser() {
            String namePatternStr = "([A-Z][a-zA-Z]+ [A-Z][a-zA-Z]+)";
            String emailPatternStr = "([A-Za-z][-0-9A-Za-z_=\\.]*@(([0-9A-Za-z_][-0-9A-Za-z_]*)\\.?)+)";
            
            this.pattern = Pattern.compile("\"" + namePatternStr + "\" " + "<" + emailPatternStr + ">");          
        }

        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m;

            m = this.pattern.matcher(val);
            
            if(m.find(start)==true) {
                String emailAddress = m.group(2);
                String name = m.group(1);
                if(this.checkEmailAddress(emailAddress)) {
                    EmailAddressNameMatchObject mo = new EmailAddressNameMatchObject(this, emailAddress, name, m.start(), m.end());
                    mo.setWeight(100);
                    return mo;
                }
            }
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
            EmailAddressNameMatchObject mo = (EmailAddressNameMatchObject) pmo;
 
            //Debug.debug(3, "relateEmailAddress " + emailAddress);
            relateEmailAddress(mo.emailAddress, mo.name, item);

        }
    }
    
    public static void relateEmailAddress(String emailAddress, String name, DefaultItem item) {
        String recname = "email/" + emailAddress.toLowerCase();
        DefaultItem addr_item;
        try {
            addr_item = WinterMute.my_db.getItem(WinterMute.my_db.getOIDFromName(recname));
        } catch (eNoSuchItem e_noitem) {
            // create a new record!
            addr_item = WinterMute.my_db.newItem(null, "Default", recname);
            addr_item.put("email", emailAddress);
        }
        
        try {
            item.relateToOnce(WinterMute.messageEmailAddressRelation, addr_item);
        } catch (eDuplicateRelation edup) {
            //Debug.debug("Duplicate Relation!!");
        }
        
        
        if (name != null && name.length() > 4) {
            addr_item.put("name", name);
            
            String recname2 = "contact/" +  name.toLowerCase();
            DefaultItem contact_item;
            try {
                contact_item = WinterMute.my_db.getItem(WinterMute.my_db.getOIDFromName(recname2));
            } catch (eNoSuchItem e_noitem) {
                // create a new record!
                contact_item = WinterMute.my_db.newItem(null, "Default", recname2);
                contact_item.put("Name", name);
            }
            
            try {
                addr_item.relateToOnce(WinterMute.emailAddressContactRelation, contact_item);
            } catch (eDuplicateRelation edup) {
                //Debug.debug("Duplicate Relation!!");
            }
            
            
            // add to the global contact database.
            DefaultItem contacts;
            try {
                contacts = WinterMute.my_db.getItem(WinterMute.my_db.getOIDFromName("Contacts"));
                contacts.relateToOnce(WinterMute.containerContainsRelation, contact_item);
            } catch (eNoSuchItem e_noitem) {
            } catch (eDuplicateRelation e_noitem) {
            }
            
            
            
        }
    }

    
    

    
    
    class DateParser3 implements Parser {
        RBPattern[] patterns;
        int weight;

        private Pattern pattern;
        

        public DateParser3(int weight, RBPattern[] patterns) {
            this.weight = weight;
            this.patterns = patterns;
            
            this.pattern = this.computePattern(patterns);

        }
        
        private Pattern computePattern(RBPattern[] patterns) {
            StringBuffer buf = new StringBuffer();
            buf.append("(");
            for(int i=0; i<patterns.length; i++) {
                buf.append(patterns[i].pattern);
            }
            buf.append(")");
            String pat = buf.toString();
            Pattern retPat = Pattern.compile(pat);
            
            return retPat;
        }

        

        
        private boolean checkDate(Calendar c) {
            int year = c.get(c.YEAR);
            if(year < 1900 || year > 2200) return false;
            
            int month = c.get(c.MONTH);
            if(month < 0 || month >= 13) return false;
            
            int day = c.get(c.DAY_OF_MONTH);
            if(day < 0 || day >= 32) return false;
            return true;
        }
        
        
        
        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            //Debug.debug("pattern", this.pattern.pattern());
            
            Calendar c = (Calendar) now.clone();      
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            if(m.find(start)==true) {
                String dateStr = m.group(1);
                Debug.debug(debugLevel, "matched=" + dateStr);
                int j=0;
                boolean timeSet = false;
                boolean daySet = false;
                boolean yearSet = false;
                
                
                for(int i=0; i<this.patterns.length; i++) {
                    
                    RBPattern rbp = this.patterns[i];
                    if(rbp.num == -1) continue;

                    String part = m.group(j+2);
                    
                    //Debug.debug("parsing", part, rbp.key);
                    int value = rbp.parse(part);
                    if(value != -1) {
                        //Debug.debug("setting calendar: " + rbp.num + " " + value);
                        if(rbp.num == Calendar.HOUR_OF_DAY) timeSet = true;
                        else if(rbp.num == Calendar.DATE) daySet = true;
                        else if(rbp.num == Calendar.DAY_OF_MONTH) daySet = true;
                        else if(rbp.num == Calendar.YEAR) yearSet = true;
                        
                        if(rbp.num == Calendar.AM_PM) {
                            int hour = c.get(Calendar.HOUR_OF_DAY);
                            if(value == 1) {
                                c.set(Calendar.HOUR_OF_DAY, hour+12);
                            }
                        } else {
                            c.set(rbp.num, value);
                            
                        }
                    } else {
                        return null;
                    }
                    j++;
                }
                if(daySet == false) {
                    c.set(Calendar.DAY_OF_MONTH, 0);
                }
                if(yearSet == false) {
                    int month = c.get(Calendar.MONTH);
                    int month2 = now.get(Calendar.MONTH);
                    
                    if(c.getTime().before(now.getTime())) {
                        c.add(Calendar.YEAR, 1);
                    }
                }
                
                if(this.checkDate(c)) {
                    Date date = c.getTime();
                    Date endDate = date;
                    if(daySet == false) {
                        c.add(Calendar.MONTH, 1);
                        c.add(Calendar.MILLISECOND, -1);
                    } else if(timeSet == false) {
                        c.add(Calendar.DATE, 1);
                        c.add(Calendar.MILLISECOND, -1);
                    }
              
                    endDate = c.getTime();
                    
                    DateMatchObject mo = new DateMatchObject(this, date, endDate, m.start(), m.end());
                    
                    mo.setWeight(this.weight);
                    return mo;
                }
            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
            DateMatchObject mo = (DateMatchObject) pmo;
            
            String datenum = Long.toString(mo.date.getTime());
            
            if(key.equals("")) key = "body." + pmo.start + "-" + pmo.end;
            
            item.put("#Date." + key, datenum);       
            
            CalendarItemModel.relateCalendarItem(item);    
            
        }
    }
    
    
    
    
    
    
    
    class DateParser_DayOfWeek implements Parser {
        int weight;
        RBPattern rbpattern;
        Pattern pattern;
        
        public DateParser_DayOfWeek(int weight, RBPattern rbpattern) {
            this.weight = weight;
            this.rbpattern = rbpattern;
            this.pattern = Pattern.compile(rbpattern.pattern);

        }

        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            
            Calendar c = (Calendar) now.clone();      
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            if(m.find(start)==true) {
                String dateStr = m.group(1);
                Debug.debug(debugLevel, "matched=" + dateStr);
                
                
                int dow = this.rbpattern.parse(dateStr);
                if(dow != -1) {
                    int pdow = c.get(Calendar.DAY_OF_WEEK);
                    
                    //Debug.debug("dow:" + dow);
                    c.set(Calendar.DAY_OF_WEEK, dow);
                    Date startDate = c.getTime();
                    
                    //Debug.debug("before", now.getTime());
                    if(now.getTime().getTime() > c.getTime().getTime()) c.add(Calendar.DATE, 7);
                    //Debug.debug("after", c.getTime());
                    
                    startDate = c.getTime();
                    
                    // calculate the end Date
                    c.add(Calendar.DATE, 1);
                    c.add(Calendar.MILLISECOND, -1);
                    Date endDate = c.getTime();
                    
                    DateMatchObject mo = new DateMatchObject(this, startDate, endDate, m.start(), m.end());
                    mo.setWeight(this.weight);
                    return mo;
                }

            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
            DateMatchObject mo = (DateMatchObject) pmo;
            
            String datenum = Long.toString(mo.date.getTime());
            
            if(key.equals("")) key = "body." + pmo.start + "-" + pmo.end;
            
            item.put("#Date." + key, datenum);       
            
            CalendarItemModel.relateCalendarItem(item);    
            
        }
    }
        
    
    public class URLParser implements Parser {
        Pattern pattern;
        
        public URLParser() {
            String pat = "(?i)((http|ftp|https|ical)://[^\\s>\\]]+)";
            this.pattern = Pattern.compile(pat);
        }

        
        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            
            if(m.find(start)==true) {
                String url = m.group();
                if(true) {
                    StringMatchObject mo = new StringMatchObject(this, url, m.start(), m.end());
                    mo.setWeight(100);
                    return mo;
                }
            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
        }
    }

    public class PhoneParser implements Parser {
        Pattern pattern;
        
        public PhoneParser() {
            String pat = "((\\(?[0-9]{3}+\\)?[- \\.]?)?[0-9]{3}+[- \\.]?[0-9]{4}+)";
            this.pattern = Pattern.compile(pat);
        }

        
        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            
            if(m.find(start)==true) {
                String url = m.group();
                if(true) {
                    StringMatchObject mo = new StringMatchObject(this, url, m.start(), m.end());
                    mo.setWeight(100);
                    return mo;
                }
            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
        }
    }    
    
    public class FullNameParser implements Parser {
        Pattern pattern;
        
        public FullNameParser() {
            String pat = "([A-Z][A-Za-z]+ [A-Z][A-Za-z]+)";
            this.pattern = Pattern.compile(pat);
        }

        
        public MatchObject test(int start, Calendar now, String key, String val) {
            
            Matcher m = this.pattern.matcher(val);
            
            if(m.find(start)==true) {
                String url = m.group();
                if(true) {
                    StringMatchObject mo = new StringMatchObject(this, url, m.start(), m.end());
                    mo.setWeight(100);
                    return mo;
                }
            }
            
            return null;
        }
        
        public void relate(String key, String val, DefaultItem item, MatchObject pmo) {
        }
    }        
    
    
    
    
    
    public static void init() {
        rbuilder = new RelationshipBuilder();
    }

    
    public static LinkedList test(Calendar now, String val) {
        String key = "";
        Debug.debug(debugLevel, "");
        Debug.debug(debugLevel, "val=" + val);

        LinkedList matches = RelationshipBuilder.rbuilder.match(rbuilder.kvparsers, now, "", val);
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            MatchObject mo = (MatchObject) iter.next();
            if(debugLevel < 3) mo.display();
        }

        Debug.debug(debugLevel, "");
        return matches;
    }
    
    public static LinkedList testData(Calendar now, String val) {
        String key = "";
        Debug.debug(debugLevel, "");
        Debug.debug(debugLevel, "val=" + val);
        LinkedList matches = RelationshipBuilder.rbuilder.match(rbuilder.dataparsers, now, "", val);
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            MatchObject mo = (MatchObject) iter.next();
            if(debugLevel < 3) mo.display();
        }        

        Debug.debug(debugLevel, "");
        return matches;
    }    
    
    public static boolean findEmailMatch(LinkedList matches, String emailAddress) {
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            MatchObject mo = (MatchObject) iter.next();
            if(mo instanceof EmailAddressMatchObject) {
                EmailAddressMatchObject emo = (EmailAddressMatchObject) mo;
                
                if(emo.emailAddress.equals(emailAddress)) return true;
            }
        }        
        return false;
    }

    public static boolean findEmailNameMatch(LinkedList matches, String emailAddress, String name) {
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            MatchObject mo = (MatchObject) iter.next();
            if(mo instanceof EmailAddressNameMatchObject) {
                EmailAddressNameMatchObject emo = (EmailAddressNameMatchObject) mo;
                
                if(emo.emailAddress.equals(emailAddress) && emo.name.equals(name)) return true;
            }
        }      
        return false;
    }
    
    public static boolean findDateMatch_AllDay(String data, LinkedList matches, Calendar startDate) {
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.MILLISECOND, -1);      
        return findDateMatch(data, matches, startDate, endDate);
    }

    public static boolean findDateMatch(String data, LinkedList matches, Calendar startDate, Calendar endDate) {
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            MatchObject mo = (MatchObject) iter.next();
            if(mo instanceof DateMatchObject) {
                DateMatchObject dmo = (DateMatchObject) mo;
                
                if(dmo.date.equals(startDate.getTime()) && dmo.endDate.equals(endDate.getTime())) {
                    Debug.debug("test passed", startDate.getTime(), data); 
                    
                    return true;
                }
            }
        }        
        Debug.debug("*** TEST FAILED: ", startDate.getTime(), endDate.getTime(), data);  
        return false;
    }    
    
    public static void main(String args[]) throws Exception {
        Debug.start();
        Debug.debug("testing...");
        
        
        RelationshipBuilder.init();
        
        SimpleDateFormat dp = new SimpleDateFormat("yyyyMMdd'T'hhmmss");
        Date d = dp.parse("20020124T143000");
        Debug.debug(d, "" + d.getTime());
        
        dp.setTimeZone(TimeZone.getTimeZone("PST"));
        d = dp.parse("20020124T143000");
        
        Debug.debug(d);
        
        Calendar now = new GregorianCalendar(2003, 0, 12, 18, 29, 00);
        Calendar now2 = new GregorianCalendar(2003, 0, 12, 18, 29, 00);
        
        LinkedList matches;
        boolean test = false;
        String data;
        Calendar startDate;
        Calendar endDate;
        
        data = "Scott Hassan <hassan@dotfunk.com>";
        matches = test(now, data);
        test = findEmailMatch(matches, "hassan@dotfunk.com");
        if(test == false) Debug.debug("test failed: ", data);
        else Debug.debug("test passed", data);
        
        
        data = "     Scott Hassan <hassan@dotfunk.com>   ";
        matches = test(now, data);
        test = findEmailMatch(matches, "hassan@dotfunk.com");
        if(test == false) Debug.debug("test failed: ", data);        
        else Debug.debug("test passed", data);

        
        data = "\"Allison Huynh\" <allison@huynh.com>";
        matches = test(now, data);
        test = findEmailNameMatch(matches, "allison@huynh.com", "Allison Huynh");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        
        
        data = "  20020124T143000 ";
        matches = test(now, data);
        startDate = new GregorianCalendar(2002, 0, 24, 14, 30, 00);
        test = findDateMatch(data, matches, startDate, startDate);
        
        data = "  jeske@chat.net   blong@fiction.net   \"Scott Hassan\" <hassan@dotfunk.com> ";
        matches = test(now, data);
        test = findEmailNameMatch(matches, "hassan@dotfunk.com", "Scott Hassan");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        test = findEmailMatch(matches, "jeske@chat.net");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        test = findEmailMatch(matches, "blong@fiction.net");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);

        
        data = "  jeske@chat.net, blong@fiction.net, Scott Hassan <hassan@dotfunk.com> ";
        matches = test(now, data);
        test = findEmailMatch(matches, "hassan@dotfunk.com");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        test = findEmailMatch(matches, "jeske@chat.net");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        test = findEmailMatch(matches, "blong@fiction.net");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        
        
        data = "  20030130T090000  jeske@chat.net, 2002-12-10    hassan@dotfunk.com    2002-01-01     2002-01-02 18:52:17    20020103T120115  Jan 12 19:56:22 2003  ";
        matches = testData(now, data);
        test = findEmailMatch(matches, "hassan@dotfunk.com");        
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        test = findEmailMatch(matches, "jeske@chat.net");
        if(test == false) Debug.debug("test failed: ", data);  
        else Debug.debug("test passed", data);
        
        startDate = new GregorianCalendar(2003, 0, 30, 9, 00, 00);
        test = findDateMatch(data, matches, startDate, startDate);  
        
        startDate = new GregorianCalendar(2002, 11, 10, 00, 00, 00);
        test = findDateMatch_AllDay(data, matches, startDate);
        
        startDate = new GregorianCalendar(2002, 0, 1, 00, 00, 00);
        test = findDateMatch_AllDay(data, matches, startDate);
        
        startDate = new GregorianCalendar(2002, 0, 2, 18, 52, 17);
        test = findDateMatch(data, matches, startDate, startDate);
        startDate = new GregorianCalendar(2002, 0, 3, 12, 01, 15);
        test = findDateMatch(data, matches, startDate, startDate);  
        startDate = new GregorianCalendar(2003, 0, 12, 19, 56, 22);
        test = findDateMatch(data, matches, startDate, startDate);          
        
        
        
        data = " Jan 12 19:56:22 2003  ";
        matches = testData(now, data);        
        startDate = new GregorianCalendar(2003, 0, 12, 19, 56, 22);
        test = findDateMatch(data, matches, startDate, startDate);                 
        
        data  = "         // Cirque du Soleil on his birthday (Thurs, Dec. 12).  My thought is that ";
        Calendar now5 = new GregorianCalendar(2002, 11, 01, 18, 29, 00);
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2002, 11, 12, 00, 00, 00);   
        test = findDateMatch_AllDay(data, matches, startDate);                 
        
        data = " we're hosting on January 12th at 4:00pm.  This wil";
        now5 = new GregorianCalendar(2002, 11, 15, 18, 29, 00);
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 0, 12, 16, 00, 00);
        endDate = (Calendar) startDate.clone();       
        test = findDateMatch(data, matches, startDate, endDate);             
        
        data = "Scheduled Delivery: 15-January-2003  ";
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 0, 15, 00, 00, 00);   
        test = findDateMatch_AllDay(data, matches, startDate);   
        
        data = "We aren't free on Friday, but we will be all weekend.  ";
        now5 = new GregorianCalendar(2003, 0, 15, 18, 29, 00);        
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 0, 17, 00, 00, 00); 
        test = findDateMatch_AllDay(data, matches, startDate);   
        
        // Jan 12 19:56:22 2003
        // 12 Jan 2003 02:07:00 -0000
        // Jan 11, 2003 at 06:44:21PM -0500
        //   Date: Sat, 11 Jan 2003 05:02:37 -0000
        //Introduction    Jan 14-15
        
        data = " Introduction     Jan 20 ";
        now5 = new GregorianCalendar(2003, 0, 15, 18, 29, 00);        
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 0, 20, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate);           
        
        data = "Wed, 8 Jan 2003 20:46:02 GMT";
        matches = testData(now, data);
        startDate = new GregorianCalendar(2003, 0, 8, 20, 46, 02);  
        test = findDateMatch(data, matches, startDate, startDate); 
        
        data = "using the January 6, 2003 close for this stock.";
        matches = testData(now, data);
        startDate = new GregorianCalendar(2003, 0, 6, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate); 
        
        data = "quarter high and has been steadily rising. On Thursday, January 2, Home";
        now5 = new GregorianCalendar(2002, 11, 15, 18, 29, 00);        
        matches = testData(now5, data);
        if(matches.size() != 1) {
            Debug.debug("TEST FAILED: too many matches", data);
        }
        startDate = new GregorianCalendar(2003, 0, 2, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate); 
        
        data = "$10.61 (January 14, 2002) and $19.55 (April 22, 2002) with an average =";
        matches = testData(now, data);
        startDate = new GregorianCalendar(2002, 0, 14, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate); 
        startDate = new GregorianCalendar(2002, 3, 22, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate);         
        
        
        //testData(now, "Handling Anger - Jan 2003 (In San Francisco)");
        
        
        data = "we can get to Buffalo on Thursday and then head back";
        now5 = new GregorianCalendar(2002, 11, 15, 18, 29, 00);        
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2002, 11, 19, 00, 00, 00); 
        test = findDateMatch_AllDay(data, matches, startDate);           
        
        data = "Cirque du Soleil on his birthday (Thurs, Dec. 12).  My thought is that";
        now5 = new GregorianCalendar(2002, 11, 5, 18, 29, 00);        
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2002, 11, 12, 00, 00, 00);  
        test = findDateMatch_AllDay(data, matches, startDate);         
        
        
        data = "Thursday, Nov. 21st, 7 - 9 PM";
        now5 = new GregorianCalendar(2002, 9, 5, 18, 29, 00);        
        matches = testData(now5, data);
        if(matches.size() != 1) {
            Debug.debug("TEST FAILED: too many matches", data);
        }
        startDate = new GregorianCalendar(2002, 10, 21, 7+12, 00, 00); 
        endDate = new GregorianCalendar(2002, 10, 21, 9+12, 00, 00);  
        test = findDateMatch(data, matches, startDate, endDate);     
        
        
        data = "Sent: Thursday, January 09, 2003 9:36 PM";
        matches = testData(now, data);
        startDate = new GregorianCalendar(2003, 0, 9, 9+12, 36, 00);  
        test = findDateMatch(data, matches, startDate, startDate); 
        
        data = "then. either Friday or Sat night depending on lots of";
        now5 = new GregorianCalendar(2002, 4, 8, 20, 04, 14);
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2002, 4, 10, 00, 00, 00);
        test = findDateMatch_AllDay(data, matches, startDate);           
        startDate = new GregorianCalendar(2002, 4, 11, 00, 00, 00);
        test = findDateMatch_AllDay(data, matches, startDate);   
        
        data = "Subject: housewarming party, Friday Dec. 13 5PM-9PM, RSVP please";
        now5 = new GregorianCalendar(2002, 10, 5, 18, 29, 00);        
        matches = testData(now5, data);
        if(matches.size() != 1) {
            Debug.debug("TEST FAILED: too many matches", data);
        }        
        startDate = new GregorianCalendar(2002, 11, 13, 5+12, 00, 00); 
        endDate = new GregorianCalendar(2002, 11, 13, 9+12, 00, 00);  
        test = findDateMatch(data, matches, startDate, endDate);         
                
        
        // 2003.01.09 20:04:14
        
        data = "Hi,  We aren't free on Friday, but we will be all weekend.  I haven't read the whole article but the gist is that they have a 'desktop application' They have better numbers than weather.com because a lot of people have it loaded and it pulls pages in the background, this makes the counting a bit bogus since it is always running..  Alan  ---------------------------- Alan Steremberg 415-543-5021 x 103 http://www.wunderground.com ";
        now5 = new GregorianCalendar(2003, 0, 11, 20, 04, 14);
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 0, 17, 00, 00, 00);
        test = findDateMatch_AllDay(data, matches, startDate);   
        
        // 2003.01.11 11:36:30
        data = "Dear Family and Friends in the Bay Area (cc Mom and a few others),  I will be flying up to the Bay Area for the weekend of Feb. 14-17, probably (depending on his plans) staying with my stepbrother Carl Page in SF. Arriving on Southwest to San Jose Fri, Feb 14, 1:00 pm, leaving from San Jose Mon, Feb. 17, around 1:00 pm, renting a car during.  Hope to see many or all of you!";
        now5 = new GregorianCalendar(2003, 0, 11, 11, 36, 30);
        
        matches = testData(now5, data);
        startDate = new GregorianCalendar(2003, 1, 14, 13, 00, 00);
        test = findDateMatch(data, matches, startDate, startDate);   
        startDate = new GregorianCalendar(2003, 1, 17, 13, 00, 00);
        test = findDateMatch(data, matches, startDate, startDate);   

        data = "  586-8563   (415) 586-8563  415.586.8563  ";        
        matches = testData(now5, data);    
    }    
    
    
    /////////////////////////////////////////////////////////////////////
    //             E N T R Y    P O I N T S                            //
    /////////////////////////////////////////////////////////////////////
    

    public void buildRelationships(DefaultItem item) {
        this.buildRelationships(this.now, item);
    }

    public void buildRelationships(Calendar now, DefaultItem item) {
        Debug.debug("1 buildRelationships", item);
        
        LinkedList keys = item.keyList();
        int i = 0;
        
        for(Iterator iter = keys.iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            String val = item.get(key);
            if(val!=null && val.length() > 0 && !val.startsWith("#")) {
                LinkedList retList = this.match(this.kvparsers, now, key, val);
                for(Iterator iter2 = retList.iterator(); iter2.hasNext(); ) {
                    MatchObject mo = (MatchObject) iter2.next();
                    mo.relate(key, val, item);
                }
            }
        }
        Debug.debug("1 done buildRelationship");
        
    }
    
    public void buildRelationships(Date then, DefaultItem item, String content) {
        Debug.debug("2 buildRelationships", then, item);
        Calendar thenCal = (Calendar) this.now.clone();
        thenCal.setTime(then);
        
        content = content.substring(0, Math.min(1024, content.length()));
        
        String key = "";
        LinkedList retList = this.match(this.dataparsers, thenCal, key, content);
        for(Iterator iter2 = retList.iterator(); iter2.hasNext(); ) {
            MatchObject mo = (MatchObject) iter2.next();
            mo.relate(key, content, item);
        }
        Debug.debug("2 done buildRelationships", then);
    }
}
