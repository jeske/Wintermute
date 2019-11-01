/*
 * MailboxFolder.java
 *
 * Created on November 26, 2002, 3:47 PM
 */

package simpleimap;

import javax.mail.*;
import java.util.*;
import javax.mail.internet.*;
import java.io.*;
import java.security.*;

/**
 *
 * @author  hassan
 */
public class MailboxFolder {
    File path;
    
    /** Creates a new instance of MailboxFolder */
    public MailboxFolder(String filename) {
        path = new File(filename);
    }
    
    long getSize() {
       return path.length();
    }
    
    String getMD5() {
        int buflen = 8196;
        byte[] buf = new byte[buflen];
        
        try {
            MessageDigest hash = MessageDigest.getInstance("md5");
            FileInputStream fr = new FileInputStream(path);
            int n;
            while((n = fr.read(buf)) != -1) {
                hash.update(buf, 0, n);
            }
            byte[] digest = hash.digest();
            String folderDigest = new String(digest);
            return folderDigest;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("md5 error");
        }
    }
    

    
    class MailboxIterator implements Iterator {
         MailboxFolder folder;
         BufferedReader in;
         boolean isMore;
         String lastline;
         
        public MailboxIterator(MailboxFolder folder) {
          this.folder = folder;  
          this.in = null;
          this.isMore = true;
          this.lastline = null;
        }

        private void open() {
            FileReader fr;
        
            try {
                fr = new FileReader(folder.path);
                this.in = new BufferedReader(fr);
            } catch (Exception e) {
                 throw new RuntimeException("file open failed!");

            }
            this.isMore = true;
        }
        
        public boolean hasNext() {
            return this.isMore;
        }
        
        public Object next() {
            MailboxMessage msg = new MailboxMessage();
            String str;

            if(this.isMore == false) 
                throw new RuntimeException("no more left");
            
            if(lastline != null) {
                msg.strbuf.append(lastline + "\n");
            }
            
            if(this.in == null) {
                this.open();
            }
            
            try {     
                while ((str = in.readLine()) != null)
                {
                    if ((str.startsWith("From ") == false) || (str.length() == 0)) {
                       msg.strbuf.append(str + "\n");
                    } else {
                        if (msg.strbuf.length() > 0) {
                           lastline = str;
                           return msg;
                        }
                    }
                }
                // save last message, because while loop aborted before being able to save message
                if (msg.strbuf.length() > 0) {
                    this.isMore = false;
                    in.close();
                    return msg;
                }
                in.close();   
            } catch (Exception e) {
                e.printStackTrace();

                 throw new RuntimeException("mail read failed!");
            }            
            return null;
        }
        

        public void remove() {
            throw new UnsupportedOperationException();
      
        }
        
    }
    
    public Iterator forEach() {
        return new MailboxIterator(this);     
    }
    
}
