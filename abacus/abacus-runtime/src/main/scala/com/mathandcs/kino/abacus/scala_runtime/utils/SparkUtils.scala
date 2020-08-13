package com.mathandcs.kino.abacus.scala_runtime.utils

import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dashwang on 10/11/16.
  */
object SparkUtils {

  val sparkConf = new SparkConf().setMaster("local[*]").setAppName("spark-app-template")

  val sparkContext = new SparkContext(sparkConf)

  var withHiveContext: Boolean = false

  // function should be called precede sqlContext
  def switchToHiveContext() = {
    withHiveContext = true
    this
  }

  lazy val sqlContext = if (withHiveContext) getHiveContext else getSqlContext

  lazy val javaSparkContext = getJavaSparkContext

  private def getHiveContext = new HiveContext(sparkContext)

  private def getSqlContext = new SQLContext(sparkContext)

  private def getJavaSparkContext = new JavaSparkContext(sparkContext)

  private val SPARK_SHUFFLE_MANAGER = "spark.shuffle.manager"
  private val SPARK_SHUFFLE_CONSOLIDATE = "spark.shuffle.consolidateFiles"

  // after spark-1.3, the default shuffle strategy is sort shuffle
  private lazy val originShuffleStrategy = SparkUtils.sparkConf.get(SPARK_SHUFFLE_MANAGER)

  private lazy val originShuffleConsolidate = SparkUtils.sparkConf.get(SPARK_SHUFFLE_CONSOLIDATE)

  // set to hash shuffle strategy with file consolidation
  def setShuffleStrategyToHashWithConsolidation(): Unit = {
    SparkUtils.sparkConf.set(SPARK_SHUFFLE_MANAGER, "hash")
    SparkUtils.sparkConf.set(SPARK_SHUFFLE_CONSOLIDATE, "true")
  }

  def recoverShuffleStrategy(): Unit = {
    SparkUtils.sparkConf.set(SPARK_SHUFFLE_MANAGER, originShuffleStrategy)
    SparkUtils.sparkConf.set(SPARK_SHUFFLE_CONSOLIDATE, originShuffleConsolidate)
  }


}