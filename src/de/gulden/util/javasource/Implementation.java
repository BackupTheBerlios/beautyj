/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Implementation
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

import org.w3c.dom.*;
import de.gulden.util.javasource.jjt.Node;
import java.util.*;

/**
 * Class Implementation.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class Implementation extends SourceObject {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The raw.
     */
    protected String raw;


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
        return super.buildXML(d); // no own action
    }

    /**
     * Initialize this object from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        // no own action
    }

} // end Implementation
