/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.sourclet.SourcletOptions
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

package de.gulden.util.javasource.sourclet;

/**
 * Class SourcletOptions.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public interface SourcletOptions {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Gets a string value option.
     */
    public String getOption(String option);

    /**
     * Gets an integer value option.
     */
    public int getIntOption(String option);

    /**
     * Tests if a boolean option is selected.
     */
    public boolean isOption(String option);

    /**
     * Tests if an option has the specified value.
     */
    public boolean isOption(String option, String value);

    /**
     * Tests if a multi-value option has a specified value in its set of values.
     */
    public boolean hasOption(String option, String value);

} // end SourcletOptions
