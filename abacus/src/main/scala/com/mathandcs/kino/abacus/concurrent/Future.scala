package com.mathandcs.kino.abacus.concurrent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.util.Success

/**
  * Created by wangdongxu on 10/12/17.
  * ref: https://stackoverflow.com/questions/34808024/how-do-i-wait-for-a-scala-futures-onsuccess-callback-to-complete
  */
object FutureAndPromise {

  def waitFuture(): Unit = {
    val s = "Hello"
    val f: Future[String] = Future {
      println(s + " World!")
      s + " World!"
    }
    f.onComplete {
      case Success(res) =>  {
        Thread.sleep(10000)
        println("The program waited patiently for this callback to finish. result is: " + res)
      }
    }

    // This waits for `f` to complete but doesn't wait for the callback
    // to finish running.
    Await.ready(f, Duration.Inf)
  }

  def waitFutureAndCallback(): Unit = {
    val s = "Hello"
    val f: Future[String] = Future{
      println(s + " World!")
      s + " World!"
    }
    val p = Promise[Unit]

    f.onComplete {
      case Success(res) =>  {
        Thread.sleep(1000)
        println("The program waited patiently for this callback to finish. result is: " + res)
        p.success()
      }
    }

    Await.ready(f, Duration.Inf)
    Await.ready(p.future, Duration.Inf)
  }

  def main(args: Array[String]): Unit = {
    // just wait future done
    waitFuture()

    // wait both future and it's callback done
    waitFutureAndCallback()
  }
}
