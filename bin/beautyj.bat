@echo off

rem - Wrapper script for running BeautyJ on a Windows console.
rem - (c) Jens Gulden 2002-2004
rem - Licensed under the GNU Public License (GPL).
rem - This comes with NO WARRANTY, see file license.txt.

rem - IMPORTANT: Edit the line below to match your installation directory,
rem -            if the default does not work on your configuration.
rem -            E.g., "set BEAUTYJ_HOME=c:\java\beautyJ-1_1"
set BEAUTYJ_HOME=%0\..\..

rem - If the Java runtime executable is not available on your PATH,
rem - uncomment and edit the lines below:
REM set JAVA_HOME=c:\java\j2sdk1.4.1
REM set PATH=%PATH%;%JAVA_HOME%\bin

rem - Additional classpath - add your classes and archives here:
set CP=

rem - run BeautyJ:
java -cp %BEAUTYJ_HOME%\lib\beautyj.jar;%CP% beautyj %1 %2 %3 %4 %5 %6 %7 %8
