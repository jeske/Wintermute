/*
 * ItemRDFSource.java
 *
 * Created on January 9, 2003, 5:01 PM
 */

package simpleimap;
import java.io.*;
import java.security.*;
import java.util.Properties;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.*;

import java.net.*;

import java.text.*;

import com.room4me.xml.*;

/**
 *
 * @author  hassan
 */
public class ItemRDFSource extends ItemSync {
     public static final String TypeID = "ItemRDFSource";
 
    /** Creates a new instance of ItemRDFSource */
    public ItemRDFSource() {
        super();
    }
    
     public static void register(SimpleDB db) {
        db.registerTypeFactory(TypeID, new ItemFactory(db) {
            public DefaultItem construct() {
                return new ItemRDFSource();
            }
        });
    }   

    /**
      * Return the content of a text file as a String object.
      */
    private static String getTextFromFile(BufferedReader oIn) throws IOException
    {
        String sLine; //We will read each line into this temporary variable.
        StringBuffer sBuffer = new StringBuffer("");

        while((sLine = oIn.readLine()) != null)
        {
          sBuffer.append(sLine  + "\r\n");
        }

        //Return the stuff as a regular string object.
        return sBuffer.toString();
    }

    
    private String getURL(String url) {
       URLConnection conn; 

        try {
            URL _url = new URL(url);
            conn = _url.openConnection();
            
        } catch (MalformedURLException e) {     // new URL() failed
            throw new RuntimeException("MalformedURLException");
        } catch (IOException e) {               // openConnection() failed
            Debug.debug("openConnection Failed " + url);
            throw new RuntimeException("openConnection Failed");
        }
        BufferedReader in;
        String page;
        try {
            in = new BufferedReader(
            new InputStreamReader(
            conn.getInputStream()));
            page = getTextFromFile(in);
            in.close();
            return page;
            
        } catch (IOException e) {               // openConnection() failed
            throw new RuntimeException("read Failed");
        }
        
    }
     
    public void parseChannel(ArrayList oNodeList, MultiSyncSource items) {
        
        for(Iterator iter=oNodeList.iterator();iter.hasNext();) {
            
            Node node = (Node) iter.next();
            
            PropMap story = new PropMap();    

            ArrayList oChildren = node.getChildNodes();
            Debug.debug("node=" + node.getName());

            String nodeName = node.getName();
            if(nodeName.equals("channel")) {
                this.parseChannel(oChildren, items);
            }
            if(nodeName.equals("item") || nodeName.equals("story")) {

                for(int j=0;j<oChildren.size();j++) {
                    Node child = (Node) oChildren.get(j);
                    story.put(child.getName(), child.getText());
                    Debug.debug("node " + j + " " + child.getName() + " " + child.getText());
                }      

                String storyid = story.get("url");
                if(storyid == null) {
                    storyid = story.get("link");
                }
                if(storyid != null) {
                    story.put("name", storyid);

                    MultiSyncRecord rec = new MultiSyncRecord(storyid, story, 0);
                    items.add(rec);
                }
            }
        }        
    }
    
    
    public MultiSyncSource parseXML(String page) {
       SmallXMLParser oParse; //Declare the parser variable.

        //Debug.debug(page);
        
        //We must check for errors when parsing a document.
        try {
          oParse = new SmallXMLParser(page);
        } catch(Exception e)  {
            e.printStackTrace();
           throw new RuntimeException("SmallXMLParser Failed");
        }
        //Print the parsed document with nice indenting.
        Debug.debug(oParse.getXMLAsText());
        
        
        ArrayList oNodeList;


        
        Node rootnode = oParse.getRootNode();
        oNodeList = rootnode.getChildNodes();

        MultiSyncSource items = new MultiSyncSource();
        
        this.parseChannel(oNodeList, items);
    
        return items;
    }
    
    public void SyncServer() {
        String url = this.get("url");
        
        String page = this.getURL(url);
        
        MultiSyncSource remote_items;
        try {
            remote_items = this.parseXML(page);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // deal with local items
        MultiSyncSource localRecords = new LocalItemSyncSource(this);
        
        MultiSync sync = new MultiSync();
        sync.Sync(remote_items, localRecords);
        
    } 
    
    
    private class LocalItemSyncSource extends MultiSyncSource {
        DefaultItem parent;
        
        public LocalItemSyncSource(DefaultItem parent) {
            super();
            this.parent = parent;
            
            List msg2items = parent.getRelatedItems(WinterMute.containerContainsRelation);
            
            Iterator iter2 = msg2items.iterator();
            while(iter2.hasNext()) {
                DefaultItem item2 = (DefaultItem) iter2.next();
                MultiSyncRecord item3 = new MultiSyncRecord(item2.get("name"), item2, 0);
                this.add(item3);
            }
        }
        
 
        public void notify_addItem(MultiSyncRecord rec) {
            Debug.debug("added item id=<" + rec.getID() + ">");
            DefaultItem newitem = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(newitem == null) {
                newitem = (DefaultItem) WinterMute.my_db.newItem(null, "Default", rec.getID());
                this.parent.relateTo(WinterMute.containerContainsRelation, newitem);
            }

            this.copyKeyValues(rec, newitem);
        }
        
        public void notify_updateItem(MultiSyncRecord rec, DefaultItem olditem) {
            Debug.debug("updated item id=<" + rec.getID() + ">");
            DefaultItem item = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(item != null) {
                this.copyKeyValues(rec, item);
            }
        }
        
        public void notify_removeItem(MultiSyncRecord rec) {
            Debug.debug("removed item id=<" + rec.getID() + ">");
            DefaultItem item = this.parent.getItem(WinterMute.containerContainsRelation, rec.getID());
            if(item != null) {
                this.parent.removeItem(item);
            }
        }
        

    }         
    
}
