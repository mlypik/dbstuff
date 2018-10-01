package com.example

import java.sql.SQLException

import doobie._
import doobie.implicits._
import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import doobie.postgres.sqlstate

object DoobieErrorHandling extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )

  val y = xa.yolo

  import y._

  val p = 42.pure[ConnectionIO]

  val attempt: ConnectionIO[Either[Throwable, Int]] = p.attempt

  List(
    sql"""drop table if exists person""",
    sql"""create table person (
      id SERIAL,
      name VARCHAR not null unique
      )"""
  ).traverse(_.update.quick).void.unsafeRunSync()

  case class Person(id: Int, name: String)

  def insert(s: String): ConnectionIO[Person] = {
    sql"insert into person (name) values ($s)"
      .update.withUniqueGeneratedKeys("id", "name")
  }

  insert("bob").quick.unsafeRunSync()

  try {
    insert("bob").quick.unsafeRunSync()
  } catch {
    case e: SQLException =>
      println(e.getMessage)
      println(e.getSQLState)
  }

  def safeInsert(s: String): ConnectionIO[Either[String, Person]] =
    insert(s).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => "Oops!"
    }

  safeInsert("steve").quick.unsafeRunSync()
  safeInsert("bob").quick.unsafeRunSync()

}
