/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.SourceObjectDeclared
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
import de.gulden.util.xml.XMLToolbox;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Class SourceObjectDeclared.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public abstract class SourceObjectDeclared extends SourceObject {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     */
    public Class declaringClass;

    /**
     */
    public Documentation myDocumentation;

    /**
     * The modifier.
     */
    protected int modifier = 0;

    /**
     * The source.
     */
    protected String source;

    /**
     * The source position array.
     */
    protected int[] sourcePosition;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the modifier.
     */
    public int getModifier() {
        return modifier;
    }

    /**
     * Returns the modifier string.
     */
    public String getModifierString() {
        return java.lang.reflect.Modifier.toString(getModifier());
    }

    /**
     * Sets the modifier.
     */
    public void setModifier(int m) {
        modifier=m;
    }

    /**
     * Returns the class within which this SourceObjectDeclared is declared.
     */
    public Class getDeclaringClass() {
        return declaringClass;
    }

    /**
     * Sets the class within which this SourceObjectDeclared is declared.
     */
    public void setDeclaringClass(Class c) {
        declaringClass=c;
    }

    /**
     * Returns the documentation.
     */
    public Documentation getDocumentation() {
        return myDocumentation;
    }

    /**
     * Sets the documentation.
     */
    public void setDocumentation(Documentation d) {
        myDocumentation=d;
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

        // get modifier 'final'
        this.modifier=0;
        if (XMLToolbox.isYesAttribute(element,"final")) {
            this.modifier|=java.lang.reflect.Modifier.FINAL;
        }

        // get javadoc comment
        Element doc=XMLToolbox.getChild(element,"documentation");
        if (doc!=null) {
            DocumentationDeclared d=new DocumentationDeclared();
            d.initFromXML(doc);
            myDocumentation=d;
        }
        else {
            myDocumentation=null;
        }

        if (this instanceof Typed) {
            Typed typed=(Typed)this;
            Type t=null;
            if (this instanceof Member) {
                t=new Type((Member)this);
            }
            else if (this instanceof Parameter) {
                t=new Type(((Parameter)this).myMemberExecutable);
            }
            else if (this instanceof Exception) {
                t=new Type(((Exception)this).myMemberExecutable);
            }
            t.initFromXML(XMLToolbox.getChildRequired(element,"type"));
            typed.setType(t);
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

        if (java.lang.reflect.Modifier.isFinal(modifier)) {
            e.setAttribute("final","yes");
        }
        if (java.lang.reflect.Modifier.isStrict(modifier)) {
            e.setAttribute("strictfp","yes");
        }

        // javadoc
        Documentation doc=getDocumentation();
        if (doc!=null) {
            e.appendChild(doc.buildXML(d));
        }

        return e;
    }

    /**
     * Returns the full signature of this SourceObject. This is the fully
     * qualified name and, depending on the type of the SourceObject,
     * the (return-)type, and the parameters' signatures
     */
    public String getSignature() {
        String type;
        if (this instanceof Typed) {
            Typed tt=(Typed)this;
            Type t=tt.getType();
            type=t.getFullTypeName()+" ";
        }
        else {
            type="";
        }
        return type+super.getSignature();
    }

    /**
     * Returns the source.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     */
    public void setSource(String source) {
        this.source=source;
    }

    /**
     * Returns the source position.
     */
    public int[] getSourcePosition() {
        return sourcePosition;
    }

    /**
     * Returns the source position string.
     */
    public String getSourcePositionString() {
        return "["+getSourcePosition()[0]+":"+getSourcePosition()[1]+"]";
    }

    public String toString() {
        if (getSource()!=null) {
            return super.toString()+" "+getSource()+" "+getSourcePositionString();
        }
        else {
            return super.toString();
        }
    }

    /**
     * Initialize this object with values from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        super.initFromAST(rootnode);

        source=rootnode.getSource();
        sourcePosition=rootnode.getSourcePosition();

        // get modifier 'final'
        this.modifier=0;
        Node mod=rootnode.getChild(JJT_MODIFIER);
        if (mod!=null) {
            if (mod.hasChild(JJT_FINAL)) {
                this.modifier|=java.lang.reflect.Modifier.FINAL;
            }
            if (mod.hasChild(JJT_STRICTFP)) {
                this.modifier|=java.lang.reflect.Modifier.STRICT;
            }
        }

        // get javadoc comment
        Token t=rootnode.getStartToken();
        t=t.specialToken;
        if (t!=null) {
            String doc=t.image;
            if (doc.startsWith("/**")) // could be an 'ordinary'-comment (then ignore)
            {
                DocumentationDeclared d=new DocumentationDeclared();
                d.setSourceObjectDeclared(this);
                doc = doc.replace('\r', ' '); // workaround: avoid problems with crlf-linefeeds
                d.setRaw(doc);
                myDocumentation=d;
            }
            else {
                myDocumentation=null;
            }
        }
        else {
            myDocumentation=null;
        }
    }

} // end SourceObjectDeclared
