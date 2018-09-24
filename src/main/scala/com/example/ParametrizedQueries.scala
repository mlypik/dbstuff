package com.example

import doobie._
import doobie.implicits._
import cats._
import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._


object ParametrizedQueries extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )

  val y = xa.yolo

  import y._

  case class Country(code: String, name: String, pop: Int, gnp: Option[Double])

  sql"select code, name, population, gnp from country"
    .query[Country]
    .stream
    .take(5)
    .quick
    .unsafeRunSync()


  def biggerThan(minPop: Int): doobie.Query0[Country] = sql"""
    select code, name, population, gnp
    from country
    where population > $minPop
    """.query[Country]

  println("single param:")
  biggerThan(150000000).quick.unsafeRunSync()

  def populationIn(range: Range) = sql"""
    select code, name, population, gnp
    from country
    where population > ${range.min}
    and population < ${range.max}
    """.query[Country]

  println("multiple params:")
  populationIn(10000000 to 300000000).quick.unsafeRunSync()

  def populationInCodes(range: Range, codes: NonEmptyList[String]) = {
    val q = fr"""
      select code, name, population, gnp
      from country
      where population > ${range.min}
      and population < ${range.max}
      and """ ++ Fragments.in(fr"code", codes)
    q.queryWithLogHandler[Country](LogHandler.jdkLogHandler)
  }

  println("multiple params fragment:")
  populationInCodes(10000000 to 300000000, NonEmptyList.of("USA", "BRA", "PAK", "GBR"))
    .list
    .transact(xa)
    .unsafeRunSync()
    .foreach(println)

}
