/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.sourclet.Sourclet
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
import de.gulden.framework.amoda.model.option.Options;
import java.io.*;
import java.util.Enumeration;

/**
 * This is the pluggable Sourclet interface for customizing BeautyJ's output.
 * Besides <code>init(..)</code>, the only method to be implemented is
 * <code>buildSource(..)</code>, which handles all the conversion
 * from SourceObjects to Java source code.<br>
 * See <code>AbstractSourclet</code> for a further refined model of the process of generating
 * Java source code, and <code>StandardSourclet</code> for a full implementation.
 *  
 * @author  Jens Gulden
 * @version  1.0
 * @see  de.gulden.util.javasource.sourclet.AbstractSourclet
 * @see  de.gulden.util.javasource.sourclet.standard.StandardSourclet
 */
public interface Sourclet {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Initializes the Sourclet. This is called once before the
     * first SourceObject's code is being generated.
     */
    public void init(SourcletOptions options);

    /**
     * Outputs the source code for an entire SourceObject to an OutputStream.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildSource(OutputStream out, SourceObjectDeclared o) throws IOException;

} // end Sourclet
