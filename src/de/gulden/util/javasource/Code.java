/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Code
 * Version: 1.1
 *
 * Date:    2004-09-29
 *
 * Note:    Contains auto-generated Javadoc comments created by BeautyJ.
 *  
 * This is licensed under the GNU General Public License (GPL)
 * and comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 * Email:   beautyj@jensgulden.de
 */

package de.gulden.util.javasource;

import de.gulden.util.javasource.jjt.Node;
import de.gulden.util.javasource.jjt.*;
import de.gulden.util.xml.XMLToolbox;
import de.gulden.util.Toolbox;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents code in a method/constructor body, and in a static class initializer.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public class Code extends Implementation {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------

    /**
     * The raw code string.
     */
    protected String raw;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of Code.
     */
    public Code() {
        super();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the raw code string.
     */
    public String getRaw() {
        return raw;
    }

    /**
     * Sets the raw code string.
     */
    public void setRaw(String s) {
        raw=s;
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=d.createElement("code");
        String raw=getRaw();
        e.appendChild(d.createTextNode(raw));
        return e;
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        raw=XMLToolbox.getText(element);
        if (raw==null) {
            raw="";
        }
    }

    /**
     * Initialize this object from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        // special way of invoking!!!
        // rootnode is rootnode of the _declaration_ of the code block
        // from there, following code will be rebuilt
        // (this is necessary because due to lookahead in constructors, code start would be missing)
        StringBuffer sb=new StringBuffer();
        // find first "{"
        Token t=rootnode.getStartToken();
        while ((t.kind!=ParserConstants.LBRACE)&&(t.kind!=ParserConstants.ASSIGN)) // ASSIGN for field initializers
        {
            t=t.next;
        }
        Token endT;
        if (t.kind==ParserConstants.LBRACE) // code block { ... }
        {
            endT=findCodeEnd(t);
        }
        else // ASSIGN: field initializer
        {
            endT=rootnode.getEndToken();
        }
        raw=rootnode.getTextImage().getRange(t.beginLine,t.beginColumn,endT.endLine,endT.endColumn);
        raw=raw.trim();
        raw=raw.substring(1,raw.length()-1); // remove { } or = ;
        // remove left part if only blanks before first \n:
        int npos = raw.indexOf('\n');
        if (npos != -1) {
        	String left = raw.substring(0, npos);
        	if (left.trim().length()==0) {
        		raw = raw.substring(npos+1);
        	}
        }
        if (raw.indexOf("...fields")!=-1) {
        System.out.println("AAA");
        }
        raw = unindent(raw);
        raw = SourceParser.workaroundRestoreUnicodeSingleChar(raw); // @see SourceParser#parsePass1()
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    static Token findCodeEnd(Token t) {
        int cnt=1;
        while (cnt>0) {
        t=t.next;
        switch (t.kind) {
        case ParserConstants.LBRACE: cnt++;
        break;
        case ParserConstants.RBRACE: cnt--;
        break;
        }
        }
        return t;
    }

    static Token findCodeEndInitializer(Token t) {
        while ((t.kind!=ParserConstants.SEMICOLON)&&(t.kind!=ParserConstants.COMMA)) {
            t=t.next;
            if (t.kind==ParserConstants.LBRACE) {
                t=findCodeEnd(t).next;
            }
        }
        return t;
    }

    /**
     * Creates an unindented representation of a code block by shifting all lines to the left so that
     * the first line will start at column 0. All other lines are shifted relative to the shift of the first line.
     * The specified code block is expected to be passed without enclosing braces.
     *  
     * @return  unindented code
     */
    static String unindent(String code) {
        List l = Toolbox.getLines(code);
        StringBuffer sb = new StringBuffer();
        String nl = System.getProperty("line.separator");
        int shift = -1;
           for (ListIterator it = l.listIterator(); it.hasNext(); ) {
           	String line = (String)it.next();
           	if (line.trim().length()==0) { // blank line
           		if ( it.hasNext() ) { // skip last line if empty, it is just the remaining part until '}'
           			sb.append( nl );
           		}
           	} else {
           		String newLine;
           		if (shift == -1) { // first line
           			newLine = Toolbox.trimLeft(line);
           			shift = line.length() - newLine.length();
           		} else {
           			if (line.length() > shift) {
           				String cutoff = line.substring(0, shift);
           				if (cutoff.trim().length()==0) { // no content would be cut off by shifting: normal shift
           					newLine = line.substring(shift);
           				} else {
           					newLine = Toolbox.trimLeft(line); // otherwise just trim all left whitespace, original relative shift to first line will be lost
           				}
           			} else {
        				newLine = Toolbox.trimLeft(line); // otherwise just trim all left whitespace, original relative shift to first line will be lost
           			}
           		}
           		sb.append(Toolbox.trimRight(newLine));
           		if (it.hasNext() /*&& (
           		     ( !
           		     		( ((String)it.next()).trim().length()==0 ) && ( !it.hasNext() ) // append new line only if not last line or line before an empty last line
         )
         & ( dummy(it.previous()) )
        )  */) {
           			sb.append(nl);
           		}
           	}
           }
           String s = sb.toString();
           if (s.endsWith(nl)) { // truncate very last line-break, if there (very last blank line is rest until '}' and will be auto-generated anyway)
           	s = s.substring(0, s.length()-nl.length());
           }
           return s;
    }

    private static boolean dummy(Object o) {
        // (allows to call it.previous()) from within boolean expression
        return true;
    }

} // end Code
