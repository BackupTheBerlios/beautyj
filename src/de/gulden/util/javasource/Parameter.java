/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Parameter
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
import java.io.*;
import java.util.*;

/**
 * Represents a parameter declaration.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class Parameter extends SourceObjectDeclared implements Typed, ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The executable member to which this parameter belongs.
     */
    public MemberExecutable myMemberExecutable;

    /**
     * The name.
     */
    protected String name;

    /**
     * The type.
     */
    protected Type type;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Parameter.
     */
    public Parameter(MemberExecutable parent) {
        myMemberExecutable=parent;
    }


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
     * Returns the type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(Type t) {
        type=t;
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
                Element e=super.buildXML(d);
        e.appendChild(getType().buildXML(d));
        return e;
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
        name=element.getAttribute("name"); // adjust name
        addDocumentationToMember((DocumentationDeclared)getDocumentation(),myMemberExecutable);
    }

    /**
     */
    public Documentation getDocumentation() {
        // overwrites SourceObjectDeclared.getDocumentation().
        DocumentationDeclared dd=(DocumentationDeclared)myMemberExecutable.getDocumentation();
        if (dd!=null) {
            return dd.findTag("@param",this.getName());
        }
        else {
            return null;
        }
    }

    /**
     * Initialize this object from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        name=rootnode.getName();
        type=new Type(myMemberExecutable);
        type.initFromAST(rootnode); // special way of invoking (using rootnode)
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Adds a documentation to member.
     */
    static void addDocumentationToMember(DocumentationDeclared d, MemberExecutable mex) {
        if (d!=null) {
            DocumentationDeclared dd;
            if (mex.getDocumentation()==null) {
                dd=new DocumentationDeclared();
                dd.setText("");
                mex.setDocumentation(dd);
            }
            else {
                dd=(DocumentationDeclared)mex.getDocumentation();
            }
            for (Enumeration e=d.getTags();e.hasMoreElements();) {
                DocumentationTagged dt=(DocumentationTagged)e.nextElement();
                dd.addTag(dt);
            }
        }
    }

} // end Parameter
