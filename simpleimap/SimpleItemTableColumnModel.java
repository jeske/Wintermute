package simpleimap;

import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;

 ////////////////////////////////// List Table Model ///////////////////////////
    
    public class SimpleItemTableColumnModel extends DefaultTableColumnModel {
        static final int LVL = 3;
        
        DefaultItem save_to = null;
        boolean isActive = false;
       
               
        protected void fireColumnMarginChanged() {
            super.fireColumnMarginChanged();
            this.saveColumns();
        }
        
        protected void fireColumnAdded(TableColumnModelEvent e) {
            super.fireColumnAdded(e);

            Debug.debug(LVL, "added tablecolumnmodelevent: from=" + e.getFromIndex() + " to=" + e.getToIndex());
        }

        protected void fireColumnMoved(TableColumnModelEvent e) {
            super.fireColumnMoved(e);
            if (e.getFromIndex() != e.getToIndex()) {
                Debug.debug(LVL, "moved tablecolumnmodelevent: from=" + e.getFromIndex() + " to=" + e.getToIndex());
                this.saveColumns();
            }
        }
         public void fireColumnRemoved(TableColumnModelEvent e) {
            super.fireColumnRemoved(e);
            Debug.debug(LVL, "removed tablecolumnmodelevent: from=" + e.getFromIndex() + " to=" + e.getToIndex());
        }    
        
        public void clearColumns() {
            this.save_to = null;
            while (this.getColumnCount() > 0) {
                this.removeColumn(this.getColumn(0));
            }
        }
        public void stopWatching() {
            isActive = false;
        }
        
        public void startWatching() {
           isActive = true;
        }
         
        public void loadColumns(DefaultItem from_item) {
            this.save_to = from_item;
            
            Debug.debug(LVL, "------------------ LLLLOOOOAAAADDDDIIIINNNNGGGG CCCCCOOOOOLLLLUUUUMMMMNNNSSSS --");
            try {
                DefaultItem cc = this.save_to;
                if(cc==null) return;
                TableColumnModel cm = this;

                String value = cc.get("columnTableConfig");
                if(value == null) return;
                String[] colstrs = value.split(" ");
                
                int dest_col = 0; // the destination position in the view...
                                  // this differs from "i" only when there are 
                                  // columns added or removed.
                
                for(int i=0; i<colstrs.length; i++) {
                    String[] parts = colstrs[i].split(",");
                    if(parts.length == 2) {
                        int colnum = Integer.parseInt(parts[0]);
                        int colwidth = Integer.parseInt(parts[1]);

                        // ignore extra columns...
                        if (colnum > cm.getColumnCount()) { continue; }
                        
                        int tcolnum = -1;
                        for (int fc=0;fc < cm.getColumnCount();fc++) {
                            if (cm.getColumn(fc).getModelIndex() == colnum) {
                                tcolnum = fc;
                                break;
                            }
                        }
                        // ignore missing columns
                        if (tcolnum == -1) { continue; }
                        
                        if (tcolnum != dest_col) {
                            Debug.debug(LVL, "moving " + tcolnum + " to " + dest_col);
                            try {
                                this.moveColumn(tcolnum, dest_col);
                            } catch (java.lang.IllegalArgumentException e) {
                                Debug.debug(e);
                                return;
                            }
                        }
                        
                        TableColumn tc = cm.getColumn(dest_col);                
                        tc.setPreferredWidth(colwidth);
                        dest_col++;
                    }
                }
            } finally {
                startWatching();
                Debug.debug(LVL, "---loading done --- ");
            }
        }
        
        private void saveColumns() {
            if(this.isActive == false) return;
            
            DefaultItem cc = this.save_to;
            if(cc==null) return;
            
            TableColumnModel cm = this;
            //Debug.debug("columns:");
            StringBuffer s = new StringBuffer();
            
            for(int i=0; i<this.getColumnCount(); i++) {
                TableColumn tc = cm.getColumn(i);                
                int width = tc.getWidth();    
                int j     = tc.getModelIndex();
                //Debug.debug("" + i + "->" + j + "   width=" + width);
                
                if(i!=0) s.append(" ");
                s.append("" + j + "," + width); 
            } 
            String value = s.toString();
            String oldvalue = cc.get("columnTableConfig");
            if(oldvalue == null || !oldvalue.equals(value)) {
                cc.putNoNotify("columnTableConfig", value);
                 Debug.debug(LVL, "columnTableConfig=" + value);
           } else {
                 //Debug.debug("no save: colu\mnTableConfig=" + value);
           }
        }
    
    }