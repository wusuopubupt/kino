package com.mathandcs.kino.abacus.examples.sql

import com.mathandcs.kino.abacus.utils.SparkUtil

/**
  * Created by wangdongxu on 10/13/16.
  */
object DataFrameExample {
  def main(args: Array[String]) {
    val sc = SparkUtil.sparkContext
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    // Create the DataFrame
    val df = sqlContext.read.json("abacus/src/main/resources/people.json")

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
