#p("one" -> 1, "two" -> 2, "three" -> 3)   
//Map of type Map[String, Int]  
val map2 = Map(1 -> "one", "2" -> 2.0, 3.0 -> false)   
//Map of type Map[Any, Any]  

import collection.mutable  
val mmap = mutable.HashMap("a" -> 1, "b" -> 2 , "c" -> 3)   
//the "mutable version" of Map  

//Maps remove duplicate keys:  
println(Map("a" -> 1, "a" -> 2)) //Map(a -> 2)  

//Get items using map(key)   
val one = map1("one")  

//NoSuchElementException will be thrown if key doesn't exist!  
//e.g. this code: val fourExists = map1("four")   
//throws NoSuchElementException: key not found: four  
//the get method returns an Option, which will be explained later  
val fourExistsOption = map1.get("four")  

println(one) // 1  
println(fourExistsOption.isDefined) // false  

//You can set / modify items using map(key) = value  
mmap("d") = 4   
println(mmap) //Map(b -> 2, d -> 4, a -> 1, c -> 3)  

//Concatenation using the ++ operator   
//(removes duplicate keys, order not guaranteed)  
val concatenated = map1 ++ map2 ++ mmap  
println(concatenated)   
// Map(three -> 3, 1 -> one, two -> 2, a -> 1, b -> 2, 3.0 -> false, 2 -> 2.0, c -> 3, one -> 1, d -> 4)  
//Concatenation doesn't modify the maps themselves   
println(map1) //Map(one -> 1, two -> 2, three -> 3)  

//Removing elements (mutable Sets only)  
mmap -= "c"  
println (mmap) //Map(b -> 2, d -> 4, a -> 1)  

//Adding elements (mutable Lists only)  
mmap += "e" -> 5  
mmap ++= Map("f" -> 6, "g" -> 7)  

println (mmap) //Map(e -> 5, b -> 2, d -> 4, g -> 7, a -> 1, f -> 6)  

//Find   
val personMap = Map(("Alice",1), ("Bob",2), ("Carol",3))  
def findByName(name:String) = personMap.getOrElse(name, 4)  
val findBob = findByName("Bob")  
val findEli = findByName("Eli")  

println(findBob) //2  
println(findEli) //4  

