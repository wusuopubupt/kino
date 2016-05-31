#!/bin/bash
# build
sbt package
# submit 
spark-submit --class "Sort" --master local[4] target/scala-2.10/sort_2.10-1.0.jar
