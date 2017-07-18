### LinstenerBus

LinstenerBus是一个trait, 提供2个泛型参数： Linstener的类型L, 和事件的类型E
提供了listener注册和event分发的功能


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