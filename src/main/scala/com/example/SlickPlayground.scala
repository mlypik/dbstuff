package com.example

import com.example.schema.{Coffees, Suppliers}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object SlickPlayground extends App {

  val db = Database.forConfig("slick-postgres")
  try {
    val suppliers = TableQuery[Suppliers]
    val coffees = TableQuery[Coffees]

    val setup = DBIO.seq(
      // Create the tables, including primary and foreign keys
      (suppliers.schema ++ coffees.schema).create,
      //coffees.schema.create,

      // Insert some suppliers
      suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      // Equivalent SQL code:
      // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffees ++= Seq(
        ("Colombian", 101, 799, 0, 0),
        ("French_Roast", 49, 899, 0, 0),
        ("Espresso", 150, 999, 0, 0),
        ("Colombian_Decaf", 101, 899, 0, 0),
        ("French_Roast_Decaf", 49, 999, 0, 0)
      )
      // Equivalent SQL code:
      // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
    )

    val setupFuture: Future[Unit] = db.run(setup)
    setupFuture.andThen{
      case Success(_) => println("done")
      case Failure(exception) => println(exception)
    }

    println("Coffees:")
    db.run(coffees.result).map(_.foreach {
      case (name, supID, price, sales, total) =>
        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    })

  } finally {
    db.close()
  }

}
