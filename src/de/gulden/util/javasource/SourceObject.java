/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.SourceObject
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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * This is the common super-class of all classes in this package which represent Java source code element declarations.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public abstract class SourceObject implements Named, Serializable, ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    /**
     * The nl.
     */
    public static String nl = System.getProperty("line.separator");


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The my package.
     */
    public Package myPackage;

    /**
     * The name.
     */
    protected String name;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String n) {
        name=n;
    }

    /**
     * Gets the unqualified name of this, that means the name without any leading package information.
     */
    public String getUnqualifiedName() {
        return unqualify(getName());
    }

    /**
     * Returns the package of which this is a member.
     */
    public Package getPackage() {
        return myPackage;
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The corresponding XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        name=element.getAttribute("name");
        // to be extended (not overwritten) by subclasses
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        // must be extended by subclasses (i.e. overwritten starting with ...=super.buildXML(..))
        String tagName=getXMLName();
        Element e=d.createElement(tagName);
        e.setAttribute("name",getName());
        return e;
    }

    /**
     * Returns the full signature of this SourceObject. This is the fully
     * qualified name and, depending on the type of the SourceObject,
     * the (return-)type, and the parameters' signatures
     */
    public String getSignature() {
        // will be extended by subclasses
        return getUnqualifiedName();
    }

    /**
     * Tests if two source objects represent the same source code element.
     * The test is performed based on the fully referenced signature strings.
     * Executing this is quite expensive, so consider testing for reference identity (a==b) where possible.
     *  
     * @return  <code>true</code> if equal.
     */
    public boolean equals(Object o) {
        return o.getClass().equals(this.getClass())
        && ((SourceObject)o).getSignature().equals(this.getSignature());
    }

    public String toString() {
        return getXMLName()+" "+getSignature();
    }

    /**
     * Returns the name of the XML tag representing this SourceObject.
     */
    protected String getXMLName() {
        return SourceObjectDeclaredVisible.unqualify(this.getClass().getName()).toLowerCase(); // dynamically get tag name from class name (!)
    }

    /**
     * Initialize this object with values from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        // get name
        name=rootnode.getName();
        // to be extended (not overwritten) by subclasses
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    /**
     * Returns the unqualified name part of the string.
     *  
     * @param n The qualified name.
     * @return  The name's part after the last occurrence of '.', or the unchanged name if no '.' is contained.
     */
    static String unqualify(String n) {
        int pos=n.lastIndexOf('.');
        if (pos!=-1) {
            return n.substring(pos+1);
        }
        else {
            return n;
        }
    }

    /**
     * Returns a less qualified name part of the string, including withParents parents, too.
     *  
     * @param n The qualified name.
     * @return  The name's part after the withParent's-last occurrence of '.', or the unchanged name if no '.' is contained.
     */
    static String unqualify(String n, int withParents) {
        int pos=n.lastIndexOf('.');
        while ((pos > 0) && (withParents > 0)) {
        	pos = n.lastIndexOf('.', pos-1);
        	withParents--;
        }
        if (pos != -1) {
            return n.substring(pos+1);
        } else {
            return n;
        }
    }

    /**
     * Tool method for outputting a string to an OutputStream.
     *  
     * @throws IOException if an i/o error occurs
     */
    static void write(OutputStream out, String s) throws IOException {
        out.write(s.getBytes());
    }

    /**
     * Tool method for converting an array to a Vector.
     */
    static void stringsIntoVector(Object[] s, Vector v) {
        v.removeAllElements();
        for (int i=0;i<s.length;i++) {
            v.addElement(s[i]);
        }
    }

} // end SourceObject
