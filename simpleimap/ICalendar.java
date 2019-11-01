

package simpleimap;


import  java.util.Enumeration;
import  java.util.StringTokenizer;
import  java.util.Vector;
import  java.util.NoSuchElementException;

import java.util.*;
import java.text.*;

import java.net.*;
import java.util.*;
import java.io.*;
/**
 * ICalendar: ICalendar (rfc2445).
 *
 * Currently: only supports VEVENT components;
 * many properties of which are not (fully) supported.
 *
 * @author slh
 * @version 0.11 2002/02/27 slh
 */
public class  ICalendar  {
    String url = null;
    
/*----------------------------------------------------------------------------
 *						Constructors
 *--------------------------------------------------------------------------*/
    public ICalendar(String url) {
        this.url = url;
    }
    
    private static String getTextFromFile(BufferedReader oIn) throws IOException {
        String sLine; //We will read each line into this temporary variable.
        StringBuffer sBuffer = new StringBuffer("");
        
        while((sLine = oIn.readLine()) != null) {
            sBuffer.append(sLine  + "\r\n");
        }
        
        //Return the stuff as a regular string object.
        return sBuffer.toString();
    }
    
    public String getURL(String url) {
        URLConnection conn;
        String page = null;
        
        try {
            URL _url = new URL(url);
            conn = _url.openConnection();
            
        } catch (MalformedURLException e) {     // new URL() failed
            throw new RuntimeException("MalformedURLException");
        } catch (IOException e) {               // openConnection() failed
            Debug.debug("openConnection Failed " + url);
            throw new RuntimeException("openConnection Failed");
        }
        BufferedReader in;
        try {
            in = new BufferedReader(
            new InputStreamReader(
            conn.getInputStream()));
            page = getTextFromFile(in);
            in.close();
            
        } catch (IOException e) {               // openConnection() failed
            throw new RuntimeException("read Failed");
        }
        
        Debug.debug("-----------\r\n" + page + "\r\n--------------\r\n");
        return page;
    }
    
    public class ParseException extends Exception {
        public ParseException() {
            super();
        }
        public ParseException(String s) {
            super(s);
        }
    }
    
    protected
    MultiSyncSource
    Import  (DefaultItem parent)
    throws Exception	/* InvalidStreamException */ {
        StringTokenizer	st;
        String		strToken;
        
        String strICalendar = this.getURL(this.url);
        
        MultiSyncSource items = new MultiSyncSource();
        
        
        
        st = new StringTokenizer( strICalendar , strDelimiters , true );
        /* if not begin calendar line... */
        if (!st.nextToken(  ).equals( strBegin ) ||
        !st.nextToken(  ).equals( strValueInducer ) ||
        !st.nextToken(  ).equals( strVCalendar )) {
            throw new RuntimeException(": missing calendar begin" );
        }
        /*void*/eatEOL( null , st );
        
        try {
        /*void*/addComponent(items , parent, null, null , st );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        //this.relateDates(component);
        
        return items;
    }
    
    
/*----------------------------------------------------------------------------
 *						Object Public Methods
 *--------------------------------------------------------------------------*/
    
    
    
/*----------------------------------------------------------------------------
 *						Object Protected Methods
 *--------------------------------------------------------------------------*/
    /*-------------------------------------- ICalendar Stream Support	*/
    
    
    
    
    protected
    void
    addComponent  (MultiSyncSource		source	,
    PropMapType parent,
    PropMapType            comp,
    String			strName	,
    StringTokenizer	st	)
    throws ParseException	/* InvalidStreamException */ {
        //DefaultItem	component;
        String	strToken;
        
        /* if not just adding to existing component... */
        if(strName != null) {
            if(comp != null) {
                String uid = comp.get("UID");
                comp.put("name", uid);
                MultiSyncRecord rec = new MultiSyncRecord(uid, comp, 0);
                source.add(rec);
                comp=null;
            } 
            
            comp = new PropMap();
        }

        
        strToken = st.nextToken( strDelimiters );
        /* while not at the end of this component... */
        for ( ; !strToken.equals( strEnd ) ; ) {
            /* if not the beginning of a (sub)component... */
            if (!strToken.equals( strBegin )) {
                /* ...must be a property... */
                if(comp != null) {
                    addProperty( comp , strToken , st );
                } else {
                    addProperty(parent, strToken, st);
                }
                /* ...else must be the start of a (sub)component... */
            } else {
                if (!st.nextToken(  ).equals( strValueInducer )) {
                    throw new ParseException(
                    ": value delimiter not found" );
                }
                strToken = st.nextToken(  );	// get name of component
                /*void*/eatEOL( null , st );
                addComponent(source, parent, comp , strToken , st );
            }
            strToken = st.nextToken( strDelimiters );
        }
        
        /* if not at end line with matching component name... */
        if (!strToken.equals( strEnd ) ||
        !st.nextToken(  ).equals( strValueInducer ) ||
        //???
        !st.nextToken(  ).equals( strName ) && strName != null) {
            throw new ParseException(
            ": missing or nonmatching component end" );
        }
        /*void*/eatEOL( null , st );
        
        /* if not just adding to existing component... */
        if (strName != null) {
            if(comp != null) {
                String uid = comp.get("UID");
                if(uid != null) {
                    comp.put("name", uid);
                    MultiSyncRecord rec = new MultiSyncRecord(uid, comp, 0);
                    source.add(rec);
                } else {
                    Debug.debug("Cannot find UID for object!");
                }
            }
            //Debug.debug("comp.addComponent( component )" + strName);
        }
    }
    
    
    /*???objComp arg will go as will appear inside Component/descendent*/
    protected
    void
    addProperty  (PropMapType comp,
    String		strName	,
    StringTokenizer	st	)
    throws ParseException	/* InvalidStreamException */ {
        String	strToken;
        
        strToken = st.nextToken( strDelimiters );
        for ( ; strToken.equals( strParamInducer ) ; ) {
            strToken = st.nextToken(  );			// get param name
            addParameter(strToken , st );
            //Debug.debug("addparameter " + strName + "=" + strToken);
            strToken = st.nextToken(  );
        }
        if (strToken.equals( strValueInducer )) {
            strToken = st.nextToken( strLineTerm );	// get prop value
            //property.set( strToken );
            comp.put(strName, strToken);
            //Debug.debug("property.set " + strName + "=" + strToken);
            strToken = st.nextToken(  );
        }
        /*void*/eatEOL( strToken , st );
        
        //comp.addProperty( property );
        //Debug.debug("addproperty");
    }
    
    
    /*???objProp arg will go as will appear inside Property/descendent*/
    protected
    void
    addParameter  (//Property		prop	,
    String			strName	,
    StringTokenizer	st	)
    throws ParseException	/* InvalidStreamException */ {
        //Parameter	parameter;
        String	strToken;
        
        //parameter = (Parameter)parameterNameToObject( strName );
        strToken = st.nextToken( strDelimiters );
        if (!strToken.equals( strParamValueInducer )) {
            throw new ParseException(
            ": missing " + strParamValueInducer );
        }
        strToken = st.nextToken(  );	// get param value
        
        //Debug.debug("prop=" + strToken);
        
    }
    
    
    
    
    
    
    /**
     * Check for and consume proper line termination.
     */
    protected
    void
    eatEOL  (String		strToken	,
    StringTokenizer	st		) throws ParseException {
        String	strTmp		= "";
        
        try {
            strTmp = getDelim( strToken , strLineTerm , st );
        } catch (NoSuchElementException	e	) {
            ;		/* short string will trigger throw below */
        }
        
        if (!strTmp.equals( strLineTerm )) {
            throw new ParseException(
            ": proper line termination sequence not found" );
        }
    }
    
    
    /**
     * Accumulate and return (probably) multi-char line terminator.
     */
    protected
    String
    getDelim  (String		strToken	,
    String		strDelim	,
    StringTokenizer	st		)
    throws NoSuchElementException {
        String	strTmp		= "";
        int		idx		= 0;
        
        //Debug.debug("getDelim: <" + strToken + "> <" + strDelim + ">");
        
        if (strToken == null) {
            strToken = st.nextToken( strDelim );
        }
        strTmp += strToken;
        idx++;
        
        for ( ; idx < strDelim.length(  ) ; idx++) {
            strTmp += st.nextToken( strDelim );
        }
        
        return strTmp;
    }
    
    
    
    
    
    final static public  String	strBegin		= "BEGIN";
    final static public  String	strEnd			= "END";
    
    final static public  String	strValueInducer		= ":";
    final static public  String	strParamInducer		= ";";
    final static public  String	strParamValueInducer	= "=";
    final static public  String	strLineTerm		= "\r\n";
    
    final static public  String	strDelimiters		=
    strValueInducer + strParamInducer + strParamValueInducer +
    strLineTerm;
    
    final static public  String	strParam_AltRep		= "ALTREP";
    final static public  String	strParam_CN		= "CN";
    final static public  String	strParam_CUType		= "CUTYPE";
    final static public  String	strParam_DelFrom	= "DELFROM";
    final static public  String	strParam_DelTo		= "DELTO";
    final static public  String	strParam_Dir		= "DIR";
    final static public  String	strParam_Encoding	= "ENCODING";
    final static public  String	strParam_FmtType	= "FMTTYPE";
    final static public  String	strParam_FBType		= "FBTYPE";
    final static public  String	strParam_Language	= "LANGUAGE";
    final static public  String	strParam_Member		= "MEMBER";
    final static public  String	strParam_PartStat	= "PARTSTAT";
    final static public  String	strParam_Range		= "RANGE";
    final static public  String	strParam_TrigRel	= "TRIGREL";
    final static public  String	strParam_RelType	= "RELTYPE";
    final static public  String	strParam_Role		= "ROLE";
    final static public  String	strParam_RSVP		= "RSVP";
    final static public  String	strParam_SentBy		= "SENTBY";
    final static public  String	strParam_TZID		= "TZID";
    final static public  String	strParam_ValueType	= "VALUE";
    /* ianaparam */
    /* xparam */
    
    /* 4.3 Property Value Data Types */
    final static public  String	strValue_Binary		= "BINARY";
    final static public  String	strValue_Boolean	= "BOOLEAN";
    final static public  String	strValue_CalAddress	= "CAL-ADDRESS";
    final static public  String	strValue_Date		= "DATE";
    final static public  String	strValue_DateTime	= "DATE-TIME";
    final static public  String	strValue_Duration	= "DURATION";
    final static public  String	strValue_Float		= "FLOAT";
    final static public  String	strValue_Integer	= "INTEGER";
    final static public  String	strValue_Period		= "PERIOD";
    final static public  String	strValue_Recur		= "RECUR";
    final static public  String	strValue_Text		= "TEXT";
    final static public  String	strValue_Time		= "TIME";
    final static public  String	strValue_Uri		= "URI";
    final static public  String	strValue_UtcOffset	= "UTC-OFFSET";
    /* iana-token */
    /* x-name */
    
    /* 4.4 iCalendar Object */
    final static public  String	strVCalendar		= "VCALENDAR";
    
    /* 4.6 Calendar Components (.1 - .6) */
    final static public  String	strComp_VEvent		= "VEVENT";
    final static public  String	strComp_VTodo		= "VTODO";
    final static public  String	strComp_VJournal	= "VJOURNAL";
    final static public  String	strComp_VFreebusy	= "VFREEBUSY";
    final static public  String	strComp_VTimezone	= "VTIMEZONE";
    final static public  String	strComp_VAlarm		= "VALARM";
    
    /* 4.7 Calendar Properties (.1 - .4) */
    final static public  String	strProp_CalScale	= "CALSCALE";
    final static public  String	strProp_Method		= "METHOD";
    final static public  String	strProp_ProdId		= "PRODID";
    final static public  String	strProp_Version		= "VERSION";
    
    /* 4.8.1 Descriptive Component Properties (.1 - .12) */
    final static public  String	strProp_Attach		= "ATTACH";
    final static public  String	strProp_Categories	= "CATEGORIES";
    final static public  String	strProp_Class		= "CLASS";
    final static public  String	strProp_Comment		= "COMMENT";
    final static public  String	strProp_Description	= "DESCRIPTION";
    final static public  String	strProp_Geo		= "GEO";
    final static public  String	strProp_Location	= "LOCATION";
    final static public  String	strProp_PercentComplete	= "PERCENT-COMPLETE";
    final static public  String	strProp_Priority	= "PRIORITY";
    final static public  String	strProp_Resources	= "RESOURCES";
    final static public  String	strProp_Status		= "STATUS";
    final static public  String	strProp_Summary		= "SUMMARY";
    /* 4.8.2 Date and Time Component Properties (.1 - .7) */
    final static public  String	strProp_Completed	= "COMPLETED";
    final static public  String	strProp_DTEnd		= "DTEND";
    final static public  String	strProp_Due		= "DUE";
    final static public  String	strProp_DTStart		= "DTSTART";
    final static public  String	strProp_Duration	= "DURATION";
    final static public  String	strProp_Freebusy	= "FREEBUSY";
    final static public  String	strProp_Transp		= "TRANSP";
    /* 4.8.3 Time Zone Component Properties (.1 - .5) */
    final static public  String	strProp_TZID		= "TZID";
    final static public  String	strProp_TZName		= "TZNAME";
    final static public  String	strProp_TZOffsetFrom	= "TZOFFSETFROM";
    final static public  String	strProp_TZOffsetTo	= "TZOFFSETTO";
    final static public  String	strProp_TZUrl		= "TZURL";
    /* 4.8.4 Relationship Component Properties (.1 - .7) */
    final static public  String	strProp_Attendee	= "ATTENDEE";
    final static public  String	strProp_Contact		= "CONTACT";
    final static public  String	strProp_Organizer	= "ORGANIZER";
    final static public  String	strProp_RecurrenceId	= "RECURRENCE-ID";
    final static public  String	strProp_RelatedTo	= "RELATED-TO";
    final static public  String	strProp_URL		= "URL";
    final static public  String	strProp_UID		= "UID";
    /* 4.8.5 Recurrence Component Properties (.1 - .4) */
    final static public  String	strProp_ExDate		= "EXDATE";
    final static public  String	strProp_ExRule		= "EXRULE";
    final static public  String	strProp_RDate		= "RDATE";
    final static public  String	strProp_RRule		= "RRULE";
    /* 4.8.6 Alarm Component Properties (.1 - .3) */
    final static public  String	strProp_Action		= "ACTION";
    final static public  String	strProp_Repeat		= "REPEAT";
    final static public  String	strProp_Trigger		= "TRIGGER";
    /* 4.8.7 Change Management Component Properties (.1 - .4) */
    final static public  String	strProp_Created		= "CREATED";
    final static public  String	strProp_DTStamp		= "DTSTAMP";
    final static public  String	strProp_LastModified	= "LAST-MODIFIED";
    final static public  String	strProp_Sequence	= "SEQUENCE";
    /* 4.8.8 Miscellaneous Component Properties (.1 - .2) */
    /* x-prop */
    final static public  String	strProp_RequestStatus	= "REQUEST-STATUS";
    
    /* 4.8.1.3 Classification */
    final static public  String	strValue_Public		= "PUBLIC";
    final static public  String	strValue_Private	= "PRIVATE";
    final static public  String	strValue_Confidential	= "CONFIDENTIAL";
    /* iana-token */
    
}


 