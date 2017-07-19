### LinstenerBus

#### 1. LiveListenerBus
Spark的DAGScheduler内部的listenerBus是LiveListenerBus类， 继承自AsynchronousListenerBus和SparkListenrBus,声明如下：

```scala
package org.apache.spark.scheduler

import java.util.concurrent.atomic.AtomicBoolean
import org.apache.spark.util.AsynchronousListenerBus

/**
 * Asynchronously passes SparkListenerEvents to registered SparkListeners.
 *
 * Until start() is called, all posted events are only buffered. Only after this listener bus
 * has started will events be actually propagated to all attached listeners. This listener bus
 * is stopped when it receives a SparkListenerShutdown event, which is posted using stop().
 */
private[spark] class LiveListenerBus
  extends AsynchronousListenerBus[SparkListener, SparkListenerEvent]("SparkListenerBus")
  with SparkListenerBus {

  private val logDroppedEvent = new AtomicBoolean(false)

  override def onDropEvent(event: SparkListenerEvent): Unit = {
    if (logDroppedEvent.compareAndSet(false, true)) {
      // Only log the following message once to avoid duplicated annoying logs.
      logError("Dropping SparkListenerEvent because no remaining room in event queue. " +
        "This likely means one of the SparkListeners is too slow and cannot keep up with " +
        "the rate at which tasks are being started by the scheduler.")
    }
  }

}
```

#### 2. LinstenerBus
上面AsynchronousListenerBus和SparkListenrBus都继承自LinstenerBus, 它是一个trait, 提供2个泛型参数： Linstener的类型L, 和事件的类型E,负责listener注册和event分发的功能


```scala
package org.apache.spark.util

import java.util.concurrent.CopyOnWriteArrayList

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.control.NonFatal

import org.apache.spark.Logging

/**
 * An event bus which posts events to its listeners.
 */
private[spark] trait ListenerBus[L <: AnyRef, E] extends Logging {

  // ArrayList的线程安全实现
  private[spark] val listeners = new CopyOnWriteArrayList[L]

  /**
   * 添加listener, 对各个线程是线程安全的
   */
  final def addListener(listener: L) {
    listeners.add(listener)
  }

  /**
   * Post the event to all registered listeners. The `postToAll` caller should guarantee calling
   * `postToAll` in the same thread for all events
   * 
   * 把event传递到所有注册过的listener
   */
  final def postToAll(event: E): Unit = {
    // JavaConverters can create a JIterableWrapper if we use asScala.
    // However, this method will be called frequently. To avoid the wrapper cost, here ewe use
    // Java Iterator directly.
    val iter = listeners.iterator
    while (iter.hasNext) {
      val listener = iter.next()
      try {
        onPostEvent(listener, event)
      } catch {
        case NonFatal(e) =>
          logError(s"Listener ${Utils.getFormattedClassName(listener)} threw an exception", e)
      }
    }
  }

  /**
   * Post an event to the specified listener. `onPostEvent` is guaranteed to be called in the same
   * thread.
   */
  def onPostEvent(listener: L, event: E): Unit

  private[spark] def findListenersByClass[T <: L : ClassTag](): Seq[T] = {
    val c = implicitly[ClassTag[T]].runtimeClass
    listeners.asScala.filter(_.getClass == c).map(_.asInstanceOf[T]).toSeq
  }

}

```

#### 3. SparkListenerBus

泛型参数Linstener的类型L, 和事件的类型E分别具体化为SparkListener和SparkListenerEvent

```scala
private[spark] trait SparkListenerBus extends ListenerBus[SparkListener, SparkListenerEvent] {

  override def onPostEvent(listener: SparkListener, event: SparkListenerEvent): Unit = {
    event match {
      case stageSubmitted: SparkListenerStageSubmitted =>
        listener.onStageSubmitted(stageSubmitted)
      case stageCompleted: SparkListenerStageCompleted =>
        listener.onStageCompleted(stageCompleted)
      case ...
      case logStart: SparkListenerLogStart => // ignore event log metadata
    }
  }
```

#### 4. AsynchronousListenerBus
异步方式传递事件到已注册的Listeners

```scala
package org.apache.spark.util

import java.util.concurrent._
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import scala.util.DynamicVariable
import org.apache.spark.SparkContext

/**
 * Asynchronously passes events to registered listeners.
 *
 * Until `start()` is called, all posted events are only buffered. Only after this listener bus
 * has started will events be actually propagated to all attached listeners. This listener bus
 * is stopped when `stop()` is called, and it will drop further events after stopping.
 *
 * @param name name of the listener bus, will be the name of the listener thread.
 * @tparam L type of listener
 * @tparam E type of event
 */
private[spark] abstract class AsynchronousListenerBus[L <: AnyRef, E](name: String)
  extends ListenerBus[L, E] {

  self =>

  private var sparkContext: SparkContext = null

  /* Cap the capacity of the event queue so we get an explicit error (rather than
   * an OOM exception) if it's perpetually being added to more quickly than it's being drained. 
   *
   * 指定事件队列的容量为10000
   */
  private val EVENT_QUEUE_CAPACITY = 10000
  private val eventQueue = new LinkedBlockingQueue[E](EVENT_QUEUE_CAPACITY)

  // Indicate if `start()` is called, 启动标识
  private val started = new AtomicBoolean(false)
  // Indicate if `stop()` is called， 终止标识
  private val stopped = new AtomicBoolean(false)

  /** A counter for dropped events. It will be reset every time we log it. 
  * 被丢弃事件的计数器，每隔60秒重置成0
  */
  private val droppedEventsCounter = new AtomicLong(0L)

  /** When `droppedEventsCounter` was logged last time in milliseconds. 
  * 最后一次记录丢弃事件数的时间戳，用于控制打日志的时间间隔
  */
  @volatile private var lastReportTimestamp = 0L

  // Indicate if we are processing some event, 正在处理事件的标识
  // Guarded by `self`
  private var processingEvent = false

  // A counter that represents the number of events produced and consumed in the queue
  private val eventLock = new Semaphore(0)

  private val listenerThread = new Thread(name) {
    setDaemon(true)
    override def run(): Unit = Utils.tryOrStopSparkContext(sparkContext) {
      AsynchronousListenerBus.withinListenerThread.withValue(true) {
        while (true) {
          eventLock.acquire()
          // 同步设置正在处理事件标识为true
          self.synchronized {
            processingEvent = true
          }
          try {
            // 从事件队列拉取事件
            val event = eventQueue.poll
            // 如果event为null, 说明listenerBus被停止了
            if (event == null) {
              // Get out of the while loop and shutdown the daemon thread
              if (!stopped.get) {
                throw new IllegalStateException("Polling `null` from eventQueue means" +
                  " the listener bus has been stopped. So `stopped` must be true")
              }
              return
            }
            // 发送到注册的listeners
            postToAll(event)
          } finally {
            // 同步设置正在处理事件标识为false
            self.synchronized {
              processingEvent = false
            }
          }
        }
      }
    }
  }

  /**
   * 启动listenerThread
   * Start sending events to attached listeners.
   *
   * This first sends out all buffered events posted before this listener bus has started, then
   * listens for any additional events asynchronously while the listener bus is still running.
   * This should only be called once.
   *
   * @param sc Used to stop the SparkContext in case the listener thread dies.
   */
  def start(sc: SparkContext) {
    if (started.compareAndSet(false, true)) {
      sparkContext = sc
      listenerThread.start()
    } else {
      throw new IllegalStateException(s"$name already started!")
    }
  }

  /**
   * 向事件队列添加新的事件
   * 如果添加成功，则释放eventLock
   * 如果添加失败(一般是由于队列满了)，则让droppedEventsCounter+1, 同时判断当前时间距离上次打日志的时间是否超出60秒，如果超出则打日志记录并使用CAS操作重置droppedEventsCounter为0
   */
  def post(event: E) {
    if (stopped.get) {
      // Drop further events to make `listenerThread` exit ASAP
      logError(s"$name has already stopped! Dropping event $event")
      return
    }
    val eventAdded = eventQueue.offer(event)
    if (eventAdded) {
      eventLock.release()
    } else {
      onDropEvent(event)
      droppedEventsCounter.incrementAndGet()
    }

    val droppedEvents = droppedEventsCounter.get
    if (droppedEvents > 0) {
      // Don't log too frequently
      if (System.currentTimeMillis() - lastReportTimestamp >= 60 * 1000) {
        // There may be multiple threads trying to decrease droppedEventsCounter.
        // Use "compareAndSet" to make sure only one thread can win.
        // And if another thread is increasing droppedEventsCounter, "compareAndSet" will fail and
        // then that thread will update it.
        if (droppedEventsCounter.compareAndSet(droppedEvents, 0)) {
          val prevLastReportTimestamp = lastReportTimestamp
          lastReportTimestamp = System.currentTimeMillis()
          logWarning(s"Dropped $droppedEvents SparkListenerEvents since " +
            new java.util.Date(prevLastReportTimestamp))
        }
      }
    }
  }

  /**
   * If the event queue exceeds its capacity, the new events will be dropped. The subclasses will be
   * notified with the dropped events.
   * 当事件队列满时，新来的事件会被丢掉，子类需要时间onDropEvent方法做处理
   *
   * Note: `onDropEvent` can be called in any thread.
   */
  def onDropEvent(event: E): Unit
}

private[spark] object AsynchronousListenerBus {
  /* Allows for Context to check whether stop() call is made within listener thread
  */
  val withinListenerThread: DynamicVariable[Boolean] = new DynamicVariable[Boolean](false)
}

```
