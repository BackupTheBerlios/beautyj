/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Class
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
 * Represents a Java class declaration.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class Class extends SourceObjectDeclaredVisible implements PackageMember {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * Members (fields, constructors, methods).
     */
    public Vector myMember;

    /**
     * Inner classes.
     */
    public Vector myClassInner;

    /**
     * Imported packages and classes.
     */
    public Vector myImport;

    /**
     * Static initializer code or null.
     */
    public Code initializer;

    /**
     * The interface flag.
     */
    protected boolean interfaceFlag;

    /**
     * The interface names.
     */
    protected Vector interfaceNames;

    /**
     * The superclass name.
     */
    protected String superclassName;

    /**
     * Temporary storage for syntax tree node.
     * Will be be null in any externally valid object state.
     *  
     * @see  #initFromASTPass2()
     */
    private transient Node rootnode;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Class.
     */
    public Class() {
        super();
        superclassName="java.lang.Object";
        myImport=new Vector();
        interfaceFlag=false;
        initializer=null;
        interfaceNames=new Vector();
        myMember=new Vector();
        myClassInner=new Vector();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Sets the package for the class represented by this.
     */
    public void setPackage(Package p) {
        myPackage=p;
        registerAtPackage(p);
    }

    /**
     * Returns the imported packages and classes.
     */
    public NamedIterator getImports() {
        return new NamedIterator(myImport);
    }

    /**
     * Returns the name of the class that gets extended by this class.
     * If the parsed class declaration did not contain an explicit
     * 'extends'-clause, "java.lang.Object" is returned.
     */
    public String getSuperclassName() {
        return superclassName;
    }

    /**
     * Sets the name of the class that gets extended by this class.
     */
    public void setSuperclassName(String n) {
        superclassName=n;
    }

    /**
     * Returns all interface names which are implemented by this class.
     */
    public Enumeration getInterfaceNames() {
        return interfaceNames.elements();
    }

    /**
     * Sets all interface names which are implemented by this class.
     */
    public void setInterfaceNames(String[] names) {
        stringsIntoVector(names,interfaceNames);
    }

    /**
     * Returns the code of a static class initializer, if exists. Otherwise, returns <code>null</code>.
     */
    public Code getInitializer() {
        return initializer;
    }

    /**
     * Set the code of a static class initializer. May be <code>null</code>.
     */
    public void setInitializer(Code c) {
        initializer=c;
    }

    /**
     * Returns all member elements of this class that match the specified type.
     */
    public NamedIterator getMembers(Class type, int modifiers) {
        return new NamedIterator(myMember,type,modifiers);
    }

    /**
     * Returns all member elements of this class.
     * These are fields, constructors, methods and inner classes.
     */
    public NamedIterator getAllMembers() {
        return new NamedIterator(myMember);
    }

    /**
     * Returns all iner classes.
     */
    public NamedIterator getInnerClasses() {
        return new NamedIterator(myClassInner);
    }

    /**
     * Tests whether this Class object represents a Java interface (true),
     * or is an ordinary class (false).
     */
    public boolean isInterface() {
        return interfaceFlag;
    }

    /**
     * Sets whether this Class object represents a Java interface (true),
     * or is an ordinary class (false).
     */
    public void setInterface(boolean inter) {
        interfaceFlag=inter;
    }

    /**
     * Fully qualifies a class identifier, taking into account the current
     * package of this class and the import statements.
     *  
     * @return  The fully qualified class identifier.
     */
    public String qualify(String name) {
        String q=qualifyInternal(name);
        if (q!=null) {
            return q;
        }
        else {
            throw new NoClassDefFoundError("cannot qualify class name "+name+" in class "+getName());
        }
    }

    /**
     * Add this class to a package.
     */
    public void addToPackage(Package p) {
        String packageName=getPackage().getName();
        Package pp=p.findPackage(packageName); // package already in target ClassBundle?
        if (pp!=null) {
            setPackage(pp); // ...yes: use this one as new associated package
            pp.registerClass(this);
        }
        else { // .. no: insert own package
            p.add(myPackage); // this has already been registered with it before
        }
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=super.buildXML(d); // tag name "class" / "interface" decided by getXMLName()
        
        // imports
        for (NamedIterator it=getImports();it.hasMore();) {
            Import im=(Import)it.next();
            e.appendChild(im.buildXML(d));
        }
        
        // superclass
        Element sup=d.createElement("extends");
        sup.setAttribute("class",superclassName);
        e.appendChild(sup);
        
        // implemented interfaces
        for (Enumeration en=getInterfaceNames();en.hasMoreElements();) {
            String in=(String)en.nextElement();
            Element inE=d.createElement("implements");
            inE.setAttribute("interface",in);
            e.appendChild(inE);
        }
        
        // initializer
        if (getInitializer()!=null) {
            Element initializer=d.createElement("initializer");
            initializer.appendChild(getInitializer().buildXML(d));
            e.appendChild(initializer);
        }
        
        // members
        for (NamedIterator it=getAllMembers();it.hasMore();) {
            Member m=(Member)it.next();
            e.appendChild(m.buildXML(d));
        }
        
        // inner classes and interfaces
        for (NamedIterator it=getInnerClasses();it.hasMore();) {
            Class c=(Class)it.next();
            e.appendChild(c.buildXML(d));
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
        
        interfaceFlag=element.getTagName().equals("interface");
        
        // imports
        myImport.removeAllElements();
        NodeList nl=XMLToolbox.getChildren(element,"import");
        for (int i=0;i<nl.getLength();i++) {
            Import im=Import.createFromXML(getPackage(),(Element)nl.item(i));
            myImport.addElement(im);
        }
        
        // superclass
        Element sup=XMLToolbox.getChild(element,"extends");
        if (sup!=null) {
            superclassName=XMLToolbox.getAttributeRequired(sup,"class");
        }
        else {
            superclassName="java.lang.Object";
        }
        
        // implemented interfaces
        interfaceNames.removeAllElements();
        nl=XMLToolbox.getChildren(element,"implements");
        for (int i=0;i<nl.getLength();i++) {
            Element imE=(Element)nl.item(i);
            String imp=XMLToolbox.getAttributeRequired(imE,"interface");
            interfaceNames.addElement(imp);
        }
        
        // initializer
        Element ini=XMLToolbox.getChild(element,"initializer");
        if (ini!=null) {
            initializer=new Code();
            initializer.initFromXML(XMLToolbox.getChildRequired(ini,"code"));
        }
        else {
            initializer=null;
        }
        
        // members
        myMember.removeAllElements();
        
        // fields
        nl=XMLToolbox.getChildren(element,"field");
        for (int i=0;i<nl.getLength();i++) {
            Field fi=new Field(this);
            fi.initFromXML((Element)nl.item(i));
            myMember.addElement(fi);
        }
        
        // constructors
        nl=XMLToolbox.getChildren(element,"constructor");
        for (int i=0;i<nl.getLength();i++) {
            Constructor co=new Constructor(this);
            co.initFromXML((Element)nl.item(i));
            myMember.addElement(co);
        }
        
        // methods
        nl=XMLToolbox.getChildren(element,"method");
        for (int i=0;i<nl.getLength();i++) {
            Method me=new Method(this);
            me.initFromXML((Element)nl.item(i));
            myMember.addElement(me);
        }
        
        // inner classes / interfaces
        myClassInner.removeAllElements();
        
        // inner classes
        nl=XMLToolbox.getChildren(element,"class");
        for (int i=0;i<nl.getLength();i++) {
            ClassInner ci=new ClassInner();
            ci.setDeclaringClass(this);
            ci.initFromXML((Element)nl.item(i));
            myClassInner.addElement(ci);
        }
        
        // inner interfaces
        nl=XMLToolbox.getChildren(element,"interface");
        for (int i=0;i<nl.getLength();i++) {
            ClassInner ci=new ClassInner();
            ci.setDeclaringClass(this);
            ci.setInterface(true);
            ci.initFromXML((Element)nl.item(i));
            myClassInner.addElement(ci);
        }
    }

    protected void registerAtPackage(Package p) {
        // will be overwritten by ClassInner
        p.registerClass(this);
    }

    /**
     * Fully qualifies a class identifier, taking into account the current
     * package of this class and the import statements.
     *  
     * @return  The fully qualified class identifier.
     */
    protected String qualifyInternal(String name) {
        String q;
        NamedIterator it;
        
        if (isPrimitive(name)) {
            return name;
        }
        
        String selfUnqualified=getUnqualifiedName();
        
        // self?
        if (name.equals(selfUnqualified)) {
            return getName();
        }
        
        Package basePackage=getPackage().getBasePackage();
        
        // is already fully qualified?
        // ...from sources
        if (basePackage.findClass(name)!=null) {
            return name;
        }
        // ...from classpath
        try {
            java.lang.Class.forName(name);
            return name;
        }
        catch (ClassNotFoundException cnfe) {
            // fall through
        }
        
        // inner class?
        String innername=name;
        if (innername.startsWith(selfUnqualified+".")) {
            innername=innername.substring(selfUnqualified.length()+1);
        }
        it=getInnerClasses();
        while (it.hasMore()) {
            ClassInner ci=(ClassInner)it.next();
            if (ci.getUnqualifiedName().equals(innername)) {
                return ci.getName();
            }
        }
        
        // qualifiable by imports?
        it=getImports();
        while (it.hasMore()) {
            Import im=(Import)it.next();
            q=im.qualify(name);
            if (q!=null) {
                return q;
            }
        }
        
        // if reached here, name could not be qualified by imports -> try to qualify to be in same package
        String packageName=getPackage().getName();
        if (!packageName.equals("")) {
            q=packageName+"."+name;
        }
        else {
            q=name;
        }
        Class c=basePackage.findClass(q); // existing?
        if (c!=null) {
            return q;
        }
        
        // ...from java.lang.*?
        q="java.lang."+name;
        try {
            java.lang.Class.forName(q);
            return q;
        }
        catch (ClassNotFoundException cnfe) {
            // fall through
        }
        
        // if reached here, not found
        return null;
    }

    /**
     * Tests whether a type name denotes a primitive type.
     */
    protected boolean isPrimitive(String type) {
        return type.equals("boolean")
        || type.equals("byte")
        || type.equals("char")
        || type.equals("short")
        || type.equals("int")
        || type.equals("long")
        || type.equals("float")
        || type.equals("double")
        || type.equals("void");
    }

    /**
     * Returns the name of the XML tag representing this SourceObject.
     */
    protected String getXMLName() {
        // overwrites SourceObject.getXMLName()
        return interfaceFlag?"interface":"class";
    }

    /**
     * Initialize this object from parsed Java code.
     *  
     * @param rootnode The corresponding node in the abstract syntax tree (AST).
     */
    void initFromAST(Node rootnode) {
        Node n;
        Node[] nodes;
        
        // get name
        super.initFromAST(rootnode); // sets 'name' to unqualified name
        String packageName=getPackage().getName();
        if (packageName.length()>0) {
            name=packageName+"."+name; // qualify name if class is not member of base package
        }
        
        // get inner classes / interfaces...
        myClassInner.removeAllElements();
        // ...inner classes
        nodes=rootnode.getChildren(JJT_INNERCLASS);
        for (int i=0;i<nodes.length;i++) {
            ClassInner c=new ClassInner();
            c.setDeclaringClass(this);
            c.initFromAST(nodes[i]);
            myClassInner.addElement(c);
        }
        // ...inner interfaces
        nodes=rootnode.getChildren(JJT_INNERINTERFACE);
        for (int i=0;i<nodes.length;i++) {
            ClassInner c=new ClassInner();
            c.setDeclaringClass(this);
            c.setInterface(true);
            c.initFromAST(nodes[i]);
            myClassInner.addElement(c);
        }
        
        this.rootnode=rootnode; // remember for pass 2
    }

    /**
     * Inits the from a s t pass2.
     */
    void initFromASTPass2() {
        Node n;
        Node[] nodes;
        
        // get superclass
        n=rootnode.getChild(JJT_SUPERCLASS);
        if (n!=null) {
            superclassName=qualify(n.getName());
        }
        else {
            superclassName="java.lang.Object";
        }
        
        // get implemented interfaces
        interfaceNames.removeAllElements();
        nodes=rootnode.getChildren(JJT_IMPLEMENTS);
        for (int i=0;i<nodes.length;i++) {
            String name=nodes[i].getName();
            interfaceNames.addElement(qualify(name));
        }
        
        n=rootnode.getChild(JJT_INITIALIZER);
        if (n!=null) {
            initializer=new Code();
            initializer.initFromAST(n);
        }
        else {
            initializer=null;
        }
        
        // get members...
        myMember.removeAllElements();
        // ...fields
        nodes=rootnode.getChildren(JJT_FIELD);
        for (int i=0;i<nodes.length;i++) {
            Node[] vars=nodes[i].getChildren(JJT_FIELDVAR);
            for (int j=0;j<vars.length;j++) {
                Field f=new Field(this);
                f.initFromAST(nodes[i],vars[j]);
                myMember.addElement(f);
            }
        }
        // ...constructors
        nodes=rootnode.getChildren(JJT_CONSTRUCTOR);
        for (int i=0;i<nodes.length;i++) {
            Constructor c=new Constructor(this);
            c.initFromAST(nodes[i]);
            myMember.addElement(c);
        }
        // ...methods
        nodes=rootnode.getChildren(JJT_METHOD);
        for (int i=0;i<nodes.length;i++) {
            Method c=new Method(this);
            c.initFromAST(nodes[i]);
            myMember.addElement(c);
        }
        
        // inner classes and interfaces...
        NamedIterator it;
        it=getInnerClasses();
        while (it.hasMore()) {
            ClassInner c=(ClassInner)it.next();
            c.initFromASTPass2();
        }
        
        rootnode=null; // free memory and remove temporal reference to non-serializable object
    }

} // end Class
