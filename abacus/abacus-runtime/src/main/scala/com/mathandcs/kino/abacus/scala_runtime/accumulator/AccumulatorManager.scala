package com.mathandcs.kino.abacus.scala_runtime.accumulator

import java.io.File

import com.google.gson.Gson
import com.mathandcs.kino.abacus.common.Format
import com.mathandcs.kino.abacus.scala_runtime.io.DataWriter
import com.mathandcs.kino.abacus.scala_runtime.io.codec.Codec
import com.mathandcs.kino.abacus.scala_runtime.utils.ScalaUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.Logging

/**
  * read and write accumulator
  */

trait AccumulatorManager {

  var accumulatorPath: String = _
  var accumulator: Accumulator = _

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

  private var fs : FileSystem = _

  def this(accumulatorPath: String) = {
    this
    this.accumulatorPath = accumulatorPath
    this.fs = new Path(accumulatorPath).getFileSystem(new Configuration())
    this.accumulator = new Accumulator
  }

  override def getAccumulator() = {
    super.getAccumulator()
  }

  override def updateAccumulator(): Unit = {}

  override def saveAccumulator(): Unit = {
    val accumulatorFile = new Path(accumulatorPath)
    if(fs.exists(accumulatorFile)) {
      log.warn(s"Accumulator file ${accumulatorPath} already existed, deleting..." )
      fs.delete(accumulatorFile, true)
    }
    val fos = fs.create(accumulatorFile)
    ScalaUtils.tryWithSafeFinally{
      //fos.write(Codec.serialize(accumulator))
      fos.write(new Gson().toJson(accumulator).getBytes)
    } {
      fos.close()
    }
  }

}
