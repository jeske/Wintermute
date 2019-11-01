/*
 * MultiPattern.java
 *
 * Created on January 16, 2003, 12:23 PM
 */

package simpleimap;

import java.util.*;
import java.text.*;
import java.util.regex.*;
/**
 *
 * @author  hassan
 */
public class MultiPattern {
    private LinkedList patternList;
    private Hashtable patterns;
    String prependStr;
    
    /** Creates a new instance of MultiPattern */
    public MultiPattern() {
        this.patterns = new Hashtable();
        this.patternList = new LinkedList();
        this.prependStr = "";
    }
    
    public void setPrepend(String str) {
        this.prependStr = str;
    }
    
    public void addPattern(String pat, String replacement) {
        this.patterns.put(pat, replacement);
        this.patternList.add(pat);
    }
    
    private Pattern buildPattern() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        int i=0;
        for(Iterator iter=this.patternList.iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            if(i>0) {
                sb.append("|");
            }
            sb.append("(" + key + ")"); 
            i++;
        }
        sb.append(")");
        Pattern pat = Pattern.compile(this.prependStr + sb.toString());
        //System.out.println("pat=" + pat.pattern());
        return pat;
    }
    
    public String replaceAll(String text) {
        Pattern p3 = this.buildPattern();
        Matcher m3 = p3.matcher(text);

        StringBuffer sb = new StringBuffer();
        m3.reset();
        
        while(m3.find()) {
            int n = m3.groupCount();
            //System.out.println("n=" + n);
            String matchStr=null;
            int matchIndex=-1;
            for(matchIndex=2; matchIndex<=n; matchIndex++) {
                String match = m3.group(matchIndex);
                //System.out.println("match #" + matchIndex + "=" + match);
                if(match != null) {
                    matchStr = match;
                    break;
                }
            }
            //System.out.println("matchIndex=" + matchIndex);
            //System.out.println("matchStr=" + matchStr);
            if(matchStr != null) {
                String origPat = (String) this.patternList.get(matchIndex-2);
                String replacement = (String) this.patterns.get(origPat);
                //System.out.println("matched: " + matchStr);
                //String replacement = (String) this.patterns.get(matchStr);
                if(replacement != null) {                   
                    m3.appendReplacement(sb, replacement);
                }
            }
        }
        m3.appendTail(sb);
        
        String ret = sb.toString();
        return ret;
    }    

    public static void main(String args[]) throws Exception {
        
        MultiPattern mp = new MultiPattern();
        mp.addPattern("ab", "AB");
        mp.addPattern("cd", "CD");
        
        String text = "  cd ab cd ef gh    cd";
        
        text = mp.replaceAll(text);

        System.out.println("text=" + text);
        
    }
}
