package com.example

import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._


object TypecheckedQueries extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )

  val y = xa.yolo

  import y._


  case class CountryBad(code: Int, name: String, pop: Int, gnp: Double)


  def biggerThanBad(minPop: Short) = sql"""
    select code, name, population, gnp, indepyear
    from country
    where population > $minPop
    """.query[CountryBad]

  biggerThanBad(0).check.unsafeRunSync()

  case class CountryFixed(code: String, name: String, popultaion: Int, gnp: Option[BigDecimal], indepyear: Option[Short])

  def biggerThanFixed(minPop: Int) = sql"""
    select code, name, population, gnp, indepyear
    from country
    where population > $minPop
    """.query[CountryFixed]

  biggerThanFixed(0).check.unsafeRunSync()

  biggerThanFixed(0).checkOutput.unsafeRunSync()

}
