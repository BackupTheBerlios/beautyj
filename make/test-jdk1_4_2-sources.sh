#!/bin/sh

# For testing: runs BeautyJ on the original J2SDK 1.4.2 sources. 
# Afterwards the beautyified sources get compiled to practically verify
# correct beautification.    	
# The original JDK 1.4.2 sources are supposed to be available in 
# directory /usr/java/j2sdk1.4.2_04/src/.

JAVA=/usr/java/java
BEAUTYJ=/usr/java/beautyJ-1_1
# Some Xalan-packages are needed to qualify/compile sources in package org.*
XALAN=/usr/java/xalan
BUILD=/home/jgulden/tmp/java2

rm -r $BUILD/src
mkdir $BUILD/src

for package in `ls $JAVA/src`; do
  $JAVA/bin/java -Xmx256M -cp $BEAUTYJ/lib/beautyj.jar:$XALAN/bin/xalan.jar beautyj -verbose -d $BUILD/src -project.name "TEST TEST TEST - BeautyJ - TEST TEST TEST" -project.description "Contains auto-generated Javadoc comments created by BeautyJ." -code.qualify -code.preserve.fields.order -field.create.text description -method.create.text "description,param,return,throws" $JAVA/src/$package ;
done

rm -r $BUILD/classes
mkdir $BUILD/classes

cd $BUILD/src

# Compilation here might fail because 'find ...' delivers too many results for a command-line call
# (use ANT to compile if that happens):

$JAVA/bin/java -Xmx400M -cp $JAVA/lib/tools.jar:$XALAN/bin/xalan.jar  com.sun.tools.javac.Main  -d ../classes -source 1.4 `find -name *.java`

# Note: The tested version fails with 4 compilation errors, but these errors
#       are the same after _and before_ beautification (i.e., the original 
#       source codes as supplied with the Linux version of Sun's J2SDK 1.4.2_04
#       fail to compile with these 4 errors, too).
