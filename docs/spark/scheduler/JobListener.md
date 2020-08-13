### JobListener

#### JobListener trait:
用于监听Job运行结束(成功/失败)事件，声明了taskSucceeded和jobFailed方法

```scala
package org.apache.spark.scheduler

/**
 * Interface used to listen for job completion or failure events after submitting a job to the
 * DAGScheduler. The listener is notified each time a task succeeds, as well as if the whole
 * job fails (and no further taskSucceeded events will happen).
 */
private[spark] trait JobListener {
  def taskSucceeded(index: Int, result: Any)
  def jobFailed(exception: Exception)
}

```

#### JobResult
JobResult有2种类型: JobSucceeded和JobFailed

```scala
package org.apache.spark.scheduler

import org.apache.spark.annotation.DeveloperApi

/**
 * :: DeveloperApi ::
 * A result of a job in the DAGScheduler.
 */
@DeveloperApi
sealed trait JobResult

@DeveloperApi
case object JobSucceeded extends JobResult

@DeveloperApi
private[spark] case class JobFailed(exception: Exception) extends JobResult
```

#### JobWatcher:

```scala
/**
 * An object that waits for a DAGScheduler job to complete. As tasks finish, it passes their
 * results to the given handler function.
 */
private[spark] class JobWaiter[T](
    dagScheduler: DAGScheduler,
    val jobId: Int,
    totalTasks: Int,
    resultHandler: (Int, T) => Unit)
  extends JobListener {

  private var finishedTasks = 0

  // Is the job as a whole finished (succeeded or failed)?
  @volatile
  private var _jobFinished = totalTasks == 0

  def jobFinished: Boolean = _jobFinished

  // If the job is finished, this will be its result. In the case of 0 task jobs (e.g. zero
  // partition RDDs), we set the jobResult directly to JobSucceeded.
  private var jobResult: JobResult = if (jobFinished) JobSucceeded else null

  /**
   * Sends a signal to the DAGScheduler to cancel the job. The cancellation itself is handled
   * asynchronously. After the low level scheduler cancels all the tasks belonging to this job, it
   * will fail this job with a SparkException.
   */
  def cancel() {
    dagScheduler.cancelJob(jobId)
  }
  
  /**
   * 同步操作，判断finishedTasks是否等于totalTaks，如果是则更新jobResult为JobSucceeded，并执行notifyAll方法唤醒等待的线程
   */
  override def taskSucceeded(index: Int, result: Any): Unit = synchronized {
    if (_jobFinished) {
      throw new UnsupportedOperationException("taskSucceeded() called on a finished JobWaiter")
    }
    // 调用resultHandler处理成功事件
    resultHandler(index, result.asInstanceOf[T])
    finishedTasks += 1
    if (finishedTasks == totalTasks) {
      _jobFinished = true
      // JobSucceeded是JobResult的case class
      jobResult = JobSucceeded
      this.notifyAll()
    }
  }

  /**
   * 同步操作，更新私有成员jobResult的值为JobFailed，并执行notifyAll方法唤醒等待的线程
   */
  override def jobFailed(exception: Exception): Unit = synchronized {
    _jobFinished = true
    // JobFailed是JobResult的case class
    jobResult = JobFailed(exception)
    this.notifyAll()
  }

  /**
   * 同步操作，当前线程等待，直到其他线程对当前对象调用notify或者notifyAll方法
   */
  def awaitResult(): JobResult = synchronized {
    while (!_jobFinished) {
      this.wait()
    }
    return jobResult
  }
}
```