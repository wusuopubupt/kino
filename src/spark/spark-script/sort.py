#!/usr/bin/env python
from __future__ import print_function
from pyspark import SparkConf, SparkContext

if __name__ == "__main__":
    conf = (SparkConf()
             .setMaster("local")
             .setAppName("My app")
             .set("spark.executor.memory", "1g"))
    sc = SparkContext(conf = conf)
    # the input file is:
    # spark hadoop hive spark spark hadoop
    text_file = sc.textFile("/user/wangdongxu/input/file1.txt")
    #sortedCount = text_file.flatMap(lambda x: x.split(' ')) \
    #    .map(lambda x: (x, 1)) \
    #    .groupByKey()
    sortedCount = text_file.flatMap(lambda x: x.split(' ')) \
            .map(lambda word: (word, 1)) \
            .reduceByKey(lambda v1, v2: v1+v2) \
            .map(lambda (k,v) : (v,k)) \
            .sortByKey()
    output = sortedCount.collect()
    for (item, count) in output:
        print(item, count)

    # output:
    # 1 hive 
    # 2 hadoop
    # 3 spark
