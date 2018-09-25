package com.example

import doobie._
import doobie.implicits._
import cats._
import cats.data.NonEmptyList
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

  case class Country(code: Int, name:String, pop: Int, gnp: Double)

  def biggerThan(minPop: Short) = sql"""
    select code, name, population, gnp, indepyear
    from country
    where population > $minPop
    """.query[Country]

  biggerThan(0).check.unsafeRunSync()


}
