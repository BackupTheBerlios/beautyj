/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.ClassInner
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

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

/**
 * Represents an inner class declaration.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class ClassInner extends Class {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     */
    public Vector myClass;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of ClassInner.
     */
    public ClassInner() {
        super();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Sets the class within which this SourceObjectDeclared is declared.
     */
    public void setDeclaringClass(Class declaring) {
        super.setDeclaringClass(declaring);
        setPackage(declaring.getPackage());
        myImport=declaring.myImport;
    }

    /**
     * Returns the name.
     */
    public String getName() {
        return getDeclaringClass().getName()+"."+getUnqualifiedName();
    }

    /**
     * Gets the unqualified name of this, that means the name without any leading package information.
     */
    public String getUnqualifiedName() {
        return unqualify(name);
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=super.buildXML(d);
        Element outer=d.createElement("outerclass");
        outer.appendChild(d.createTextNode(getDeclaringClass().getName()));
        e.appendChild(outer);
        return e;
    }

    /**
     * Fully qualifies a class identifier, taking into account the current
     * package of this class and the import statements.
     *  
     * @return  The fully qualified class identifier.
     */
    protected String qualifyInternal(String name) {
        return getDeclaringClass().qualifyInternal(name); // will also find inner classes of this
    }

    protected void registerAtPackage(Package p) {
        // overwrites Class.registerAtPackage
        // nop
    }

} // end ClassInner
