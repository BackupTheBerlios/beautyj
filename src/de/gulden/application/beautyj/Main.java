/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.Main
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

/**
 *  
 * @author  java
 * @version  1.0
 */
public class Main {

    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Passes the call to the application's run method, which is provided
     * by the application framework.
     *  
     * @see  de.gulden.framework.amoda.environment.commandline.CommandLineApplication#run(String[])
     * @see  de.gulden.framework.amoda.generic.core.GenericApplication#run()
     */
    public static void main(String[] args) {
        new BeautyJ().run(args);
    }

} // end Main
