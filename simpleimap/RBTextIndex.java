/*
 * RBTextIndex.java
 *
 * Text Index
 * Relation Builder
 *
 * This module is available to perform text-indexing of fields and
 * data bodies. Searches performed then build new nodes with a
 * relation to existing OIDs.
 *
 * Created on January 18, 2003, 9:53 AM
 */

package simpleimap;

import java.util.*;
import java.io.*;

// for indexing
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

// for Query
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;

import org.apache.lucene.document.*;

/**
 *
 * @author  jeske
 */
public class RBTextIndex {
    RBTextIndexThread indexworker;
    RBTextQueryThread queryworker;

    public static final String INDEX_LOCATION = "/c:/neo/db/index";
    
    public void finalize() {
        indexworker.cleanlyclose();
        boolean unjoined = true;
        while (unjoined) {
            try {
                indexworker.join();
                unjoined = false;
            } catch (java.lang.InterruptedException e) {
                // pass
                Debug.debug(e);
            }
        }
    }
    
    /** Creates a new instance of RBTextIndex */
    public RBTextIndex() {
        // kill off the lockfile!
        File lockfile = new File(INDEX_LOCATION + "/write.lock");
        if (lockfile.exists()) {
            lockfile.delete();
        }
        
        indexworker = new RBTextIndexThread();
        queryworker = new RBTextQueryThread();
    }
    
    public void start() {
        // this is decoupled to make sure we don't try to make the index
        // directory before the DB directory is created!
        indexworker.start();
        queryworker.start();
    }
    
    public void indexItem(DefaultItem item) {
        indexworker.enqueue(item);
    }
    
    public DefaultItem searchFor(String query) {
        DefaultItem resultsitem = WinterMute.my_db.newItem(null,"Default", null);
        resultsitem.put("icon_path","images/search.png");
        resultsitem.put("name","Search: " + query);
        resultsitem.put("default_viewer","folder_viewer");
        queryworker.enqueue(resultsitem,query);
        return resultsitem;
    }
    
    class RBTextQueryThread extends Thread {
        List toQuery;
        boolean keep_running = true;
        RBTextQueryThread() {
            this.setDaemon(true);
            this.setName("RBTextIndex.Query");
            toQuery = new LinkedList();
        }
        private class QueryData {
            public DefaultItem resultsitem;
            public String query;
            QueryData(DefaultItem ri,String q) {
                this.resultsitem = ri;
                this.query = q;
            }
        };
        
        public void enqueue(DefaultItem resultsitem,String query) {
            synchronized (toQuery) {
                toQuery.add(new QueryData(resultsitem,query));
            }
        }
        
        public void run() {
            Debug.debug("awake...");
            
            while (keep_running) {
                QueryData current = null;
                synchronized (toQuery) {
                    if (toQuery.size() > 0) {
                        current = (QueryData) toQuery.get(0);
                        toQuery.remove(0);
                    }
                }
                if (current == null) { 
                    try { 
                        this.sleep(2000); 
                    } catch (Exception e) { 
                        /* pass */ 
                    }
                    
                    continue;
                }
                Debug.debug("wakeup.");
                
                // preform the query
                Searcher searcher = null;
                try {
                    searcher = new IndexSearcher(INDEX_LOCATION);
                } catch (IOException e) {
                    Debug.debug(e);
                    continue;
                }
                try {
                    Analyzer analyzer = new StandardAnalyzer();
                    Query query = QueryParser.parse(current.query, "contents", analyzer);

                    Hits hits = searcher.search(query);
                    Debug.debug("Found " + hits.length() + " hits for query: " + current.query);
                    
                    for (int x=0;x<hits.length();x++) {
                        Document doc = hits.doc(x);
                        try {
                            int oid = Integer.parseInt(doc.get("OID"));

                            // add to resultset
                            DefaultItem matched = WinterMute.my_db.getItem(oid);
                            current.resultsitem.relateTo(WinterMute.containerContainsRelation,matched);
                        } catch (Exception e) {
                            Debug.debug(e);
                        }
                    }
                } catch (Exception e) {
                    Debug.debug(e);
                }

            }
        }
        
    };
    
    class RBTextIndexThread extends Thread {
        List toIndex;
        boolean keep_running = true;
        IndexWriter writer = null;
        RBTextIndexThread() {
           // this.setDaemon(true);
           this.setName("RBTextIndex.Index");
            
           toIndex = new LinkedList();
        }
        
        public void enqueue(DefaultItem item) {
            synchronized (toIndex) {
                toIndex.add(item);
            }
        }
        public void finalize() {
            if (writer != null) {
                System.out.println("index close");
                try {
                    writer.close();
                } catch (IOException e) {
                    Debug.debug(e);
                }
                writer = null;
            }
        }
        public void cleanlyclose() {
            keep_running = false;
            this.interrupt();
        }
        
        public void run() {
            Debug.debug("awake...");
            
            try {
                if (!(new File(INDEX_LOCATION)).isDirectory()) {
                    // create if missing!
                    writer = new IndexWriter(INDEX_LOCATION, new StandardAnalyzer(), true);     
                } else {
                    writer = new IndexWriter(INDEX_LOCATION, new StandardAnalyzer(), false);     
                }
            } catch (Exception e) {
                Debug.debug(e);
                return;
            }
            
            boolean need_optimize = false;
            while (keep_running) {
                synchronized (toIndex) {
                    if (toIndex.size() > 0) {
                        
                        for (Iterator itr=toIndex.iterator();itr.hasNext();) {
                            DefaultItem item = (DefaultItem)itr.next();
                            itr.remove();
                            if (item.getData() != null) {
                                // create index document
                                Document doc = new Document();
                                try {
                                    String body_text = new String(item.getData(),"US-ASCII");
                                    Field body = new Field("contents", body_text, false,true,true);
                                    doc.add(body);
                                    doc.add(new Field("OID", "" + item.get_oid(), true,true,false));
                                    writer.addDocument(doc);
                                    need_optimize = true;
                                    Debug.debug("added OID: " + item.get_oid());
                                } catch (UnsupportedEncodingException e) {
                                    Debug.debug(e); // during string decode
                                } catch (IOException e) {
                                    Debug.debug(e); // during encode
                                }
                            }
                        }
                    }
                }
                
                if (need_optimize) {
                    try {
                        writer.optimize();
                    } catch (IOException e) {
                        Debug.debug(e);
                    }
                    need_optimize = false;
                }
                try {
                    this.sleep(2000);                
                } catch (java.lang.InterruptedException e) {
                    // pass
                    // Debug.debug(e);
                }
                
            }
            try {
                writer.close();
            } catch (Exception e) {
                Debug.debug(e);
            }
            Debug.debug("done. (closed)");
        }
    };
}
