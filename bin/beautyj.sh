# Wrapper script for running BeautyJ on a Linux console.
# (c) Jens Gulden 2002-2004
# Licensed under the GNU Public License (GPL).
# This comes with NO WARRANTY, see file license.txt.

# IMPORTANT: Edit the line below to match your installation directory.
BEAUTYJ_HOME=/usr/java/beautyJ-1_1

# Make sure that the JAVA_HOME variable is set correctly,
# otherwise uncomment and edit the line below.
# JAVA_HOME=/usr/java/j2sdk1.4.2_04

# Additional classpath - add your classes and archives here:
CP=

# run BeautyJ:
$JAVA_HOME/bin/java -cp $BEAUTYJ_HOME/lib/beautyj.jar:$CP beautyj $1 $2 $3 $4 $5 $6 $7 $8
