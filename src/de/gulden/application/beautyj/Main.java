/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.Main
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

import de.gulden.framework.amoda.environment.gui.GUIApplicationEnvironment;

/**
 * Main class to invoke BeautyJ.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public class Main {

    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Invokes the application inside a GUIApplicationEnvironment.
     * As BeautyJ actually is an instance of CommandLineApplication only,
     * this enables use of auto-gui generation.
     * BeautyJ sets no-gui mode as default, so use parameter -gui
     * to use auto-gui.
     *  
     * @see  de.gulden.framework.amoda.environment.commandline.CommandLineApplication#run(String[])
     * @see  de.gulden.framework.amoda.generic.core.GenericApplication#run()
     */
    public static void main(String[] args) throws Exception {
        GUIApplicationEnvironment.invoke(BeautyJ.class, args);
    }

} // end Main
