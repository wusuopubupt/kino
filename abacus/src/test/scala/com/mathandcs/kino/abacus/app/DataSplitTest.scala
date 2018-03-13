package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.utils.HDFSUtils
import org.apache.spark.Logging
import org.scalatest.FlatSpec

/**
  * Created by dash wang on 2/28/17.
  */
class DataSplitTest extends FlatSpec with Logging {

  behavior of "DataSplitTest"

  it should "partitionByLevelAndSplitByRatio" in {
    log.info("foo")
  }

  it should "execute run method" in {
    val args = Array("src/test/resources/data-split.json")
    val app = new DataSplit()
    HDFSUtils.deleteIfExist("file:///Users/dashwang/Project/github/wusuopubupt/kino/abacus/src/test/resources/tmp/data-split/data1")
    HDFSUtils.deleteIfExist("file:///Users/dashwang/Project/github/wusuopubupt/kino/abacus/src/test/resources/tmp/data-split/data2")
    app.execute(args(0))
  }

}
