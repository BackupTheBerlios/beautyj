/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.BeautyJ
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

package de.gulden.application.beautyj;

import de.gulden.framework.amoda.environment.commandline.*;
import de.gulden.framework.amoda.generic.core.*;
import de.gulden.framework.amoda.generic.option.*;
import de.gulden.framework.amoda.generic.data.GenericValue;
import de.gulden.framework.amoda.model.data.*;
import de.gulden.framework.amoda.model.option.*;
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
 * @version  1.0
 * @see  de.gulden.framework.amoda.environment.commandline.CommandLineApplication
 * @see  de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper
 * @see  de.gulden.framework.amoda.model.core.Application
 * @see  de.gulden.application.beautyj.ant.Task
 */
public class BeautyJ extends CommandLineApplication implements SourcletOptions, SingleArgParser {

    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Constant OPTION_SOURCLET.
     */
    public static final String OPTION_SOURCLET="sourclet";

    /**
     * Constant OPTION_OUTDIR.
     */
    public static final String OPTION_OUTDIR="d";

    /**
     * Constant OPTION_XML_INPUT.
     */
    public static final String OPTION_XML_INPUT="xml.in";

    /**
     * Constant OPTION_XML_VALIDATE.
     */
    public static final String OPTION_XML_VALIDATE="xml.validate";

    /**
     * Constant OPTION_XML_OUTPUT.
     */
    public static final String OPTION_XML_OUTPUT="xml.out";

    /**
     * Constant OPTION_XML_DOCTYPE.
     */
    public static final String OPTION_XML_DOCTYPE="xml.doctype";

    /**
     * Constant MESSAGE_SUCCESS.
     */
    public static final String MESSAGE_SUCCESS="message.success";

    /**
     * Constant MESSAGE_XML_CREATED.
     */
    public static final String MESSAGE_XML_CREATED="message.xmlcreated";


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The args parser.
     */
    protected ArgsParser argsParser;

    /**
     * The original single arg parser.
     */
    protected SingleArgParser originalSingleArgParser;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * <p>
     * This is a pre-init method that is used to configure the application
     * environment before it takes any action. Nothing is initialized at this
     * time, options are not available.
     * </p>
     * <p>
     * Called by the application environment.
     * </p>
     *  
     * @param environment The environment.
     */
    public void prepare(GenericApplicationEnvironment environment) {
        argsParser=environment.getArgsParser();
        originalSingleArgParser=argsParser.getSingleArgParser();
        argsParser.setSingleArgParser(this);
    }

    /**
     * <p>
     * Run code transformation.
     * </p>
     * <p>
     * Called by the application environment.
     * </p>
     */
    public void start() {
        // no batch mode or interaction - just one single action
        Sourclet sourclet=null;
        Value[] params=getEnvironment().getInputValues();
        File outDir=getOptions().getFile(OPTION_OUTDIR);
        File xmlIn=getOptions().getFile(OPTION_XML_INPUT);
        File xmlOut=getOptions().getFile(OPTION_XML_OUTPUT);
        
        if (xmlOut==null) { // skip Sourclet if generating XML
            if (outDir==null) {
                error("output directory must be specified using option '-d'",null,true);
            }
            if (!outDir.isDirectory()) {
                error(outDir.getAbsolutePath()+" is not a valid output directory.",null,true);
            }
            String sourcletClass=getOptions().getString(OPTION_SOURCLET);
            try {
                Class cl=Class.forName(sourcletClass);
                sourclet=(Sourclet)cl.newInstance();
            } catch (Exception e) {
                error("cannot load Sourclet",e,true);
            }
            sourclet.init(this);
        } else { // XML output
            if (outDir!=null) {
                error("output directory cannot be specified when using XML output",null,true);
            }
        }
        
        try {
            de.gulden.util.javasource.Package pakkage=null;
            de.gulden.util.javasource.SourceParser.verbose=this.isVerbose();
            if (xmlIn!=null) { // get input from XML
                if (params.length==0) {
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
                if (params.length>0) {
                    File[] inFiles=new File[params.length];
                    for (int i=0;i<inFiles.length;i++) {
                        inFiles[i]=params[i].getFile();
                    }
                    pakkage=de.gulden.util.javasource.SourceParser.parse(inFiles,null);
                } else {
                    error("nothing to do - please specify at least one input file or directory",null,true);
                }
            }
            
            if (xmlOut==null) {
                de.gulden.util.javasource.SourceParser.buildSource(pakkage,outDir,sourclet);
                message(MESSAGE_SUCCESS);
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
                message(MESSAGE_XML_CREATED);
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
     * Implements interface <code>SingleArgsParser</code>.<br>
     * This specially modifies the way the application framework parses
     * options.<br>
     * It allows hierarchic specification of multiple boolean options at once,
     * for example <code>-doc.method=create</code> has the same effect as
     * setting the individual options<br>
     * -<b>doc.method</b>.description.<b>create</b>.dummy<br>
     * -<b>doc.method</b>.description.<b>create</b>.text<br>
     * -<b>doc.method</b>.throws.<b>create</b>.dummy<br>
     * etc.<br>
     * When hiearchically grouping boolean options, any option whose name
     * starts with the left-hand-side of the equation sign and also contains
     * the right-hand-side of the equation sign as parts of its qualified name,
     * will be set to true.
     *  
     * @see  de.gulden.framework.amoda.generic.core.SingleArgParser
     * @see  #prepare
     */
    public boolean parseIndividualOption(String name, String suggestedValue, GenericOptions options) {
        try {
            return originalSingleArgParser.parseIndividualOption(name,suggestedValue,options);
        } catch (IllegalOptionError ioe) {
            // alternatively, try if option to be set is a hierarchical shortcut for a set of boolean options
            // find any option starting with optionId
            //name=name.substring(1); // remove leading '-'
            boolean foundAny=false;
            for (Iterator it=options.getEntries().iterator();it.hasNext();) {
                GenericOptionEntry o=(GenericOptionEntry)it.next();
                if (Boolean.class.isAssignableFrom(o.getType())) {
                    String id=o.getId();
                    if (id.startsWith(name+".")) { // possible candidate
                        String restName=id.substring(name.length());
                        // allow multiple values, seperated by commas
                        StringTokenizer st=new StringTokenizer(suggestedValue,",");
                        while (st.hasMoreTokens()) {
                            String tok=st.nextToken();
                            if (
                                    (restName.endsWith("."+tok))
                                    ||(restName.indexOf("."+tok+".")!=-1)
                               ) { // options name contains second part
                                   ((GenericValue)o.getValue(Option.STATE_CURRENT)).setObject(Boolean.TRUE);
                                   foundAny=true;
                            }
                        }
                    }
                }
            }
            if (foundAny) {
                return true;
            } else {
                throw ioe;
            }
        }
    }

} // end BeautyJ
