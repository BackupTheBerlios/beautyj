/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Field
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
 * Represents a field definition.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class Field extends Member implements Typed {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The type.
     */
    protected Type type;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Field.
     */
    public Field(Class c) {
        super(c);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
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
        e.insertBefore(type.buildXML(d),e.getFirstChild());
        if (getCode()!=null) {
            Element ee=d.createElement("initializer");
            ee.appendChild(getCode().buildXML(d));
            e.appendChild(ee);
        }
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
        
        // type
        Element ty=XMLToolbox.getChildRequired(element,"type");
        type=new Type(this);
        type.initFromXML(ty);
        
        // field initializer
        Element in=XMLToolbox.getChild(element,"initializer");
        if (in!=null) {
            code=new Code();
            code.initFromXML(XMLToolbox.getChildRequired(in,"code"));
        }
        else {
            code=null;
        }
    }

    /**
     * Initialize this object from parsed Java code.
     * Special way of invoking.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode, Node varnode) {
        super.initFromAST(rootnode);
        String className=getDeclaringClass().getName();
        name=className+"."+varnode.getName();
        
        type=new Type(this);
        type.initFromAST(rootnode); // special way of invoking (using rootnode)
        
        Node codenode=varnode.getChild(JJT_CODE);
        if (codenode!=null) // field has initializer
        {
            code=new Code();
            code.initFromAST(varnode); // special way of invoking
        }
        else {
            code=null;
        }
    }

} // end Field
