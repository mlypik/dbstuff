package com.example

import doobie._
import doobie.implicits._
import cats._
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

  biggerThan(150000000).quick.unsafeRunSync()


}
