/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Code
 * Version: 1.0
 *
 * Date:    2002-10-27
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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents code in a method/constructor body, and in a static class initializer.
 *  
 * @author  Jens Gulden
 * @version  1.0
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
        raw=raw.trim();
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

} // end Code
