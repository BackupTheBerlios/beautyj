/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.sourclet.SourcletOptions
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

/**
 * Class SourcletOptions.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public interface SourcletOptions {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the option.
     */
    public String getOption(String option);

    /**
     * Returns the int option.
     */
    public int getIntOption(String option);

    public boolean isOption(String option);

} // end SourcletOptions
