@echo off

rem - Wrapper script for running BeautyJ on a Windows console.
rem - (c) Jens Gulden 2002
rem - Licensed under the GNU Public License (GPL).
rem - This comes with NO WARRANTY, see file license.txt.

rem - IMPORTANT: Edit the line below to match your installation directory,
rem -            if the default does not work on your configuration.
rem -            E.g., "set BEAUTYJ_HOME=c:\java\beautyJ-1_0"
set BEAUTYJ_HOME=%0\..\..

rem - IMPORTANT: Make sure that the JAVA_HOME variable is set correctly,
rem - or uncomment and edit the line below.
REM set JAVA_HOME=c:\java\j2sdk1.4.1

rem - run BeautyJ:
%JAVA_HOME%\bin\java -jar %BEAUTYJ_HOME%\lib\beautyj.jar %1 %2 %3 %4 %5 %6 %7 %8
