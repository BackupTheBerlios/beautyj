/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Exception
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
 * Represents a "throws" declaration, thrown by a method/constructor.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class Exception extends SourceObjectDeclared {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The my member executable.
     */
    public MemberExecutable myMemberExecutable;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Exception.
     */
    public Exception(MemberExecutable parent) {
        myMemberExecutable=parent;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=d.createElement("throws");
        e.setAttribute("exception",getName());
        return e;
    }

    /**
     * Overwrites SourceObjectDeclared.getDocumentation().
     */
    public Documentation getDocumentation() {
        DocumentationDeclared dd=(DocumentationDeclared)myMemberExecutable.getDocumentation();
        if (dd!=null) {
            return dd.findTag("@exception",myMemberExecutable.getDeclaringClass().qualify(this.getName()));
        }
        else {
            return null;
        }
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        // to be extended (not overwritten) by subclasses
        super.initFromXML(element);
        name=element.getAttribute("exception");
        Parameter.addDocumentationToMember((DocumentationDeclared)getDocumentation(),myMemberExecutable);
    }

    /**
     * Initialize this object from parsed Java code.
     */
    void initFromAST(Node namenode) {
        String name=namenode.retrieveName();
        setName(myMemberExecutable.getDeclaringClass().qualify(name));
    }

} // end Exception
