/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.ProgressTracker
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

/**
 * A class that implements this can monitor the progress of beautification.
 * The ProgressTracker gets informed about each new estimation of
 * the overall items to do and the current number of items already done.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public interface ProgressTracker {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    public void todo(int i);

    public void done(int i);

} // end ProgressTracker
