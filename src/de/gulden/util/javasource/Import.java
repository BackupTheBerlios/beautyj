/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Import
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
 * Represent an import statement.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class Import extends SourceObject implements ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Import.
     */
    public Import(Package p) {
        myPackage=p;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Fully qualifies a class identifier, if it matches the import statement.
     *  
     * @return  The qualified class identifier, or <code>null</code> if the identifer could not be qualified by this import.
     */
    public abstract String qualify(String name);

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        // overwrites SourceObject.buildXML
        // will be overloaded by subclasses
        String tagName=getXMLName();
        Element e=d.createElement(tagName);
        e.appendChild(d.createTextNode(getName()));
        return e;
    }

    /**
     * Returns the name of the XML tag representing this SourceObject.
     */
    protected String getXMLName() {
        return "import";
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------
    /**
     * Create a new Import-object, specified by the XML element.
     * The created object will either be of type ImportPackage or ImportClass.
     *  
     * @throws IOException if an i/o error occurs
     */
    public static Import createFromXML(Package parent, Element element) throws IOException {
        String name=XMLToolbox.getText(element);//element.getAttribute("name");
        String kind=element.getAttribute("kind");
        boolean isClass=kind.equals("class");
        if (!isClass) {
            return new ImportPackage(parent,name);
        }
        else {
            return new ImportClass(parent,name);
        }
    }

    /**
     * Create a new Import-object.
     * The created object will either be of type ImportPackage or ImportClass.
     */
    static Import createFromAST(Package p, Node node) {
        Import im;
        if (node.getChild(JJT_PACKAGEIMPORT)!=null) // sub-node 'ImportPackage' exists
        {
            im=new ImportPackage(p);
            im.setName(node.getName()+".*");
        }
        else {
            im=new ImportClass(p);
            im.setName(node.getName());
        }
        return im;
    }

} // end Import
