<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : options-to-task 
    Created on : September 14, 2004 
    Author     : Jens Gulden
    Comment    : Create source code for BeautyJ ANT-Task wrapper 
                 by generating setter-methods from the application-xml.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:output method="text" encoding="ISO-8859-1"/>

	<!--    
    <xsl:strip-space elements="..."/>
    <xsl:preserve-space elements="..."/>
    -->


    <!-- template rule matching source root element -->

    <xsl:template match="/">
/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.application.beautyj.ant.Task
 * Version: 1.1
 *
 * AUTO-GENERATED at build-time by make/options-to-task.xslt
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

    public GenericApplication createWrappedApplication() {
        return new BeautyJ();
    }
    
    /*protected void setOption(String option, String value) {
      	getApplication().getOptions().getOptionEntry(option).getValue(Option.STATE_CURRENT).set(value);
    }*/
    
    <xsl:apply-templates select="application/options"/>
}
    </xsl:template>


    <xsl:template match="options">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="options-group">
        <xsl:apply-templates/>
    </xsl:template>



    <xsl:template match="option[not (@system or @constant)]">
		<xsl:variable name="optionName"><xsl:value-of select="@name"/></xsl:variable>
		<xsl:variable name="setterName"><xsl:value-of select="concat( translate(substring($optionName, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), translate(substring($optionName, 2), '.-', '__'))"/></xsl:variable>
		<xsl:variable name="shortcutName"><xsl:value-of select="concat( translate(substring(@shortcut,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), translate(substring(@shortcut, 2), '.-', '__'))"/></xsl:variable>
		/**
		* <xsl:value-of select="normalize-space(metadata/description/langstring)"/>
<xsl:if test="default-value">
        * (Default: <xsl:value-of select="normalize-space(default-value)"/>)</xsl:if>
		*/    
        public void set<xsl:value-of select="$setterName"/>(String s) {
        	setOption("<xsl:value-of select="$optionName"/>", s);
        }

		<xsl:if test="$shortcutName!=''">
		/**
		* <xsl:value-of select="normalize-space(metadata/description/langstring)"/>
<xsl:if test="default-value">
        * (Default: <xsl:value-of select="normalize-space(default-value)"/>)</xsl:if>
		*/    
        public void set<xsl:value-of select="$shortcutName"/>(String s) {
        	setOption("<xsl:value-of select="$optionName"/>", s);
        }
		</xsl:if>
    </xsl:template>


    <xsl:template match="*" priority="-1"/>


</xsl:stylesheet> 