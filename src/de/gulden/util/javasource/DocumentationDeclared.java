/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.DocumentationDeclared
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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Class DocumentationDeclared.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public class DocumentationDeclared extends Documentation {

    // ------------------------------------------------------------------------
    // --- final static field                                               ---
    // ------------------------------------------------------------------------

    /**
     * Constant workaroundReplaceSingleStar.
     */
    private static final String workaroundReplaceSingleStar = "###SINGLE-STAR###";


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The my documentation tagged.
     */
    public Vector myDocumentationTagged;

    /**
     * The my source object declared.
     */
    public SourceObjectDeclared mySourceObjectDeclared;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of DocumentationDeclared.
     */
    public DocumentationDeclared() {
        myDocumentationTagged=new Vector();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the tags.
     */
    public Enumeration getTags() {
        return myDocumentationTagged.elements();
    }

    public DocumentationTagged findTag(String tag, String item) {
        for (Enumeration e=myDocumentationTagged.elements();e.hasMoreElements();) {
            DocumentationTagged dt=(DocumentationTagged)e.nextElement();
            if (
                dt.getTag().equals(tag)
                &&
                (
                    ( (item==null) && (dt.getItem()==null) )
                    ||
                    ( (item!=null) && (dt.getItem()!=null) && (item.equals(dt.getItem())) )
                )
            ) {
                return dt;
            }
        }
        return null;
    }

    /**
     * Adds a tag.
     */
    public void addTag(DocumentationTagged tag) {
        myDocumentationTagged.addElement(tag);
    }

    /**
     * Removes a tag.
     */
    public void removeTag(DocumentationTagged tag) {
        myDocumentationTagged.removeElement(tag);
    }

    /**
     * Removes all tags.
     */
    public void removeAllTags() {
        myDocumentationTagged.removeAllElements();
    }

    /**
     * Returns the source object declared.
     */
    public SourceObjectDeclared getSourceObjectDeclared() {
        return mySourceObjectDeclared;
    }

    /**
     * Sets the source object declared.
     */
    public void setSourceObjectDeclared(SourceObjectDeclared sourceObjectDeclared) {
        this.mySourceObjectDeclared = sourceObjectDeclared;
    }

    /**
     * Extends Documentation.setRaw(String raw).
     */
    public void setRaw(String raw) {
                super.setRaw(raw);
                try {
                	raw = raw.trim();
                	// workaround 1: a Javadoc-comment might end with ***..**/ (multiple stars). Remove all additional stars so that comments ends cleanly with "*/" (otherwise stars will be seen as part of the comment text, which is very most likely not intended in such case)
                	while (raw.charAt(raw.length()-3)=='*') {
                		raw = raw.substring(0, raw.length()-3) + "*/";
                	}
                	// workaround 2: Javadoc-parser fails if no blank before ending */, so insert if needed (will not distort parse results, will be trimmed anyway)
                	if (raw.charAt(raw.length()-3)!=' ') {
                		raw = raw.substring(0, raw.length()-2) +" */";
                	}
                	// workaround 3: avoid single " * " occurrences in Javadoc, replace now and replace-back later
                	raw = workaroundAvoidSingleStar(raw);
                	// parse
                    Node docnode=JavadocParser.parse(raw);
                    this.text = getTextFromNode(docnode.getChild(JavadocParserTreeConstants.JJTDESCRIPTION)).trim();
                    Node[] tags=docnode.getChildren(JavadocParserTreeConstants.JJTTAG);
                    for (int i=0;i<tags.length;i++) {
                        String tag;
                        String tagItem;
                        String tagText;
                        tag=tags[i].getValue();
                        Node itemNode=tags[i].getChild(JavadocParserTreeConstants.JJTTAGITEM);
                        if (itemNode!=null) {
                            tagItem=itemNode.getChild(JavadocParserTreeConstants.JJTWORD).getValue();
                        }
                        else {
                            tagItem=null;
                        }
                        tagText=getTextFromNode(tags[i]).trim();
                        DocumentationTagged dt=new DocumentationTagged();
                        dt.setTag(tag);
                        dt.setItem(tagItem);
                        dt.setText(tagText);
                        this.addTag(dt);
                    }
                    return;
                }
                catch (TokenMgrError te) {
                	// fallthrough
                }
                catch (ParseException pe) {
                	// fallthrough
                }
                text="... warning: could not parse javadoc comment ...";
                String msg = "warning: could not parse javadoc comment, warning message inserted instead";
                SourceObjectDeclared sourceObjectDeclared = getSourceObjectDeclared();
                if (sourceObjectDeclared != null) {
                	Class declaringClass = sourceObjectDeclared.getDeclaringClass();
                	String n = sourceObjectDeclared.getName();
                	msg += " ["+ ( declaringClass!=null ? declaringClass.getName()+(n!=null?".":"") : "") + (n!=null?n:"") + "]";
                }
                System.err.println(msg);
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The corresponding XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        // to be extended (not overwritten) by subclasses
        name=element.getAttribute("name"); // adjust name
        setText(XMLToolbox.getText(element));
        myDocumentationTagged.removeAllElements();
        NodeList nl=XMLToolbox.getChildren(element,"tag");
        for (int i=0;i<nl.getLength();i++) {
            DocumentationTagged dt=new DocumentationTagged();
            dt.initFromXML((Element)nl.item(i));
            myDocumentationTagged.addElement(dt);
        }
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=super.buildXML(d);
        for (Enumeration ee=getTags();ee.hasMoreElements();) {
            DocumentationTagged dt=(DocumentationTagged)ee.nextElement();
            String tag=dt.getTag();
            if (!(tag.equals("@param") // these are handled at the referenced SourceObjectDeclared's
            ||tag.equals("@exception")
            ||tag.equals("@throws")
            )) {
                e.appendChild(dt.buildXML(d));
            }
        }
        return e;
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    protected static String workaroundAvoidSingleStar(String s) {
        int pos = s.indexOf('*');
        while (pos != -1) {
        	if (s.charAt(pos+1)!='/') { // not the very end of the comment
        		int linestart = s.lastIndexOf(nl, pos) + 1; // will result in 0 for 'not found' which is wanted
        		String beforeStar = s.substring(linestart, pos);
        		//if (containsText(beforeStar)) { // found a star to replace
        		if ((beforeStar.indexOf('*')!=-1) && (!beforeStar.equals("/*"))) {
        			s = s.substring(0, pos) + workaroundReplaceSingleStar + s.substring(pos+1);
        			return workaroundAvoidSingleStar(s);
        		}
           		pos = s.indexOf('*', pos+1);
        	} else {
        		pos = -1;
        	}
        }
        return s;
    }

    protected static String workaroundRestoreSingleStar(String s) {
        int pos = s.indexOf(workaroundReplaceSingleStar);
        if (pos != -1) {
        	return s.substring(0, pos) + "*" + workaroundRestoreSingleStar(s.substring(pos+workaroundReplaceSingleStar.length()));
        } else {
        return s;
        }
    }

    /**
     * Returns the text from node.
     */
    private static String getTextFromNode(Node n) {
        StringBuffer sb=new StringBuffer();
        Node[] lines=n.getChildren(JavadocParserTreeConstants.JJTLINE);
        for (int i=0;i<lines.length;i++) {
            Node[] words=lines[i].getChildren(JavadocParserTreeConstants.JJTWORD);
            for (int j=0;j<words.length;j++) {
                sb.append( workaroundRestoreSingleStar( words[j].getValue() ) );
                if (j<words.length-1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

} // end DocumentationDeclared
