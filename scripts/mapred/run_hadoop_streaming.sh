#!/bin/bash

$HADOOP_HOME/bin/hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-2.7.1.jar \
	-D mapreduce.job.reduces=0 \
	-D mapred.job.name="enlarge_miaoche_data" \
	-input /user/dashwang/data/miaoche/instance_intent_0301_0314 \
	-output /user/dashwang/data/miaoche/140day \
	-file ./enlarge_data.sh \
    -mapper enlarge_data.sh
	
