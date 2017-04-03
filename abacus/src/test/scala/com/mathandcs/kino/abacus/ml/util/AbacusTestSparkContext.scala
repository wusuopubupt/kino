package com.mathandcs.kino.abacus.ml.util

import org.scalatest.{BeforeAndAfterAll, Suite}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

trait AbacusTestSparkContext extends BeforeAndAfterAll { self: Suite =>
  @transient var sc: SparkContext = _
  @transient var sqlContext: SQLContext = _

  override def beforeAll() {
    super.beforeAll()
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("AbacusUnitTest")
    sc = new SparkContext(conf)
    SQLContext.clearActive()
    sqlContext = new SQLContext(sc)
    SQLContext.setActive(sqlContext)
  }

  override def afterAll() {
    sqlContext = null
    SQLContext.clearActive()
    if (sc != null) {
      sc.stop()
    }
    sc = null
    super.afterAll()
  }
}
