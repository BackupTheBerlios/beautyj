/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.LogPerformer
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

package de.gulden.util.javasource;

/**
 * Copied from de.gulden.framework.amoda.model.interaction.LogPerformer.
 * (As its own interface to keep de.gulden.util.javasourceindependent from AMODA. )
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public interface LogPerformer {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    /**
     * The d e f a u l t.
     */
    public static LogPerformer DEFAULT = new LogPerformer() {
	public void log(String text) {
		log(text, null);
	}
	public void log(String text, Object source) {
		System.out.println( ( (source != null) ? (source.getClass().getName()+": ") : "") + text );
	}
};


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void log(String text);

    public void log(String text, Object source);

} // end LogPerformer
