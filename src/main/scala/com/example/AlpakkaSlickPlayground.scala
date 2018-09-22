package com.example

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.alpakka.slick.scaladsl._
import slick.jdbc.GetResult

import scala.util.{Failure, Success}

object AlpakkaSlickPlayground extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  case class Coffee(name: String, supID: Int, price: Double, sales: Int, total: Int)

  implicit val session: SlickSession = SlickSession.forConfig("slick-akka-postgres")
  implicit val getUserResult: AnyRef with GetResult[Coffee] = GetResult(r => Coffee(r.nextString(), r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt()))

  import session.profile.api._


  val coffeesStreams = scala.collection.immutable.Seq(
    Coffee("Colombian", 101, 799, 0, 0),
    Coffee("French_Roast", 49, 899, 0, 0),
    Coffee("Espresso", 150, 999, 0, 0),
    Coffee("Colombian_Decaf", 101, 899, 0, 0),
    Coffee("French_Roast_Decaf", 49, 999, 0, 0)
  )

//  val populate: Future[Done] =
//    Source(coffeesStreams)
//      .runWith(
//        // add an optional first argument to specify the parallism factor (Int)
//        Slick.sink(coffee => sqlu"INSERT INTO coffees VALUES(${coffee.name}, ${coffee.supID}, ${coffee.price},${coffee.sales},${coffee.total})")
//      )
//
//  populate.andThen{
//    case Success(_) => println("done")
//    case Failure(exception) => println(exception)
//  }

    val done: Future[Done] =
      Slick
        .source(sql"SELECT * FROM coffees".as[String])
        .log("user")
        .runWith(Sink.ignore)

    done.onComplete {
      case Failure(exception) => println(exception)
      case _ =>
        session.close()
        system.terminate()
    }


}
