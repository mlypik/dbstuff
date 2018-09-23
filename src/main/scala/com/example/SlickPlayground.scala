package com.example

import com.example.schema.Countries
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object SlickPlayground extends App {

  val db = Database.forConfig("slick-postgres")

  try {
    val countries = TableQuery[Countries]

    db.run(countries.result).map(_.take(5).foreach {
      case (code, name, continent) =>
        println("  " + code + "\t" + name + "\t" + continent)
    })

  } finally {
    db.close()
  }

}
