package com.mathandcs.kino.abacus

import com.mathandcs.kino.abacus.app.AppFactory
import com.mathandcs.kino.abacus.accumulator.AccumulatorListener
import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.Logging

/**
  * Created by dash wang on 5/11/16.
  */
object Entrance extends Logging {

  def main(args: Array[String]) {
    //addListener()

    val appClassName = args(0)
    val appConfigFile = args(1)
    val app = AppFactory.produce(appClassName)
    app.execute(appConfigFile)
  }

  def addListener() = {
    val sc = SparkUtil.sparkContext
    val listener = new AccumulatorListener()
    sc.addSparkListener(listener)
  }

}
