/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.SourceParser
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
 * @version  1.1
 * @see  de.gulden.util.javasource.sourclet.Sourclet
 * @see  de.gulden.util.javasource.sourclet.standard.StandardSourclet
 */
public class SourceParser implements ParserTreeConstants {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Version
     */
    public static final String VERSION = "1.1";

    /**
     * Constant workaroundUnicodeSingleCharMarker.
     */
    protected static final String workaroundUnicodeSingleCharMarker = "-" + "unicodechar" + "-";


    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Linefeed.
     */
    public static String nl = System.getProperty("line.separator");

    /**
     * Flag specifying whether to include a DTD reference (<!DOCTYPE..>) into generated XML.
     * Externally set.
     */
    public static boolean includeXMLDoctype = false;

    /**
     * Flag specifying whether to validate an XML file against its DTD before it is parsed.
     * Externally set.
     */
    public static boolean validateXML = false;

    /**
     * Global verbose flag.
     */
    public static boolean verbose = false;

    /**
     * Log performer, may be set externally.
     */
    public static LogPerformer logPerformer = LogPerformer.DEFAULT;

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
        return parse(new File[] {file}, pt);
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
        parse(files, base, pt);
        return base;
    }

    /**
     * Parses files and adds the parsed objects to the specified base package.
     *  
     * @throws IOException if an i/o error occurs
     */
    public static void parse(File[] files, Package basePackage, ProgressTracker pt) throws IOException, ParseException {
        parsePass1(basePackage ,files,pt);
        analysePass2(basePackage ,pt);
    }

    /**
     * Parses a file and adds the parsed objects to the specified bas package.
     *  
     * @throws IOException if an i/o error occurs
     */
    public static void parse(File file, Package basePackage, ProgressTracker pt) throws IOException, ParseException {
        parse(new File[] {file},basePackage, pt);
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
        root.setAttribute("version", VERSION);
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
    public static void buildSource(Package p, File dir, File[] sources, Sourclet sourclet) throws IOException {
        // classes
        NamedIterator it=p.getClasses();
        while (it.hasMore()) {
            Class c=(Class)it.next();
            if ( (sources == null) || (c.getSource()==null) || isInSources(new File(c.getSource()), sources) ) { // if originating from files, suppress building those sources that have only been loaded for referencing classes, but not been specified as inputs
            	String classname=c.getUnqualifiedName();
            	File file=new File(dir,classname+".java");
            	log("writing "+file.getPath());
            	FileOutputStream f=new FileOutputStream(file);
            	BufferedOutputStream buf = new BufferedOutputStream(f); // this might cause little optimization, as we are writing many small bits in sequence to the stream
            	sourclet.buildSource(buf,c);
            	buf.close();
            }
        }

        // inner packages
        it=p.getInnerPackages();
        while (it.hasMore()) {
            Package pp=(Package)it.next();
            String pname=pp.getUnqualifiedName();
            File indir=new File(dir,pname);
            boolean created=indir.mkdir();
            if (created) {
            	log("directory "+indir.getPath()+" created");
            }
            buildSource(pp,indir,sources, sourclet);
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
     * Restores manipualted Java source code which avoided single-char unicode characters
     * back to the original code.
     * Called only from Code.java.
     *  
     * @param s manipulated Java source string, as returned from workaroundAvoidUnicodeSingleChar()
     * @return  the original Java source code, as it had been passed as input to workaroundAvoidUnicodeSingleChar()
     * @see  #workaroundAvoidUnicodeSingleChar(String)
     */
    public static String workaroundRestoreUnicodeSingleChar(String s) {
        int pos = s.indexOf("\"" + workaroundUnicodeSingleCharMarker);
        int l = workaroundUnicodeSingleCharMarker.length();
        if (pos != -1) {
        	return s.substring(0, pos) + "'\\u" + s.substring(pos+(l+1), pos+(l+5)) + "'" + workaroundRestoreUnicodeSingleChar(s.substring(pos+(l+6)));
        } else {
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
     *  
     * @throws IOException if an i/o error occurs
     */
    protected static void parsePass1(Package basePackage, File[] files, ProgressTracker pt) throws IOException, ParseException {
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
                    parsePass1(basePackage,ff,pt); //recursion
                }
            }
            else {
                warning("warning: can't find input file/directory "+files[i].getPath()+", ignoring");
            }
        }

        for (Enumeration e=todoFiles.elements();e.hasMoreElements();) {
            File f=(File)e.nextElement();
            log("parsing pass 1: "+f.getPath());
            String code = readFile(f);
            // workaround 1: avoid \r
            code = code.replace('\r', ' ');
            // workaround 2: parser would resolve unicode character declarations ' \ u xxxx ', so change them to a pseudo-string (this is quite an ugly workaround...)
            // need to call workarondUnicodeRestore() after parsing code blocks
            // Another workaround for a parser bug: if last line of a source file is a single-line-comment without ending line-break, the parser will crash
            // so append a 'safety-linefeed' after each input file, it can't do any harm (see JavaCC FAQ 3.15)
            code = workaroundAvoidUnicodeSingleChar(code) + nl;
            InputStream in = new StringBufferInputStream(code);
            analysePass1(basePackage, in, f.getAbsolutePath(), pt);
        }
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
            if (!c.pass2) { // (ask here to suppress message)
            	log("parsing pass 2: " + c.getName());
                c.initFromASTPass2();
            }
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
     * Replaces all occurrences of single-character-constants using unicode
     * with a pseudo-string. This way, the parser does not resolve the unicode char.
     * This is quite an ugly workaround, but usually not too costy, as single unicode chars
     * are rarely used.
     *  
     * @param s Java source code string, maybe containg single-char unicode constants.
     * @return  manipulated Java source string
     * @see  #workaroundRestoreUnicodeSingleChar(String)
     */
    protected static String workaroundAvoidUnicodeSingleChar(String s) {
        int pos = s.indexOf("'\\u");
        if (pos != -1) {
        	// make sure this is not inside a string constant
        	int linestart = s.lastIndexOf(nl, pos) + 1; // will result in 0 for 'not found' which is wanted
        	char q = endsQuoted(s.substring(linestart, pos));
        	if (q ==(char)0) {
        		return s.substring(0, pos) + "\"" + workaroundUnicodeSingleCharMarker + s.substring(pos+3, pos+7) + "\"" + workaroundAvoidUnicodeSingleChar(s.substring(pos+8));
        	} else {
        		int qe = quoteEnd(s, pos, q);
        		return s.substring(0, qe+1) + workaroundAvoidUnicodeSingleChar(s.substring(qe+1));
        	}
        } else {
        	return s;
        }
    }

    /**
     *  
     * @throws IOException if an i/o error occurs
     */
    protected static String readFile(File f) throws IOException {
        FileReader r = new FileReader(f);
        char[] c = new char[(int)f.length()];
        r.read(c);
        r.close();
        return new String(c);
    }

    protected static char endsQuoted(String s) {
        char[] cc = new char[s.length()];
        s.getChars(0, cc.length, cc, 0);
        boolean escaped = false;
        char quoted = (char)0;

        for (int i=0; i<cc.length;i++) {
        	char c = cc[i];
        	if (escaped) {
        		escaped = false;
        	} else {
        		switch (c) {
        			case '\\': escaped = true;
        						   break;
                       case '\"': switch (quoted) {
                       					case '\'':  break;
                       					case '\"': quoted = (char)0; // unquote again
                       								   break;
                       					default: quoted = '\"';
                       								   break;
                       			   }
                       				break;
                       case '\'': switch (quoted) {
        					case '\"':  break;
        					case '\'': quoted = (char)0; // unquote again
        								break;
        					default: quoted = '\'';
        							break;
                       				}
                       				break;
                       }
        		}
        	}
        return quoted;
    }

    protected static int quoteEnd(String s, int pos, char quoteChar) {
        boolean escaped = false;
        while (pos < s.length()) {
        	if (escaped) {
        		escaped = false;
        	} else {
        		char c = s.charAt(pos);
        		if (c == quoteChar) {
        			return pos;
        		} else if (c == '\\') {
        			escaped = true;
        		}
        	}
        	pos++;
        }
        return -1;
    }

    /**
     * Outputs a log message if the verbose-flag is set..
     *  
     * @param msg The log message string.
     */
    protected static void log(String msg) {
        if (verbose) {
        	logPerformer.log(msg);
        }
    }

    /**
     * Outputs a warning message, which is the same as outputting a log message, but is performed even is verbose==false.
     *  
     * @param msg The warning message string.
     */
    protected static void warning(String msg) {
        logPerformer.log(msg);
    }

    private static boolean isInSources(File f, File[] sources) {
        try {
        	String fc = f.getCanonicalPath();
        	for (int i=0; i<sources.length; i++) {
        		String ffc = sources[i].getCanonicalPath();
        		if (fc.startsWith(ffc)) { // or equal
        			return true;
        		}
        	}
        	return false;
        } catch (IOException ioe) {
        	return false;
        }
    }

} // end SourceParser
