/*
 * TestTreeTable.java
 *
 * Created on February 4, 2003, 4:20 PM
 */

package tbtest;

/**
 *
 * @author  David W Jeske
 */
public class TestTreeTable extends javax.swing.JFrame {
    JTreeTable ttbl;
    
    /** Creates new form TestTreeTable */
    public TestTreeTable() {
       // ttbl = new JTreeTable();
       // this.getContentPane().add(ttbl,BorderLayout.CENTER);
        
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        pack();
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new TestTreeTable().show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}