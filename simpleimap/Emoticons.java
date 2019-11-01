/*
 * Emoticons.java
 *
 * Created on January 16, 2003, 7:59 AM
 */

package simpleimap;

import java.util.*;
import java.text.*;
import java.util.regex.*;

/**
 *
 * @author  hassan
 */
public class Emoticons {
    String[] emoticons;
    Hashtable emoticonHash;
    String emoticonsPattern1;
    String emoticonsPattern2;
    
    /** Creates a new instance of Emoticons */
    public Emoticons() {
        this.emoticons = new String[36];
        int i=1;
        this.emoticons[i++] = ":)";
        this.emoticons[i++] = ":(";
        this.emoticons[i++] = ";)"; // wink
        this.emoticons[i++] = ":D"; //  big grin
        this.emoticons[i++] = ";;)"; //  batting eyelashes
        this.emoticons[i++] = ":-/"; //  confused
        this.emoticons[i++] = ":x"; //  love struck
        this.emoticons[i++] = ":\">"; //  blushing
        this.emoticons[i++] = ":p"; //  tongue
        this.emoticons[i++] = ":*"; //  kiss
        this.emoticons[i++] = ":O"; //  shock
        this.emoticons[i++] = "X-("; //  angry
        this.emoticons[i++] = ":>"; //  smug
        this.emoticons[i++] = "B-)"; //  cool
        this.emoticons[i++] = ":-s"; //  worried
        this.emoticons[i++] = ">:)"; //  devilish
        this.emoticons[i++] = ":(("; //  crying
        this.emoticons[i++] = ":))"; //  laughing
        this.emoticons[i++] = ":|"; //  straight face
        this.emoticons[i++] = "/:)"; //  raised eyebrow
        this.emoticons[i++] = "O:)"; //  angel
        this.emoticons[i++] = ":-B"; // nerd
        this.emoticons[i++] = "=;"; //  talk to the hand
        this.emoticons[i++] = "I-)"; //  sleep
        this.emoticons[i++] = "8-|"; //  rolling eyes
        this.emoticons[i++] = ":-&"; //  sick
        this.emoticons[i++] = ":-$"; //  shhh
        this.emoticons[i++] = "[-("; //  not talking
        this.emoticons[i++] = ":o)"; //  clown
        this.emoticons[i++] = "8-}"; //  silly
        this.emoticons[i++] = "(:|"; //  tired
        this.emoticons[i++] = "=P~"; //  drooling
        this.emoticons[i++] = ":-?"; //  thinking
        this.emoticons[i++] = "#-o"; //  d'oh!
        this.emoticons[i++] = "=D>"; //  applause

        this.emoticonHash = new Hashtable();
        
        StringBuffer sb1 = new StringBuffer(); 
        StringBuffer sb2 = new StringBuffer(); 
        
        for(i=1; i<this.emoticons.length; i++) {
            String fn = Integer.toString(i);
            if(fn.length() == 1) fn = "0" + fn;
            
            this.emoticonHash.put(this.emoticons[i], "smileys/" + fn + ".gif");
            String r = this.emoticons[i].replaceAll(">","&gt;");
            //System.out.println("r= " + r);
            if(!r.equals(this.emoticons[i])) this.emoticonHash.put(r, "smileys/" + fn + ".gif");
            
            String e = this.emoticons[i];
            e = e.replaceAll("\\(", "\\\\(");
            e = e.replaceAll("\\)", "\\\\)");
            e = e.replaceAll("\\]", "\\\\]");
            e = e.replaceAll("\\[", "\\\\[");
            e = e.replaceAll("\\|", "\\\\|");
            e = e.replaceAll("\\?", "\\\\?");
            e = e.replaceAll("\\*", "\\\\*");
            e = e.replaceAll("\\$", "\\\\\\$");
            //e = e.replaceAll("\\>", "\\\\>");

            if(i<6) {
                if(i>1) sb1.append("|");
                sb1.append(e);
            } else {
                if(i>6) sb2.append("|");
                sb2.append(e);
            }
        }
        //this.emoticonHash.put("&gt;:)", this.emoticonHash.get(">:)"));
        
        
        this.emoticonsPattern1 = "(" + sb1.toString() + ")";
        this.emoticonsPattern2 = "(" + sb2.toString() + ")";
        this.emoticonsPattern2 = this.emoticonsPattern2.replaceAll(">", "&gt;");

        
    }
    
    public String getPattern() { return this.emoticonsPattern2; }
    public String getImage(String smile) {
        String ret;
        ret = (String) this.emoticonHash.get(smile);
        return ret;
    }
    public String findReplaceEmoticons(String text) {
        Pattern p3 = Pattern.compile(Emoticons.obj.emoticonsPattern2);
        text = this.findReplaceEmoticons(text, p3);
        Pattern p4 = Pattern.compile(Emoticons.obj.emoticonsPattern1);
        text = this.findReplaceEmoticons(text, p4);    
        return text;
    }
    
    public String findReplaceEmoticons(String text, Pattern p3) {
        Matcher m3 = p3.matcher(text);

        StringBuffer sb = new StringBuffer();
        m3.reset();
        boolean result = m3.find();
        while(result) {
            String matchStr = m3.group(1);
            String img = "";
            //System.out.println("matched: " + matchStr);
            img = Emoticons.obj.getImage(matchStr);
            if(img != null) {            
                //System.out.println("found:" + img);
                //System.out.println("foudn:" + matchStr);
                String replacement = "<img src=" + img + ">";
                int s = m3.start();
                int e = m3.end();

                char cs;
                char ce;
                if(s == 0) cs = ' ';
                else cs = text.charAt(s-1);
                if(e == text.length()) ce = ' ';
                else ce = text.charAt(e);

                if(cs == ' ' || cs == '\r' || cs == '\n') {
                    if(ce == ' ' || ce == '\r' || ce == '\n') {
                        m3.appendReplacement(sb, replacement);
                    }
                }
            }
            result = m3.find();
        }
        m3.appendTail(sb);
        
        String ret = sb.toString();
        return ret;
    }
    
    public static Emoticons obj;
    public static void init() {
        obj = new Emoticons();
    }
    
    public static void main(String args[]) throws Exception {
        Emoticons.init();
        
        Pattern p = Pattern.compile(obj.getPattern());
        //String text = "   :)    :\">    >:)   :((  :))    ";
        String text = "   :)    =D&gt;  :-$  &gt;:)  :&gt;  :\"&gt;   ";
        
        text = Emoticons.obj.findReplaceEmoticons(text);

        System.out.println("text=" + text);
        
    }
}
