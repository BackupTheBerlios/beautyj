/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.sourclet.AbstractSourclet
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

package de.gulden.util.javasource.sourclet;

import de.gulden.util.javasource.SourceObjectDeclared;
import java.io.*;
import java.util.*;

/**
 * Class AbstractSourclet.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public abstract class AbstractSourclet implements Sourclet, SourcletOptions {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Helper for quick newline character(s) access.
     */
    public static String nl=System.getProperty("line.separator");


    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * Proxy object that may work as provider of option values.
     *  
     * @see  #setOptions
     */
    protected SourcletOptions options=null;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of AbstractSourclet.
     */
    protected AbstractSourclet() {
        super();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Sets an object that works as a proxy for asccessing option values.
     * The default implementations of <code>getOption</code>,
     * <code>getIntOption</code> and <code>isOption</code> acess this proxy
     * to deliver option values to the Sourclet.<br>
     * Alternatively, <code>getOption</code>,
     * <code>getIntOption</code> and <code>isOption</code> can be reimplemented
     * (overwritten) by the Sourclet implementation to use its own option
     * retrieval mechanism.
     *  
     * @see  #getOption
     * @see  #getIntOption
     * @see  #isOption
     */
    public void setOptions(SourcletOptions options) {
        this.options=options;
    }

    /**
     * Returns the options.
     *  
     * @return  the options proxy, if it exists. Otherwise returns <code>null</code>.
     */
    public SourcletOptions getOptions() {
        return options;
    }

    /**
     * Initializes the Sourclet. This is called once before the
     * first SourceObject's code is being generated.
     */
    public void init(SourcletOptions options) {
        setOptions(options);
    }

    /**
     * Passes requests for option values to the SourcletOptions proxy object that has been set through setOptions(..).
     * May be overwritten by a subclass to provide options from a different source.
     */
    public String getOption(String name) {
        return options.getOption(name);
    }

    /**
     * Passes requests for option values to the SourcletOptions proxy object that has been set through setOptions(..).
     * May be overwritten by a subclass to provide options from a different source.
     */
    public int getIntOption(String name) {
        return options.getIntOption(name);
    }

    /**
     * Passes requests for option values to the SourcletOptions proxy object that has been set through setOptions(..).
     * May be overwritten by a subclass to provide options from a different source.
     */
    public boolean isOption(String name) {
        return options.isOption(name);
    }

    /**
     * Outputs the source code for an entire SourceObject to an OutputStream.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildSource(OutputStream out, SourceObjectDeclared o) throws IOException {
            buildStartSource(out,o);
    buildHeadSource(out,o);
    buildBodySource(out,o);
    buildEndSource(out,o);
    }

    /**
     * Outputs the start part of a source object.
     * This is the part which comes before the 'normal' head
     * (e.g. before a method's signature), so usually this is
     * the place where to output Javadoc comments.
     *  
     * @throws IOException if an i/o error occurs
     */
    public abstract void buildStartSource(OutputStream out, SourceObjectDeclared o) throws IOException;

    /**
     * Outputs the head part of a source object.
     * This is the actual Java code that declares the source object,
     * for example a method's signature.
     *  
     * @throws IOException if an i/o error occurs
     */
    public abstract void buildHeadSource(OutputStream out, SourceObjectDeclared o) throws IOException;

    /**
     * Outputs the body content of the source object. For example,
     * in case of methods this is Java code, in case of classes this recursively
     * contains other SourceObjects' code.
     *  
     * @throws IOException if an i/o error occurs
     */
    public abstract void buildBodySource(OutputStream out, SourceObjectDeclared o) throws IOException;

    /**
     * Outputs everything that occurs after the SourceObject.
     *  
     * @throws IOException if an i/o error occurs
     */
    public abstract void buildEndSource(OutputStream out, SourceObjectDeclared o) throws IOException;


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------
    /**
     * Tool method for writing a string to an OutputStream.
     *  
     * @throws IOException if an i/o error occurs
     */
    public static void write(OutputStream out, String s) throws IOException {
        out.write(s.getBytes());
    }

    /**
     * Tool method for writing a string concatenated with a newline-feed to an OutputStream.
     *  
     * @throws IOException if an i/o error occurs
     */
    public static void writeln(OutputStream out, String s) throws IOException {
        write(out,s+nl);
    }

} // end AbstractSourclet
