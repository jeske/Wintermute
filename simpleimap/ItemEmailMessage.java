/*
 * ItemEmailMessage.java
 *
 * Created on January 17, 2003, 10:00 AM
 */

package simpleimap;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.lang.ref.*;
import java.io.*;

/**
 *
 * @author  hassan
 */
public abstract class ItemEmailMessage extends DefaultItem {
    
    /** Creates a new instance of ItemEmailMessage */
    public ItemEmailMessage() {
                super();

    }

    public void buildEmailAddressRelationships(Address[] list) {
        if(list != null) {
            for(int i=0; i<list.length; i++) {
                Address fromWhom = list[i];
                if(fromWhom instanceof InternetAddress) {
                    InternetAddress ia = (InternetAddress) fromWhom;
                    RelationshipBuilder.relateEmailAddress(ia.getAddress(), ia.getPersonal(), this);
                } else {
                    RelationshipBuilder.relateEmailAddress(fromWhom.toString(), null, this);
                }
            }
        }
    }
    
    public void handleMessageHeaders(javax.mail.Message m) {
        DefaultItem item = this;

        
        // FROM ADDRESS
        
        Address[] from_list = null;
        try {
            from_list = m.getFrom();
        } catch (Exception e) {
            Debug.debug(e);
            from_list = null;
        }
        
        String fromWhom = "no one";
        if(from_list != null) {
            this.buildEmailAddressRelationships(from_list);            
            fromWhom = from_list[0].toString();
            item.put("From",fromWhom);               
        }
        
        try {
            Address[] toWhoms = m.getAllRecipients();
            this.buildEmailAddressRelationships(toWhoms);
        } catch (MessagingException e) {
        }        

        // SUBJECT
        
        String subject = "no subject";
        try {
            subject = m.getSubject();
            if(subject != null) {
                item.put("Subject",subject);
                item.put("Summary",subject);
            }
        } catch (MessagingException e) {
        }
     
        Debug.debug("Message:" + fromWhom + " " + subject);            
        
       
        
        
        // TO and CC list
        
        try {
            String[] toWhomList = m.getHeader("To");
            if(toWhomList != null && toWhomList.length > 0) {
                StringBuffer b = new StringBuffer();
                for(int i=0; i<toWhomList.length; i++) {
                    if(i!=0) b.append(", ");
                    b.append(toWhomList[i]);
                }
                item.put("To",b.toString());
            }
        } catch (MessagingException e) {
        }        
        try {
            String[] ccWhomList = m.getHeader("Cc");
            if(ccWhomList != null && ccWhomList.length > 0) {
                StringBuffer b = new StringBuffer();
                for(int i=0; i<ccWhomList.length; i++) {
                    if(i!=0) b.append(", ");
                    b.append(ccWhomList[i]);
                }
                item.put("Cc", b.toString());
            }
        } catch (MessagingException e) {
        }               
        
        // DATE
        
        try {
            Date date = m.getSentDate();
            if(date == null) date = m.getReceivedDate();
            item.put("Date",date);
        } catch (MessagingException e) {
        }
        
        // CONTENT-TYPE
        
        try {
            String[] hdrs2 = { "Content-Type" };
            Enumeration e2 = m.getMatchingHeaders(hdrs2);
            if (e2.hasMoreElements()) {
                Header mid_hdr = (Header)e2.nextElement();
                item.put("Content-Type","" + mid_hdr.getValue());
            }
        } catch (MessagingException e) {
        }
    }
    
    public void buildRelationships_old() {
       RelationshipBuilder.rbuilder.buildRelationships(this);

       if(this instanceof ItemUnixMailboxMessage) {
           try {
               MimeMessage message = this.getMimeMessage();
               
               String ct_str="text/plain";
               ct_str = message.getContentType();
               if(ct_str == null) ct_str = "text/plain";
               
               ct_str = ct_str.toLowerCase();
               
               ContentType ct = new ContentType(ct_str);
               
               Date then = message.getSentDate();
               if(then == null) then = message.getReceivedDate();
               
               if(ct.getPrimaryType().equals("text")) {
                   String content = (String) message.getContent();
                   RelationshipBuilder.rbuilder.buildRelationships(then, this, content);
               }
           } catch (Exception e) {
               Debug.debug(e);
           }
       }
    }
    

    private SoftReference my_MimeMessage;
    
    public MimeMessage getMimeMessage() {
        // cache a mime parse of the message
        MimeMessage msgobj = null;
        
        if (my_MimeMessage != null) {
            msgobj = (MimeMessage)my_MimeMessage.get();
            
        } 
        if (msgobj == null) {
            byte[] data = this.getData();
            if (data == null) {
                return null;
            }
            InputStream is = new ByteArrayInputStream(data);

            
            Session session = Session.getInstance(System.getProperties());
            try {
                msgobj = new MimeMessage(session,is);
            } catch (MessagingException e) {
                throw new RuntimeException("MimeDecode failed!");
            }
            my_MimeMessage = new SoftReference(msgobj);
        }
        return msgobj;
    }
    
    
    public String getStatus() {
        String status = this.get("status");
        if(status != null && !status.equals("")) return status;
        
        long readt = this.getReadDate();
        if(readt == 0) {
            status = "New";
        } else {
            Date readDate = new Date();
            readDate.setTime(readt);
            Calendar now = WinterMute.now();
            now.add(Calendar.DATE, -1);
            //now.add(Calendar.MINUTE, -1);

            Date yesterday = now.getTime();
            
            if(readDate.after(yesterday)) status = "Read";
            else status = "Seen";
        }
        
        return status;
    }
    
    public long getReadDate() {
        long readDate = this.getLong("readDate", 0);
        return readDate;
    }
    
    public void setMessageRead() {
        long readDate = this.getReadDate();
        if(readDate == 0) {
            Date nowDate = new Date();
            long now = nowDate.getTime();
            this.put("readDate", now);
        }
    }
    
    public void setMessageSeen() {
        this.put("status", "Seen");
    }
    
    public void setMessageDeleted() {
        this.put("status", "Deleted");
    }
    public void unDeleteMessage() {
        this.put("status", "Read");
    }    
    public void setMessageAnswered() {
        this.put("answered", "1");
    }

}
