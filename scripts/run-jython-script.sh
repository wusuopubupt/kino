#!/bin/bash

cd ..
mvn -DskipTests clean package

ENTRANCE="com.mathandcs.kino.effectivejava.jvm.jython.PythonScriptEvaluator"
java -cp effective-java/target/effective-java-1.0-SNAPSHOT.jar:effective-java/target/dependency/*  $ENTRANCE
