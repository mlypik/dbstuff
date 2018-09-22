import java.util.Formatter
import java.util.logging.SimpleFormatter

import scala.annotation.tailrec

//object Task1Scala {
//
//  def sumOfElementsOnOddIndices(l: List[Int]): Int = {
//    def sumOfElementsOnOddIndicesAcc(l: List[Int])(acc: Int = 0): Int = {
//      l match {
//        case List() => acc
//        case List(e) => e + acc
//        case head :: _ :: rest => sumOfElementsOnOddIndicesAcc(rest)(head + acc)
//      }
//    }
//    sumOfElementsOnOddIndicesAcc(l)()
//  }
//
//  def sumOfElementsOnOddIndices2(l: List[Int]): Int = {
//    l.zipWithIndex
//      .filter(p => p._2 % 2 == 0)
//      .map(p => p._1)
//      .sum
//  }
//}
//
//Task1Scala.sumOfElementsOnOddIndices(List(1, 2))
//Task1Scala.sumOfElementsOnOddIndices(List(1, 2, 3))
//Task1Scala.sumOfElementsOnOddIndices2(List(1, 2))
//Task1Scala.sumOfElementsOnOddIndices2(List(1, 2, 3))

trait Formatter {
  def format(args: Any*): String
}

class SimpleFormatter extends Formatter {
  override def format(args: Any*): String ={
    args.mkString(" ")
  }


}
object Task2Scala {
  val formatter: Formatter = new SimpleFormatter
  def buildString(args: Any*): String = {
    formatter.format(args: _*)
  }
}


Task2Scala.buildString("Touk", "likes", "holiday")