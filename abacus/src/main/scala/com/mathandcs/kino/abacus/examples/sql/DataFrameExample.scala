package com.mathandcs.kino.abacus.examples.sql

import com.mathandcs.kino.abacus.utils.SparkUtils
import org.apache.spark

/**
  * Created by dashwang on 02/13/16.
  */

case class Person(name: String, age: Int)

object DataFrameExample {
  def main(args: Array[String]) {
    val sc = SparkUtils.sparkContext
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    //
    // Spark SQL
    //

    // Create the DataFrame
    val df = sqlContext.read.json("abacus/src/main/resources/people.json")
    df.registerTempTable("people")

    // Create an RDD of Person objects and register it as a table.
    //import sqlContext.implicits._
    //val people = sc.textFile("abacus/src/main/resources/people.txt").map(_.split(",")).map(p => Person(p(0), p(1).trim.toInt)).toDF()
    //people.registerTempTable("people")

    val sqlDF = sqlContext.sql("SELECT name, age FROM people WHERE age >= 13 AND age <= 19")
    sqlDF.show()
    sqlDF.explain(true)


    //
    // DataFrame
    //

    // Show the content of the DataFrame
    df.show()
    // age  name
    // null Michael
    // 30   Andy
    // 19   Justin

    // Print the schema in a tree format
    df.printSchema()
    // root
    // |-- age: long (nullable = true)
    // |-- name: string (nullable = true)

    // Select only the "name" column
    df.select("name").show()
    // name
    // Michael
    // Andy
    // Justin

    // Select everybody, but increment the age by 1
    df.select(df("name"), df("age") + 1).show()
    // name    (age + 1)
    // Michael null
    // Andy    31
    // Justin  20

    // Select people older than 21
    df.filter(df("age") > 21).show()
    // age name
    // 30  Andy

    // Count people by age
    df.groupBy("age").count().show()
    // age  count
    // null 1
    // 19   1
    // 30   1

  }
}
