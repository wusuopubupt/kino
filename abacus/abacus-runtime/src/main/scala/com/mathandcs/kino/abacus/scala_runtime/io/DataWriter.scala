package com.mathandcs.kino.abacus.scala_runtime.io

import com.mathandcs.kino.abacus.common.Format
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.Logging
import org.apache.spark.sql.DataFrame

/**
  * Created by dash wang on 10/18/16.
  */
trait Saver {
  def save(df: DataFrame, url: String)
}

object TsvSaver extends Saver {
  override def save(df: DataFrame, url: String): Unit = {
    df.rdd.map(_.mkString("\t")).saveAsTextFile(url, classOf[GzipCodec])
  }
}

object CsvSaver extends Saver {
  override def save(df: DataFrame, url: String): Unit = {
    df.rdd.map(_.mkString(",")).saveAsTextFile(url, classOf[GzipCodec])
  }
}

object ParquetSaver extends Saver {
  override def save(df: DataFrame, url: String): Unit = {
    // Saving parquet by calling DataFrameWriter with gzip compression enabled
    // see: [org.apache.spark.sql.DataFrameWriter]
    df.write.option("spark.sql.parquet.compression.codec", "gzip").parquet(url)
  }
}

object JsonSaver extends Saver {
  override def save(df: DataFrame, url: String): Unit = {
    df.write.option("spark.sql.parquet.compression.codec", "gzip").mode("append").json(url)
  }
}

object DataWriter extends Logging {

  def save(df: DataFrame, url: String, format: String): Unit = {
    log.info(s"Saving output data to : ${url}")
    Format.withName(format) match {
      case Format.tsv => TsvSaver.save(df, url)
      case Format.csv => CsvSaver.save(df, url)
      case Format.parquet => ParquetSaver.save(df, url)
      case Format.json => JsonSaver.save(df, url)
      case _ => throw new IllegalArgumentException(s"Unsupported data format: ${format}." +
        s"Accepted formats are 'tsv', 'csv', 'parquet', 'json'.")
    }
  }

}


