/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Class
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
import de.gulden.util.xml.XMLToolbox;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents a Java class or interface declaration.
 *  
 * @author  Jens Gulden
 * @version  1.1
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
     * Static initializers.
     */
    public Vector staticInitializers;

    /**
     * Instance initializers.
     */
    public Vector instanceInitializers;

    /**
     * Flag to indicate that this object represents an interface declaration.
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
     * Flag to indicate whether parsing pass 2 has already started (avoid cyclic calls).
     * Also directly accessed by SourceParser.
     */
    transient boolean pass2 = false;

    /**
     * Temporary storage for syntax tree node.
     * Will be be null in any externally valid object state.
     *  
     * @see  #initFromASTPass2()
     */
    private transient Node rootnode;

    /**
     * The qualify cache.
     */
    private HashMap qualifyCache;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of Class.
     */
    public Class() {
        super();
        myImport=new Vector();
        interfaceFlag=false;
        staticInitializers = new Vector();
        instanceInitializers = new Vector();
        interfaceNames=new Vector();
        myMember=new Vector();
        myClassInner=new Vector();
        qualifyCache = new HashMap();
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
        return superclassName!=null ? superclassName : "java.lang.Object";
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
     * Returns the code of a static class initializers.
     */
    public Code[] getStaticInitializers() {
        Code[] c = new Code[staticInitializers.size()];
        staticInitializers.copyInto(c);
           return c;
    }

    /**
     * Returns the code of a instance class initializers.
     */
    public Code[] getInstanceInitializers() {
        Code[] c = new Code[instanceInitializers.size()];
        instanceInitializers.copyInto(c);
           return c;
    }

    /**
     * Adds a static initializer.
     */
    public void addStaticInitializer(Code c) {
        staticInitializers.addElement(c);
    }

    /**
     * Adds a instance initializer.
     */
    public void addInstanceInitializer(Code c) {
        instanceInitializers.addElement(c);
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
            	String q;
            	String cached = (String)qualifyCache.get(name);
            	if (cached != null) {
            		return cached;
            	} else {
            		if (qualifyCache.containsKey(name)) { // had been qualified before, but not found
            			q = null;
            		} else { // uncached search
            	        q=qualifyInternal(name);
            	        qualifyCache.put(name, q);
            		}
                    if (q!=null) {
                        return q;
                    } else {
                        throw new NoClassDefFoundError("cannot qualify class name "+name+" in class "+getName());
                    }
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

        if ( ! isInterface() ) {
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
        } else { // interface
        	// extended interfaces
        	for (Enumeration en=getInterfaceNames();en.hasMoreElements();) {
        		String in=(String)en.nextElement();
        		Element inE=d.createElement("extends");
        		inE.setAttribute("interface",in);
        		e.appendChild(inE);
        	}
        }

        // initializers
        Code[] initializers = getStaticInitializers();
        for (int i = 0; i<initializers.length; i++) {
            Element initializer=d.createElement("initializer");
            initializer.setAttribute("static","yes");
            initializer.appendChild(initializers[i].buildXML(d));
            e.appendChild(initializer);
        }
        initializers = getInstanceInitializers();
        for (int i = 0; i<initializers.length; i++) {
            Element initializer=d.createElement("initializer");
            initializer.appendChild(initializers[i].buildXML(d));
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
        nl = XMLToolbox.getChildren(element,"initializer");
        for (int i=0;i<nl.getLength();i++) {
            Element iniE=(Element)nl.item(i);
            Code initializer=new Code();
            initializer.initFromXML(XMLToolbox.getChildRequired(iniE,"code"));
            boolean isStatic = XMLToolbox.isYesAttribute(iniE, "static");
            if (isStatic) {
            	staticInitializers.addElement(initializer);
            } else {
            	instanceInitializers.addElement(initializer);
            }
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

    public ClassInner findInnerClass(String name) {
            	String selfName = getName();
                String selfUnqualified=getUnqualifiedName();
                // fully qualified?
                if (name.startsWith(selfName+".")) {
                    name=name.substring(selfName.length()+1);
                // 'half'-qualified, starting with this class's name?
                } else if (name.startsWith(selfUnqualified+".")) {
                    name=name.substring(selfUnqualified.length()+1);
                }
                // name might now be unqualified name of inner class,
                // or an inner class's inner class, either starting with
                // a name of a direct inner class of this (i.e. "fully qualified from here on")
                // [disabled, non-Java: or even somewhere deeper in the hierarchy of inner classes's inner classes]
                String find;
                int dot = name.indexOf('.'); // may be an inner class's inner class, so split at first "." and iterate
                if (dot != -1) {
                	find = name.substring(0, dot);
                } else {
                	find = name;
                }
                Class searchClass = this;
                while (find != null) {
                    NamedIterator it = searchClass.getInnerClasses();
                    searchClass = null;
                    while ((searchClass==null) && it.hasMore()) {
                        ClassInner ci=(ClassInner)it.next();
                        if (ci.getUnqualifiedName().equals(find)) {
                        	searchClass = ci;
                        } /* else {
                        	ClassInner ci2 = ci.findInnerClass(name); // recursion (note that this allows qualification even of completely non-qualified inner-inner(-inner...) classes, which is not recognized by the Java compiler)
                        	if (ci2 != null) {
                        		return ci2;
                        	}
                        } */
                    }
                    if (searchClass != null) {
                    	if (dot!=-1) { // at least on more
                    		int nextDot = name.indexOf(',', dot+1);
                    		if (nextDot!=-1) {
                        		find = name.substring(dot+1, nextDot);
                    		} else {
                    			find = name.substring(dot+1);
                    		}
                    		dot = nextDot;
                    	} else {
                    		find = null;
                    	}
                    } else {
                    	return null;
                    }
                }
                // ...not found
                return (ClassInner)searchClass; // might be null
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


        this.initFromASTPass2(); // make sure this has run before (if called from another class's qualifyInternal), will immediately return if called before


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

        // qualifiable by single-class-import?
        it=getImports();
        while (it.hasMore()) {
            Import im=(Import)it.next();
            if (im instanceof ImportClass) {
            	q=im.qualify(name);
            	if (q!=null) {
            		return q;
            	}
            }
        }

        // try to qualify to be in same package (if not already)
        String packageName=getPackage().getName();
        if ((!packageName.equals("")) && (!name.startsWith(packageName+"."))) {
            q=packageName+"."+name;
        }
        else {
            q=name;
        }
        Class c=basePackage.findClass(q); // existing?
        if (c!=null) {
            return q;
        }

        // inner class of this class?
        ClassInner ci = findInnerClass(name);
        if (ci != null) {
        	return ci.getName();
        }

        int lastdot = name.lastIndexOf('.');

        // might be a superclass's inner class
        // (this does not take care whether a superclass's inner class is private, so should be called quite late in this method to avoid ambiguities)
        /*
        String sup = this.getSuperclassName();
        if (lastdot != -1) {
        	String f = name.substring(0, pos);
        	String r = name.substring(pos+1);
        	String first = qualifyInternal(f); // might be a superclass or superclass's inner class, recursion
        	if (first != null) {
        		if (first.startsWith(sup)) { // yes, superclass or superclass's inner class
        			String supInner = qualifyInternal(first + "." + r); // recursion, test if full qualificaton is correct
        			if (supInner != null) {
        				return supInner;
        			}
        		}
        	}
        } else { // unqualified, still could be a superclass's inner class
        	if (!sup.equals("java.lang.Object")) {
        		String supInner = qualifyInternal(sup + "." +name);
        		if (supInner != null) {
        			return supInner;
        		}
        	}
        }
        */

        // inner class from other class (from sources or classpath)?
        if (lastdot != -1) {
        	String outerName = name.substring(0, lastdot);
        	// try to qualify possible outer class (can only happen from classpath, would have already been found by Package.findClass() if from sources)
        	String outerQ;
        	if (Package.isSourcePackage(basePackage, outerName)) { // optimization
        		outerQ = null;
        	} else {
        		try {
        			outerQ = qualify(outerName); // recursion (use qualify() to allow caching)
        		} catch (NoClassDefFoundError ncdfe) {
        			outerQ = null;
        		}
        	}
        	if (outerQ != null) {
        		// known from sources?
        		q = outerQ + "." + name.substring(lastdot+1);
        		Class qcl = basePackage.findClass(q);
        		if (qcl!= null) {
        			return q;
        		}
        		// exists in classpath?
        		String q2 = outerQ + "$" + name.substring(lastdot+1);
                try {
                    //java.lang.Class.forName(name); (no idea why this sometimes does not work, so use:)
            		ClassLoader cl = ClassLoader.getSystemClassLoader(); //oc.getClassLoader();
            		java.lang.Class dummy = cl.loadClass(q2);
                    return q;
                }
                catch (ClassNotFoundException cnfe) {
                    // fallthrough
                }
                // now known in sources, after outer has been loaded?
                //return qualifyInternal(name); // recursion
        	}
        }

        // might be an unqualified superclass's inner class
        if (lastdot == -1) {
        	String sup = this.getSuperclassName();
        	while ((sup!=null) && (!sup.equals("java.lang.Object"))) {
        		Class superclass = basePackage.findClass(sup);
        		if (!name.startsWith(sup+".")) {
            			try {
            				String supInner;
            				Class qc;
                			if (superclass != null) {
                				qc = superclass;
                			} else {
                				qc = this;
                			}
                			String supInnerName = sup + "." + name;
            				supInner = qc.qualify(supInnerName); // recursion (use qualify() to allow caching)
            				return supInner;
            			} catch (NoClassDefFoundError ncdfe) {
            				// fallthrough
            			}
        		}
        		// get superclass of superclass...
        		if (superclass != null) { // could be found in sources
        			//if (superclass.superclassName == null) { // (do not access getSuperclassName(), use this to find out whether initFromAST2 has been called before (hacking) (must be done to avoid cycles in rare cases))
        			superclass.initFromASTPass2(); // make sure that this is performed before we ask for superclass
        			sup = superclass.getSuperclassName();
        		} else {
        			try {
        				java.lang.Class cl = java.lang.Class.forName(sup);
        				cl = cl.getSuperclass();
        				if (cl != null) {
        					sup = cl.getName();
        				} else {
        					sup = null;
        				}
        			} catch (ClassNotFoundException cnfe) {
        				sup = null;
        			}
        		}
            }
        }

        // can another source file for that class be loaded from the same package (which had not been loaded before, otherwise would have been found by now)?
        if ( (this.rootnode != null) && (! (this instanceof ClassInner))) { // do not do this in pass 2 (calling this method in pass 2 when testing for need of auto-import of unqualified classes that had been qualified in the original source)
            String sourcename;
            if ((!packageName.equals("")) && (name.startsWith(packageName+"."))) {
            	sourcename = name.substring(packageName.length()+1);
            }else {
            	sourcename = name;
            }
            if (sourcename.indexOf('.')==-1) {
               	sourcename += ".java";
           		File thisSource = new File(this.rootnode.getSource());
           		File source = new File(thisSource.getParentFile(), sourcename);
           		if (source.exists()) {
           			try {
           				SourceParser.parse(source, basePackage, null);
           				// and do everything again, now with this extra source file parsed
           				return qualifyInternal(name); // recursion
           			} catch (java.lang.Exception e) {
           				//nop, ignore errors and treat like unavailable source
           			}
           		}
            }
        }

        // if this is an inner class, try to further qualify by outer class (e.g. to find inner classes of superclasses of the outer class)
        if (this instanceof ClassInner) {
        	try {
        		String s = getDeclaringClass().qualify(name); // recursion (use qualify() instead of qualifyInternal() to allow caching)
        		return s;
        	} catch (NoClassDefFoundError ncdfe) {
        		// fallthrough NO
        		return null; // can already return, because outer class would find package-imports (they are naturally the same for inner classes)
        	}
        }

        // qualifiable by package-import?
        it=getImports();
        while (it.hasMore()) {
            Import im=(Import)it.next();
            if (im instanceof ImportPackage) {
            	q=im.qualify(name);
            	if (q!=null) {
            		return q;
            	}
            }
        }

        // ...from java.lang.*? (can be done already here, because if e.g. an inner class has the same name as a class in java.lang, the compiler would require an implicit class import, and so we would have found the class already before reaching here)
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
     * Pass 2.
     */
    void initFromASTPass2() {
        Node n;
        Node[] nodes;

        if ((!pass2) && (rootnode != null)) { // (rootnode also null if XML input)

        	pass2 = true;

        	Class q; // class declarating identifiers need to be resolved via the outer class
        	if (this instanceof ClassInner) {
        		q = this.getDeclaringClass();
        	} else {
        		q = this;
        	}

            // get superclass
            n=rootnode.getChild(JJT_SUPERCLASS);
            if (n!=null) {
                superclassName= q.qualify( n.getName() );
            }
            else {
                superclassName="java.lang.Object";
            }

            // get implemented interfaces
            interfaceNames.removeAllElements();
            nodes=rootnode.getChildren(JJT_IMPLEMENTS);
            for (int i=0;i<nodes.length;i++) {
                String name=nodes[i].getName();
                interfaceNames.addElement( q.qualify(name) );
            }

            staticInitializers.removeAllElements();
            instanceInitializers.removeAllElements();
            nodes=rootnode.getChildren(JJT_INITIALIZER);
            for (int i=0;i<nodes.length;i++) {
                Code ini = new Code();
                ini.initFromAST(nodes[i].getChild(JJT_CODE));
                boolean isStatic = nodes[i].hasChild(JJT_STATIC);
                if (isStatic) {
                	addStaticInitializer(ini);
                } else {
                	addInstanceInitializer(ini);
                }
            }

            // get members...

            // inner classes and interfaces... (must do first so that inner classes are known when fields and methods are analyzed)
            NamedIterator it;
            it=getInnerClasses();
            while (it.hasMore()) {
                ClassInner c=(ClassInner)it.next();
                c.initFromASTPass2();
            }

            // ...fields
            myMember.removeAllElements();
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

            rootnode=null; // free memory and remove temporal reference to non-serializable object
        }
    }

} // end Class
