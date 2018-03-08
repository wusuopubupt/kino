package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.utils.{HDFSUtil, SparkUtil}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._
import org.scalatest.{FlatSpec, FunSuite}

/**
  * Created by dash wang on 1/8/17.
  */
class ClassificationModelEvalTest extends FlatSpec {

  it should "get grouped auc" in {
    val sc = SparkUtil.sparkContext
    val sqlContext = SparkUtil.sqlContext
    import sqlContext.implicits._

    alert("Sample data from spark example")
    val rawInputDF = sc.parallelize(List(
      (0.69, "0.0", 1, "a", true), // false pos
      (0.78, "1.0", 1, "b", true), // true pos
      (0.87, "0.0", 1, "b", false), // false pos
      (0.16, "0.0", 1, "b", false), // true neg
      (0.67, "0.0", 1, "b", true), // false pos
      (0.16, "1.0", 1, "b", true), // false neg
      (0.28, "1.0", 2, "c", false), // false neg
      (0.33, "1.0", 2, "d", true), // false neg
      (0.32, "0.0", 2, "b", true), // true neg
      (0.41, "1.0", 3, "b", true)) // false neg
    ).toDF("score", "label", "count", "name", "flag")

    /*
                    Prediction

                |  Pos    |   Neg
           --------------------------
           Pos  |  1 (TP) |  4 (FN) |
     Real  --------------------------
           Neg  |  3 (FP) |  2 (TN) |
           --------------------------
     */

    implicit val formats = DefaultFormats

    val mea = new ClassificationModelEval()

    val groupAucOutputUri = "file:///tmp/model_eval_grouped_auc"
    HDFSUtil.deleteIfExist(groupAucOutputUri)
    val mm = mea.getGroupedAuc(rawInputDF, "score", "label", "count", groupAucOutputUri)

    val str = write(mm)

    val mmRdd = sc.makeRDD(Seq(str))

    val df = SparkUtil.sqlContext.read.json(mmRdd)

    df.show(1, false)

    assert(df.select("totalGroup").first.getLong(0) == 3L)
    assert(df.select("instanceFlag").first.getSeq[Int](0) == Seq(0, 0, 1))
    assert(df.select("instanceNum").first.getSeq[Long](0) == Seq(6L, 3L, 1L))
    assert(df.select("specifiedFieldContent").first.getSeq[String](0) == Seq("1", "2", "3"))
    assert(df.select("fullPosInstanceCount").first.getLong(0) == 1L)
    assert(df.select("fullPosGroupCount").first.getLong(0) == 1L)
    assert(df.select("fullNegInstanceCount").first.getLong(0) == 0L)
    assert(df.select("fullNegGroupCount").first.getLong(0) == 0L)
    assert(df.select("specifiedFieldName").first.getString(0) == "count")
  }

}
