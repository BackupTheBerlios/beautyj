/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Package
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
import de.gulden.util.javasource.jjt.Node;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents a Java package.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class Package extends SourceObject implements PackageMember {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The my class.
     */
    public Vector myClass;

    /**
     * The children.
     */
    public Vector children;

    /**
     * The father.
     */
    public Package father;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Package.
     */
    public Package() {
                myClass=new Vector();
        children=new Vector();
        name=""; // initially treat as default (base-)package
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Sets the name.
     */
    public void setName(String qualifiedName) {
        super.setName(qualifiedName);
        initEmptyFathers(); // !!
    }

    public void add(PackageMember p) {
        p.addToPackage(this);
    }

    /**
     * Returns a Class object that carries the qualifiedName, if the class
     * is contained in this package, or if it is contained in any sub-package
     * of this package.
     *  
     * @return  The Class representation, or <code>null</code> if not found.
     */
    public Class findClass(String qualifiedName) {
        NamedIterator it=getClasses();
        Class c=(Class)it.find(qualifiedName);
        if (c==null) // not found in this package
        {
            // find in inner package
            NamedIterator pit=getInnerPackages();
            while ((c==null)&&(pit.hasMore())) {
                Package p=(Package)pit.next();
                c=p.findClass(qualifiedName);
            }
        }
        // c==null if not found
        return c;
    }

    /**
     * Returns a Package object that carries the qualifiedName, if the package
     * is this package, or if it is contained in this or any sub-package
     * of this package.
     *  
     * @return  The Package representation, or <code>null</code> if not found.
     */
    public Package findPackage(String qualifiedName) {
        if (qualifiedName.equals(getName())) {
            return this;
        }
        NamedIterator it=getInnerPackages();
        Package p=(Package)it.find(qualifiedName);
        if (p==null) // not found in this package
        {
            // find in inner package
            NamedIterator pit=getInnerPackages();
            while ((p==null)&&(pit.hasMore())) {
                Package pp=(Package)pit.next();
                p=pp.findPackage(qualifiedName);
            }
        }
        // p==null if not found
        return p;
    }

    /**
     * Returns an iterator that carries all classes and interfaces in this package.
     */
    public NamedIterator getClasses() {
        NamedIterator it=new NamedIterator(myClass);
        return it;
    }

    /**
     * Returns an iterator that carries all classes and interfaces in this package
     * and recursively in all its sub-packages.
     */
    public NamedIterator getAllClasses() {
        Vector v=new Vector();
        for (NamedIterator it=getClasses();it.hasMore();) {
            v.addElement(it.next());
        }
        for (NamedIterator it=getInnerPackages();it.hasMore();) {
            Package p=(Package)it.next();
            for (NamedIterator it2=p.getAllClasses();it2.hasMore();) {
                v.addElement(it2.next());
            }
        }
        return new NamedIterator(v);
    }

    /**
     * Returns an iterator that carries all direct sub-packages of this package.
     */
    public NamedIterator getInnerPackages() {
        return new NamedIterator(children);
    }

    /**
     * Returns the parent package of this package.
     */
    public Package getParentPackage() {
        return getPackage();
    }

    /**
     * Add this package to its parent package <code>p</code>.
     */
    public void addToPackage(Package p) {
        if (this.getName().startsWith(p.getName())) // legal to insert?
        {
            if (p.getName().equals(getName())) // target is same package
            {
                // add all classes in this package
                NamedIterator classes=getClasses();
                while (classes.hasMore()) {
                    p.add((Class)classes.next());
                }
                // add child packages
                NamedIterator packages=getInnerPackages();
                while (packages.hasMore()) {
                    Package inner=(Package)packages.next();
                    p.add(inner);
                }
            }
            else if (p.getName().equals(getParentPackage().getName())) // target is same as own _parent_ package
            {
                NamedIterator it=p.getInnerPackages();
                Package inner=(Package)it.find(getName()); // already a sub-package with same name as this?
                if (inner!=null) {
                    inner.add(this); // yes
                }
                else {
                    p.registerPackage(this); // no
                }
            }
            else {
                // add own parent package instead
                p.add(getParentPackage());
            }
        }
        else {
            throw new IllegalArgumentException("cannot add package '"+getName()+"' into package '"+p.getName()+"'");
        }
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e;
        if (!isBasePackage()) {
            e=super.buildXML(d);
        } else {
            e=d.getDocumentElement();
        }
        NamedIterator it=getClasses();
        while (it.hasMore()) {
            Class c=(Class)it.next();
            e.appendChild(c.buildXML(d));
        }
        it=getInnerPackages();
        while (it.hasMore()) {
            Package p=(Package)it.next();
            e.appendChild(p.buildXML(d));
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
        String tagname=element.getTagName();
        if (tagname.equals("package")) {
            super.initFromXML(element);
        } else if (tagname.equals("xjava")) { // special: treat <xjava> as base package tag
            name="";
        } else {
            throw new IOException("illegal tag name "+tagname+" expected 'xjava' or 'package'");
        }
        
        initEmptyFathers();
        
        // classes / interfaces
        myClass.removeAllElements();
        
        // classes
        NodeList nl=XMLToolbox.getChildren(element,"class");
        for (int i=0;i<nl.getLength();i++) {
            Class cl=new Class();
            cl.setPackage(this);
            cl.initFromXML((Element)nl.item(i));
        }
        
        // interfaces
        nl=XMLToolbox.getChildren(element,"interface");
        for (int i=0;i<nl.getLength();i++) {
            Class cl=new Class();
            cl.setPackage(this);
            cl.setInterface(true);
            cl.initFromXML((Element)nl.item(i));
        }
        
        // inner packages
        children.removeAllElements();
        nl=XMLToolbox.getChildren(element,"package");
        for (int i=0;i<nl.getLength();i++) {
            Package pa=new Package();
            pa.initFromXML((Element)nl.item(i));
            registerPackage(pa);
        }
    }

    /**
     * Tests whether this represents the root package of a Java classpath.
     * (A class C is member of the root package if you can invoke it by simply calling "java C".)
     */
    public boolean isBasePackage() {
        return getName().equals("");
    }

    /**
     * Returns the root package.
     */
    public Package getBasePackage() {
        if (isBasePackage()) {
            return this;
        }
        else {
            return getPackage().getBasePackage();
        }
    }

    /**
     * Make sure that all parent packages exist.
     */
    protected void initEmptyFathers() {
        if (!isBasePackage()) {
            String fatherName=getParentPackageName(getName());
            Package f=new Package();
            f.setName(fatherName);
            f.registerPackage(this);
            f.initEmptyFathers();
            myPackage=f;
        }
        else {
            myPackage=null;
        }
    }

    /**
     * Add a class to this package. (Without caring about the class's own reference to <code>this</code>.)
     */
    void registerClass(Class c) {
        if (!vectorContainsReference(myClass,c)) {
            myClass.addElement(c);
        }
    }

    /**
     * Add a package to this package. (Without caring about the package's own reference to <code>this</code>.)
     */
    void registerPackage(Package p) {
                children.addElement(p);
    }

    /**
     * Initialize this object from parsed Java code.
     */
    void initFromAST(Node node) {
        // to be extended (not overwritten) by subclasses
        super.initFromAST(node);
        initEmptyFathers();
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------
    /**
     * Removes the last part of the package name from the end of the
     * qualified name string.
     */
    static String getParentPackageName(String n) {
        int pos=n.lastIndexOf('.');
        if (pos!=-1) {
            return n.substring(0,pos);
        }
        else {
            return "";
        }
    }

    /**
     * Tests whether an objects <b>reference</b> is contained in the Vector.
     * (<code>Vector.contains()</code> tests if elements match calling
     * <code>equals()</code>.)
     */
    static boolean vectorContainsReference(Vector v, Object o) {
        for (Enumeration e=v.elements();e.hasMoreElements();) {
            if (e.nextElement()==o) {
                return true;
            }
        }
        return false;
    }

} // end Package
