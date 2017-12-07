#!/usr/bin/env bash

# JMC测试

ENTRANCE=com.mathandcs.kino.effectivejava.jvm.JIT.JITDemo

# replace with your path
cd /Users/dashwang/Project/github/wusuopubupt/kino/effective-java/src/main/java

# jdk1.8+
$JAVA_HOME/bin/javac com/mathandcs/kino/effectivejava/jvm/JIT/JITDemo.java

OPT="-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly"

$JAVA_HOME/bin/java $OPT -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -cp ./ $ENTRANCE
