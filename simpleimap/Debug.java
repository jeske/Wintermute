/*
 * Debug.java
 *
 * Created on January 11, 2003, 12:36 PM
 */

package simpleimap;
import java.util.logging.*;

/**
 *
 * @author  hassan
 *
 * David Jeske (2003-28) rewritten to use Java Logging
 *             http://java.sun.com/j2se/1.4.1/docs/guide/util/logging/
 */
public class Debug extends java.util.logging.Logger {
    final static boolean debugEnabled = true;
    private static Debug sharedDebugger  = new Debug();
    
    /** Creates a new instance of Debug */
    public Debug() {
        super("wm.debugging",null);
        if (debugEnabled) {
            this.addHandler(new MyHandler());
        }
    }
    
    private class MyHandler extends Handler {
         private DebugFrame debugFrame = null;
         MyHandler() {
             debugFrame = new DebugFrame(); 
             debugFrame.show();
         }

         /** Close the <tt>Handler</tt> and free all associated resources.
          * <p>
          * The close method will perform a <tt>flush</tt> and then close the
          * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
          * should no longer be used.  Method calls may either be silently
          * ignored or may throw runtime exceptions.
          *
          * @exception  SecurityException  if a security manager exists and if
          *             the caller does not have <tt>LoggingPermission("control")</tt>.
          *
          */
         public void close() throws SecurityException {
         }
         
         /** Flush any buffered output.
          *
          */
         public void flush() {
         }
         
         /** Publish a <tt>LogRecord</tt>.
          * <p>
          * The logging request was made initially to a <tt>Logger</tt> object,
          * which initialized the <tt>LogRecord</tt> and forwarded it here.
          * <p>
          * The <tt>Handler</tt>  is responsible for formatting the message, when and
          * if necessary.  The formatting should include localization.
          *
          * @param  record  description of the log event
          *
          */
         public void publish(LogRecord record) {
             debugFrame.debug(0, record.getMessage());
         }
         
    }
     public static void debug(String line) {
        Debug.sharedDebugger.log(Level.INFO,line);
     }
     
     public static void debug(int level, String line) {
         if(level <= 5) {
             sharedDebugger.log(Level.INFO,line);
         }
     }
     
     public static void debug(Object obj) {
         if(obj == null) {
             obj = "null";         
         }
         sharedDebugger.log(Level.INFO, obj.toString());
     }
     
     public static void debug(Object obj1, Object obj2) {
         if(obj1 == null) obj1 = "null";
         if(obj2 == null) obj2 = "null";
         Debug.debug(0,  obj1.toString() + " " + obj2.toString());
     }
     public static void debug(Object obj1, Object obj2, Object obj3) {
         if(obj1 == null) obj1 = "null";
         if(obj2 == null) obj2 = "null";
         if(obj3 == null) obj3 = "null";
         Debug.debug(0, obj1.toString() + " " + obj2.toString() + " " + obj3.toString());
     }
     public static void debug(Object obj1, Object obj2, Object obj3, Object obj4) {
         if(obj1 == null) obj1 = "null";
         if(obj2 == null) obj2 = "null";
         if(obj3 == null) obj3 = "null";
         if(obj4 == null) obj4 = "null";
         Debug.debug(0, obj1.toString() + " " + obj2.toString() + " " + obj3.toString() + " " + obj4.toString());
     }
     public static void debug(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5) {
         if(obj1 == null) obj1 = "null";
         if(obj2 == null) obj2 = "null";
         if(obj3 == null) obj3 = "null";
         if(obj4 == null) obj4 = "null";
         if(obj5 == null) obj5 = "null";
         Debug.debug(0, obj1.toString() + " " + obj2.toString() + " " + obj3.toString() + " " + obj4.toString() + " "  + obj5.toString());
     }     
     public static void debug(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
         if(obj1 == null) obj1 = "null";
         if(obj2 == null) obj2 = "null";
         if(obj3 == null) obj3 = "null";
         if(obj4 == null) obj4 = "null";
         if(obj5 == null) obj5 = "null";
         if(obj6 == null) obj6 = "null";
         Debug.debug(0, obj1.toString() + " " + obj2.toString() + " " + obj3.toString() + " " + obj4.toString() + " "  + obj5.toString() + " "  + obj6.toString());
     }        

     public static void debug(Throwable e) {         
         StackTraceElement[] stack = e.getStackTrace();

         Debug.debug("");         
         Debug.debug("----------------------------------------------------------");
         Debug.debug("");
         
         Debug.debug(e.toString());
         for(int i=0;i<stack.length; i++) {
             Debug.debug("  " + stack[i].toString());
         }
         Debug.debug("");         
         Debug.debug("----------------------------------------------------------");
         Debug.debug("");
         
         e.printStackTrace();
     }
     
     public static void start() {
         // no longer necessary!
     }

}
