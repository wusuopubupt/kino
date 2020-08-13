package com.mathandcs.kino.abacus.scala_runtime.accumulator

import org.apache.spark.executor.TaskMetrics
import org.apache.spark.scheduler._
import org.apache.spark.{ExceptionFailure, Logging, TaskFailedReason}


class AccumulatorListener extends SparkListener with Logging {

  private var accumulatorManager: AccumulatorManager = _

  /**
    * Auxiliary constructor
    */
  def this(accumulatorManager: AccumulatorManager) = {
    this()
    this.accumulatorManager = accumulatorManager
  }

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = synchronized {
    val counter = accumulatorManager.getAccumulator()
    val stage = stageCompleted.stageInfo
    if (stage.failureReason.isEmpty) {
      counter.numCompletedStages += 1
    } else {
      counter.numFailedStages += 1
    }

    accumulatorManager.updateAndSaveAccumulator()
  }

  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = synchronized {
    val counter = accumulatorManager.getAccumulator()
    jobEnd.jobResult match {
      case JobSucceeded =>
        counter.numCompletedJobs += 1
      case _ =>
        counter.numFailedJobs += 1
    }
  }

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = synchronized {
    val counter = accumulatorManager.getAccumulator()
    val info = taskEnd.taskInfo
    if (info != null && taskEnd.stageAttemptId != -1) {
      val metrics: Option[TaskMetrics] =
        taskEnd.reason match {
          case org.apache.spark.Success =>
            counter.numCompletedTasks += 1
            Option(taskEnd.taskMetrics)
          case e: ExceptionFailure =>
            counter.numFailedTasks += 1
            e.metrics
          case e: TaskFailedReason =>
            counter.numFailedTasks += 1
            None
        }

      if (!metrics.isEmpty) {
        val taskMetrics = metrics.get
        counter.executorRunTime += taskMetrics.executorRunTime
        counter.executorGCTime += taskMetrics.jvmGCTime
        counter.resultSize += taskMetrics.resultSize
        if (!taskMetrics.inputMetrics.isEmpty) {
          counter.numInputRecords += taskMetrics.inputMetrics.get.recordsRead
        }
        if (!taskMetrics.outputMetrics.isEmpty) {
          counter.numOutputRecords += taskMetrics.outputMetrics.get.recordsWritten
        }
      }
    }

    accumulatorManager.updateAndSaveAccumulator()
  }
}
