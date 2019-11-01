/*
 * YahooIntellisync.java
 *
 * Created on January 5, 2003, 2:15 PM
 */

package simpleimap;

import java.net.*;
import java.util.*;
import java.io.*;
import java.math.*;

import java.text.*;

/**
 *
 * @author  hassan
 */
public class YahooIntellisync {
    final static int kAddAction = 1;
    final static int kRemoveAction = 2;
    final static int kUpdateAction = 4;
    
    private String url;
    private String username;
    private String password;
    
    /** Creates a new instance of YahooIntellisync */
    public YahooIntellisync(String username, String password) {
        this.url = "http://psync.yahoo.com/";
        //this.url = "http://216.136.175.176/";
        this.username = username;
        this.password = password;
        
    }
    
    class UnPackByteBuffer {
        public byte[] data;
        public int i;
        
        public UnPackByteBuffer(byte[] data) {
            this.data = data;
            i = 0;
        }
        
        public boolean eof() {
            if(i >= this.data.length) return true;
            return false;
        }
        
        public int readInt() {
            int j = this.i;
            
            i += 4;
            return ((int)(this.data[j] & 0xff) << 24) + 
            ((int)(this.data[j+1] & 0xff) << 16) + 
            ((int)(this.data[j+2] & 0xff) << 8) + 
            ((int)(this.data[j+3] & 0xff));
        }
        
        public int readWord() {
            int j = i;
            i += 2;
            int a = this.data[j] & 0xff;
            int b = this.data[j+1] & 0xff;
            
            return (a<<8) | b;
        }
        public int readByte() {
            int j = i;
            i++;
            return (int)(this.data[j] & 0xff);
        }

        
        public byte[] readBytes(int n) {
            int j = i;
            i+=n;
            byte[] ret = new byte[n];
            System.arraycopy(this.data, j, ret, 0, n);
            return ret;
        }
        
        public String readString(int n) {
            
            if(n == 0) {
                int k = i;
                i = this.data.length;
                return new String(this.data, k, this.data.length-k);
            }
            
            int j = i;
            i += n;
            return new String(this.data, j, n);
        }
        
    }
    
    class ByteBuffer {
        private StringBuffer b;
        public ByteBuffer() {
            this.b = new StringBuffer();
        }
        
        public int length() {
            return b.length();
        }
        
        public String toString() {
            return b.toString();
        }
        
        public void packByteString(String s) {
            int l = s.length();
            this.b.append((char)(l&0xff));
            this.b.append(s);
        }
        public void pack(String s) {
            this.b.append(s);
        }
        public void packShortString(String s) {
            int l = s.length();
            this.packShort(l);
            this.b.append(s);
        }
        public void packIntString(String s) {
            int l = s.length();
            this.pack(l);
            this.b.append(s);
        }
        public void pack(int i) {
            this.b.append((char)((i>>24) & 0xff));
            this.b.append((char)((i>>16) & 0xff));
            this.b.append((char)((i>>8) & 0xff));
            this.b.append((char)((i>>0) & 0xff));
        }
        public void packShort(int i) {
            this.b.append((char)((i>>8) & 0xff));
            this.b.append((char)((i>>0) & 0xff));
        }
    }
    
    
    
    public String buildHeader(String method, String app) {
        ByteBuffer postdata = new ByteBuffer();
        
        ByteBuffer h = new ByteBuffer();
        h.packShortString(this.username);
        h.packShortString(this.password);
        h.packShort(1);
        h.packShort(1);
        h.packShort(9);
        h.packShort(0);
        

        
        postdata.pack("CAP:");
        postdata.pack("4");
        postdata.packIntString(h.toString());
        
        ByteBuffer clientCmd = new ByteBuffer();
        
        clientCmd.packByteString("Yahoo Desktop Client");
        
        String client_cmd = "";
	
	if(app.equals("calendar")) client_cmd = "\001\001" ;
        if(app.equals("todo")) client_cmd = "\001\002";
        if(app.equals("address")) client_cmd = "\001\003";
        if(app.equals("notes")) client_cmd = "\001\004";
                
        clientCmd.pack(client_cmd);
        
        if(method.equals("dump")) {
            clientCmd.pack(0); // last sync time
            postdata.pack("0");
            postdata.packIntString(clientCmd.toString());
        }
            
        return postdata.toString();
    }
    
    public void println(String s) {
        StringBuffer b = new StringBuffer();
        
        for(int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if(c >= 32 && c <= 126) {
                b.append(c);
            } else {
                int j = c;
                b.append("\\" + Integer.toOctalString(j));
            }
        }
        Debug.debug(b.toString());
    }
    
    private static byte[] getTextFromFile(BufferedInputStream oIn) throws IOException
    {
        LinkedList ret = new LinkedList();
        
        int ch;
        int total=0;
        
        while(true) {
            byte[] cbuf = new byte[8196];

            int n = oIn.read(cbuf, 0, 1024);
            
            if(n  == -1) break;
            
            byte[] nbuf = new byte[n];
            System.arraycopy(cbuf, 0, nbuf, 0, n);
            total+=n;
            ret.add(nbuf); 
        }
        byte[] retbuf = new byte[total];
        Iterator iter = ret.iterator();
        int i=0;
        while(iter.hasNext()) {
            byte[] buf = (byte[]) iter.next();
            System.arraycopy(buf, 0, retbuf, i, buf.length);
            i+=buf.length;
        }
        //Debug.debug("total=" + total);
        //Debug.debug("i=" + i);
        return retbuf;
    }
    
    public byte[] getURL(String url, String postdata) {
        URLConnection conn;
        
        try {
            URL _url = new URL(url);
            conn = _url.openConnection();
            conn.setRequestProperty( "User-Agent", "MyClient" );
            conn.setDoInput( true );
            conn.setDoOutput( true);
            

        } catch (MalformedURLException e) {     // new URL() failed
            throw new RuntimeException("MalformedURLException");
        } catch (IOException e) {               // openConnection() failed
            Debug.debug("openConnection Failed " );
            throw new RuntimeException("openConnection Failed");
        }
        BufferedReader in;
        byte[] page;
        try {
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(postdata.getBytes());
            out.close();
            
            //DataInputStream in2 = new DataInputStream(conn.getInputStream());
            BufferedInputStream in2 = new BufferedInputStream(conn.getInputStream());
            
            page = getTextFromFile(in2);
            in2.close();
            return page;
        } catch (IOException e) {               // openConnection() failed
            e.printStackTrace();
            throw new RuntimeException("read Failed");
        }
    }
    
    public String cleanQuotes(String s) {
        if(s.charAt(0) == '"') s = s.substring(1);
        if(s.charAt(s.length()-1) == '"') s = s.substring(0, s.length()-1);
        return s;
    }
    
    public void parseEntry(String entry, YahooSyncRecord rec) {
        //Debug.debug("entry=" + entry);
        
        
        String[] kvs = entry.split(";");
        for(int i=0; i<kvs.length; i++) {
            String[] kvparts = kvs[i].split(":", 2);
            if(kvparts.length == 2) {
                String key = cleanQuotes(kvparts[0]);
                String val = cleanQuotes(kvparts[1]);
                rec.item.put(key, val);
            }
        }
    }
    
    static public class YahooSyncRecord extends MultiSyncRecord {
        int action; 
        

        public YahooSyncRecord(String id, int lastupdated, int action) {
            super();
            
            this.id = id;
            this.lastupdated = lastupdated;
            this.action = action;
            
            this.item = new PropMap();
            
            this.item.put("name", this.id);
        }
        
        public boolean isDeleted() {
            if(this.action == kRemoveAction) {
                return true;
            }
            return false;
        }  
    }
    
    public MultiSyncSource parse(byte[] page) {
        UnPackByteBuffer b = new UnPackByteBuffer(page);
        String capheader = b.readString(4);
        if(!capheader.equals("CAP:")) {
            this.println("Invalid Header: <" + capheader + ">");
            return null;
        }
        
        int pkt_type = b.readByte();
        
        int l = b.readInt();
        int status = b.readByte();
        String pkt = b.readString(l-1);
        
        Debug.debug("status=" + status);
        if(status != 0) {
            Debug.debug("invalid username/password");
            return null;
        }
        
        int pktflag = b.readByte();
        
        int n = b.readInt();
	String pkt3 = b.readString(n);
        
        //this.println("pkt=" + pkt);
        //Debug.debug("----");

        MultiSyncSource items = new MultiSyncSource();
        
        while(!b.eof()) {
            //Debug.debug("===================================");
            //Debug.debug("b.i=" + b.i);
            
            //String r = new String(b.data, b.i, Math.min(30, b.data.length-b.i));
            //this.println("b=" + r);
            int pktType = b.readByte();
            String cmd;
           
            if(pktType == 1) cmd = "Add";
            else if(pktType == 2) cmd = "Remove";
            else if(pktType == 4) cmd = "Update";
            else cmd = "Unknown";
            
            
            
            int k = b.readInt();
            //Debug.debug("k=" + k);
            if(k<=0) return items;
            if(k>1000) return items;

            byte[] pkt2 = b.readBytes(k);
            //this.println("PKT=" + pkt2);
            
            if(true) {
                UnPackByteBuffer b2 = new UnPackByteBuffer(pkt2);

                int t = b2.readInt();
                //Debug.debug("LASTUPDATE:" + t);

                int idlen = b2.readByte();
                String id = b2.readString(idlen);

                //Debug.debug("ID=" + id);
                String entry = b2.readString(0);
                YahooSyncRecord rec = new YahooSyncRecord(id, t, pktType);
                
                this.parseEntry(entry, rec);
                

                
                rec.item.put("__LASTUPDATE", "" + t);
                rec.item.put("__ID", id);
                
                items.add(rec);
            }
        }
        return items;
    }
    
    public String bytesToString(byte[] b, int n) {
        try{
        return new String(b, 0, n, "US-ASCII");
        }catch(Exception e) {
        }
        return null;
    }
    
    public MultiSyncSource Sync(String app) {
        String postdata = this.buildHeader("dump", app);
        this.println("postdata=<" + postdata + ">");
        
        String url = "http://psync.yahoo.com/";
        
        byte[] page = getURL(url, postdata);

        String page2 = this.bytesToString(page, page.length);
        this.println("page=<" + page2.substring(0, Math.min(page2.length(), 1000)) + ">");
        MultiSyncSource items = this.parse(page);

        return items;
    }
    public MultiSyncSource SyncCalendar() {
        return Sync("calendar");
    }
    public MultiSyncSource SyncAddressBook() {
        return Sync("address");
    }
    public MultiSyncSource SyncNotes() {
        return Sync("notes");
    }
    public MultiSyncSource SyncTodo() {
        return Sync("todo");
    }
    
    public void displayItems(MultiSyncSource records) {
        Debug.debug("items length=" + records.items.size());
        
        Enumeration e = records.items.keys();
        while(e.hasMoreElements()) {
            String id = (String) e.nextElement();
            Hashtable item = (Hashtable) records.items.get(id);
            
            Enumeration e2 = item.keys();
            Debug.debug("ID=" + id);
            while(e2.hasMoreElements()) {
                String key = (String) e2.nextElement();
                String val = (String) item.get(key);  
                if(val.length() > 0)
                    Debug.debug("  " + key + "=" + val);
            }
            Debug.debug("--------------");
        }
    }
    
    
    public static void main(String args[]) throws Exception {
        Debug.debug("testing...");
        
        if(false) {
            YahooIntellisync yi = new YahooIntellisync("scotthassan", "xgosh");
            //Hashtable items = yi.SyncCalendar();
            MultiSyncSource items = yi.SyncAddressBook();
            yi.displayItems(items);
        }
    }
}

