// refer : http://www.yiibai.com/scala/scala_strings.html
object String {
	def main(args: Array[String]) {
		var palindrome  = "Dot saw I was Tod"
		// String.length()
		var len = palindrome.length()
		println("String Length is : " + len)

		// String.concat()
		var s1 = "My name is "
		println(s1.concat("dashWang"))

		// String.format() or printf()
		var s2 = printf("My name is %s, and my age is %d", "dashWang", 25)	
		println(s2)
		var s3 = "My name is %s, and my age is %d".format("dashWang", 25)	
		println(s3)
	}	
}
