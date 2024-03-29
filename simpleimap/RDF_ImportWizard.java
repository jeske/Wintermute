/*
 * RDF_ImportWizard.java
 *
 * Created on November 18, 2002, 9:31 AM
 */

package simpleimap;

import java.net.*;
import java.util.*;
import java.io.*;

import com.room4me.xml.*;

/**
 *
 * @author  hassan
 */
public class RDF_ImportWizard extends javax.swing.JDialog {
    private DefaultItem item;
    private aListModel model;

        
    /** Creates new form RDF_ImportWizard */
    public RDF_ImportWizard(java.awt.Frame parent, boolean modal, DefaultItem item) {
        super(parent, modal);
        this.item = item;
        initComponents();
        
        this.model = new aListModel();
        //this.model.add("Slashdot", "http://slashdot.org/slashdot.xml");
        this.model.add("Advogato", "http://advogato.org/rss/articles.xml");
        this.model.add("Barrapunto", "http://barrapunto.com/barrapunto.rdf");
        this.model.add("Barrapunto GNOME", "http://barrapunto.com/gnome.rdf");
        this.model.add("BSD Today", "http://www.bsdtoday.com/backend/bt.rdf");
        this.model.add("Beyond 2000", "http://beyond2000.com/b2k.rdf");
        this.model.add("Dictionary.com Word of the Day", "http://www.dictionary.com/wordoftheday/wotd.rss");
        this.model.add("DVD Review", "http://www.dvdreview.com/rss/newschannel.rss");
        this.model.add("Freshmeat", "http://freshmeat.net/backend/fm.rdf");
        this.model.add("Footnotes - GNOME News", "http://www.gnomedesktop.org/backend.php");
        this.model.add("Internet.com", "http://headlines.internet.com/internetnews/prod-news/news.rss");
        this.model.add("HispaLinux", "http://www.hispalinux.es/backend.php");
        this.model.add("KDE Dot News", "http://dot.kde.org/rdf");
        this.model.add("Kuro5hin", "http://www.kuro5hin.org/backend.rdf");
        this.model.add("Linux Games", "http://linuxgames.com/bin/mynetscape.pl");
        this.model.add("Linux Today", "http://linuxtoday.com/backend/my-netscape.rdf");
        this.model.add("Linux Weekly News", "http://lwn.net/headlines/rss");
        this.model.add("Memepool", "http://memepool.com/memepool.rss");
        this.model.add("Mozilla", "http://www.mozilla.org/news.rdf");
        this.model.add("Mozillazine", "http://www.mozillazine.org/contents.rdf");
        this.model.add("The Motley Fool", "http://www.fool.com/about/headlines/rss_headlines.asp");
        this.model.add("Newsforge", "http://www.newsforge.com/newsforge.rss");
        this.model.add("Pigdog", "http://www.pigdog.org/pigdog.rdf");
        this.model.add("Python.org", "http://www.python.org/channews.rdf");
        this.model.add("Quotes of the Day", "http://www.quotationspage.com/data/mqotd.rss");
        this.model.add("Salon", "http://www.salon.com/feed/RDF/salon_use.rdf");
        this.model.add("Slashdot", "http://slashdot.org/slashdot.xml");
        this.model.add("The Register", "http://www.theregister.co.uk/tonys/slashdot.rdf");
        this.model.add("Web Reference", "http://www.webreference.com/webreference.rdf");
        this.model.add("Ximian Red Carpet News", "http://redcarpet.ximian.com/red-carpet.rdf");
        
        
        
        this.rdfList.setModel(model);
    }
    
    public class aListModel extends javax.swing.AbstractListModel {
        LinkedList strings;
        
        public aListModel() {
            this.strings = new LinkedList();
        }
        
        public class aListObject {
            String title;
            String data;
            aListObject(String title, String data) {
                this.title = title;
                this.data = data;
            }
            
        }
        
        public void add(String title, String url) {
            aListObject obj = new aListObject(title, url);
            strings.add(obj);
        }
        
        
        public int getSize() {
            return strings.size();
        }
        
        public aListObject getObjAt(int i) {
            aListObject obj = (aListObject) strings.get(i);
            return obj;
        }
        
        public Object getElementAt(int i) {
            aListObject obj = (aListObject) strings.get(i);
            
            return obj.title;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rdfList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        urlField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setText("Cancel");
        jPanel1.add(cancelButton);

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        jPanel1.add(importButton);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 440, 40));

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rdfList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        rdfList.setVisibleRowCount(10);
        jScrollPane1.setViewportView(rdfList);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 430, 200));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        urlField.setColumns(35);
        jPanel3.add(urlField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 350, 20));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("RDF URL:");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 430, 50));

        pack();
    }//GEN-END:initComponents


    
    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        // Add your handling code here:

        
        try {
            int row = this.rdfList.getSelectedIndex();
            if(row != -1) {
                aListModel.aListObject obj = (aListModel.aListObject) this.model.getObjAt(row);
                //String url = this.urlField.getText();
                ItemRDFSource rdfitem = (ItemRDFSource) WinterMute.my_db.newItem(null, "ItemRDFSource", "RDF: " + obj.title);
                rdfitem.put("url", obj.data);
                this.item.addChild(rdfitem);
            } else {
                String url = this.urlField.getText();
                if(url.length() > 0) {
                    ItemRDFSource rdfitem = (ItemRDFSource) WinterMute.my_db.newItem(null, "ItemRDFSource", "RDF: " + url);
                    rdfitem.put("url", url);
                    this.item.addChild(rdfitem);  
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("calendar parse Failed");
        } finally {
  
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_importButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
    //    new RDF_ImportWizard(new javax.swing.JFrame(), true).show();
    //}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList rdfList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField urlField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton importButton;
    // End of variables declaration//GEN-END:variables
    
}
