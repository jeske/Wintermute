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
 * MyDbt.java
 *
 * Created on October 29, 2002, 4:46 PM
 */

package simpleimap;

import com.sleepycat.je.*;
import java.io.*;

/**
 *
 * @author  David Jeske
 */
public class MyDbt extends DatabaseEntry {
    byte[] holder;
        
    public MyDbt() {
        super();
        
        // set_flags(Db.DB_DBT_MALLOC);
    }
    /** Creates a new instance of MyDbt */
    public MyDbt(String str) {
        super();
        // set_flags(Db.DB_DBT_MALLOC);
        // set_string(str);
        this.setString(str);
    }
        
    public void setString(String str) {
        try {
            holder = str.getBytes("UTF-8");
            setData(holder);
            setSize(holder.length);
        } catch (java.io.UnsupportedEncodingException e) {
            System.exit(1);
        }
    }
    
    
    
    public MyDbt(Object o) throws java.io.IOException {
        setObject(o);
    }
        
    public void setObject(Object o) throws java.io.IOException {
       ByteArrayOutputStream ba = new ByteArrayOutputStream();
       ObjectOutputStream  s_out  =  new  ObjectOutputStream(ba);
       s_out.writeObject(o);
       s_out.flush();
       s_out.close();
       holder = ba.toByteArray();
       this.setData(holder);
       this.setSize(holder.length);
    }
    
    public String getString() {
        try {
            return new String(this.getData(),"UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            System.exit(1);
            return "";
        }
    }
    
    public Object getObject() throws java.io.IOException, java.lang.ClassNotFoundException {
        ByteArrayInputStream ba = new ByteArrayInputStream(this.getData());
        ObjectInputStream s_in = new ObjectInputStream(ba);
        return s_in.readObject();
    }
    
    public String toString() {
        String a_str = "";
        byte[] data = getData();
        
        int len = data.length;
        
        if (len > 40) {
            len = 40;
        }
        
        for(int i=0;i<len;i++) {
            a_str = a_str + (char)data[i];
        }
        
        return a_str;
    }
    
}
