/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   beautyj
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


/**
 * This is a wrapper class for calling de.gulden.application.beautyj.Main.
 * This allows running BeautyJ by calling "java beautyj {parameters}",
 * if <BEAUTYJ_HOME>/lib/beautyj.jar has previously been included in the
 * classpath.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class beautyj {

    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Passes the call to BeautyJ's main class
     * de.gulden.application.beautyj.Main.
     *  
     * @param args The command line arguments passed from the shell.
     * @see  de.gulden.application.beautyj.Main#main
     */
    public static void main(String[] args) {
        de.gulden.application.beautyj.Main.main(args);
    }

} // end beautyj
