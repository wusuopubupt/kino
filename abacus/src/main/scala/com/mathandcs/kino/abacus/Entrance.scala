package com.mathandcs.kino.abacus

import com.mathandcs.kino.abacus.app.AppFactory
import com.mathandcs.kino.abacus.accumulator.{AccumulatorListener, HDFSAccumulatorManager}
import com.mathandcs.kino.abacus.utils.SparkUtils
import org.apache.spark.Logging

/**
  * Created by dash wang on 5/11/16.
  */
object Entrance extends Logging {

  def main(args: Array[String]) {
    addListener()

    val appClassName = args(0)
    val appConfigFile = args(1)
    val app = AppFactory.produce(appClassName)
    app.execute(appConfigFile)
  }

  def addListener() = {
    val sc = SparkUtils.sparkContext
    val accumulatorManager = new HDFSAccumulatorManager("hdfs://baidu-hadoop-yf-105:8020/tmp/a")
    val listener = new AccumulatorListener(accumulatorManager)
    sc.addSparkListener(listener)
  }

}
