// refer: http://www.tutorialspoint.com/scala/scala_classes_objects.htm
import java.io._

class Point(val xc: Int, val yc: Int) {
	var x: Int = xc
	var y: Int = yc

	def move(dx: Int, dy: Int) {
		x = x + dx
		y = y + dy
		println("Point x location: " + x)
		println("Point y location: " + y)
	}
}

// 继承,重写
class Location(override val xc: Int, override val yc: Int,
	val zc: Int) extends Point(xc, yc) {
	var z: Int = zc

	def move(dx: Int, dy: Int, dz: Int) {
		x = x + dx
		y = y + dy
		z = z + dz
		println("Point x location : " +  x)
		println("Point y location : " +  y)
		println("Point z location : " +  z)
	}
}

object ClassAndObject {
	def main(args: Array[String]) {
		var pt = new Point(10, 20)
		pt.move(10, 10)

		println("=====")

		var loc = new Location(10, 20, 30)
		loc.move(10, 10, 10)
	}
}
