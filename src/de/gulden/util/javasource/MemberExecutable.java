/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.MemberExecutable
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
 * Class MemberExecutable.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class MemberExecutable extends Member {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The parameters of this executable member.
     */
    public Vector myParameter;

    /**
     * The exceptions thrown by this executable member.
     */
    public Vector myException;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of MemberExecutable.
     */
    protected MemberExecutable(Class c) {
        super(c);
        myParameter=new Vector();
        myException=new Vector();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the parameters.
     */
    public NamedIterator getParameters() {
        return new NamedIterator(myParameter);
    }

    /**
     * Returns the exceptions.
     */
    public NamedIterator getExceptions() {
        return new NamedIterator(myException);
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
                Element e=super.buildXML(d);
        NamedIterator it=getParameters();
        while (it.hasMore()) {
            Parameter p=(Parameter)it.next();
            e.appendChild(p.buildXML(d));
        }
        it=getExceptions();
        while (it.hasMore()) {
            Exception exc=(Exception)it.next();
            e.appendChild(exc.buildXML(d));
        }
        if (code!=null) {
            e.appendChild(code.buildXML(d));
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
        
        // parameters
        myParameter.removeAllElements();
        NodeList nl=XMLToolbox.getChildren(element,"parameter");
        for (int i=0;i<nl.getLength();i++) {
            Parameter pa=new Parameter(this);
            pa.initFromXML((Element)nl.item(i));
            myParameter.addElement(pa);
        }
        
        // exceptions
        myException.removeAllElements();
        nl=XMLToolbox.getChildren(element,"throws");
        for (int i=0;i<nl.getLength();i++) {
            Exception ex=new Exception(this);
            ex.initFromXML((Element)nl.item(i));
            myException.addElement(ex);
        }
        
        // code
        Element co=XMLToolbox.getChild(element,"code");
        if (co!=null) {
            code=new Code();
            code.initFromXML(co);
        }
        else {
            code=null;
        }
    }

    /**
     * Returns the parameter signature.
     */
    public String getParameterSignature() {
        StringBuffer pb=new StringBuffer();
        boolean first=true;
        for (NamedIterator it=getParameters();it.hasMore();) {
            Parameter p=(Parameter)it.next();
            if (!first) {
                pb.append(",");
            }
            else {
                first=false;
            }
            pb.append(p.getType().getFullTypeName());
        }
        return "("+pb.toString()+")";
    }

    /**
     * Get the full signature of this executable member.
     */
    public String getSignature() {
        return super.getSignature()+getParameterSignature();
    }

    /**
     * Initialize this object from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        super.initFromAST(rootnode);
        
        Node r=rootnode.getChild(JJT_CODE);
        if (r!=null) {
            code=new Code();
            code.initFromAST(rootnode); // pass rootnode, not r (see Code.initFromAST)
        }
        else {
            code=null; // abstract declaration
        }
        
        Node[] nodes;
        
        myParameter.removeAllElements();
        nodes=rootnode.getChildren(JJT_PARAMETER);
        for (int i=0;i<nodes.length;i++) {
            Parameter c=new Parameter(this);
            c.initFromAST(nodes[i]);
            myParameter.addElement(c);
        }
        
        myException.removeAllElements();
        Node t=rootnode.getChild(JJT_THROWS);
        if (t!=null) {
            nodes=t.getChildren(JJT_NAME);
            for (int i=0;i<nodes.length;i++) {
                Exception ex=new Exception(this);
                ex.initFromAST(nodes[i]);
                myException.addElement(ex);
            }
        }
    }

} // end MemberExecutable
