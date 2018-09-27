package com.example

import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._


object DoobieInserting extends App {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/world",
    "postgres",
    "slickpass"
  )
  val y = xa.yolo
  import y._

  println("Data definition")
  val drop = sql"""
    drop table if exists person
    """.update.run

  val create = sql"""
    create table person(
    id SERIAL,
    name VARCHAR NOT NULL UNIQUE,
    age SMALLINT
    )
    """.update.run

  println("Inserting")
  val affectedRows = (drop, create).mapN(_ + _).transact(xa).unsafeRunSync()
  println(s"affected rows: $affectedRows")

  def insert1(name: String, age: Option[Short]): Update0 = sql"""
    insert into person (name, age) values ($name, $age)
    """.update

  val insertResult = insert1("Alice", Some(12)).run.transact(xa).unsafeRunSync()
  println(s"affected rows: $insertResult")
  insert1("Bob", none).quick.unsafeRunSync()

  case class Person(id: Long, name: String, age: Option[Short])

  sql"select id, name, age from person".query[Person].quick.unsafeRunSync()

  println("Updating")
  sql"update person set age = 15 where name = 'Alice'".update.quick.unsafeRunSync()

  sql"select id, name, age from person".query[Person].quick.unsafeRunSync()


  println("Retrieving results")
  def insert2(name: String, age: Option[Short]): ConnectionIO[Person] =
    for {
      _ <- sql"insert into person (name, age) values ($name, $age)".update.run
      id <- sql"select lastval()".query[Long].unique
      p <- sql"select id, name, age from person where id = $id".query[Person].unique
    } yield p


  insert2("Jimmy", Some(42)).quick.unsafeRunSync()

  def insert2_H2(name: String, age: Option[Short]) =
    for {
      id <- sql"insert into person (name, age) values ($name, $age)"
          .update
          .withUniqueGeneratedKeys[Int]("id")
      p <- sql"select id, name, age from person where id = $id"
          .query[Person]
          .unique
    } yield p

  insert2_H2("Ramone", Some(42)).quick.unsafeRunSync()


  def insert3(name: String, age: Option[Short]): ConnectionIO[Person] =
    sql"insert into person (name, age) values ($name, $age)"
    .update
    .withUniqueGeneratedKeys("id", "name", "age")

  insert3("Elvis", None).quick.unsafeRunSync()

  val up = sql"update person set age = age + 1 where age is not null"
    .update
    .withGeneratedKeys[Person]("id", "name", "age")

  up.quick.unsafeRunSync()
  up.quick.unsafeRunSync()

  println("Batch updates")
  type PersonInfo = (String, Option[Short])

  def insertMany(ps: List[PersonInfo]): ConnectionIO[Int] = {
    val sql = "insert into person (name, age) values (?, ?)"
    Update[PersonInfo](sql).updateMany(ps)
  }
  val data = List[PersonInfo](
    ("Frank", Some(12)),
    ("Daddy", None)
  )
  insertMany(data).quick.unsafeRunSync()

  def insertMany2(ps: List[PersonInfo]): fs2.Stream[ConnectionIO, Person] = {
    val sql = "insert into person (name, age) values (?, ?)"
    Update[PersonInfo](sql).updateManyWithGeneratedKeys[Person]("id", "name", "age")(ps)
  }
  val data2 = List[PersonInfo](
    ("Banjo", Some(39)),
    ("Skeeter", None),
    ("Jim-Bob", Some(12))
  )
  insertMany2(data2).quick.unsafeRunSync()
}
