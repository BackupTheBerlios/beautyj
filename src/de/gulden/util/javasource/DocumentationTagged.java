/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.DocumentationTagged
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

import de.gulden.util.xml.XMLToolbox;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents a Javadoc tag, like e.g. "@param".
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class DocumentationTagged extends Documentation {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The my documentation declared.
     */
    public Vector myDocumentationDeclared;

    /**
     * The tag.
     */
    protected String tag;

    /**
     * The item.
     */
    protected String item;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag.
     */
    public void setTag(String tag) {
        this.tag=tag;
    }

    /**
     * Returns the item.
     */
    public String getItem() {
        return item;
    }

    /**
     * Sets the item.
     */
    public void setItem(String item) {
        this.item=item;
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=d.createElement("tag");
        e.setAttribute("type",getTag());
        e.appendChild(d.createTextNode(getText()));
        return e;
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        String type=element.getAttribute("type");
        String item=element.getAttribute("item");
        String text=XMLToolbox.getText(element);
        setTag(type);
        setItem(item); // may be null
        setText(text);
    }

} // end DocumentationTagged
