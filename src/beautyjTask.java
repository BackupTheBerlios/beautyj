/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   beautyjTask
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


import de.gulden.application.beautyj.ant.Task;

/**
 * This is an alias class for BeautyJ's ANT-task implementation
 * de.gulden.application.beautyj.ant.Task. This way, BeautyJ can
 * be used from ANT by adding <code>&lt;taskdef name="beautyj"
 * classname="beautyj" classpath="<beatyj_inst_dir>/beautyj.jar"/&gt;</code>
 * to the ANT build script. This the same as <code>&lt;taskdef name="beautyj"
 * classname="beautyj" classpath="<beatyj_inst_dir>/beautyj.jar"/&gt;</code>
 * Although possible, this functionality is not joined with the 'beautyj'
 * wrapper (which could extend de.gulden.application.beautyj.ant.Task, too).
 * This is to allow running beautyj from the command-line without the ANT API
 * classes in the classpath.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class beautyjTask extends Task {
} // end beautyjTask
