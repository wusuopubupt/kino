import scala.collection.mutable  

val arrayBuffer = mutable.ArrayBuffer(1, 2, 3)   
val listBuffer = mutable.ListBuffer("a", "b", "c")  
val hashSet = mutable.Set(0.1, 0.2, 0.3)  
val hashMap = mutable.Map("one" -> 1, "two" -> 2)  

arrayBuffer += 4  
listBuffer += "d"  
arrayBuffer -= 1  
listBuffer -= "a"  
hashMap += "four" -> 4  
hashMap -= "one"  

arrayBuffer ++= List(5, 6, 7)  
hashMap ++= Map("five" -> 5, "six" -> 6)  
hashMap --= Set("one", "three")  


println(arrayBuffer)  
println(listBuffer)  
println(hashMap)  

