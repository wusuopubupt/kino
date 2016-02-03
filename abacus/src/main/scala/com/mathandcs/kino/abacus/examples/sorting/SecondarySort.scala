package com.mathandcs.kino.abacus.examples.sorting

import org.apache.spark.Partitioner

/**
  * Created by dashwang on 05/12/16.
  */
class SecondarySort {
  
}


case class FlightItem(val key: String, val score:Double, val label: Double)

object FlightItem {
  // ordering: key ASC, then score DESC
  implicit def orderingByKeyVal[A <: FlightItem] : Ordering[A] = {
    Ordering.by(fi => (fi.key, fi.score * -1))
  }
}

class FlightItemPartitioner(partitions: Int) extends Partitioner {
  require(partitions > 0,  s"Number of partitions ($partitions) cannot be negative.")

  override def numPartitions: Int = partitions

  override def getPartition(key: Any) :Int = {
    val k = key.asInstanceOf[FlightItem]
    k.key.hashCode() % numPartitions
  }
}

