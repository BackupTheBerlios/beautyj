/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.SourceObjectDeclaredVisible
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
 * Class SourceObjectDeclaredVisible.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class SourceObjectDeclaredVisible extends SourceObjectDeclared {

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
        Element e=super.buildXML(d);
        
        e.setAttribute("unqualifiedName",getUnqualifiedName());
        
        if (java.lang.reflect.Modifier.isStatic(modifier)) {
            e.setAttribute("static","yes");
        }
        if (java.lang.reflect.Modifier.isAbstract(modifier)) {
            e.setAttribute("abstract","yes");
        }
        if (java.lang.reflect.Modifier.isSynchronized(modifier)) {
            e.setAttribute("synchronized","yes");
        }
        if (java.lang.reflect.Modifier.isTransient(modifier)) {
            e.setAttribute("transient","yes");
        }
        if (java.lang.reflect.Modifier.isVolatile(modifier)) {
            e.setAttribute("volatile","yes");
        }
        if (java.lang.reflect.Modifier.isNative(modifier)) {
            e.setAttribute("native","yes");
        }
        
        if (java.lang.reflect.Modifier.isPublic(modifier)) {
            e.setAttribute("public","yes");
        }
        else if (java.lang.reflect.Modifier.isProtected(modifier)) {
            e.setAttribute("protected","yes");
        }
        else if (java.lang.reflect.Modifier.isPrivate(modifier)) {
            e.setAttribute("private","yes");
        }
        else {
            e.setAttribute("packageprivate","yes"); // !(public|protected|private)
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
        
        if (XMLToolbox.isYesAttribute(element,"static")) {
            this.modifier|=java.lang.reflect.Modifier.STATIC;
        }
        if (XMLToolbox.isYesAttribute(element,"abstract")) {
            this.modifier|=java.lang.reflect.Modifier.ABSTRACT;
        }
        if (XMLToolbox.isYesAttribute(element,"synchronized")) {
            this.modifier|=java.lang.reflect.Modifier.SYNCHRONIZED;
        }
        if (XMLToolbox.isYesAttribute(element,"transient")) {
            this.modifier|=java.lang.reflect.Modifier.TRANSIENT;
        }
        if (XMLToolbox.isYesAttribute(element,"volatile")) {
            this.modifier|=java.lang.reflect.Modifier.VOLATILE;
        }
        if (XMLToolbox.isYesAttribute(element,"native")) {
            this.modifier|=java.lang.reflect.Modifier.NATIVE;
        }
        
        if (XMLToolbox.isYesAttribute(element,"public")) {
            this.modifier|=java.lang.reflect.Modifier.PUBLIC;
        }
        if (XMLToolbox.isYesAttribute(element,"protected")) {
            this.modifier|=java.lang.reflect.Modifier.PROTECTED;
        }
        if (XMLToolbox.isYesAttribute(element,"private")) {
            this.modifier|=java.lang.reflect.Modifier.PRIVATE;
        }
    }

    /**
     * Initialize this object with values from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        super.initFromAST(rootnode);
        
        // modifier
        Node mod=rootnode.getChild(JJT_MODIFIER);
        if (mod!=null) {
            if (mod.hasChild(JJT_STATIC)) {
                this.modifier|=java.lang.reflect.Modifier.STATIC;
            }
            if (mod.hasChild(JJT_ABSTRACT)) {
                this.modifier|=java.lang.reflect.Modifier.ABSTRACT;
            }
            if (mod.hasChild(JJT_TRANSIENT)) {
                this.modifier|=java.lang.reflect.Modifier.TRANSIENT;
            }
            if (mod.hasChild(JJT_VOLATILE)) {
                this.modifier|=java.lang.reflect.Modifier.VOLATILE;
            }
            if (mod.hasChild(JJT_NATIVE)) {
                this.modifier|=java.lang.reflect.Modifier.NATIVE;
            }
            if (mod.hasChild(JJT_SYNCHRONIZED)) {
                this.modifier|=java.lang.reflect.Modifier.SYNCHRONIZED;
            }
            
            if (mod.hasChild(JJT_PUBLIC)) {
                this.modifier|=java.lang.reflect.Modifier.PUBLIC;
            }
            else if (mod.hasChild(JJT_PROTECTED)) {
                this.modifier|=java.lang.reflect.Modifier.PROTECTED;
            }
            else if (mod.hasChild(JJT_PRIVATE)) {
                this.modifier|=java.lang.reflect.Modifier.PRIVATE;
            }
        }
    }

} // end SourceObjectDeclaredVisible
