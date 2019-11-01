package tbtest2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

class TBTest extends JFrame {
    
    //class ToolContainer extends JPanel {        
    //}
    
    class NToolbar extends JPanel {
        JButton grabButton;
        Box itemsPanel;
        Font buttonTextFont;
        NToolbar() {
            Font buttonTextFont = new Font("Arial",Font.PLAIN,10);
            
            this.setLayout(new BorderLayout());
            
            
            // add the grab button
            grabButton = new JButton();
            grabButton.setBackground(Color.BLUE);
            grabButton.setSize(2,10);
            grabButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            this.add(grabButton,BorderLayout.WEST);
            
            // add the itemsPanel
            itemsPanel = Box.createHorizontalBox();
            this.add(itemsPanel,BorderLayout.CENTER);
        }
        
        public void add(JComponent item) {
            if (item instanceof JButton) {
                item.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                item.setFont(buttonTextFont);
            }
            itemsPanel.add(item);
        }
        
    }
    
    
    TBTest() {
        this.setSize(300,300);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        NToolbar tb = new NToolbar();
        tb.add(new JButton("Reply"));
        tb.add(new JButton("Reply All"));
        
        this.getContentPane().add(tb,BorderLayout.NORTH);
        
    }
    
    private void exitForm(java.awt.event.WindowEvent evt) {
        System.exit(0);
    }
    
    public static void main(String args[]) {
        new TBTest().show();
        
    }
}