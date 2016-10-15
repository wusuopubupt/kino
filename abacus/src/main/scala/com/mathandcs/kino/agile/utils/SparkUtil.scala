package com.mathandcs.kino.agile.utils

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dashwang on 10/11/16.
  */

object SparkUtil {

  val sparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("spark-app-template"))

}