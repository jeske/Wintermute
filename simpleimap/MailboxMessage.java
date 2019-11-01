/*
 * MailboxMessage.java
 *
 * Created on November 26, 2002, 6:35 PM
 */

package simpleimap;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.security.*;

/**
 *
 * @author  hassan
 */

public class MailboxMessage {
    StringBuffer strbuf;
    String msg;

    public MailboxMessage() {
        strbuf = new StringBuffer();
        msg = null;
    }

    public int getSize() {
        return strbuf.length();
    }

    public String getMD5() {
        this.getData();

        try {
            MessageDigest hash = MessageDigest.getInstance("md5");
            hash.update(msg.getBytes());
            byte[] digest = hash.digest();
            String msgDigest = new String(digest);
           
            return msgDigest;

        } catch (Exception e) {
            throw new RuntimeException("error while md5");
        }            
    }
    
    public String getBodyMD5() {
        this.getData();

        try {
            MessageDigest hash = MessageDigest.getInstance("md5");
            
            int i = msg.indexOf("\n\n");
            String body = msg.substring(i);
            
            hash.update(body.getBytes());
            byte[] digest = hash.digest();
            String msgDigest = new String(digest);
           
            return msgDigest;

        } catch (Exception e) {
            throw new RuntimeException("error while md5");
        }     
    }
    
    public String getData() {
        if(msg == null) msg = strbuf.toString();
        return msg;
    }
    
    public MimeMessage getMessage() {
        MimeMessage msgobj = null;
        this.getData();
        
        byte[] messageBytes = msg.getBytes();
        
        InputStream is = new ByteArrayInputStream(messageBytes);

        Session session = Session.getInstance(System.getProperties());
        try {
            msgobj = new MimeMessage(session,is);
            return msgobj;
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("MimeDecode failed!");
        }
    }

}