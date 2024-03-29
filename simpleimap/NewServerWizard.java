/*
 * NewServerWizard.java
 *
 * Created on November 16, 2002, 3:31 PM
 */

package simpleimap;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.prefs.*;

/**
 *
 * @author  hassan
 */
public class NewServerWizard extends javax.swing.JFrame {
    
    /** Creates new form NewServerWizard */
    public NewServerWizard() {
        initComponents();
        
        this.readDefaultValues();

    }
    
    private void readDefaultValues() {
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        yourNameField.setText(prefs.get("NewServerWizard.yourNameField", ""));
        emailAddressField.setText(prefs.get("NewServerWizard.emailAddressField", ""));
        serverField.setText(prefs.get("NewServerWizard.serverField", ""));
        
        mailboxPathField.setText(prefs.get("NewServerWizard.mailboxPathField", ""));
        inboxPathField.setText(prefs.get("NewServerWizard.inboxPathField", ""));

        loginField.setText(prefs.get("NewServerWizard.loginField", ""));
        passwordField.setText(prefs.get("NewServerWizard.passwordField", ""));        
        smtpServerField.setText(prefs.get("NewServerWizard.smtpServerField", ""));
    }
    
    private void setDefaultValues() {
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        prefs.put("NewServerWizard.yourNameField", yourNameField.getText());
        prefs.put("NewServerWizard.emailAddressField", emailAddressField.getText());
        prefs.put("NewServerWizard.serverField", serverField.getText());
        prefs.put("NewServerWizard.mailboxPathField", mailboxPathField.getText());
        prefs.put("NewServerWizard.inboxPathField", inboxPathField.getText());
        prefs.put("NewServerWizard.loginField", loginField.getText());
        prefs.put("NewServerWizard.passwordField", passwordField.getText());
        prefs.put("NewServerWizard.smtpServerField", smtpServerField.getText());

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        emailTypeGroup = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel2 = new javax.swing.JPanel();
        yourNameField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        emailAddressField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        createButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        imapRadioButton = new javax.swing.JRadioButton();
        popRadioButton = new javax.swing.JRadioButton();
        localMailboxRadioButton = new javax.swing.JRadioButton();
        emailTypeCardPanel = new javax.swing.JPanel();
        blankCard = new javax.swing.JPanel();
        serverSettingsCard = new javax.swing.JPanel();
        serverLabel = new javax.swing.JLabel();
        serverField = new javax.swing.JTextField();
        useSSLCheckbox = new javax.swing.JCheckBox();
        loginLabel = new javax.swing.JLabel();
        loginField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        mailboxSettingsCard = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        mailboxPathField = new javax.swing.JTextField();
        inboxPathField = new javax.swing.JTextField();
        inboxPathBrowseButton = new javax.swing.JButton();
        directoryBrowseButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        smtpServerField = new javax.swing.JTextField();

        getContentPane().setLayout(new AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel2.setLayout(new AbsoluteLayout());

        jPanel2.setBorder(new javax.swing.border.TitledBorder("Account Information"));
        jPanel2.add(yourNameField, new AbsoluteConstraints(100, 30, 210, -1));

        jLabel1.setText("Email Address:");
        jPanel2.add(jLabel1, new AbsoluteConstraints(10, 60, -1, -1));

        jLabel2.setText("Your Name:");
        jPanel2.add(jLabel2, new AbsoluteConstraints(30, 30, -1, -1));

        jPanel2.add(emailAddressField, new AbsoluteConstraints(100, 60, 210, -1));

        getContentPane().add(jPanel2, new AbsoluteConstraints(0, 20, 420, 100));

        jPanel3.setLayout(new AbsoluteLayout());

        createButton.setText("Ok");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        jPanel3.add(createButton, new AbsoluteConstraints(260, 10, -1, -1));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel3.add(cancelButton, new AbsoluteConstraints(320, 10, -1, -1));

        getContentPane().add(jPanel3, new AbsoluteConstraints(0, 370, 400, 50));

        jPanel4.setLayout(new AbsoluteLayout());

        jPanel4.setBorder(new javax.swing.border.TitledBorder("Email Type"));
        imapRadioButton.setText("IMAP");
        emailTypeGroup.add(imapRadioButton);
        imapRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imapRadioButtonStateChanged(evt);
            }
        });

        jPanel4.add(imapRadioButton, new AbsoluteConstraints(10, 20, -1, -1));

        popRadioButton.setText("POP");
        emailTypeGroup.add(popRadioButton);
        popRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                popRadioButtonStateChanged(evt);
            }
        });

        jPanel4.add(popRadioButton, new AbsoluteConstraints(70, 20, -1, -1));

        localMailboxRadioButton.setText("Local Mailbox");
        emailTypeGroup.add(localMailboxRadioButton);
        localMailboxRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                localMailboxStateChanged(evt);
            }
        });

        jPanel4.add(localMailboxRadioButton, new AbsoluteConstraints(130, 20, -1, -1));

        getContentPane().add(jPanel4, new AbsoluteConstraints(0, 120, 420, 60));

        emailTypeCardPanel.setLayout(new java.awt.CardLayout());

        emailTypeCardPanel.add(blankCard, "blankCard");

        serverSettingsCard.setLayout(new AbsoluteLayout());

        serverSettingsCard.setBorder(new javax.swing.border.TitledBorder("Server Settings"));
        serverLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        serverLabel.setText("Server name:");
        serverSettingsCard.add(serverLabel, new AbsoluteConstraints(0, 20, 90, 20));

        serverSettingsCard.add(serverField, new AbsoluteConstraints(100, 20, 250, -1));

        useSSLCheckbox.setSelected(true);
        useSSLCheckbox.setText("Use SSL");
        serverSettingsCard.add(useSSLCheckbox, new AbsoluteConstraints(100, 50, -1, -1));

        loginLabel.setText("Username:");
        serverSettingsCard.add(loginLabel, new AbsoluteConstraints(30, 80, -1, 20));

        serverSettingsCard.add(loginField, new AbsoluteConstraints(100, 80, 140, -1));

        passwordLabel.setText("Password:");
        serverSettingsCard.add(passwordLabel, new AbsoluteConstraints(30, 110, -1, -1));

        serverSettingsCard.add(passwordField, new AbsoluteConstraints(100, 110, 140, -1));

        emailTypeCardPanel.add(serverSettingsCard, "serverSettingsCard");

        mailboxSettingsCard.setLayout(new AbsoluteLayout());

        mailboxSettingsCard.setBorder(new javax.swing.border.TitledBorder("Local Mailbox"));
        jLabel4.setText("Inbox Filename:");
        mailboxSettingsCard.add(jLabel4, new AbsoluteConstraints(10, 20, -1, -1));

        jLabel5.setText("Local Mailbox Directory:");
        mailboxSettingsCard.add(jLabel5, new AbsoluteConstraints(10, 70, -1, -1));

        mailboxPathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailboxPathFieldActionPerformed(evt);
            }
        });

        mailboxSettingsCard.add(mailboxPathField, new AbsoluteConstraints(30, 90, 270, -1));

        inboxPathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inboxPathFieldActionPerformed(evt);
            }
        });

        mailboxSettingsCard.add(inboxPathField, new AbsoluteConstraints(30, 40, 270, -1));

        inboxPathBrowseButton.setText("Browse...");
        inboxPathBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inboxPathBrowseButtonActionPerformed(evt);
            }
        });

        mailboxSettingsCard.add(inboxPathBrowseButton, new AbsoluteConstraints(310, 40, 90, -1));

        directoryBrowseButton.setText("Browse...");
        directoryBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryBrowseButtonActionPerformed(evt);
            }
        });

        mailboxSettingsCard.add(directoryBrowseButton, new AbsoluteConstraints(310, 90, 90, -1));

        emailTypeCardPanel.add(mailboxSettingsCard, "mailboxSettingsCard");

        getContentPane().add(emailTypeCardPanel, new AbsoluteConstraints(0, 180, 420, 150));

        jPanel7.setLayout(new AbsoluteLayout());

        jLabel3.setText("SMTP Server:");
        jPanel7.add(jLabel3, new AbsoluteConstraints(10, 0, -1, 20));

        smtpServerField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smtpServerFieldActionPerformed(evt);
            }
        });

        jPanel7.add(smtpServerField, new AbsoluteConstraints(100, 0, 250, -1));

        getContentPane().add(jPanel7, new AbsoluteConstraints(0, 340, 400, 30));

        pack();
    }//GEN-END:initComponents

    private void inboxPathFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inboxPathFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_inboxPathFieldActionPerformed

    private void smtpServerFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smtpServerFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_smtpServerFieldActionPerformed

    private void directoryBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryBrowseButtonActionPerformed
        // Add your handling code here:
        String path = mailboxPathField.getText();
        
        try {
            UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Create a file chooser
        JFileChooser chooser = new JFileChooser(path);
        ShortcutsAccessory shortcuts = new ShortcutsAccessory(chooser, "demo");
        chooser.setAccessory(shortcuts);
        Dimension d = new Dimension(700, 400);
        chooser.setMinimumSize(d);
        chooser.setPreferredSize(d);
        
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            mailboxPathField.setText(file.toString());
            
        }
    }//GEN-LAST:event_directoryBrowseButtonActionPerformed

    private void inboxPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inboxPathBrowseButtonActionPerformed
        // Add your handling code here:
        String path = mailboxPathField.getText();
        
        try {
            UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Create a file chooser
        JFileChooser chooser = new JFileChooser(path);
        ShortcutsAccessory shortcuts = new ShortcutsAccessory(chooser, "demo");
        chooser.setAccessory(shortcuts);
        Dimension d = new Dimension(700, 400);
        chooser.setMinimumSize(d);
        chooser.setPreferredSize(d);

        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            inboxPathField.setText(file.toString());
           
        }
    }//GEN-LAST:event_inboxPathBrowseButtonActionPerformed

    private void localMailboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_localMailboxStateChanged
        // Add your handling code here:
        CardLayout cl = (CardLayout)(emailTypeCardPanel.getLayout());
        cl.show(emailTypeCardPanel, "mailboxSettingsCard");
    }//GEN-LAST:event_localMailboxStateChanged

    private void imapRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imapRadioButtonStateChanged
        // Add your handling code here:
         CardLayout cl = (CardLayout)(emailTypeCardPanel.getLayout());
        cl.show(emailTypeCardPanel, "serverSettingsCard");
    }//GEN-LAST:event_imapRadioButtonStateChanged

    private void popRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_popRadioButtonStateChanged
        // Add your handling code here:
        CardLayout cl = (CardLayout)(emailTypeCardPanel.getLayout());
        cl.show(emailTypeCardPanel, "serverSettingsCard");
    }//GEN-LAST:event_popRadioButtonStateChanged

    private void mailboxPathFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailboxPathFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_mailboxPathFieldActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // Add your handling code here:
        this.setDefaultValues();
        
        if(imapRadioButton.isSelected()) {
            createIMAPServerItem();
        } else if(popRadioButton.isSelected()) {
            createPopServerItem();
        } else {
            createMailboxItem();
        }
        this.dispose();
        
    }

    private void createMailboxItem() {
        
        String item_id = emailAddressField.getText() + "(Unix Mailbox)";

        
        try {
            DefaultItem server = WinterMute.my_db.newItem(null, "UnixMailbox", item_id);
            
            server.put("smtpServer", smtpServerField.getText());
            server.put("emailAddress", emailAddressField.getText());
            server.put("yourName",  yourNameField.getText());
            
            server.put("inboxPath", inboxPathField.getText());
            server.put("mailFolderPath", mailboxPathField.getText());
            
            server.onActivate();

            DefaultItem rootItem = WinterMute.my_db.rootNode();        
            rootItem.addChild(server);  
            
        } catch (eNoSuchItem e) {
            throw new RuntimeException("couldn't make an mailbox item.");
        }
    }
    
    private void createPopServerItem() {
    }
    
    private void createIMAPServerItem() {
        String imap_folder_id = loginField.getText() + "@" + serverField.getText();
          
        try {

            
            DefaultItem server = WinterMute.my_db.newItem(null, "IMAPServer",imap_folder_id);
            server.put("server", serverField.getText());
            server.put("login", loginField.getText());
            server.put("password", passwordField.getText());
            
            server.put("smtpServer", smtpServerField.getText());
            server.put("emailAddress", emailAddressField.getText());
            server.put("yourName",  yourNameField.getText());
            
            
            if (useSSLCheckbox.isSelected()) {
                server.put("use_ssl", "true");
            } else {
                server.put("use_ssl", "false");
            }
            server.onActivate();


            DefaultItem rootItem = WinterMute.my_db.getItem(0);        
            rootItem.addChild(server);
        } catch (eNoSuchItem e) {
            throw new RuntimeException("couldn't make an imap server");
        }
   
    }//GEN-LAST:event_createButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
    }//GEN-LAST:event_exitForm
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel serverSettingsCard;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField yourNameField;
    private javax.swing.JButton inboxPathBrowseButton;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JTextField inboxPathField;
    private javax.swing.JRadioButton popRadioButton;
    private javax.swing.JButton directoryBrowseButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField mailboxPathField;
    private javax.swing.JTextField loginField;
    private javax.swing.JRadioButton imapRadioButton;
    private javax.swing.JPanel mailboxSettingsCard;
    private javax.swing.JTextField serverField;
    private javax.swing.JButton createButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel7;
    private javax.swing.ButtonGroup emailTypeGroup;
    private javax.swing.JRadioButton localMailboxRadioButton;
    private javax.swing.JTextField emailAddressField;
    private javax.swing.JCheckBox useSSLCheckbox;
    private javax.swing.JTextField smtpServerField;
    private javax.swing.JPanel blankCard;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel emailTypeCardPanel;
    // End of variables declaration//GEN-END:variables
    
}
