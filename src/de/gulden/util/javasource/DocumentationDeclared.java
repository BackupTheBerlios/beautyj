/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.DocumentationDeclared
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
 * Class DocumentationDeclared.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class DocumentationDeclared extends Documentation {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The my documentation tagged.
     */
    public Vector myDocumentationTagged;


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
     * Removes a all tags.
     */
    public void removeAllTags() {
        myDocumentationTagged.removeAllElements();
    }

    /**
     * Extends Documentation.setRaw(String raw).
     */
    public void setRaw(String raw) {
        super.setRaw(raw);
        try {
            Node docnode=JavadocParser.parse(raw);
            text=getTextFromNode(docnode.getChild(JavadocParserTreeConstants.JJTDESCRIPTION)).trim();
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
        }
        catch (TokenMgrError te) {
            text="... warning: could not parse javadoc comment ...";
            System.err.println("warning: could not parse javadoc comment, warning message inserted instead");
        }
        catch (ParseException pe) {
            text="... warning: could not parse javadoc comment ...";
            System.err.println("warning: could not parse javadoc comment, warning message inserted instead");
        }
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
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Returns the text from node.
     */
    static String getTextFromNode(Node n) {
        StringBuffer sb=new StringBuffer();
        Node[] lines=n.getChildren(JavadocParserTreeConstants.JJTLINE);
        for (int i=0;i<lines.length;i++) {
            Node[] words=lines[i].getChildren(JavadocParserTreeConstants.JJTWORD);
            for (int j=0;j<words.length;j++) {
                sb.append(words[j].getValue());
                if (j<words.length-1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

} // end DocumentationDeclared
