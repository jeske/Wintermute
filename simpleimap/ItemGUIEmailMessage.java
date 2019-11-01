/*
 * Wintermute - Personal Data Organizer
 * 
 * Copyright (C) 2002, by David Jeske
 *
 * Written by David Jeske <jeske@neotonic.com>. 
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * ItemGUIEmailMessage.java
 *
 * Created on November 2, 2002, 6:52 PM
 */

package simpleimap;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.Dimension;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.regex.*;

// import guicomp.HtmlUtilities;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;

/**
 *
 * @author  David Jeske
 */
public class ItemGUIEmailMessage extends ItemGUIBase implements ItemGUIInterface, IPropChangeNotification {
    public static final String TypeID = "gui.ItemGUIEmailMessage";
    private JComponent topLevel;
    
    private JPanel aPanel;
    private JScrollPane my_scroller;
    //private JEditorPane emailContentView;
    private JTextPane emailContentView;

    private JTextPane emailHeaderView;
    
    private java.awt.Color window_background;

    /** Creates a new instance of ItemTemplate */
    public ItemGUIEmailMessage() {
        super();
        //emailContentView = new JEditorPane();
        aPanel = new JPanel();
        aPanel.setBorder(BorderFactory.createEmptyBorder());
        this.topLevel = aPanel;
        
        aPanel.setLayout(new java.awt.BorderLayout());
        //aPanel.setLayout(new javax.swing.BoxLayout(aPanel, javax.swing.BoxLayout.Y_AXIS));

        // for a list of colors see here:
        // http://java.sun.com/products/jfc/tsc/articles/lookandfeel_reference/#system_colors
        UIDefaults d = UIManager.getDefaults();
        window_background = (java.awt.Color)d.get("menu");
        
        this.emailHeaderView = new JTextPane();
        this.emailHeaderView.setContentType("text/html");
        this.emailHeaderView.setEditable(false);
        this.emailHeaderView.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        this.emailHeaderView.setMinimumSize(new Dimension(440, 50));
        this.emailHeaderView.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
        this.emailHeaderView.setText(" test ");
        // this.emailHeaderView.setPreferredSize(new Dimension(440,50));
        this.emailHeaderView.invalidate();

        this.emailContentView = new JTextPane();
        this.emailContentView.setEditable(false);
        this.emailContentView.setContentType("text/html");   
        this.emailContentView.setMinimumSize(new Dimension(440, 300));
        
        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        this.emailContentView.setEditorKit(kit);
        
        // to prevent line wrapping, make the right margin extend 20 inches beyond the vertical scrollbar
        SimpleAttributeSet attrs = new SimpleAttributeSet(emailContentView.getParagraphAttributes());
        StyleConstants.setRightIndent(attrs, 72 * 1);	
        emailContentView.setParagraphAttributes(attrs, true);
        

        this.emailHeaderView.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Debug.debug("URL clicked:" + event.getURL());
                    WinterMute.launchURL(event.getURL().toString());
                }
              }
        });        
        
        emailContentView.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Debug.debug("URL clicked:" + event.getURL());
                    WinterMute.launchURL(event.getURL().toString());
                }
              }
        });
      
        
        KeyListener kl = new MyKeyListener(this);
        emailContentView.addKeyListener(kl);
        
        my_scroller = new JScrollPane();
        my_scroller.setName("ItemGUIEmailMessage.Scroller");
        my_scroller.getViewport().setName("ItemGUIEmailMessage.Scroller.Viewport");
        my_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        my_scroller.setViewportView(emailContentView);
        my_scroller.setBorder(BorderFactory.createEmptyBorder());

        
        JScrollPane my_scroller2 = new JScrollPane();
       
        my_scroller2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        my_scroller2.setViewportView(emailHeaderView);        
        my_scroller2.setBorder(BorderFactory.createEtchedBorder());
        
        // emailContentView.
        aPanel.add(my_scroller2, java.awt.BorderLayout.NORTH);
        aPanel.add(my_scroller, java.awt.BorderLayout.CENTER);
        //aPanel.add(emailContentView, java.awt.BorderLayout.CENTER);
        
        
        if(false) {
            my_scroller.addComponentListener(new ComponentListener() {
                public void componentMoved(ComponentEvent e) {
                }
                public void componentResized(ComponentEvent e) {
                    Rectangle r1 = emailContentView.getBounds();
                    Rectangle r2 = emailHeaderView.getBounds();
                    Rectangle r = my_scroller.getBounds();
                    //put("Dimension", "" + r.width + "," + r.height);
                    Debug.debug(3, "** resize for '" + item_storage.name + "' : (" + r.width + "," + r.height + ")");
                    
                    int height = Math.max(r.height, r1.height+r2.height);
                    //emailContentView.
                    aPanel.setMinimumSize(new Dimension(r.width-20, r.height));
                    aPanel.setPreferredSize(new Dimension(r.width-20, height));
                    aPanel.setMaximumSize(new Dimension(r.width-20, height));
                }
                public void componentShown(ComponentEvent e) {
                }
                public void componentHidden(ComponentEvent e) {
                }
                
            });
        }
        
    }

    class  ViewOriginalItemAction {
        DefaultItem item;
        ViewOriginalItemAction(DefaultItem item) {
            //super("Inspect Item");
            this.item = item;
        }
        
        public void actionPerformed() {
            ItemGUIFrame frm = (ItemGUIFrame) WinterMute.my_db.newItem(null, ItemGUIFrame.TypeID, "Email View");
            frm.setTransient(true);
            
            ItemGUITextView itemview1 = (ItemGUITextView) WinterMute.my_db.newItem(null, ItemGUITextView.TypeID, "textview");
            itemview1.put("relation_to_view","SELF");
            frm.addChild(itemview1);            

            itemview1.setViewedItem(this.item);
        }
    }        
    
    class InspectItemAction  {
        DefaultItem item;
        InspectItemAction(DefaultItem item) {
            //super("Inspect Item");
            this.item = item;
        }
        
        public void actionPerformed() {
            ItemGUIFrame frm = (ItemGUIFrame) WinterMute.my_db.newItem(null, ItemGUIFrame.TypeID, "Item Inspector");
            frm.setTransient(true);
            
            DefaultItem split = (DefaultItem) WinterMute.my_db.newItem(null, ItemGUISplitPane.TypeID, "split");
            frm.addChild(split);
            split.put("vsplit","yes");
            //itemvtop.put("resizeWeight","0.30");
            split.put("dividerLocation", 100);
            
            ItemGUITable itemview1 = (ItemGUITable) WinterMute.my_db.newItem(null, ItemGUITable.TypeID, "item_view");
            itemview1.put("relation_to_view","SELF");
            split.addChild(itemview1);
            
            ItemGUITable itemview2 = (ItemGUITable) WinterMute.my_db.newItem(null, ItemGUITable.TypeID, "item_view");
            itemview2.put("relation_to_view",WinterMute.containerContainsRelation.getSpec());
            split.addChild(itemview2);
            
            itemview1.setViewedItem(this.item);
            itemview2.setViewedItem(this.item);

        }
    }     
    
    private class MyKeyListener implements KeyListener {
        ItemGUIEmailMessage item;
        public MyKeyListener(ItemGUIEmailMessage item) {
            this.item = item;
        }
        public void keyTyped(KeyEvent event) {
            Debug.debug("key", "" + event.getKeyChar());
            DefaultItem viewedItem = this.item.getViewedItem();
            
            char key = event.getKeyChar();
            
            ItemEmailMessage message = null;
            if(viewedItem instanceof ItemEmailMessage) {
                message = (ItemEmailMessage) viewedItem;
            }
            
            if(message != null) {
                if (key == 'd') {
                    message.setMessageDeleted();
                } else if (key == 'u') {
                    message.unDeleteMessage();
                } else if (key == 'i') {
                    InspectItemAction iia = new InspectItemAction(message);
                    iia.actionPerformed();
                } else if (key == 'o') {
                    ViewOriginalItemAction iia = new ViewOriginalItemAction(message);
                    iia.actionPerformed();
                }
            }
        }
        public void keyPressed(KeyEvent event) {
            
        }
        public void keyReleased(KeyEvent event) {
            
        }
    };
    
    public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemGUIEmailMessage();
            }
        });
    }
    
    public void clearViewedItem() {
        super.clearViewedItem();
        this.emptyViews();
    }
    
    public java.awt.Component getComponent() {
        return this.topLevel;
    }
    
    private static String striphtml(String text) {
        // this java html part does not ignore unknown tags very well...
        //text = text.replaceFirst("(?i)<!DOCTYPE[^>]*>[\r\n]*", "");
        //text = text.replaceAll("(?i)<META[^>]*>",""); 
        //text = text.replaceAll("(?i)<DIV[^>]*>",""); 
        //text = text.replaceAll("<?i></?HTML>", "");
        
        MultiPattern mp = new MultiPattern();
        mp.setPrepend("(?i)");
        mp.addPattern("(?i)<!DOCTYPE[^>]*>[\r\n]*", "");
        mp.addPattern("<META[^>]*>",""); 
        mp.addPattern("<DIV[^>]*>",""); 
        mp.addPattern("</?HTML>", "");
        mp.addPattern("/>", ">");
        text = mp.replaceAll(text);
                
        //text = text.replaceAll("(?i)(<!DOCTYPE[^>]*>[\r\n]*|<META[^>]*>|<DIV[^>]*>|</?HTML>)", "");
        //text = text.replaceAll("/>", ">");
        
        return text;
    }
    


    
    private static String text2html(String text) {
        if(text == null) return null;
       
        MultiPattern mp = new MultiPattern();
        mp.addPattern(">", "&gt;");
        mp.addPattern("<", "&lt;");
        text = mp.replaceAll(text);


        Pattern p = Pattern.compile("([A-Za-z][-0-9A-Za-z_=\\.]*@[0-9A-Za-z_][-0-9A-Za-z_\\.]*)");
        Matcher m = p.matcher(text);
        text = m.replaceAll("<a href=\"mailto:$1\">$1</a>");

        Pattern p2 = Pattern.compile("(?i)((http|ftp|https|ical)://[^\\s>\\]]+)");
        Matcher m2 = p2.matcher(text);
        text = m2.replaceAll("<a href=\"$1\">$1</a>");

        text = Emoticons.obj.findReplaceEmoticons(text);

        text = text.replaceAll("\n", "<br>\n");

        //text = "<body>\n" + text + "\n</body>";
        return text;
    }
    
    protected String buildEmailHeaderView(DefaultItem item) {
        StringBuffer buf = new StringBuffer();
        
        String subject = item.get("Subject");
        String fromWhom = this.text2html(item.get("From"));
        String toWhom = this.text2html(item.get("To"));
        String ccWhom = this.text2html(item.get("Cc"));

        String date = item.get("Date");
        // String colorStr = HtmlUtilities.getHtmlColorCode(window_background);
        String colorStr = "#CCFFFF";
        buf.append("<html><body bgcolor='" + colorStr + "'>");
        buf.append("<table cellspacing=1 cellpadding=1>\n");
        if(fromWhom != null) buf.append("<tr><td align=right nowrap valign=top><b>From:</b></td><td>" + fromWhom + "</td></tr>\n");
        if(toWhom != null)   buf.append("<tr><td align=right nowrap valign=top><b>To:</b></td><td>" + toWhom + "</td></tr>\n");
        if(ccWhom != null)   buf.append("<tr><td align=right nowrap valign=top><b>Cc:</b></td><td>" + ccWhom + "</td></tr>\n");
        if(subject!=null)    buf.append("<tr><td align=right nowrap valign=top><b>Subject:</b></td><td>" + subject + "</td></tr>\n");
        buf.append("<tr><td align=right nowrap valign=top><b>Date:</b></td><td>" + date + "</td></tr>\n");
        buf.append("</table>\n");
        buf.append("</body></html>");
        
        return buf.toString();
    }
    
    class HTMLMessageConverter {
        Part msg;
        StringBuffer html;
        LinkedList attachments;
        
        public HTMLMessageConverter(Part msg) {
            this.msg = msg;
            this.html = new StringBuffer();
            this.attachments = new LinkedList();
            
            this.convertMessageToHTML(this.msg, this.html);
        }
        
        public String toString() {
            return html.toString();
        }
        
        private void convertMessageToHTML(Part part, StringBuffer out) {
            if(part == null) {
                out.append("<p>Message Part is null.");
                return;
            }
            String disposition = null;
            try {
                disposition = part.getDisposition();
            } catch (MessagingException e) {
            }
            if(disposition == null) disposition = "inline";
            
            if (disposition.equals("attachment")) {
                this.dealWithAttachment(part, out);
            } else {
                String ret = this.convertPartToHTML(part);
                out.append(ret);
            }
        }

        private void dealWithAttachment(Part part, StringBuffer out) {
            String disposition = null;
            try {
                disposition = part.getDisposition();
            } catch (MessagingException e) {
                disposition = null;
            }
            
            
            String ct_str = "";
            try {
                ct_str = part.getContentType();
            } catch (MessagingException e) {
            }

            out.append("<P><b>Attachment </b><br>");
            out.append("Content-Type=" + ct_str + "<br>");
            out.append("Content-Disposition=" + disposition + "<br>");
            out.append("<br><br>");
            
            Debug.debug("attachment " + part.toString());

            this.attachments.add(part);
        
        }
        
        private String convertPartToHTML(Part part) {         
            ContentType ct;
                        
            try {
                String disposition = part.getDisposition();
                Debug.debug("disposition=" + disposition);
               
                String ct_str = part.getContentType();
                if(ct_str == null) ct_str = "text/plain";
                ct_str = ct_str.toLowerCase();

                ct = new ContentType(ct_str);
                Debug.debug("contentType=" + ct_str);
                
                if(ct.getPrimaryType().equals("text")) {
                    if (ct.getSubType().equals("html")) {
                        return striphtml((String)part.getContent());
                    } else if (ct.getSubType().equals("plain")) {
                        return text2html((String)part.getContent());
                    } else {
                        return "cannot handle: " + ct_str;
                    }
                } else if(ct.getPrimaryType().equals("image")) {
                    Debug.debug("filename: " + part.getFileName());
                    Debug.debug("description: " + part.getDescription());
                } else if(ct.getPrimaryType().equals("multipart")) {
                    if (ct.getSubType().equals("mixed") || ct.getSubType().equals("related")) {
                        // the message contains multiple parts... we should really only display the parts
                        // which are "Content-Disposition: inline"
                        Multipart mparts = (Multipart) part.getContent();
                        int inline_count = 0;
                        
                        StringBuffer htmlout = new StringBuffer();
                        Debug.debug(">>> multipart start");
                        for(int i=0; i<mparts.getCount(); i++) {
                            BodyPart apart = mparts.getBodyPart(i);
                            String adisposition = apart.getDisposition();
                            if(adisposition == null) adisposition = "inline";
                            
                            Debug.debug("disposition=" + adisposition);
                            
                            if (adisposition.equals("inline")) {
                                inline_count++;
                                if (inline_count > 1) {
                                    // divider...
                                    htmlout.append("<hr>");
                                }
                                this.convertMessageToHTML(apart, htmlout);
                                
                            } else if(adisposition.equals("attachment")) {
                                this.dealWithAttachment(apart, htmlout);
                            }
                            
                        }
                        Debug.debug(">>> multipart end");
                        return htmlout.toString();                        
                    } else if (ct.getSubType().equals("alternative")) {
                        // need to choose HTML or Plain Text View
                        Multipart mparts = (Multipart) part.getContent();
                        Debug.debug(">>> multipart start");
                        
                        BodyPart thePart = null;
                        boolean isDone = false;
                        for(int i=0; i<mparts.getCount(); i++) {
                            if(isDone == true) break;
                            BodyPart apart = mparts.getBodyPart(i);
                            
                            if(apart.isMimeType("text/html")) {
                                 // html is the best there is.

                                String htmlContent = (String) apart.getContent();
                                if(htmlContent.indexOf("<DIV>") == -1) {
                                    thePart = apart;
                                    isDone=true;
                                }
                            } else if(apart.isMimeType("text/plain")) {
                                thePart = apart;
                            }
                        }
                        
                        String ret = this.convertPartToHTML(thePart);
                        Debug.debug(">>> multipart end");
                        return ret;
                   } else {
                        return("cannot handle unknown mimetype: " + ct_str);
                   }
                    
                } else {
                    return("unknown mimetype: " + part.getContentType());
                }
                
            } catch (java.io.UnsupportedEncodingException e) {
                return("unsupported encoding");
            } catch (javax.mail.MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException("messaging exception");
            } catch (java.io.IOException e) {
                e.printStackTrace();
                throw new RuntimeException("ioexception");
            }
            return("no content");
            
        }
    }
    
    public void setViewedItem(DefaultItem item) {
        super.setViewedItem(item);
        renderItem();
    }
    public void renderItem() {
        MimeMessage msg = null;
        DefaultItem item = null;
        if (my_viewed_items.size() > 0) {
            item = (DefaultItem) my_viewed_items.get(0);
        }
        
        if (item == null) {
            this.emptyViews();
            return;
        }
        
        if (item instanceof ItemEmailMessage) {
            ItemEmailMessage msgitem = (ItemEmailMessage) item;  
            msgitem.setMessageRead();
            msg = msgitem.getMimeMessage();
        } else if (item instanceof DefaultItem) {
           if(item.get("Subject") != null && item.get("From") != null && 
              item.get("Date") != null) {
                  if(item.get("status") == null) item.put("status", "Read");
                  
                  byte[] msg_data = item.getData();
                  if(msg_data != null) {
                      InputStream is = new ByteArrayInputStream(msg_data);
                      Session session = Session.getInstance(System.getProperties());
                      try {
                          msg = new MimeMessage(session,is);
                      } catch (MessagingException e) {
                          throw new RuntimeException("MimeDecode failed!");
                      }
                  } else {
                      Debug.debug("message data is null");
                      item.requestDataFetch();
                      return;
                  }
          } else {
            this.emptyViews();
            return;  
          }

        } else {
            Debug.debug("setViewedItem: cannot display item: " + item.toString());
            return;
        }
        
        ////////////////////////////////////////
        //
        // Build HTML Header
        
        String headerHtml = this.buildEmailHeaderView(item);
        this.emailHeaderView.setText(headerHtml);

        //
        ///////////////////////////////////////
        //
        // Build HTML Body
        //
        
        if (msg != null) {
            HTMLMessageConverter conv = new HTMLMessageConverter(msg);

            String text = conv.toString();
            if(text.indexOf("<body") == -1) {
                text = "<html><body>" + text + "</body></html>";
            } else if(text.indexOf("<html") == -1) {
                text = "<html>" + text + "</html>";
            }

            
            emailContentView.setContentType("text/html");
            if (false) {
                Thread sct = new SetContentThread(emailContentView,text);
                sct.start();
            } else {
                if (text.length() > 20000) {
                    emailContentView.setText("<b> Too big: " + text.length());
                } else {
                    emailContentView.setText(text);
                    //Debug.debug("text=" + text);
                    emailContentView.setCaretPosition(0);
                }
            }
        } else {
            // register for notification
            emailContentView.setContentType("text/html");
            emailContentView.setText("<p>Fetching Message...");
            emailContentView.setCaretPosition(0);
            item.requestDataFetch();
            item.addChangeListener(this);
        }

        
        //
        ////////////////////////////////////////
        
        // force a layout...
        // STOP: this seems to cause exceptions. - jeske
        // this.aPanel.doLayout();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                aPanel.doLayout();
            }
        });
        triggerListeners(item);
    }

    private class SetContentThread extends Thread {
        String data;
        JEditorPane cv;
        SetContentThread(JEditorPane cv, String data) {
            this.data = data;
            this.cv = cv;
        }
        public void run() {
            cv.setText(data);
            cv.setCaretPosition(0);
        }
    }
    
    private void emptyViews() {
        // String colorStr = HtmlUtilities.getHtmlColorCode(window_background);
        String colorStr = "#CCFFFF";
        this.emailHeaderView.setContentType("text/html");
        this.emailHeaderView.setText("<HTML><BODY STYLE=\"border:0px;\" BGCOLOR=\"" + colorStr + "\"<CENTER>&nbsp;<br>No items are currently selected<br>&nbsp;</CENTER>");
        this.emailContentView.setContentType("text/html");
        this.emailContentView.setText(" ");
    }
    
    public void itemChanged(DefaultItem item) {
        if (my_viewed_items.size() > 0 && my_viewed_items.get(0) == item) {
            renderItem();
        } else {
            // we should probably remove the change notification
        }
    }
}
