/*
 * Thread_ImportCSV.java
 *
 * Created on December 7, 2002, 8:20 PM
 */

package simpleimap;

import java.io.*;
import java.security.*;
import javax.mail.*;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.*;

import java.util.*;

/**
 *
 * @author  hassan
 */
public class Thread_ImportCSV extends Thread {
    File input_file;
    DefaultItem item;
    
    /** Creates a new instance of Thread_ImportCSV */
    public Thread_ImportCSV(DefaultItem item, File input_file) {
        this.input_file = input_file;
        this.item = item;
        this.setDaemon(true);
        String name = this.getClass().getName();
        if(name !=null) this.setName(name);
    }

    public void run() {
        BufferedReader r;
        CSV parser = new CSV();
        try {
            r = new BufferedReader( new InputStreamReader(new FileInputStream(input_file)));
        } catch (FileNotFoundException e) {
            return;
        }

        String line;
        try {
            while ((line = r.readLine()) != null) {
                // add item - swh
                DefaultItem newitem = WinterMute.my_db.newItem(null, "Default", null);
                
                item.addItem(newitem);
                
                Iterator itr = parser.parse(line);
                
                int col_num = 0;
                while (itr.hasNext()) {
                    String value = (String)itr.next();
                    String col_name = "A" +col_num;
                    col_num++;
                    newitem.put(col_name, value);
                }
                

                
            }
        } catch (IOException e2) {
            return;
        }
        
        // item.buildRelationships(WinterMute.containerContainsRelation);
        
    }

}
