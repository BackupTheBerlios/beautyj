/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.ImportClass
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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Class ImportClass.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class ImportClass extends Import {

    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of ImportClass.
     */
    public ImportClass(Package parent) {
        super(parent);
    }

    /**
     * Creates a new instance of ImportClass.
     */
    ImportClass(Package parent, String name) {
        this(parent);
        setName(name);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Fully qualifies a class identifier, if it matches the import statement.
     *  
     * @return  The qualified class identifier, or <code>null</code> if the identifer could not be qualified by this import.
     */
    public String qualify(String name) {
        if (this.getName().endsWith("."+name)) {
            return this.getName();
        } else {
            return null;
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
        e.setAttribute("kind","class");
        return e;
    }

} // end ImportClass
