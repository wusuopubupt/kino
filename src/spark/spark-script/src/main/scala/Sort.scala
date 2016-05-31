import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Sort {
    def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("SortApp")
        val sc = new SparkContext(conf)
        val text_file = sc.textFile("/user/wangdongxu/input/file1.txt")
        val sortedCount = text_file.flatMap(line => line.split(" "))
            .map(word => (word, 1)) 
            .reduceByKey{case (x, y) => x + y}
            .map(_.swap)
            .sortByKey()

        val output = sortedCount.collect()
        for(i <- 0 until output.length)
            println(output(i))
    }
}
