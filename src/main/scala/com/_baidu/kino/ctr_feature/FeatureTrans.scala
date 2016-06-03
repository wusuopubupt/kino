import scala.collection.mutable.ListBuffer

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

/**
 * FeatureTrans: feature to id map
 */
object FeatureTrans {

    //input (1fbe01fe,f3845767,28905ebd,ecad2386,7801e8d9)
    //output ((0:1fbe01fe),(1:f3845767),(2:28905ebd),(3:ecad2386),(4:7801e8d9))
    def parseCatFeatures(catfeatures: Array[String]) :  List[(Int, String)] = {
        var catfeatureList = new ListBuffer[(Int, String)]()
        for (i <- 0 until catfeatures.length){
            val id_feature_tuple = (i, catfeatures(i).toString)
            catfeatureList += id_feature_tuple
            //catfeatureList += i -> catfeatures(i).toString
        }
        // convert ListBuffer(mutable) to List(immutable)
        catfeatureList.toList
    }

    def main(args: Array[String]) {
        //val conf = new SparkConf().setMaster("yarn-cluster")
        val conf = new SparkConf().setMaster("local[4]")
        val sc = new SparkContext(conf)

        var ctrRDD = sc.textFile("file:///Users/dashwang/Project/kino/src/main/resource/ctr_feature/train.dat")
        println("Total records : " + ctrRDD.count)

        var train_raw_rdd = ctrRDD.cache()
        var train_rdd = train_raw_rdd.map{ line =>
            var tokens = line.split(" ",-1)
            // time
            var time = tokens(0)
            // show, click
            var numericalfeatures = tokens.slice(1,3)
            //  category features
            var catfeatures = tokens.slice(5, tokens.size-1)
            (time, numericalfeatures, catfeatures)
        }

        train_rdd.take(1)
        //scala> train_rdd.take(1)
        //res6: Array[(String, Array[String], Array[String])] = Array((1000009418151094273::0,Array(1fbe01fe, 
        //            f3845767, 28905ebd, ecad2386, 7801e8d9, 07d7df22, a99f214a, ddd2926e, 44956a24),
        //              Array(2, 15706, 320, 50, 1722, 0, 35, -1)))

        //  featrue => (feature_id, feature)
        var train_cat_rdd  = train_rdd.map{
            x => parseCatFeatures(x._3)
        }

        train_cat_rdd.take(1)
        //scala> train_cat_rdd.take(1)
        //res12: Array[List[(Int, String)]] = Array(List((0,1fbe01fe), (1,f3845767), (2,28905ebd), 
        //        (3,ecad2386), (4,7801e8d9), (5,07d7df22), (6,a99f214a), (7,ddd2926e), (8,44956a24)))

        // unqie(feature_id, feature), and dispatch ID
        var oheMap = train_cat_rdd.flatMap(x => x).distinct().zipWithIndex().collectAsMap()
        //oheMap: scala.collection.Map[(Int, String),Long] = Map((7,608511e9) -> 31527, (7,b2d8fbed) -> 42207, 
        //  (7,1d3e2fdb) -> 52791
        println("Number of features")
        println(oheMap.size)

        //create OHE for train data
        var ohe_train_rdd = train_rdd.map{ case (time, numerical_features, cateorical_features) =>
            // show, click label
            var numerical_features_int  = numerical_features.map{
                x => x.toInt
            }
            // features
            var cat_features_indexed = parseCatFeatures(cateorical_features)                        
            var cat_feature_ohe = new ListBuffer[Int]
            for (k <- cat_features_indexed) {
                //if(oheMap contains k){
                if(oheMap.exists(_._1 == k)) {
                    //cat_feature_ohe += (oheMap get (k)).get.toInt
                    // oheMap(get(k)) return option[long], oheMap(get(k)).get return long
                    cat_feature_ohe += oheMap.get(k).get.toInt
                } else {
                    cat_feature_ohe += 0
                }               
            }
            // rebuild string
            //time ++ "," ++ numerical_features_int.mkString(",") ++ "," ++  cat_feature_ohe.mkString(",")
            time ++ "," ++ (numerical_features_int ++ cat_feature_ohe).mkString(",")
        }

        ohe_train_rdd.take(10)
        ohe_train_rdd.saveAsTextFile("file:///Users/dashwang/Project/kino/src/main/resource/output/")
        //res15: Array[org.apache.spark.mllib.regression.LabeledPoint] = 
        //  Array((0.0,[43127.0,50023.0,57445.0,13542.0,31092.0,14800.0,23414.0,54121.0,
        //     17554.0,2.0,15706.0,320.0,50.0,1722.0,0.0,35.0,0.0]))

    }
}

