et1 = Set(1, 2, 3) //Immutable set of type Set[Int]  
val set2 = Set("a", 2, true) //Immutable list of type Set[Any]  
import collection.mutable  
val mset = mutable.HashSet("a", "b", "c") //the "mutable version" of Set  

//Sets remove duplicates   
println(Set(1,2,3,2,4,3,2,1,2)) //Set(1, 2, 3, 4)  

//Check if item exists using (item)   
val oneExists = set1(1)  
val fourExists = set1(4)  
println(oneExists) // true  
println(fourExists) // false  

//You can "modify" items the same way as for Lists  
//(DON'T use it this way, use mset -="a" instead)  
mset("a") = false   
println(mset) //Set(c, b)  

//Concatenation using the ++ operator   
//(removes duplicates, order not guaranteed)  
val concatenated = set1 ++ set2 ++ mset  
println(concatenated) // Set(1, a, true, 2, b, 3, c)  
//Concatenation doesn't modify the sets themselves   
println(set1) //Set(1, 2, 3)  

//Removing elements (mutable Sets only)  
mset -= "c"  
println (mset) //Set("b")  

//Adding elements (mutable Lists only)  
mset += "e"  
mset ++= Set("f", "g")  

println (mset) //Set(f, g, e, b)  

//Diff  
val diffSet = Set(1,2,3,4) diff Set(2,3)  
println(diffSet) // Set(1, 4)  

//Find (stops when item is found)  

//Note that this is not an ideal use for Set,   
//a Map would be much better data structure  
//Just for illustration purposes   
val personSet = Set(("Alice",1), ("Bob",2), ("Carol",3))  
def findByName(name:String) = personSet.find(_._1 == name).getOrElse(("David",4))  
val findBob = findByName("Bob")  
val findEli = findByName("Eli")  

println(findBob._2) //2  
println(findEli._2) //4  

