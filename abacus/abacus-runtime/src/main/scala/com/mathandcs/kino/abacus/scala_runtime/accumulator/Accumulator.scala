package com.mathandcs.kino.abacus.scala_runtime.accumulator

import java.util

import com.mathandcs.kino.abacus.scala_runtime.utils.FormatUtils
import org.apache.spark.Logging

import scala.collection.mutable

class Accumulator() extends Logging with Serializable {

  var numCompletedJobs: Int = 0
  var numFailedJobs: Int = 0
  var numCompletedStages: Int = 0
  var numFailedStages: Int = 0
  var numCompletedTasks: Int = 0
  var numFailedTasks: Int = 0
  var executorRunTime: Long = 0
  var executorGCTime: Long = 0
  var resultSize: Long = 0
  var numInputRecords: Long = 0
  var numOutputRecords: Long = 0

  val application = mutable.Map[String, String]()

  var app: App = _

  def toMap(): util.Map[String, String] = {
    val map: util.Map[String, String] = new util.HashMap[String, String]()
    for ((k, v) <- application) {
      map.put(k, v)
    }
    map.put("numCompletedJobs", numCompletedJobs.toString)
    map.put("numFailedJobs", numFailedJobs.toString)
    map.put("numCompletedStages", numCompletedStages.toString)
    map.put("numFailedStages", numFailedStages.toString)
    map.put("numCompletedTasks", numCompletedTasks.toString)
    map.put("numFailedTasks", numFailedTasks.toString)
    map.put("executorRunTime", FormatUtils.formatDuration(executorRunTime))
    map.put("executorGCTime", FormatUtils.formatDuration(executorGCTime))
    map.put("resultSize", FormatUtils.formatBytes(resultSize))
    map.put("numInputRecords", FormatUtils.formatBytes(numInputRecords))
    map.put("numOutputRecords", FormatUtils.formatBytes(numOutputRecords))
    map
  }

  def toSparkAccumulatorMap(): util.Map[String, util.Map[String, String]] = {
    val map = new util.HashMap[String, util.Map[String, String]]()
    map.put("sparkAccumulator", this.toMap)
    map
  }
}