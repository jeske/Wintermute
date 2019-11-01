/*
 * WinterMute.java
 *
 * Created on November 16, 2002, 4:22 PM
 */

package simpleimap;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.CompoundSkin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import java.security.SecureRandom;

import java.net.*;
import java.util.*;
import java.io.*;

import java.text.*;

import javax.swing.*;

import java.security.*;


/**
 *
 * @author  hassan
 */
public class WinterMute {
     public static SimpleDB my_db;
     
     public static ItemRelation parentChildRelation;
     public static ItemRelation childParentRelation;
     public static ItemRelation containsContainerRelation;
     public static ItemRelation containerContainsRelation;
     
     public static ItemRelation menuActionRelation;
     
     public static ItemRelation contactEmailAddressRelation;
     public static ItemRelation emailAddressContactRelation;
     
     public static ItemRelation emailAddressMessageRelation;
     public static ItemRelation messageEmailAddressRelation;
     
     public static ItemRelation viewedItemViewerRelation;
     public static ItemRelation setViewedItemSenderRelation;
     public static ItemRelation menuFrameRelation;
     public static ItemRelation actionMenuRelation;
     public static ItemRelation fieldColconfigRelation;
     public static ItemRelation colConfigFieldRelation;

     public static ItemRelation columnsetTableRelation;
     public static ItemRelation tableColumnsetRelation;
     public static ItemRelation columnsetColconfigRelation;
     public static ItemRelation colconfigFormatterRelation;
     
     
     
     public static  String ApplicationImagePath =  new File("application").getAbsolutePath();
     public static final String SystemImagePathKey = "system.image.path.key";
     private static final String SystemImagePath = new File("system").getAbsolutePath();
     
     protected static Calendar mainCalendar;
     protected static Locale theLocale;
     
     public static RBTextIndex indexmgr; 
     
     
     public static Calendar now() {
         Calendar cal = mainCalendar.getInstance(theLocale);
         Date now = new Date();
         cal.setTime(now);
         return cal;
     }
     
     
     public static void setSystemImagePath(String path) {
         System.setProperty(SystemImagePathKey, path);
     }

     public static void exitApplication() {
        // we should:
         
        // 1) start shutdown popup
        // 2) close all frames
        // 3) shutdown all threads
        // 4) do a final save to the db
        // 5) exit app
         
        if(WinterMute.indexmgr == null) return;
         
        WinterMute.indexmgr.finalize();// be sure to close the write index!
       
        if (WinterMute.my_db != null) {
            WinterMute.my_db.finalize();
            WinterMute.my_db = null;
        }
        
        System.out.println("exiting...");
        System.exit(0);
     }
     
     public static void setupCalendar() {
         theLocale = Locale.getDefault();
         mainCalendar = Calendar.getInstance(theLocale);
     }
     
     //////////////////////////////////
     //
     // initSSLRandomKey()
     //
     // This is necessary because Java's random key generator is REALLY
     // slow and nearly makes the app hang.
     //
     // http://forum.java.sun.com/thread.jsp?thread=4250&forum=2&message=11205
     //

     public static void initSSLRandomKey() {
         Provider prov = new WMSecureProvider();
         prov.setProperty("SecureRandom.efficient", "simpleimap.WMEfficientRandom");
         Security.insertProviderAt(prov, 1);
     }
     
     public static class SRRandomStartThread extends Thread {
        SRRandomStartThread() {
             this.setPriority(5);
             this.setDaemon(true);
             
            String name = this.getClass().getName();
            if(name != null) this.setName(name);
             
        }
        public void run() {
           Debug.debug("secure random start...");
           byte[] keybuffer = new byte[8];
           SecureRandom random = new SecureRandom();
           random.setSeed(new Date().getTime());
           random.nextBytes(keybuffer);
           Debug.debug("secure random done.");
        }
     }
     
     //////////////////////////////////////////////////////////////////////////
     //                          M A I N                                     //
     //////////////////////////////////////////////////////////////////////////
     
     public static void main(String args[]) throws Exception {
        // http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
        try {
            UIManager.setLookAndFeel(
               UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

       initSSLRandomKey();
        
       setupCalendar();
       
         
       // ---- PRE STARTUP --------------
       Debug.start(); // you want to be below this!
       // -- STARTUP ---------------------

       
       // print out UI defaults...
       if (false) {
          UIDefaults d = UIManager.getDefaults();  
          for (Enumeration e = d.keys();e.hasMoreElements();) {
              Object key = e.nextElement();
              Object value = d.get(key);
              Debug.debug("UIDefault: " + key + " = " + value);
              
          }
       }
       
       
       
       // new SRRandomStartThread().start();


       indexmgr = new RBTextIndex();
         
       if (false) {  
           // turn on skinned theme...
           try {
               Skin skin = SkinLookAndFeel.loadThemePack(ClassLoader.getSystemResource("skins/themepack.zip"));
               SkinLookAndFeel.setSkin(skin);
               javax.swing.UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
         
       
       RelationshipBuilder.init();
       Emoticons.init();
       
       ClassLoader cl = ClassLoader.getSystemClassLoader();
       //cl.getSystemResource();
       URL url = ClassLoader.getSystemResource("images");
       if (url != null) {
            ApplicationImagePath = url.getFile();
            if(ApplicationImagePath.charAt(0) == '/') {
                ApplicationImagePath = ApplicationImagePath.substring(1);
            }
       } else {
           ApplicationImagePath = "images/";
       }
       
       Debug.debug("ApplicationImagePath:", ApplicationImagePath);
       Debug.debug("SystemImagePath:", SystemImagePath);
       setSystemImagePath(SystemImagePath);
       
       
       my_db = new SimpleDB(WMConfig.dbPath);
       
       SimpleRegistry.init(my_db);

       parentChildRelation = new ItemRelation("parent", "child");
       childParentRelation = parentChildRelation.invert();

       containsContainerRelation = new ItemRelation("contains", "container");
       containerContainsRelation = containsContainerRelation.invert();
       
       menuActionRelation = new ItemRelation("menu", "action");
       
       contactEmailAddressRelation = new ItemRelation("contact", "emailAddress");
       emailAddressContactRelation = contactEmailAddressRelation.invert();
       
       messageEmailAddressRelation = new ItemRelation("message", "emailAddress");
       emailAddressMessageRelation = messageEmailAddressRelation.invert();
    
       viewedItemViewerRelation = new ItemRelation("vieweditem","viewer"); 
       setViewedItemSenderRelation = new ItemRelation("action.setViewedItem","sender");
       menuFrameRelation = new ItemRelation("menu","frame");
       actionMenuRelation = new ItemRelation("action","menu");
       
       fieldColconfigRelation  = new ItemRelation("field","colconfig");
       colConfigFieldRelation  = fieldColconfigRelation.invert();
       
       columnsetTableRelation = new ItemRelation("columnset","table");
       tableColumnsetRelation = columnsetTableRelation.invert();
       
       columnsetColconfigRelation = new ItemRelation("columnset","colconfig");
       colconfigFormatterRelation = new ItemRelation("colconfig","formatter");
       
       SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               Thread awtthread = Thread.currentThread();
               awtthread.setPriority(Thread.MAX_PRIORITY);
               Debug.debug("AWTthread priority: " + awtthread.getPriority() + " max: " + awtthread.MAX_PRIORITY);
           }
       });
       
       SwingUtilities.invokeLater(new Runnable() {
           public void run() {
                my_db.load();
           }
       });
       
       indexmgr.start();
     }
     
     public static void launchURL(String url) {
       // this launches an external process....
       // http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
       // http://forum.java.sun.com/thread.jsp?thread=284358&forum=38&message=1115943

       String protocol = "http";
       
       try {
           URL ourl = new URL(url);
           protocol = ourl.getProtocol();
           Debug.debug("protocol: " + protocol);
        } catch (Throwable t) {
            Debug.debug(t);
        }    

       if(protocol.matches("mailto")) {
           String email;
           email = url.replaceFirst("mailto:", "");
           new ComposeMessageFrame(email).show();
           
           return;
       }
         
       String osName = System.getProperty("os.name" );
	if( osName.matches("Windows.*") ) {
		Process proc = null;
		try {
                        // http://www.ericphelps.com/batch/userin/
                        url = url.replaceAll("&","^&"); // windows escaping of "&" character
                    
                	String[] cmd = new String[]{"cmd.exe","/C","start",url};
			Runtime rt = Runtime.getRuntime();
			Debug.debug("Executing " + cmd[0] + " " + cmd[1] + " " + cmd[2] + " " +cmd[3]);
			proc = rt.exec(cmd);
			// any error message?
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
			errorGobbler.start();
			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
			outputGobbler.start();

			// any error?
			int exitVal = proc.waitFor();
			Debug.debug("ExitValue: " + exitVal);
		} catch (Throwable t) {
                    Debug.debug(t);
		}

	} else{
            throw new RuntimeException("don't know how to open documents when not on Windows");
	}
     }
     
        
        private static class StreamGobbler extends Thread    {
            InputStream is;
            String type;

            StreamGobbler(InputStream is, String type)
            {
                this.is = is;
                this.type = type;
                this.setName("StreamGobbler");
            }

            public void run()
            {
                try
                {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line=null;
                    while ( (line = br.readLine()) != null)
                          System.out.println(type + ">" + line);
                } catch (IOException ioe) {
                    Debug.debug(ioe);
                    //ioe.printStackTrace();
                }
            }
        }
        
     
}
