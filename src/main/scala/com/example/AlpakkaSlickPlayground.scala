package com.example

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.alpakka.slick.scaladsl._
import slick.jdbc.GetResult

object AlpakkaSlickPlayground extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val session: SlickSession = SlickSession.forConfig("slick-akka-postgres")
  import session.profile.api._

  case class Country(name: String, continent: String)

  implicit val getCountryResult: AnyRef with GetResult[Country] = GetResult(r => Country(r.nextString(), r.nextString()))

  val done: Future[Done] =
    Slick
      .source(sql"SELECT name, continent FROM country".as[Country])
      .throttle(elements = 1, per = 1.second)
      .take(5)
      .runWith(Sink.foreach(println))

  done.onComplete {
    _ =>
      session.close()
      system.terminate()
  }


}
