
package guicomp;

import simpleimap.Debug;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.geom.*;

import java.lang.Math;
import java.lang.Math;

public class CollapsePanel extends JPanel {
    private final int LVL = 3;
    private int MAX_HEIGHT = 250;
    JButton trigger;
    JViewport viewport;
    Component subview;
    boolean isCollapsed = false;
    DriveAnimation animator = null;
    
    
    public CollapsePanel() {
        
        // setup our layout and style...
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        this.setOpaque(false);
        //this.setBorder(BorderFactory.createLineBorder(java.awt.Color.RED,2));
        this.setLayout(new VerticalStackedLayout());

        // setup our animation engine!
       
        
        // setup titlebar
        JPanel tp = new GradientPanel();
        
        trigger = new JButton("no title");
        trigger.setBorder(BorderFactory.createEmptyBorder(5,2,2,2));
        trigger.setFont((java.awt.Font)tk.getDesktopProperty("win.frame.captionFont"));
        // trigger.setHorizontalTextPosition(JButton.LEADING);
        trigger.setHorizontalAlignment(JButton.LEFT);
        trigger.setDefaultCapable(false);
        // trigger.setBackground(Color.BLUE);
        //trigger.setBackground((java.awt.Color) tk.getDesktopProperty("win.frame.inactiveCaptionGradientColor"));
        trigger.setOpaque(false);
        tp.add(trigger,BorderLayout.CENTER);
        
        trigger.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               toggleState();
           }
        });

        viewport = new JViewport();
        viewport.setName("CollapsePanel viewport");
        viewport.add(new JPanel());
        viewport.setPreferredSize(new Dimension(0,0));
        viewport.setMaximumSize(new Dimension(0,0));
        viewport.setBackground((java.awt.Color) tk.getDesktopProperty("win.mdi.backgroundColor"));

        this.add(tp);
        if (true) {
            this.add(viewport);
        } else {
           JPanel vppanel = new JPanel();
           vppanel.add(viewport);
           vppanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.GREEN,2));
           this.add(vppanel);
        }
    }

    public void wakeup() {
        if (animator == null) {
            animator = new DriveAnimation(this);
        }
        animator.wakeup();
    }
    
    //// GradientPanel used for titlebar

    private class GradientPanel extends JPanel {
        private final int LVL = 3;
        Color startColor;
        Color endColor;
        GradientPanel() {
            this.setOpaque(true);
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setLayout(new BorderLayout());
            java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
            
            startColor = Color.WHITE;
            endColor = (Color)tk.getDesktopProperty("win.frame.inactiveCaptionGradientColor");
        }
        
        public void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            
            
            
            // set preferences
            g.setRenderingHint(RenderingHints.KEY_DITHERING,
                 RenderingHints.VALUE_DITHER_ENABLE);
            // draw my gradient background
            Shape s = g.getClip();
            Dimension d = this.getSize();
            
            
            float startX = 0;
            float startY = (float)d.height;
            float endX   = (float)d.width;
            float endY   = 0;
            
     
            // A non-cyclic gradient
            GradientPaint gradient = new GradientPaint(
               startX, startY, startColor, 
               endX, endY, endColor, true);
            
            if (true) {
            
                g.setPaint(gradient);
                g.fillRect(0,0,d.width, d.height);
               
                // g.clearRect(0,0, r.width,r.height);
                // Debug.debug(s);
                // g.draw(s);
                // super.paintComponent(g);
            } else {
                // 8bpp version
                BufferedImage bi = new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_BGR);
                Graphics2D g2 = bi.createGraphics();
                g2.setPaint(gradient);
                g2.fillRect(0,0,d.width,d.height);
                
                // 5bpp version
                BufferedImage bi2 = new BufferedImage(d.width,d.height,BufferedImage.TYPE_USHORT_555_RGB);
                
                // conversion
                RenderingHints rh = new RenderingHints(null);
                rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
                ColorConvertOp op = new ColorConvertOp(rh);
                bi2 = op.filter(bi,bi2);
                // final draw
                
                
                g.drawImage(bi2,new AffineTransform(),null);
            }
        }
    }

 
    
    //// implementation
    
    int cur_velocity = 1;

    public void validate() {
    
        // when we change size
        Dimension d = getSize();

        if (subview != null) {
            // check and set subview width
            Dimension svdim = this.subview.getSize();

            if (svdim.width != d.width) {
                System.out.println("Adjusting [" + subview.getName() + "] width from: " + svdim + " to: " + d);
                Debug.debug(LVL,"Adjusting width from: " + d + " to: " + svdim);
                this.subview.setSize(new Dimension(d.width,svdim.height));
            }
        }
    
        super.validate();
    }
    
    public boolean doAnimate() {
        int step = cur_velocity;
        boolean didwork = false;
        Dimension d = viewport.getSize();
        
        int width = this.getSize().width;

        
        if (isCollapsed) {
            //viewport.setSize(wdim.width,0);
            if (d.height > 0) {
                
                Dimension newsize = new Dimension(width,Math.max(d.height-step,0));
                Debug.debug(LVL,"newsize: " + newsize);
                viewport.setPreferredSize(newsize);
                Point ul = viewport.getViewPosition();
                ul.setLocation(ul.x,ul.y+step);
                viewport.setViewPosition(ul);
                viewport.revalidate();
                didwork = true;
            }
        } else {
            Dimension dim;
            if (this.subview != null) {
                dim = this.subview.getPreferredSize();
                if (dim.height > 0 && !this.isVisible()) {
                    this.setVisible(true);
                }
                if (dim.height > d.height) {
                    if (dim.height < MAX_HEIGHT) {
                        Debug.debug(LVL,"Stepping larger from: " + d + " to: " + dim);
                        viewport.setPreferredSize(new Dimension(width,Math.min(d.height + step,dim.height)));
                        viewport.setSize(new Dimension(width,Math.min(d.height + step,dim.height)));
                        viewport.revalidate();
                        didwork = true;
                    }
                } else if (dim.height < d.height) {
                        Debug.debug(LVL,"Stepping smaller from: " + d + " to: " + dim);
                        viewport.setPreferredSize(new Dimension(width,Math.max(d.height - step,dim.height)));
                        viewport.setSize(new Dimension(width,Math.max(d.height - step,dim.height)));
                        viewport.revalidate();
                        didwork = true;
                } else {
                    if (dim.height == 0) {
                        this.setVisible(false);
                    }
                }
                
            } else {
                // the view was removed, make sure to collapse it!
                dim = new Dimension(0,0);
                viewport.setPreferredSize(new Dimension(width,dim.height));
                viewport.revalidate();
                didwork = true;
            }
        }
        if (!didwork) {
            cur_velocity = 1;
        } else {
            cur_velocity+= 3;
        }
        
        return didwork;
    }
    
    public void invalidate() {
        super.invalidate();
        wakeup();
        // sizeViewport(); // later we'll trigger the animation here!
    }
    

    public void sizeViewport() {
        int width = this.getSize().width;  
        
        this.trigger.setText(subview.getName());
        
        if (isCollapsed) {
            //viewport.setSize(wdim.width,0);
            viewport.setPreferredSize(new Dimension(width,0));
            viewport.revalidate();
        } else {
            Dimension dim;
            if (this.subview != null) {
                dim = this.subview.getPreferredSize();
                this.subview.setSize(new Dimension(width, dim.height));
                dim = this.subview.getPreferredSize();
            } else {
                dim = new Dimension(0,0);
            }
            
            viewport.setPreferredSize(new Dimension(width,dim.height));
            viewport.revalidate();
        }
        //this.validate();
        //this.revalidate();
        //this.doLayout();
        //this.invalidate();

    }

    public void toggleState() {
        isCollapsed = !isCollapsed;
        wakeup();
        // sizeViewport();
    }

    public void setSubview(Component subview) {
        if (subview instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) subview;
            
            subview = sp.getViewport().getComponent(0);
        }
        
        this.subview = subview;
        
        viewport.removeAll();
        viewport.add(subview);
        // viewport.invalidate();
        
        sizeViewport();
        
        if (false) {
            Dimension viewportsize = viewport.getSize();

            //subview.setSize(viewportsize.width, viewportsize.height);
            Dimension requestedsize = subview.getPreferredSize();
            //viewport.setSize(viewportsize.width, requestedsize.height);
            viewport.setPreferredSize(new Dimension(viewportsize.width, requestedsize.height));
            viewport.revalidate();
            //viewport.doLayout();
        }
    }
}