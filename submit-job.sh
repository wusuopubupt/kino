#!/bin/bash
/usr/local/spark/bin/spark-submit --class "FeatureTrans" --master local[*] target/scala-2.10/kino_2.10-1.0.jar

#/usr/local/spark/bin/spark-submit --class "FeatureTrans" --master yarn-cluster  --executor-memory 20G  --num-executors 4 --driver-memory 15g target/scala-2.10/kino_2.10-1.0.jar
