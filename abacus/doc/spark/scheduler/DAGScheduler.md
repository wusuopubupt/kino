### DAGScheduler

#### 1. DAGScheduler Transforming RDD Lineage(逻辑计划) Into Stage DAG（物理计划）
#### 2. DAGSchedulerEventLoop：
```scala
/* 继承自EventLoop抽象类 */
private[scheduler] class DAGSchedulerEventProcessLoop(dagScheduler: DAGScheduler)
  extends EventLoop[DAGSchedulerEvent]("dag-scheduler-event-loop") with Logging {

  private[this] val timer = dagScheduler.metricsSource.messageProcessingTimer

  /**
   * The main event loop of the DAG scheduler.
   * 重载onReceive方法
   */
  override def onReceive(event: DAGSchedulerEvent): Unit = {
    val timerContext = timer.time()
    try {
      // 具体的onReceive处理逻辑
      doOnReceive(event)
    } finally {
      timerContext.stop()
    }
  }
  
  /**
   * 接收一个DAGSchedulerEvent，通过模式匹配处理不同类型的event
   * event的定义见下面的DAGSchedulerEvent
   */
  private def doOnReceive(event: DAGSchedulerEvent): Unit = event match {
    case JobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties) =>
      dagScheduler.handleJobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties)

    case MapStageSubmitted(jobId, dependency, callSite, listener, properties) =>
      dagScheduler.handleMapStageSubmitted(jobId, dependency, callSite, listener, properties)

    case StageCancelled(stageId) =>
      dagScheduler.handleStageCancellation(stageId)

    case JobCancelled(jobId) =>
      dagScheduler.handleJobCancellation(jobId)

    case JobGroupCancelled(groupId) =>
      dagScheduler.handleJobGroupCancelled(groupId)

    case AllJobsCancelled =>
      dagScheduler.doCancelAllJobs()

    case ExecutorAdded(execId, host) =>
      dagScheduler.handleExecutorAdded(execId, host)

    case ExecutorLost(execId) =>
      dagScheduler.handleExecutorLost(execId, fetchFailed = false)

    case BeginEvent(task, taskInfo) =>
      dagScheduler.handleBeginEvent(task, taskInfo)

    case GettingResultEvent(taskInfo) =>
      dagScheduler.handleGetTaskResult(taskInfo)

    case completion @ CompletionEvent(task, reason, _, _, taskInfo, taskMetrics) =>
      dagScheduler.handleTaskCompletion(completion)

    case TaskSetFailed(taskSet, reason, exception) =>
      dagScheduler.handleTaskSetFailed(taskSet, reason, exception)

    case ResubmitFailedStages =>
      dagScheduler.resubmitFailedStages()
  }

  override def onError(e: Throwable): Unit = {
    logError("DAGSchedulerEventProcessLoop failed; shutting down SparkContext", e)
    try {
      dagScheduler.doCancelAllJobs()
    } catch {
      case t: Throwable => logError("DAGScheduler failed to cancel all jobs.", t)
    }
    dagScheduler.sc.stop()
  }

  override def onStop(): Unit = {
    // Cancel any active jobs in postStop hook
    dagScheduler.cleanUpAfterSchedulerStop()
  }
}
```

#### DAGSchedulerEvent
```scala
package org.apache.spark.scheduler

import java.util.Properties

import scala.collection.Map
import scala.language.existentials

import org.apache.spark._
import org.apache.spark.executor.TaskMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.util.CallSite

/**
 * A sealed trait can be extended only in the same file as its declaration.
 *
 * Types of events that can be handled by the DAGScheduler. The DAGScheduler uses an event queue
 * architecture where any thread can post an event (e.g. a task finishing or a new job being
 * submitted) but there is a single "logic" thread that reads these events and takes decisions.
 * This greatly simplifies synchronization.
 */
 
private[scheduler] sealed trait DAGSchedulerEvent

/** A result-yielding job was submitted on a target RDD */
private[scheduler] case class JobSubmitted(
    jobId: Int,
    finalRDD: RDD[_],
    func: (TaskContext, Iterator[_]) => _,
    partitions: Array[Int],
    callSite: CallSite,
    listener: JobListener,
    properties: Properties = null)
  extends DAGSchedulerEvent

/** A map stage as submitted to run as a separate job */
private[scheduler] case class MapStageSubmitted(
  jobId: Int,
  dependency: ShuffleDependency[_, _, _],
  callSite: CallSite,
  listener: JobListener,
  properties: Properties = null)
  extends DAGSchedulerEvent

private[scheduler] case class StageCancelled(stageId: Int) extends DAGSchedulerEvent

private[scheduler] case class JobCancelled(jobId: Int) extends DAGSchedulerEvent

private[scheduler] case class JobGroupCancelled(groupId: String) extends DAGSchedulerEvent

private[scheduler] case object AllJobsCancelled extends DAGSchedulerEvent

private[scheduler]
case class BeginEvent(task: Task[_], taskInfo: TaskInfo) extends DAGSchedulerEvent

private[scheduler]
case class GettingResultEvent(taskInfo: TaskInfo) extends DAGSchedulerEvent

private[scheduler] case class CompletionEvent(
    task: Task[_],
    reason: TaskEndReason,
    result: Any,
    accumUpdates: Map[Long, Any],
    taskInfo: TaskInfo,
    taskMetrics: TaskMetrics)
  extends DAGSchedulerEvent

private[scheduler] case class ExecutorAdded(execId: String, host: String) extends DAGSchedulerEvent

private[scheduler] case class ExecutorLost(execId: String) extends DAGSchedulerEvent

private[scheduler]
case class TaskSetFailed(taskSet: TaskSet, reason: String, exception: Option[Throwable])
  extends DAGSchedulerEvent

private[scheduler] case object ResubmitFailedStages extends DAGSchedulerEvent

```


