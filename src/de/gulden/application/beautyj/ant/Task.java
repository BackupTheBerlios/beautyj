/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.ant.Task
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

package de.gulden.application.beautyj.ant;

import de.gulden.application.beautyj.BeautyJ;
import de.gulden.framework.amoda.model.option.Option;
import de.gulden.framework.amoda.generic.core.GenericApplication;
import de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper;

/**
 * Application wrapper for running BeautyJ as an ANT task.
 *  
 * @author  Jens Gulden
 * @version  1.1
 * @see  de.gulden.application.beautyj.BeautyJ
 * @see  de.gulden.framework.amoda.environment.ant.ANTTaskApplicationWrapper
 */
public class Task extends ANTTaskApplicationWrapper {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Creates the wrapped application.
     */
    public GenericApplication createWrappedApplication() {
        return new BeautyJ();
    }

    /**
     * The directory to output the beautified sources to.
     */
    public void setOutput_directory(String s) {
        setOption("output-directory", s);
    }

    /**
     * The directory to output the beautified sources to.
     */
    public void setD(String s) {
        setOption("output-directory", s);
    }

    /**
     * Full class name of the Sourclet to be used for generating beautified class files.
     * (Default: de.gulden.util.javasource.sourclet.standard.StandardSourclet)
     */
    public void setSourclet(String s) {
        setOption("sourclet", s);
    }

    /**
     * (Default: false)
     */
    public void setQuiet(String s) {
        setOption("quiet", s);
    }

    /**
     * (Default: false)
     */
    public void setVerbose(String s) {
        setOption("verbose", s);
    }

    /**
     * (Default: false)
     */
    public void setTest(String s) {
        setOption("test", s);
    }

    /**
     */
    public void setProperties(String s) {
        setOption("properties", s);
    }

    /**
     * The project's name.
     */
    public void setProject_name(String s) {
        setOption("project.name", s);
    }

    /**
     * The project's version number.
     */
    public void setProject_version(String s) {
        setOption("project.version", s);
    }

    /**
     * The project's date.
     * (Default: today)
     */
    public void setProject_date(String s) {
        setOption("project.date", s);
    }

    /**
     * Short description of the project.
     */
    public void setProject_description(String s) {
        setOption("project.description", s);
    }

    /**
     * A text file to insert as header comment (instead of auto-building a header with project and author information).
     */
    public void setProject_headerfile(String s) {
        setOption("project.headerfile", s);
    }

    /**
     * The author's name.
     */
    public void setAuthor_name(String s) {
        setOption("author.name", s);
    }

    /**
     * The author's email address.
     */
    public void setAuthor_email(String s) {
        setOption("author.email", s);
    }

    /**
     * Remove dummy content from a class-header's javadoc. Possible values are (multiple possible, comma-seperated): description, author, version
     */
    public void setClass_remove_dummy(String s) {
        setOption("class.remove.dummy", s);
    }

    /**
     * Remove empty content from a class's javadoc comment. Possible values are (multiple possible, comma-seperated): description, author, version
     */
    public void setClass_remove_empty(String s) {
        setOption("class.remove.empty", s);
    }

    /**
     * Remove any content from a class's javadoc comment. Possible values are (multiple possible, comma-seperated): description, author, version
     */
    public void setClass_remove_text(String s) {
        setOption("class.remove.text", s);
    }

    /**
     * Create dummy content for a class's javadoc comment. Possible values are (multiple possible, comma-seperated): description, author, version
     */
    public void setClass_create_dummy(String s) {
        setOption("class.create.dummy", s);
    }

    /**
     * Create auto-text content for a class's javadoc comment. Possible values are (multiple possible, comma-seperated): description, author, version
     */
    public void setClass_create_text(String s) {
        setOption("class.create.text", s);
    }

    /**
     * Remove dummy content from a method's or constructor's javadoc comment. Possible values are (multiple possible, comma-seperated): description, param, return, throws
     */
    public void setMethod_remove_dummy(String s) {
        setOption("method.remove.dummy", s);
    }

    /**
     * Remove empty content from a method's or constructor's javadoc comment. Possible values are (multiple possible, comma-seperated): description, param, return, throws
     */
    public void setMethod_remove_empty(String s) {
        setOption("method.remove.empty", s);
    }

    /**
     * Remove any content from a method's or constructor's javadoc comment. Possible values are (multiple possible, comma-seperated): description, param, return, throws
     */
    public void setMethod_remove_text(String s) {
        setOption("method.remove.text", s);
    }

    /**
     * Create dummy content for a method's or constructor's javadoc comment. Possible values are (multiple possible, comma-seperated): description, param, return, throws
     */
    public void setMethod_create_dummy(String s) {
        setOption("method.create.dummy", s);
    }

    /**
     * Create auto-text content for a method's or constructor's javadoc comment. Possible values are (multiple possible, comma-seperated): description, param, return, throws
     */
    public void setMethod_create_text(String s) {
        setOption("method.create.text", s);
    }

    /**
     * Remove dummy description from a field's javadoc comment, if set to 'description'.
     */
    public void setField_remove_dummy(String s) {
        setOption("field.remove.dummy", s);
    }

    /**
     * Remove empty description from a field's javadoc comment, if set to 'description'.
     */
    public void setField_remove_empty(String s) {
        setOption("field.remove.empty", s);
    }

    /**
     * Remove any description from a field's javadoc comment, if set to 'description'.
     */
    public void setField_remove_text(String s) {
        setOption("field.remove.text", s);
    }

    /**
     * Create dummy description for a field's javadoc comment, if set to 'description'.
     */
    public void setField_create_dummy(String s) {
        setOption("field.create.dummy", s);
    }

    /**
     * Create auto-text description for a field's javadoc comment, if set to 'description'.
     */
    public void setField_create_text(String s) {
        setOption("field.create.text", s);
    }

    /**
     * Sets default comment texts for thrown exceptions. The default values need not to be repeated if you want them leave unchanged and add some more. Example: 'MalformedURLException=if the url is not legal,NumberFormatException=if the string does not represent a number'."
     */
    public void setException_texts(String s) {
        setOption("exception.texts", s);
    }

    /**
     * Choose between a code formatting style that outputs a line-break before the first starting curly brace ( '{' ) of a method (if set to true), or output it at the end of the method's declaring line (if set to false).
     * (Default: false)
     */
    public void setCode_braces_linebreak(String s) {
        setOption("code.braces.linebreak", s);
    }

    /**
     * Choose between a code formatting style that outputs a line-break before the first starting curly brace ( '{' ) of a method (if set to true), or output it at the end of the method's declaring line (if set to false).
     * (Default: false)
     */
    public void setB(String s) {
        setOption("code.braces.linebreak", s);
    }

    /**
     * Remove out-commented dead lines of code from the inner method bodies. Every line that starts with '//' and ends with ';', '{' or '}' will be removed.
     * (Default: false)
     */
    public void setCode_clean(String s) {
        setOption("code.clean", s);
    }

    /**
     * Remove out-commented dead lines of code from the inner method bodies. Every line that starts with '//' and ends with ';', '{' or '}' will be removed.
     * (Default: false)
     */
    public void setC(String s) {
        setOption("code.clean", s);
    }

    /**
     * Insert comment blocks between different types of class members. This highly increases readabilty.
     * (Default: true)
     */
    public void setCode_separators(String s) {
        setOption("code.separators", s);
    }

    /**
     * Preserve the order of field declarations as they are given in the orignal source file. In some cases this might be necessary to avoid illegal forward references when compiling the beautified sources.
     * (Default: false)
     */
    public void setCode_preserve_fields_order(String s) {
        setOption("code.preserve.fields.order", s);
    }

    /**
     * Format code blocks by auto-indenting the lines to a normalized standard-representation.
     * (Default: false)
     */
    public void setCode_format(String s) {
        setOption("code.format", s);
    }

    /**
     * Format code blocks by auto-indenting the lines to a normalized standard-representation.
     * (Default: false)
     */
    public void setF(String s) {
        setOption("code.format", s);
    }

    /**
     * Sets the number of spaces with which auto-indentation gets performed.
     * (Default: 4)
     */
    public void setCode_indent_spaces(String s) {
        setOption("code.indent.spaces", s);
    }

    /**
     * Sets the number of spaces with which auto-indentation gets performed.
     * (Default: 4)
     */
    public void setI(String s) {
        setOption("code.indent.spaces", s);
    }

    /**
     * Use fully qualified class names in the generated output. (E.g. use 'java.lang.String' instead of 'String'.) This might be needed in order to avoid ambiguity in class names. Usually, the classes and interfaces used in member signatures are declared by import statements.
     * (Default: false)
     */
    public void setCode_qualify(String s) {
        setOption("code.qualify", s);
    }

    /**
     * Use fully qualified class names in the generated output. (E.g. use 'java.lang.String' instead of 'String'.) This might be needed in order to avoid ambiguity in class names. Usually, the classes and interfaces used in member signatures are declared by import statements.
     * (Default: false)
     */
    public void setQ(String s) {
        setOption("code.qualify", s);
    }

    /**
     * If option -code.qualify is disabled (which is default), fully qualifiy those class-names which would otherwise be unqualifiyable.
     * (Default: true)
     */
    public void setCode_qualify_auto(String s) {
        setOption("code.qualify.auto", s);
    }

    /**
     * Read Java source from XML file, not from .java-files. Then beautify and output as .java-files. Use "-xml.in=" without specifying a file name to read from standard in.
     */
    public void setXml_in(String s) {
        setOption("xml.in", s);
    }

    /**
     * Parse Java source and create XML file, without performing any beautification. Use "-xml.out=" without specifying a file name to write to standard out, in this case option -quiet is implicit.
     */
    public void setXml_out(String s) {
        setOption("xml.out", s);
    }

    /**
     * Validate the input XML against its DTD before parsing. The input file must contain a <!DOCTYPE ..> declaration. Can be used only in combination with -xml.in.
     */
    public void setXml_validate(String s) {
        setOption("xml.validate", s);
    }

    /**
     * Include a <!DOCTYPE ..> reference into generated XML. Can be used only in combination with -xml.out.
     */
    public void setXml_doctype(String s) {
        setOption("xml.doctype", s);
    }

} // end Task
