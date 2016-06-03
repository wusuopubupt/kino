//Immutable list of type List[Int]  
val list1 = List(1, 2, 3) //> list1 = List(1, 2, 3)  

list1.foreach(println)


//Immutable list of type List[Any]  
val list2 = List("a", 2, true) //> list2 = List(a, 2, true)  
import collection.mutable   
//the "mutable version" of List  
val mlist = mutable.ArrayBuffer("a", "b", "c") //> mlist = ArrayBuffer(d, b, e, f, g)  

//Access items using (index) not [index]   
val firstItem = list1(0) //> firstItem = 1  

//Modify items the same way  (mutable Lists only)    
mlist(0) = "d"    
mlist //> ArrayBuffer(d, b, e, f, g)  

//Concatenation using the ++ operator or ::: (lists only)  
list1 ++ list2 //> List(1, 2, 3, a, 2, true)  
list1 ::: list2 //> List(1, 2, 3, a, 2, true)  

//Prepending an item using either :: (lists only) or +:  
0 :: list1 //> List(0, 1, 2, 3)  
0 +: list1 //> List(0, 1, 2, 3)  

//appending an item using :+ (not efficient for immutable List)  
list1 :+ 4 //> List(1, 2, 3, 4)  

//all together  
val concatenated = 1 :: list1 ::: list2 ++ mlist :+ 'd' //> concatenated = List(1, 1, 2, 3, a, 2, true, d, b, c, d)  
//concatenation doesn't modify the lists themselves     
list1 //> List(1, 2, 3)  

//Removing elements (mutable list only, creates a new array):  

//creates a new array with "c" removed, mlist is not touched  
mlist - "c" //> ArrayBuffer(d, b)  
//creates a new array with e, f removed, mlist is not touched  
mlist -- List("e", "f") //> ArrayBuffer(d, b, c)  
//mlist not modified  
mlist //> ArrayBuffer(d, b, e, f, g)  
//Removing elements (mutable Lists only):   

//removes c from the list itself  
mlist -= "c" //> ArrayBuffer(d, b, e, f, g)  
mlist //> ArrayBuffer(d, b, e, f, g)  
//removes e and f from mlist itself  
mlist --= List("e", "f") //> ArrayBuffer(d, b, e, f, g)  
mlist //> ArrayBuffer(d, b, e, f, g)  

//Adding elements (mutable Lists only)   
mlist += "e" //> ArrayBuffer(d, b, e, f, g)  
mlist ++= List("f", "g") //> ArrayBuffer(d, b, e, f, g)  

mlist //ArrayBuffer(d, b, e, f, g) //> ArrayBuffer(d, b, e, f, g)  

//Diff   
val diffList = List(1,2,3,4) diff List(2,3) //> diffList = List(1, 4)  

//Find (stops when item is found)   
val personList = List(("Alice",1), ("Bob",2), ("Carol",3)) //> personList = List((Alice,1), (Bob,2), (Carol,3))  
def findByName(name:String) = personList.find(_._1 == name).getOrElse(("David",4)) //> findByName(name = "foo") => (David,4)  
val findBob = findByName("Bob") //> findBob = (Bob,2)  
val findEli = findByName("Eli") //> findEli = (David,4)  

findBob._2 //> 2  
findEli._2 //> 4  

