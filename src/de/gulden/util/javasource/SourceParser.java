/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.SourceParser
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

import de.gulden.util.javasource.sourclet.Sourclet;
import de.gulden.util.javasource.jjt.Node;
import de.gulden.util.javasource.jjt.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.*;

/**
 * SourceParser main utitility class.
 * This class provides functionality to parse Java source codes and build
 * a tree of objects representing the source elements.
 * This tree of objects can be output as XML or, instead of parsing Java
 * sources, be parsed from previously generated XML.
 *  
 * @author  Jens Gulden
 * @version  1.0
 * @see  de.gulden.util.javasource.sourclet.Sourclet
 * @see  de.gulden.util.javasource.sourclet.standard.StandardSourclet
 */
public class SourceParser implements ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Flag specifying whether to include a DTD reference (<!DOCTYPE..>) into generated XML.
     * Externally set.
     */
    public static boolean includeXMLDoctype=false;

    /**
     * Flag specifying whether to validate an XML file against its DTD before it is parsed.
     * Externally set.
     */
    public static boolean validateXML=false;

    /**
     * Global verbose flag.
     */
    public static boolean verbose=false;

    /**
     * Document builder for parsing XML.
     * Will be initialized when first used.
     */
    protected static DocumentBuilder documentBuilder;


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------
    /**
     * Create object tree from Java source inputs.
     *  
     * @throws IOException if an i/o error occurs
     * @return  Root package (named "") containing all other packages with classes.
     */
    public static Package parse(File file, ProgressTracker pt) throws IOException, ParseException {
        File[] ff={file};
        return parse(ff,pt);
    }

    /**
     * Create object tree from Java source inputs.
     *  
     * @param files A list of files and/or directories. Any .java-file will be parsed, any other ignored.
     * @throws IOException if an i/o error occurs
     * @return  Root package (named "") containing all other packages with classes.
     */
    public static Package parse(File[] files, ProgressTracker pt) throws IOException, ParseException {
        Package base=new Package();
        parse(base,files,pt);
        analysePass2(base,pt);
        return base;
    }

    /**
     * Create object tree from Java source inputs.
     *  
     * @throws IOException if an i/o error occurs
     * @return  Base package (named "") containing all other packages with classes.
     */
    public static Package parse(String[] filenames, ProgressTracker pt) throws IOException, ParseException {
        File[] f=new File[filenames.length];
        for (int i=0;i<filenames.length;i++) {
            f[i]=new File(filenames[i]);
        }
        return parse(f,pt);
    }

    /**
     * Create object tree from XML input, previously created from parsed .java-files.
     *  
     * @throws IOException if an i/o error occurs
     * @throws SAXException if an XML parser error occurs
     * @return  Base package (named "") containing all other packages with classes.
     * @see  #buildXML
     */
    public static Package parseXML(InputStream in) throws IOException, SAXException {
        Document doc=getDocumentBuilder().parse(in);
        Package p=new Package();
        Element e=doc.getDocumentElement();
        p.initFromXML(e); // <xjava> may be treated as base package
        return p;
    }

    /**
     * Output an object tree of source code elements to XML.
     *  
     * @param p Package containing all other packages with classes that are to be converted to XML.
     * @return  The XML DOM-document.
     * @see  #parseXML
     */
    public static Document buildXML(Package p) {
        DOMImplementation domImplementation=getDocumentBuilder().getDOMImplementation();
        DocumentType doctype;
        if (includeXMLDoctype) {
            doctype=domImplementation.createDocumentType("xjava",null,"xjava.dtd");
        } else {
            doctype=null;
        }
        Document doc=domImplementation.createDocument(null,"xjava",doctype);
        Element root=doc.getDocumentElement();
        Element xml=p.buildXML(doc);
        if (root!=xml) { // xml may be tag 'xjava', created by base package
            root.appendChild(xml);
        }
        return doc;
    }

    /**
     * Output object tree of source code elements as Java source files,
     * applying a Sourclet for formatting the code.
     *  
     * @param p Package containing all other packages with classes that are to be output as formatted source code.
     * @param dir Base directory where to output .java-files. A directory structure matching the classes' packages structure will be created.
     * @param sourclet The Sourclet to use for formatting the output.
     * @throws IOException if an i/o error occurs
     */
    public static void buildSource(Package p, File dir, Sourclet sourclet) throws IOException {
        // classes
        NamedIterator it=p.getClasses();
        while (it.hasMore()) {
            Class c=(Class)it.next();
            String classname=c.getUnqualifiedName();
            File file=new File(dir,classname+".java");
            if (verbose) {
                System.out.println(file.getPath());
            }
            FileOutputStream f=new FileOutputStream(file);
            sourclet.buildSource(f,c);
            f.close();
        }

        // inner packages
        it=p.getInnerPackages();
        while (it.hasMore()) {
            Package pp=(Package)it.next();
            String pname=pp.getUnqualifiedName();
            File indir=new File(dir,pname);
            boolean created=indir.mkdir();
            if (verbose&&created) {
                System.out.println("directory "+indir.getPath()+" created");
            }
            buildSource(pp,indir,sourclet);
        }
    }

    /**
     * Tool function: indent a multi-line string by <i>depth</i> blank characters in front of each line.
     */
    public static String indent(String s, int depth) {
        StringBuffer sb=new StringBuffer();
        StringTokenizer st=new StringTokenizer(s,"\n");
        while (st.hasMoreTokens()) {
            sb.append(repeat("  ",depth)+st.nextToken()+(st.hasMoreTokens()?"\n":""));
        }
        return sb.toString();
    }

    /**
     * Tool function: create a new String which contains <i>s</i> repeated <i>c</i> times.
     */
    public static String repeat(String s, int c) {
        if (c>0) {
            StringBuffer sb=new StringBuffer(s);
            for (int i=1;i<c;i++) {
                sb.append(s);
            }
            return sb.toString();
        }
        else {
            return "";
        }
    }

    /**
     * Tool function: replace any occurrence of <i>old</i> in <i>s</i> with <i>neu</i>.
     */
    public static String replace(String s, String old, String neu) {
        int pos=s.indexOf(old);
        if (pos!=-1) {
            return s.substring(0,pos)+neu+replace(s.substring(pos+old.length()),old,neu);
        }
        else {
            return s;
        }
    }

    /**
     * Creates XML document builder.
     */
    protected static DocumentBuilder getDocumentBuilder() {
        if (documentBuilder==null) {
            // init on demand
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            dbf.setExpandEntityReferences(validateXML);
            dbf.setValidating(validateXML); // seems to have no effect, always true (?)
            try {
                documentBuilder=dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException pce) {
                System.out.println("ERROR: cannot initialize XML parser - "+pce.getMessage());
                // program will exit with NullPointerException after return from this method
            }
        }
        return documentBuilder;
    }

    /**
     * Parsing pass 1.<br>
     * This calls the parser generated by JavaCC and converts input source
     * code into an object-structure.<br>
     *  
     * @throws IOException if an i/o error occurs
     * @see  #analysePass2
     */
    protected static void analysePass1(Package basePackage, InputStream in, String source, ProgressTracker pt) throws IOException, ParseException {
        Node rootnode;
        Node[] nodes;

        // syntax parse
        rootnode=Parser.parse(in,source);

        if (pt!=null) {
            pt.done(3); // pass1 weighted as 3, pass2 as 1
        }

        // semantic analysis: create class structure

        // package
        Package pakkage;
        Node pak=rootnode.getChild(JJT_PACKAGE);
        if (pak!=null) {
            pakkage=new Package();
            pakkage.initFromAST(pak);
        }
        else {
            pakkage=basePackage;
        }

        // imports
        nodes=rootnode.getChildren(JJT_IMPORT);
        Vector imports=new Vector();
        for (int i=0;i<nodes.length;i++) {
            imports.addElement(Import.createFromAST(basePackage,nodes[i]));
        }

        // interfaces
        nodes=rootnode.getChildren(JJT_INTERFACE);
        for (int i=0;i<nodes.length;i++) {
            Class c=new Class();
            c.setPackage(pakkage);
            NamedIterator it=c.getImports();
            for (Enumeration e=imports.elements();e.hasMoreElements();) {
                Import im=(Import)e.nextElement();
                it.add(im);
            }
            c.setInterface(true);
            c.initFromAST(nodes[i]); // pass 1 only
        }

        // classes
        nodes=rootnode.getChildren(JJT_CLASS);
        for (int i=0;i<nodes.length;i++) {
            Class c=new Class();
            c.setPackage(pakkage);
            NamedIterator it=c.getImports();
            for (Enumeration e=imports.elements();e.hasMoreElements();) {
                Import im=(Import)e.nextElement();
                it.add(im);
            }
            c.initFromAST(nodes[i]); // pass 1 only
        }

        basePackage.add(pakkage);
    }

    /**
     * Parsing pass 2.<br>
     * Now that all classes in packages are already known,
     * unqualified references can be qualified clearly.
     * So let classes/ interfaces perform their 'real' initialization now.
     *  
     * @throws IOException if an i/o error occurs
     * @see  #analysePass1
     */
    protected static void analysePass2(Package pack, ProgressTracker pt) throws IOException, ParseException {
        NamedIterator it;
        it=pack.getClasses();
        while (it.hasMore()) {
            Class c=(Class)it.next();
            c.initFromASTPass2();
            if (pt!=null) {
                pt.done(1); // pass1 weighted as 3, pass2 as 1
            }
        }
        // perform this recursively on all inner packages
        it=pack.getInnerPackages();
        while (it.hasMore()) {
            Package p=(Package)it.next();
            analysePass2(p,pt);
        }
    }

    /**
     *  
     * @throws IOException if an i/o error occurs
     */
    protected static void parse(Package basePackage, File[] files, ProgressTracker pt) throws IOException, ParseException {
        Vector todoFiles=new Vector();
        for (int i=0;i<files.length;i++) {
            if (files[i].exists()) {
                if (files[i].isFile()) {
                    String filename=files[i].getName();
                    if (filename.endsWith(".java")) {
                        if (pt!=null) {
                            pt.todo(4);
                        }
                        todoFiles.addElement(files[i]);
                    }
                    else {
                        //nop, ignore other file types
                    }
                }
                else if (files[i].isDirectory()) {
                    String[] list=files[i].list();
                    File[] ff=new File[list.length];
                    for (int j=0;j<list.length;j++) {
                        File ffile=new File(files[i],list[j]);
                        ff[j]=ffile;
                    }
                    parse(basePackage,ff,pt); //recursion
                }
            }
            else {
                System.err.println("warning: can't find input file/directory "+files[i].getPath()+", ignoring");
            }
        }

        for (Enumeration e=todoFiles.elements();e.hasMoreElements();) {
            File f=(File)e.nextElement();
            if (verbose) {
                System.out.println(f.getPath());
            }
            FileInputStream in=new FileInputStream(f);
            analysePass1(basePackage,in,f.getAbsolutePath(),pt);
            in.close();
        }
    }

} // end SourceParser
