/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Documentation
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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

/**
 * Class Documentation.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class Documentation extends Implementation implements ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The text.
     */
    protected String text;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Documentation.
     */
    public Documentation() {

    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     */
    public void setText(String s) {
        text=s;
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
                Element e=d.createElement("documentation");
        e.appendChild(d.createTextNode(getText()));
        return e;
    }

    public String toString() {
        return "documentation "+getText();
    }

} // end Documentation
