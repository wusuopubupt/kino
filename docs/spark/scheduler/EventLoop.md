### EventLoop

#### 

EventLoop.scala源码如下：

```scala

package org.apache.spark.util

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, LinkedBlockingDeque}

import scala.util.control.NonFatal

import org.apache.spark.Logging

/**
 * An event loop to receive events from the caller and process all events in the event thread. It
 * will start an exclusive event thread to process all events.
 *
 * Note: The event queue will grow indefinitely. So subclasses should make sure `onReceive` can
 * handle events in time to avoid the potential OOM.
 */
private[spark] abstract class EventLoop[E](name: String) extends Logging {

  // event队列
  private val eventQueue: BlockingQueue[E] = new LinkedBlockingDeque[E]()

  // 终止标识
  private val stopped = new AtomicBoolean(false)

  // loop线程
  private val eventThread = new Thread(name) {
    setDaemon(true)

    override def run(): Unit = {
      try {
        while (!stopped.get) {
          // 从队列获取event
          val event = eventQueue.take()
          try {
            // 抽象方法，由子类实现处理event的方法
            onReceive(event)
          } catch {
            case NonFatal(e) => {
              try {
                onError(e)
              } catch {
                case NonFatal(e) => logError("Unexpected error in " + name, e)
              }
            }
          }
        }
      } catch {
        case ie: InterruptedException => // exit even if eventQueue is not empty
        case NonFatal(e) => logError("Unexpected error in " + name, e)
      }
    }

  }

  /* 启动loop线程 */
  def start(): Unit = {
    if (stopped.get) {
      throw new IllegalStateException(name + " has already been stopped")
    }
    // Call onStart before starting the event thread to make sure it happens before onReceive
    onStart()
    eventThread.start()
  }

  /* 终止loop线程 */
  def stop(): Unit = {
    // stopped == false时，执行CAS操作置为true
    if (stopped.compareAndSet(false, true)) {
      eventThread.interrupt()
      var onStopCalled = false
      try {
        eventThread.join()
        // Call onStop after the event thread exits to make sure onReceive happens before onStop
        onStopCalled = true
        onStop()
      } catch {
        case ie: InterruptedException =>
          Thread.currentThread().interrupt()
          if (!onStopCalled) {
            // ie is thrown from `eventThread.join()`. Otherwise, we should not call `onStop` since
            // it's already called.
            onStop()
          }
      }
    } else {
      // Keep quiet to allow calling `stop` multiple times.
    }
  }

  /**
   * Put the event into the event queue. The event thread will process it later.
   */
  def post(event: E): Unit = {
    eventQueue.put(event)
  }

  /**
   * Return if the event thread has already been started but not yet stopped.
   */
  def isActive: Boolean = eventThread.isAlive

  /**
   * Invoked when `start()` is called but before the event thread starts.
   */
  protected def onStart(): Unit = {}

  /**
   * Invoked when `stop()` is called and the event thread exits.
   */
  protected def onStop(): Unit = {}

  /**
   * Invoked in the event thread when polling events from the event queue.
   *
   * Note: Should avoid calling blocking actions in `onReceive`, or the event thread will be blocked
   * and cannot process events in time. If you want to call some blocking actions, run them in
   * another thread.
   * (onReceive的实现不要有阻塞操作，如果有则新起一个线程做)
   *
   */
  protected def onReceive(event: E): Unit

  /**
   * Invoked if `onReceive` throws any non fatal error. Any non fatal error thrown from `onError`
   * will be ignored.
   */
  protected def onError(e: Throwable): Unit

}

```
