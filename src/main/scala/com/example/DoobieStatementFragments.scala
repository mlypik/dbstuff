package com.example

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._


object DoobieStatementFragments extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )
  val y = xa.yolo

  import y._

  println("Composing SQL literals")
  val a: Fragment = fr"select name from country"
  val b: Fragment = fr"where code = 'USA'"
  val c = a ++ b

  c.query[String].unique.quick.unsafeRunSync()

  def whereCode(s: String) = fr"where code = $s"

  val fra = whereCode("FRA")
  (fr"select name from country" ++ fra).query[String].quick.unsafeRunSync()

  //note: Fragment const does no escaping -> injection risk
  def count(table: String) = (fr"select count(*) from" ++ Fragment.const(table)).query[Int].unique

  count("city").quick.unsafeRunSync()

  println("Whitespace handling")
  println(
    fr"IN (" ++ List(1, 2, 3).map(n => fr"$n").intercalate(fr",") ++ fr")"
  )
  println(
    fr0"IN (" ++ List(1, 2, 3).map(n => fr0"$n").intercalate(fr",") ++ fr")"
  )

  println("The Fragments module")

  import Fragments.{in, whereAndOpt}

  case class Info(name: String, code: String, population: Int)

  def select(name: Option[String], pop: Option[Int], codes: List[String], limit: Long) = {
    val f1 = name.map(s => fr"name like $s")
    val f2 = pop.map(n => fr"population > $n")
    val f3 = codes.toNel.map(cs => in(fr"code", cs))

    val q: Fragment =
      fr"select name, code, population from country" ++
        whereAndOpt(f1, f2, f3) ++
        fr"limit $limit"
    q.query[Info]
  }

  select(None, None, Nil, 10).check.unsafeRunSync()
  select(Some("U%"), None, Nil, 10).check.unsafeRunSync()
  select(Some("U%"), Some(12345), List("FRA", "GBR"), 10).check.unsafeRunSync()


}
