/*
 * Wintermute - Personal Data Organizer
 *
 * Copyright (C) 2002, by David Jeske and Neotonic Software Corporation.
 *
 * Written by David Jeske <jeske@neotonic.com>.
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * DataStore.java
 *
 * Created on October 28, 2002, 7:48 AM
 */

package packrat;
import java.io.*;
import java.lang.*;
import java.util.*;

import packrat.*;

/**
 *
 * @author  David Jeske
 */
public class DataStore {
    private String base_path;
    private File base_dir;
    public ClassificationInfo ciTop;
    private int node_id;
    
    
    /** Creates a new instance of DataStore */
    public DataStore() {
        
        base_path = "c:\\pkrt";
        base_dir = new File(base_path);
        if (!base_dir.exists()) {
            base_dir.mkdir();
        }
        
        node_id = 0;
        
        ciTop = new ClassificationInfo(this,"Top");
        createTree();
    }
    
    public int addClassificationInfo(ClassificationInfo node) {
        int new_node_id = node_id++;
        
        
        return new_node_id;
    }
    
    private void createTree() {
        try {
            throw new Exception("foo");
            /*
            FileInputStream in = new FileInputStream("c:\\packrat.tre");
            ObjectInputStream s_in = new ObjectInputStream(in);
            ci_top = (ClassificationInfo)s_in.readObject();
             */
        } catch (Exception e) {
      
            // instantiate starter CI tree...
            ClassificationInfo ci,ci2;
            DefaultItem test_item = new DefaultItem("test");
            
            ci = new ClassificationInfo(this,"People");
            ci.propertyList.add("Name");
            ci.propertyList.add("Email");
            ci.propertyList.add("Address");
            ci.propertyList.add("Phone");
            
            ciTop.addChild(ci);
            ci.addChild(ci2=new ClassificationInfo(this,"Scott Hassan"));
            test_item.addToClassification(ci2);
            ci.addChild(new ClassificationInfo(this,"Brandon Long"));

            
            ci = new ClassificationInfo(this,"Priority");
            ciTop.addChild(ci);
            ci.addChild(new ClassificationInfo(this,"Low"));
            ci.addChild(new ClassificationInfo(this,"Medium"));
            ci.addChild(new ClassificationInfo(this,"High"));
            
            ci = new ClassificationInfo(this,"ReadState");
            ciTop.addChild(ci);
            ci.addChild(new ClassificationInfo(this,"Read"));
            ci.addChild(ci2=new ClassificationInfo(this,"UnRead"));
            test_item.addToClassification(ci2);
            
            
            
        }

        
    }
    
    
    
    public void save() {
        try {
            File tree_filename = new File(base_dir,"packrat.tre");
            FileOutputStream f = new FileOutputStream(tree_filename.getAbsolutePath());
            
           
            ObjectOutputStream  s_out  =  new  ObjectOutputStream(f);
            s_out.writeObject(ciTop);
            s_out.flush();
        } catch (Exception e) {
            // error saving file!   
        }
    }
    
}
