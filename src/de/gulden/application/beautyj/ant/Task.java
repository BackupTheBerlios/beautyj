/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.ant.Task
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

package de.gulden.application.beautyj.ant;

import de.gulden.application.beautyj.BeautyJ;
import de.gulden.framework.amoda.generic.core.GenericApplication;
import de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper;
import org.apache.tools.ant.*;

/**
 * Application wrpper for running BeautyJ as an ANT task.
 *  
 * @author  Jens Gulden
 * @version  1.0
 * @see  de.gulden.application.beautyj.BeautyJ
 * @see  de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper
 */
public class Task extends ANTTaskApplicationWrapper {

    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------
    /**
     * Creates the wrapped application.
     */
    public GenericApplication createWrappedApplication() {
        return new BeautyJ();
    }

} // end Task
