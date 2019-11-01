/*
 * ComposeMessageFrame.java
 *
 * Created on November 17, 2002, 1:26 PM
 */

package simpleimap;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


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
public class ComposeMessageFrame extends javax.swing.JFrame {
    private DefaultComboBoxModel fromModel;
    private Hashtable actions;
    private MyKeyListener bodyTextListener;
    Popup popup;
    
    /** Creates new form ComposeMessageFrame */
    public ComposeMessageFrame() {
        initComponents();
        
        this.bodyTextListener = new MyKeyListener(this);
        this.bodyText.addKeyListener(this.bodyTextListener);
        
        populateFromModel();
        this.popup = null;
        
    }
    
    public ComposeMessageFrame(String toEmailAddr) {
        initComponents();     
        populateFromModel();
        toField.setText(toEmailAddr);
        this.popup = null;
        
    }
    
    
    protected void populateFromModel() {
        LinkedList accounts = WinterMute.my_db.getUserAccountEmailAddresses();
                
        fromModel = new DefaultComboBoxModel();
        fromField.setModel(fromModel);
        
        for(Iterator iter = accounts.iterator(); iter.hasNext(); ) {
            SimpleDB.AccountInformation account = (SimpleDB.AccountInformation) iter.next();
            fromModel.addElement("\"" + account.fullName + "\" <" + account.emailAddress + ">"); 
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        sendButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        fromField = new javax.swing.JComboBox();
        toField = new javax.swing.JTextField();
        subjectField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        attachments = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bodyText = new javax.swing.JEditorPane();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));

        jPanel2.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanel2.setMinimumSize(new java.awt.Dimension(400, 189));
        jPanel2.setPreferredSize(new java.awt.Dimension(640, 202));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(3, 3, 3, 3)));
        jPanel3.setMinimumSize(new java.awt.Dimension(400, 80));
        jPanel3.setPreferredSize(new java.awt.Dimension(600, 92));
        sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mail-config-druid-send.png")));
        sendButton.setText("Send");
        sendButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        sendButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sendButton.setMaximumSize(new java.awt.Dimension(100, 72));
        sendButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jPanel3.add(sendButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 80));

        jPanel2.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS));

        jPanel4.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 1, 10, 1)));
        jPanel4.setPreferredSize(new java.awt.Dimension(640, 120));
        jPanel5.setLayout(new java.awt.GridLayout(3, 1, 0, 3));

        jPanel5.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 10, 0, 10)));
        jPanel5.setMaximumSize(new java.awt.Dimension(180, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(57, 100));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("From:");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel5.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("To:");
        jPanel5.add(jLabel3);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Subject:");
        jPanel5.add(jLabel4);

        jPanel4.add(jPanel5);

        jPanel6.setLayout(new java.awt.GridLayout(3, 1, 0, 3));

        fromField.setBackground(new java.awt.Color(255, 255, 255));
        fromField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Scott Hassan <hassan2@dotfunk.com>", "David Jeske <jeske@chat.net>", "Brandon Long <blong@fiction.net>" }));
        fromField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromFieldActionPerformed(evt);
            }
        });

        jPanel6.add(fromField);

        toField.setFont(new java.awt.Font("Dialog", 0, 14));
        toField.setText("jeske@chat.net");
        jPanel6.add(toField);

        subjectField.setFont(new java.awt.Font("Dialog", 0, 14));
        subjectField.setText("subject");
        subjectField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectFieldActionPerformed(evt);
            }
        });

        jPanel6.add(subjectField);

        jPanel4.add(jPanel6);

        jPanel7.setLayout(new java.awt.CardLayout());

        jPanel7.setBorder(new javax.swing.border.TitledBorder("Attachments"));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));
        jPanel7.setMaximumSize(new java.awt.Dimension(150, 2147483647));
        jPanel7.setPreferredSize(new java.awt.Dimension(200, 0));
        attachments.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        attachments.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "test1" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jPanel7.add(attachments, "card2");

        jPanel4.add(jPanel7);

        jPanel2.add(jPanel4);

        getContentPane().add(jPanel2);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 10, 1)));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 311));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 300));
        bodyText.setBorder(new javax.swing.border.EtchedBorder());
        this.bodyTextPostInit();
        jScrollPane1.setViewportView(bodyText);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1);

        pack();
    }//GEN-END:initComponents
    
    
    
    private class MyKeyListener implements KeyListener {
        ComposeMessageFrame frame;
        
        public MyKeyListener(ComposeMessageFrame frame) {
            this.frame = frame;
        }
        public void keyTyped(KeyEvent event) {
        }
        public void keyPressed(KeyEvent event) {
            //this.frame.keyTyped(event);  
        }
        public void keyReleased(KeyEvent event) {
            this.frame.keyTyped(event);
        }
    };

    private LinkedList lastMatches = null;
    
    public void keyTyped(KeyEvent event) {
        
        char key = event.getKeyChar();
        
        String text = this.bodyText.getText();
        Calendar now = WinterMute.now();        
        LinkedList matches = RelationshipBuilder.rbuilder.match(RelationshipBuilder.rbuilder.dataparsers, now, "", text);
        
        if(lastMatches != null && lastMatches.size() == matches.size()) {
            int len = lastMatches.size();
            boolean all_same = true;
            for (int i=0;i<len;i++) {
                RelationshipBuilder.MatchObject mo1 = (RelationshipBuilder.MatchObject) lastMatches.get(i);
                RelationshipBuilder.MatchObject mo2 = (RelationshipBuilder.MatchObject) matches.get(i);
                if (!(mo1.start == mo2.start && mo1.end == mo2.end)) {
                    all_same = false;
                    continue;
                }
            }
            if (all_same) {
                return;
            }
        }
        lastMatches = matches;
        
        if(this.popup != null) this.popup.hide();
        
        Caret c = this.bodyText.getCaret();
        int dot;
        
        
        //dot = c.getDot();
        //Debug.debug("" + dot);
        
        try {
            
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW,2));
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            dot = this.relateText(panel, matches);
            if(dot == -1) return;
            
            Rectangle caretCoords = this.bodyText.modelToView(dot);
            int x = caretCoords.x+10;
            int y = caretCoords.y-10;
            
            //Debug.debug("" + x, "" + y);
            
            Point p = new Point(x,y);
            SwingUtilities.convertPointToScreen(p, this.bodyText);
            
            panel.validate();
            Dimension d = panel.getPreferredSize();
            Rectangle bounds =panel.getBounds();
            
            Debug.debug("panel size ",d);
            p.y = p.y - d.height;
            
            PopupFactory factory = PopupFactory.getSharedInstance();
            this.popup = factory.getPopup(this, panel, p.x,p.y);
            
            this.popup.show();
        } catch (Exception e) {
            Debug.debug(e);
        }
        
        
    }
    
    public int relateText(JPanel panel, LinkedList matches) {
        
        int dot = -1;
        
        for(Iterator iter=matches.iterator(); iter.hasNext(); ) {
            RelationshipBuilder.MatchObject mo = (RelationshipBuilder.MatchObject) iter.next();
            if(dot == -1) {
                dot = mo.start;
            }
            if(mo instanceof RelationshipBuilder.DateMatchObject) {
                RelationshipBuilder.DateMatchObject dmo = (RelationshipBuilder.DateMatchObject) mo;
                
                Date date = dmo.date;
                
                SimpleDateFormat date_formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
                String datestr = date_formatter.format(date);
                
                JLabel label = new JLabel("Match=" + datestr);
                panel.add(label);
                
                
                CalendarDayView dayview = new CalendarDayView();
                dayview.showDate(date);
                
                DefaultItem root = WinterMute.my_db.rootNode();
                DefaultItem mainCalendar = root.getItem(WinterMute.parentChildRelation, "Calendar");
                if(mainCalendar == null) {
                    mainCalendar = WinterMute.my_db.newItem(null, "Default", "Calendar");
                }
                
                CalendarItemModel aModel = new CalendarItemModel(mainCalendar);
                dayview.setModel(aModel);
                
                panel.add(dayview);
            } else {
                JLabel label = new JLabel("Match=" + mo.toString());
                panel.add(label);
            }
            //mo.display();
        }
        return dot;
        
    }
    
    
    private void createActionTable(JTextComponent textComponent) {
        actions = new Hashtable();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    }
    
    private Action getActionByName(String name) {
        return (Action)(actions.get(name));
    }
    
    //Add a couple of emacs key bindings to the key map for navigation.
    protected void addKeymapBindings() {
        //Add a new key map to the keymap hierarchy.
        Keymap keymap = bodyText.addKeymap("MyEmacsBindings", bodyText.getKeymap());
        
        //Ctrl-b to go backward one character
        Action action = getActionByName(DefaultEditorKit.backwardAction);
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        //Ctrl-f to go forward one character
        action = getActionByName(DefaultEditorKit.forwardAction);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        //Ctrl-p to go up one line
        action = getActionByName(DefaultEditorKit.upAction);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        //Ctrl-n to go down one line
        action = getActionByName(DefaultEditorKit.downAction);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        //        action = getActionByName(DefaultEditorKit.pageUpAction);
        //        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK);
        //        keymap.addActionForKeyStroke(key, action);
        
        //        action = getActionByName(DefaultEditorKit.pageDownAction);
        //        key = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
        //        keymap.addActionForKeyStroke(key, action);
        
        action = getActionByName(DefaultEditorKit.endLineAction);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        action = getActionByName(DefaultEditorKit.beginLineAction);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);
        
        
        
        bodyText.setKeymap(keymap);
    }
    
    private void bodyTextPostInit() {
        this.createActionTable(bodyText);
        this.addKeymapBindings();
    }
    
    private void fromFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_fromFieldActionPerformed

    private void subjectFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_subjectFieldActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // Add your handling code here:
        String host = "blue.dotfunk.com";
        String from = (String) fromField.getSelectedItem();
        String to = toField.getText();
        String subject = subjectField.getText();
        String body = bodyText.getText();

        // Get system properties
        Properties props = System.getProperties();
       

        // Setup mail server
        props.put("mail.smtp.host", host);

        // Get session
        Session session = Session.getDefaultInstance(props, null);

        // Define message
        MimeMessage message = new MimeMessage(session);
        
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            message.addHeader("X-Mailer", "Wintermute 0.1");
            message.addHeader("Content-Type", "text/plain");
            
            message.setSentDate(new java.util.Date()); 

            // Send message
            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("sending message failure");
        }

        this.dispose();
    }//GEN-LAST:event_sendButtonActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        // send message -- swh
    }//GEN-LAST:event_exitForm
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton sendButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField subjectField;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JComboBox fromField;
    private javax.swing.JList attachments;
    private javax.swing.JTextField toField;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JEditorPane bodyText;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables
    
    public static void main(String args[]) throws Exception {
        Debug.start();
        
        RelationshipBuilder.init();
        WinterMute.setupCalendar();
 
        new ComposeMessageFrame().show();
    }
    
}