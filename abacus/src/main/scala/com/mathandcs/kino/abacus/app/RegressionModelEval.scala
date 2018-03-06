package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.app.RegressionModelEval._
import com.mathandcs.kino.abacus.config.AppConfig
import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.sql.DataFrame
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

/**
  * Created by dash wang on 2016-08-16.
  */
class RegressionModelEval extends BaseApp {

  implicit val formats = DefaultFormats

  var scoreField: String = null
  var labelField: String = null

  override def run(appConfig: AppConfig): Unit = {
    var rawInputDF = DataImport.loadToDataFrame(appConfig.inputTables(0), null)

    scoreField = appConfig.extra.get("scoreField").toString
    labelField = appConfig.extra.get("labelField").toString

    // check partition not exceeding PARTITION_UPPER_BOUND
    if (PARTITION_UPPER_BOUND < rawInputDF.rdd.getNumPartitions) {
      log.info(s"Input partition exceeds $PARTITION_UPPER_BOUND, repartition into $PARTITION_UPPER_BOUND blocks")
      rawInputDF = rawInputDF.coalesce(PARTITION_UPPER_BOUND)
    }

    val predictionAndLabels = rawInputDF.select(scoreField, labelField)

    log.info("Begin to assemble report info...")
    val metrics = assembleReport(predictionAndLabels)
    log.info("Calculation completed, wrap into metric map.")
    val metricsRdd = SparkUtil.sparkContext.makeRDD(Seq(write(metrics)))

    metricsRdd.saveAsTextFile(appConfig.outputTables(0).url)
  }

  def assembleReport(predictionAndLabels: DataFrame): Metrics = {

    val sc = SparkUtil.sparkContext
    val sumY = sc.accumulator[Double](0.0)
    val sumPrediction = sc.accumulator[Double](0.0)
    val sumAbsoluteDelta = sc.accumulator[Double](0.0)
    val sumSquaredDelta = sc.accumulator[Double](0.0)
    val count = sc.accumulator[Long](0L)

    predictionAndLabels.foreach(row => {
      val prediction = row.getAs[Double](scoreField)
      val y = row.getAs[String](labelField).toDouble
      val delta = y - prediction

      sumY += y
      sumPrediction += prediction
      sumAbsoluteDelta += math.abs(delta)
      sumSquaredDelta += math.pow(delta, 2)
      count += 1L
    })

    val lineNum = count.value
    assert(lineNum > 0)

    new Metrics(
      mse = sumSquaredDelta.value / lineNum,
      mae = sumAbsoluteDelta.value / lineNum,
      yMean = sumY.value / lineNum,
      predictionMean = sumPrediction.value / lineNum
    )
  }

}


object RegressionModelEval {

  /** upper bound in case of two many small partitions */
  val PARTITION_UPPER_BOUND = 20

  case class Metrics(
                      mse: Double,
                      mae: Double,
                      predictionMean: Double,
                      yMean: Double
                    )

}
