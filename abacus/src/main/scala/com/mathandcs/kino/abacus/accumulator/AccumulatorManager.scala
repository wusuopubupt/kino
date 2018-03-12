package com.mathandcs.kino.abacus.accumulator

import org.apache.spark.Logging

/**
  * read and write accumulator
  */

trait AccumulatorManager {

  var accumulatorPath: String = _
  var accumulator: Accumulator = _

  def init(accumulatorPath: String) = {
    this.accumulatorPath = accumulatorPath
    this.accumulator = new Accumulator
  }

  def getAccumulator() = {
    accumulator
  }

  def updateAccumulator()

  def saveAccumulator()

  def updateAndSaveAccumulator() = {
    updateAccumulator()
    saveAccumulator()
  }

}

class HDFSAccumulatorManager extends AccumulatorManager with Logging {

  override def getAccumulator() = {
    super.getAccumulator()
  }

  def read() = {

  }

  def write(Accumulator: Accumulator) = {

  }

  override def updateAccumulator(): Unit = {}

  override def saveAccumulator(): Unit = {}
}
