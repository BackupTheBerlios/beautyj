/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.BeautyJ
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

package de.gulden.application.beautyj;

import de.gulden.framework.amoda.environment.commandline.*;
import de.gulden.framework.amoda.generic.core.*;
import de.gulden.framework.amoda.generic.option.*;
import de.gulden.framework.amoda.generic.data.GenericValue;
import de.gulden.framework.amoda.model.core.ApplicationEnvironment;
import de.gulden.framework.amoda.model.data.*;
import de.gulden.framework.amoda.model.option.*;
import de.gulden.util.javasource.LogPerformer;
import de.gulden.util.javasource.sourclet.*;
import de.gulden.util.javasource.jjt.ParseException;
import java.io.*;
import java.util.*;

/**
 * The BeautyJ main program.
 * As this class extends CommandLineApplication, which itself
 * extends GenericApplication, this program can be used as
 * ANT-task, too. This feature is provided by the wrapper class
 * <code>de.gulden.application.beautyj.ant.Task</code>.
 *  
 * @author  Jens Gulden
 * @version  1.1
 * @see  de.gulden.framework.amoda.environment.commandline.CommandLineApplication
 * @see  de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper
 * @see  de.gulden.framework.amoda.model.core.Application
 * @see  de.gulden.application.beautyj.ant.Task
 */
public class BeautyJ extends CommandLineApplication implements SourcletOptions, LogPerformer {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Constant OPTION_SOURCLET.
     */
    public static final String OPTION_SOURCLET = "sourclet";

    /**
     * Constant OPTION_INPUT.
     */
    public static final String OPTION_INPUT = "input";

    /**
     * Constant OPTION_OUTDIR.
     */
    public static final String OPTION_OUTDIR = "output-directory";

    /**
     * Constant OPTION_XML_INPUT.
     */
    public static final String OPTION_XML_INPUT = "xml.in";

    /**
     * Constant OPTION_XML_VALIDATE.
     */
    public static final String OPTION_XML_VALIDATE = "xml.validate";

    /**
     * Constant OPTION_XML_OUTPUT.
     */
    public static final String OPTION_XML_OUTPUT = "xml.out";

    /**
     * Constant OPTION_XML_DOCTYPE.
     */
    public static final String OPTION_XML_DOCTYPE = "xml.doctype";

    /**
     * Constant MESSAGE_SUCCESS.
     */
    public static final String MESSAGE_SUCCESS = "message.success";

    /**
     * Constant MESSAGE_XML_CREATED.
     */
    public static final String MESSAGE_XML_CREATED = "message.xmlcreated";


    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------

    /**
     * Cache for pre-parsed multi-value entries.
     *  
     * @see  #hasOption(String,String)
     */
    protected HashMap multiValueCache;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void init(ApplicationEnvironment env) {
        super.init(env);
        if (getOptions().getBoolean("gui")) {
        	((GenericOptionEntry)getOptions().getOptionEntry("input")).setSystem(false); // enable option "-input" (to be able to set one input file or diectory via options-dialog when running in auto-gui-mode)
        }
    }

    /**
     * <p>
     * Run code transformation.
     * </p>
     * <p>
     * Called by the application environment.
     * </p>
     */
    public void perform() {
        // no batch mode or interaction - just one single action
        Sourclet sourclet=null;
        Value[] params=getEnvironment().getInputValues();
        File input=getOptions().getFile(OPTION_INPUT);
        File outDir=getOptions().getFile(OPTION_OUTDIR);
        File xmlIn=getOptions().getFile(OPTION_XML_INPUT);
        File xmlOut=getOptions().getFile(OPTION_XML_OUTPUT);
        multiValueCache = new HashMap();

        if (xmlOut==null) { // skip Sourclet if generating XML
            if (outDir==null) {
                error("output directory must be specified" + (getOptions().getBoolean("gui") ? "" : " using option '-d'"),null,true);
            }
            if (!outDir.isDirectory()) {
                error(outDir.getAbsolutePath()+" is not a valid output directory.",null,true);
            }
            sourclet = (Sourclet)getOptions().getClassInstance(OPTION_SOURCLET);
            sourclet.init(this);
        } else { // XML output
            if (outDir!=null) {
                error("output directory cannot be specified when using XML output",null,true);
            }
        }

        try {
            de.gulden.util.javasource.Package pakkage=null;
            de.gulden.util.javasource.SourceParser.verbose= this.isVerbose() || this.getOptions().getBoolean("gui");
            de.gulden.util.javasource.SourceParser.logPerformer = this;
            boolean test = this.getOptions().getBoolean("test");
            File[] inFiles = null;
            if (xmlIn!=null) { // get input from XML
                if (params.length==0 && input==null) {
                    InputStream in;
                    if (!xmlIn.equals(new File(""))) {
                        in=new FileInputStream(xmlIn);
                    } else {
                        in=System.in;
                    }
                    de.gulden.util.javasource.SourceParser.validateXML=isOption(OPTION_XML_VALIDATE);
                    pakkage=de.gulden.util.javasource.SourceParser.parseXML(in);
                    if (in instanceof FileInputStream) {
                        in.close();
                    }
                } else {
                    error("input files or directories cannot be specified when using XML input",null,true);
                }
            } else { // input from .java-files
            	if (input != null) {
            		inFiles = new File[] {input};
            	} else if (params.length>0) {
                    inFiles=new File[params.length];
                    for (int i=0;i<inFiles.length;i++) {
                        inFiles[i]=params[i].getFile();
                    }
                } else {
                    error("nothing to do - please specify at least one input file or directory",null,true);
                }
            	if (inFiles != null) {
            		pakkage=de.gulden.util.javasource.SourceParser.parse(inFiles,null);
            	}
            }

            if (!test) {
                if (xmlOut==null) {
                	if (pakkage != null) {
                		de.gulden.util.javasource.SourceParser.buildSource(pakkage, outDir, inFiles, sourclet); // build beautified sources
                		getMessage(MESSAGE_SUCCESS).perform();
                	}
                } else { // XML output
                    de.gulden.util.javasource.SourceParser.includeXMLDoctype=isOption(OPTION_XML_DOCTYPE);
                    org.w3c.dom.Document doc=de.gulden.util.javasource.SourceParser.buildXML(pakkage);
                    OutputStream out;
                    if (!xmlOut.equals(new File(""))) {
                        out=new FileOutputStream(xmlOut);
                    } else {
                        out=System.out;
                    }
                    if ((out instanceof FileOutputStream)&&this.isVerbose()) {
                        System.out.println("writing "+xmlOut.getAbsolutePath());
                    }
                    org.apache.xml.serialize.OutputFormat outputFormat=new org.apache.xml.serialize.OutputFormat();
                    outputFormat.setIndenting(true);
                    outputFormat.setPreserveSpace(true);
                    org.apache.xml.serialize.XMLSerializer serializer=new org.apache.xml.serialize.XMLSerializer(out,outputFormat);
                    org.apache.xml.serialize.DOMSerializer domSerializer=serializer.asDOMSerializer();
                    domSerializer.serialize(doc);
                    if (out instanceof FileOutputStream) {
                        out.close();
                    }
                    getMessage(MESSAGE_XML_CREATED).perform();
                }
            }
        } catch (IOException ioe) {
            error("an i/o exception occurred",ioe,true);
        } catch (ParseException pe) {
            error("Java parser error",pe,true);
        } catch (org.xml.sax.SAXException se) {
            error("XML parser error",se,true);
        }
    }

    /**
     * Tests whether the application is set to quiet-mode.
     */
    public boolean isQuiet() {
        File xmlOut=getOptions().getFile(OPTION_XML_OUTPUT);
        boolean xml_to_stdout=(xmlOut!=null)&&(xmlOut.equals(new File("")));
        // always be quiet if xml output on stdout is generated
        return super.isQuiet()||xml_to_stdout;
    }

    /**
     * Returns a string option.
     *  
     * @param name option name
     */
    public String getOption(String name) {
        return getOptions().getString(name);
    }

    /**
     * Returns an integer option.
     *  
     * @param name option name
     */
    public int getIntOption(String name) {
        return getOptions().getInt(name);
    }

    /**
     * Returns a boolean option.
     *  
     * @param name option name
     */
    public boolean isOption(String name) {
        return getOptions().getBoolean(name);
    }

    /**
     * Tests if an option has a specified value.
     *  
     * @param name option name
     * @param value value to test for
     */
    public boolean isOption(String name, String value) {
        return getOptions().getBoolean(name);
    }

    /**
     * Tests if a multi-value option contains a specified value among its set of values.
     * This uses a comma-sperated string as multi-value set.
     *  
     * @param name option name
     * @param value value to test for
     */
    public boolean hasOption(String name, String value) {
        TreeSet cached = (TreeSet)multiValueCache.get(name);
        if (cached == null) {
        	cached = new TreeSet();
               String s = getOption(name);
               if (s != null) {
               	StringTokenizer st = new StringTokenizer(s, ",", false);
               	while (st.hasMoreTokens()) {
               		cached.add(st.nextToken().trim());
               	}
               }
        	multiValueCache.put(name, cached);
        }
           return cached.contains(value);
    }

} // end BeautyJ
