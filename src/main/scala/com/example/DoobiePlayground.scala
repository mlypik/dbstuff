package com.example

import doobie._
import doobie.implicits._

import cats._
import cats.effect._
import cats.implicits._

object DoobiePlayground extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )

  val program1 = 42.pure[ConnectionIO]

  val io = program1.transact(xa)
  println(io.unsafeRunSync())

  val program2 = sql"select 42".query[Int].unique
  val io2 = program2.transact(xa)
  println(io2.unsafeRunSync())

  val program3: ConnectionIO[(Int, Double)] =
    for {
      a <- sql"select 42".query[Int].unique
      b <- sql"select random()".query[Double].unique
    } yield (a, b)

  println(program3.transact(xa).unsafeRunSync())

  sql"select name from country"
    .query[String]
    .to[List]
    .transact(xa)
    .unsafeRunSync()
    .take(5)
    .foreach(println)

}
